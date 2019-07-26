package acw.setm.model;

import acw.common.utils.collection.StringIdDualDict;
import acw.common.utils.stdout.StdOut;
import acw.setm.files.SETMFile_Phi;
import acw.setm.files.SETMFile_Psi;
import acw.setm.files.SETMFile_Rho;
import acw.setm.files.SETMFile_Theta;
import acw.setm.files.SETMFile_Varphi;
import acw.setm.param.SETMCmdOption;

public class SETMInferencer {	
	/**
	 * Model parameters
	 */
	private SETMCmdOption option;
	
	/**
	 * Global Dictionary of words
	 */
	public StringIdDualDict globalDictW;
	public StringIdDualDict globalDictSE;
	public StringIdDualDict globalDictOE;

	/**
	 * Previously trained model
	 */
	public SETMModel trnModel;
	
	/**
	 * The new model for input dataset
	 */
	private SETMModel newModel;
	
	/**
	 * VToken * \beta
	 */
	private double VOE_delta;
	private double VW_gamma;
	
	/**
	 * Number of iterations for inference sampling 
	 */
	public int niters = 100;

	//-----------------------------------------------------
	// Init method
	//-----------------------------------------------------
	public boolean init(SETMCmdOption option){
		this.option = option;
		trnModel = new SETMModel();

		// initialize trained model
		if (!trnModel.initEstimatedModel(option))
			return false;

		globalDictW = trnModel.data.localDictW;
		globalDictSE = trnModel.data.localDictSE;
		globalDictOE = trnModel.data.localDictOE;

		SETMFile_Theta.computeTheta(trnModel);
		SETMFile_Phi.computePhi(trnModel);
		SETMFile_Varphi.computeVarphi(trnModel);
		SETMFile_Rho.computeRho(trnModel);
		SETMFile_Psi.computePsi(trnModel);

		return true;
	}

	public SETMModel inference(){
		/*
		 * New model initialization
		 */
		newModel = new SETMModel();
		if (!newModel.initNewModelWithExistingModel(option, trnModel)) return null;

		/*
		 * some pre-calculated values
		 */
		VOE_delta = newModel.paramStatic.VOE * newModel.paramStatic.delta;
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
				
				// sample for observed entities
				for (int e = 0; e < newModel.data.docs[m].oeCount; e++){
					// sampling the topic corresponding to the current source entity
					int topic = infSamplingObsEntity(m, e);
					Integer[] topicArr = newModel.paramRuntime.u_t.get(m);
					topicArr[e] = topic;
					newModel.paramRuntime.u_t.set(m, topicArr);
				}
			}//end foreach new doc

		}// end iterations

		StdOut.infoStdOut("Gibbs sampling for inference completed! Saving the inference outputs!");		

		SETMFile_Theta.computeTheta(newModel);
		SETMFile_Phi.computePhi(newModel);
		SETMFile_Psi.computePsi(newModel);
		SETMFile_Rho.computeRho(newModel);
		SETMFile_Varphi.computeVarphi(newModel);
		
		newModel.paramStatic.liter--;
		SETMModelIO.saveModel(newModel, option.dpTest, "final", false);

		return newModel;
	}

	/**
	 * Do sampling
	 * @param m document number
	 * @param w word number
	 * @return topic id
	 */
	public int infSamplingWord(int m, int wIdx){
		/*
		 * Get current word information, word itself, its facet and topic associated to srcEntity 
		 */
		int word = newModel.data.docs[m].words[wIdx];
		int word_global = newModel.data.lid2gidW.get(word);
		int topic = newModel.paramRuntime.y_t.get(m)[wIdx];
		
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
			double item1_numerator = newModel.paramRuntime.n_w2y[word][k] + trnModel.paramRuntime.n_w2y[word_global][k] + newModel.paramStatic.gamma;
			double item1_denominator = newModel.paramRuntime.nsum_w2y[k] + trnModel.paramRuntime.nsum_w2y[k] + VW_gamma;
			double item1_val = item1_numerator / item1_denominator;
			
			/*
			 * Item 2 numerator and denominator
			 */
			double item2_numerator = newModel.paramStatic.alpha + newModel.paramRuntime.nd_e2u[m][k] + newModel.paramRuntime.nd_w2y[m][k];
			double item2_denominator = newModel.paramStatic.K * newModel.paramStatic.alpha + newModel.paramRuntime.ndsum_e2u[m] + newModel.paramRuntime.ndsum_w2y[m];

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
	
	public int infSamplingObsEntity(int m, int e){
		int obsEntity = newModel.data.docs[m].obsEntities[e];
		int obsEntity_global = newModel.data.lid2gidOE.get(obsEntity);
		int topic = newModel.paramRuntime.u_t.get(m)[e];

		/*
		 * Remove y_i from the count variable
		 */
		newModel.paramRuntime.n_e2u[obsEntity][topic] -= 1;
		newModel.paramRuntime.nsum_e2u[topic] -= 1;
		newModel.paramRuntime.nd_e2u[m][topic] -= 1;
		newModel.paramRuntime.ndsum_e2u[m] -= 1;

		/*
		 * Calculate probability for each topic
		 */
		for (int k = 0; k < newModel.paramStatic.K; k++){
			/*
			 * Item 1
			 */
			double item1_numerator = newModel.paramRuntime.n_e2u[obsEntity][k] + trnModel.paramRuntime.n_e2u[obsEntity_global][k] + newModel.paramStatic.delta;
			double item1_denominator = newModel.paramRuntime.nsum_e2u[k] + trnModel.paramRuntime.nsum_e2u[k] + VOE_delta;
			double item1_val = item1_numerator / item1_denominator;

			/*
			 * Item 2
			 */
			double item2_numerator = newModel.paramStatic.alpha + newModel.paramRuntime.nd_e2u[m][k] + newModel.paramRuntime.nd_w2y[m][k];
			double item2_denominator = newModel.paramStatic.K * newModel.paramStatic.alpha + newModel.paramRuntime.ndsum_e2u[m] + newModel.paramRuntime.ndsum_w2y[m];

			/*
			 * final combination
			 */
			newModel.paramRuntime.p_z[k] = item1_val * item2_numerator / item2_denominator;
			if(newModel.paramRuntime.p_z[k] < 0 || newModel.paramRuntime.p_z[k] > 1.0){
				System.out.println("ERROR");
			}
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
		 * add newly estimated z_t to count variables
		 */
		newModel.paramRuntime.n_e2u[obsEntity][topic] += 1;
		newModel.paramRuntime.nsum_e2u[topic] += 1;
		newModel.paramRuntime.nd_e2u[m][topic] += 1;
		newModel.paramRuntime.ndsum_e2u[m] += 1;

		return topic;
	}
}
