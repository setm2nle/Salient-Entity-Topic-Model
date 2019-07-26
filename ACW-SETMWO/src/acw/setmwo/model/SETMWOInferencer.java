package acw.setmwo.model;

import acw.common.utils.collection.StringIdDualDict;
import acw.common.utils.stdout.StdOut;
import acw.setmwo.files.SETMWOFile_Phi;
import acw.setmwo.files.SETMWOFile_Theta;
import acw.setmwo.files.SETMWOFile_Varphi;
import acw.setmwo.param.SETMWOCmdOption;

public class SETMWOInferencer {	
	/**
	 * Model parameters
	 */
	private SETMWOCmdOption option;
	
	/**
	 * Global Dictionary of words
	 */
	public StringIdDualDict globalDictW;
	public StringIdDualDict globalDictE;

	/**
	 * Previously trained model
	 */
	public SETMWOModel trnModel;
	
	/**
	 * The new model for input dataset
	 */
	private SETMWOModel newModel;
	
	/**
	 * VToken * \beta
	 */
	private double VE_beta;
	private double VW_gamma;
	private double K_alpha;
	private double lambda_weighted_prior;
	private double K_lambda_weighted_prior;
	
	/**
	 * Number of iterations for inference sampling 
	 */
	public int niters = 100;

	//-----------------------------------------------------
	// Init method
	//-----------------------------------------------------
	public boolean init(SETMWOCmdOption option){
		this.option = option;
		trnModel = new SETMWOModel();

		// initialize trained model
		if (!trnModel.initEstimatedModel(option))
			return false;

		globalDictW = trnModel.data.localDictWE;
		globalDictE = trnModel.data.localDictSE;

		SETMWOFile_Theta.computeTheta(trnModel);
		SETMWOFile_Phi.computePhi(trnModel);
		SETMWOFile_Varphi.computeVarphi(trnModel);

		return true;
	}

	public SETMWOModel inference(){
		/*
		 * New model initialization
		 */
		newModel = new SETMWOModel();
		if (!newModel.initNewModelWithExistingModel(option, trnModel)) return null;

		/*
		 * some pre-calculated values
		 */
		VE_beta = trnModel.paramStatic.VE * trnModel.paramStatic.beta;
		VW_gamma = trnModel.paramStatic.VW * trnModel.paramStatic.gamma;
		K_alpha = trnModel.paramStatic.K * trnModel.paramStatic.alpha;
		lambda_weighted_prior = trnModel.paramStatic.lambda * trnModel.paramStatic.alpha + (1 - trnModel.paramStatic.lambda) * trnModel.paramStatic.sigma;
		K_lambda_weighted_prior = trnModel.paramStatic.K * lambda_weighted_prior;
		
		VW_gamma = newModel.paramStatic.VW * newModel.paramStatic.gamma;
		
		/*
		 * Start sampling for inference
		 */
		StdOut.infoStdOut("Sampling " + niters + " iteration for inference!");
		for (newModel.paramStatic.liter = 1; newModel.paramStatic.liter <= niters; newModel.paramStatic.liter++){

			// for each document
			for (int m = 0; m < newModel.paramStatic.M; m++){
				// sample for words
				for (int w = 0; w < newModel.data.docs[m].words.length; w++){
					// sampling the topic corresponding to the current source entity
					int topic = infSamplingWord(m, w);
					Integer[] topicArr = newModel.paramRuntime.y_t.get(m);
					topicArr[w] = topic;
					newModel.paramRuntime.y_t.set(m, topicArr);
				}
			}//end foreach new doc
		}// end iterations

		StdOut.infoStdOut("Gibbs sampling for inference completed! Saving the inference outputs!");		

		SETMWOFile_Theta.computeTheta(newModel);
		SETMWOFile_Phi.computePhi(newModel);
		SETMWOFile_Varphi.computeVarphi(newModel);
		newModel.paramStatic.liter--;
		SETMWOModelIO.saveModel(newModel, option.dpTest, "final", false);

		return newModel;
	}

	/**
	 * Do sampling
	 * @param m document number
	 * @param w word number
	 * @return topic id
	 */
	public int infSamplingWord(int m, int w){
		/*
		 * Get current word information, word itself, its facet and topic associated to srcEntity 
		 */
		int word = newModel.data.docs[m].words[w];
		int wordTrn = newModel.data.lid2gidWE.get(word);
		int topic = newModel.paramRuntime.y_t.get(m)[w];
		
		/*
		 * Remove z_i from the count variable
		 */
		newModel.paramRuntime.n_w2y[word][topic] -= 1;
		newModel.paramRuntime.nsum_w2y[topic] -= 1;
		newModel.paramRuntime.nd_w2y[m][topic] -= 1;
		newModel.paramRuntime.ndsum_w2y[m] -= 1;
		
		/*
		 * Calculate probability for each topic
		 */
		for (int k = 0; k < newModel.paramStatic.K; k++){
			/*
			 * Item 1
			 */
			double item1_numerator = newModel.paramRuntime.n_w2y[word][k] + trnModel.paramRuntime.n_w2y[wordTrn][k] + trnModel.paramStatic.gamma;
			double item1_denominator = newModel.paramRuntime.nsum_w2y[k] + trnModel.paramRuntime.nsum_w2y[k] + VW_gamma;
			double item1_val = item1_numerator / item1_denominator;
			
			double item2_numerator = 0;
			double item2_denominator = 0;
			if(newModel.data.docs[m].entities != null){
				/*
				 * Item 2 numerator
				 */
				item2_numerator = lambda_weighted_prior;
				item2_numerator += trnModel.paramStatic.lambda * newModel.paramRuntime.nd_w2y[m][k];
				int sum_from_salient_entities = 0;
				for (int i = 0; i < newModel.data.docs[m].entities.length; i++) {
					int entity = newModel.data.docs[m].entities[i];
					int entityTrn = newModel.data.lid2gidSE.get(entity);
					int countUnderTopic_k = newModel.paramRuntime.n_e2z[entity][k] + trnModel.paramRuntime.n_e2z[entityTrn][k];
					sum_from_salient_entities += countUnderTopic_k;
				}
				item2_numerator += (1 - trnModel.paramStatic.lambda) * (double) sum_from_salient_entities / newModel.data.docs[m].entities.length;

				/*
				 * Item 2 denominator
				 */
				item2_denominator = K_lambda_weighted_prior + trnModel.paramStatic.lambda * newModel.paramRuntime.ndsum_w2y[m];
				sum_from_salient_entities = 0;
				for (int i = 0; i < newModel.data.docs[m].entities.length; i++) {
					int entity = newModel.data.docs[m].entities[i];
					int entityTrn = newModel.data.lid2gidSE.get(entity);
					int countUnderAllTopic = newModel.paramRuntime.nsum_z2e[entity] + trnModel.paramRuntime.nsum_z2e[entityTrn];
					sum_from_salient_entities += countUnderAllTopic;
				}
				item2_denominator += (1 - trnModel.paramStatic.lambda) * (double) sum_from_salient_entities / newModel.data.docs[m].entities.length;
			}else{
				/*
				 * Item 2 numerator
				 */
				item2_numerator = trnModel.paramStatic.alpha + newModel.paramRuntime.nd_w2y[m][k];

				/*
				 * Item 2 denominator
				 */
				item2_denominator = trnModel.paramStatic.K * trnModel.paramStatic.alpha + newModel.paramRuntime.ndsum_w2y[m];
			}

			/*
			 * final combination
			 */
			newModel.paramRuntime.p_z[k] = item1_val * item2_numerator / item2_denominator;
		}

		/*
		 * Scaled sample topic because of unnormalized p_z[]
		 */
		for (int k = 1; k < newModel.paramStatic.K; k++){
			newModel.paramRuntime.p_z[k] += newModel.paramRuntime.p_z[k - 1];
		}

		double u = Math.random() * newModel.paramRuntime.p_z[newModel.paramStatic.K - 1];

		for (topic = 0; topic < newModel.paramStatic.K; topic++){
			if (newModel.paramRuntime.p_z[topic] > u) //sample topic w.r.t distribution p
				break;
		}

		/*
		 * add newly estimated z_w to count variables
		 */
		newModel.paramRuntime.n_w2y[word][topic] += 1;
		newModel.paramRuntime.nsum_w2y[topic] += 1;
		newModel.paramRuntime.nd_w2y[m][topic] += 1;
		newModel.paramRuntime.ndsum_w2y[m] += 1;

		return topic;
	}
	
	public int samplingEntity(int m, int n){
		/*
		 * Get current word information, word itself, its facet and topic associated to srcEntity 
		 */
		int entity = newModel.data.docs[m].entities[n];
		int entityTrn = trnModel.data.lid2gidSE.get(entity);
		int topic = newModel.paramRuntime.z_t.get(m)[n];

		/*
		 * Remove z_i from the count variable
		 */
		newModel.paramRuntime.n_e2z[entity][topic] -= 1;
		newModel.paramRuntime.nsum_e2z[topic] -= 1;
		newModel.paramRuntime.nd_e2z[m][topic] -= 1;
		newModel.paramRuntime.ndsum_e2z[m] -= 1;

		/*
		 * Calculate probability for each topic
		 */
		for (int k = 0; k < trnModel.paramStatic.K; k++){
			/*
			 * Item 1
			 */
			double item1_numerator = newModel.paramRuntime.n_e2z[entity][k] + trnModel.paramRuntime.n_e2z[entityTrn][k] + trnModel.paramStatic.beta;
			double item1_denominator = newModel.paramRuntime.nsum_e2z[k] + trnModel.paramRuntime.nsum_e2z[k] + VE_beta;
			double item1_val = item1_numerator / item1_denominator;

			/*
			 * Item 2
			 */
			double item2_numerator = newModel.paramRuntime.nd_e2z[m][k] + trnModel.paramStatic.alpha;
			double item2_denominator = newModel.paramRuntime.ndsum_e2z[m] + K_alpha;
			double item2_val = item2_numerator / item2_denominator;

			/*
			 * final combination
			 */
			newModel.paramRuntime.p_z[k] = item1_val * item2_val;
		}

		/*
		 * Scaled sample topic because of unnormalized p_z[]
		 */
		for (int k = 1; k < newModel.paramStatic.K; k++){
			newModel.paramRuntime.p_z[k] += newModel.paramRuntime.p_z[k - 1];
		}

		double u = Math.random() * newModel.paramRuntime.p_z[trnModel.paramStatic.K - 1];

		for (topic = 0; topic < trnModel.paramStatic.K; topic++){
			if (newModel.paramRuntime.p_z[topic] > u) //sample topic w.r.t distribution p
				break;
		}

		/*
		 * add newly estimated z_t to count variables
		 */
		newModel.paramRuntime.n_e2z[entity][topic] += 1;
		newModel.paramRuntime.nsum_e2z[topic] += 1;
		newModel.paramRuntime.nd_e2z[m][topic] += 1;
		newModel.paramRuntime.ndsum_e2z[m] += 1;

		return topic;
	}
}
