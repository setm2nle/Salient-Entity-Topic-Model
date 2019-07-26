package acw.setmwo.model;

import acw.common.utils.stdout.StdOut;
import acw.common.utils.time.TimePrintUtils;
import acw.setmwo.files.SETMWOFile_Phi;
import acw.setmwo.files.SETMWOFile_Theta;
import acw.setmwo.files.SETMWOFile_Varphi;
import acw.setmwo.files.SETMWO_FP;
import acw.setmwo.param.SETMWOCmdOption;
import acw.setmwo.utils.Conversion;

public class SETMWOEstimator {

	/*
	 * Output model
	 */
	protected SETMWOModel trnModel;

	/**
	 * input parameters
	 */
	private SETMWOCmdOption option;

	/**
	 * VToken * \beta
	 */
	private double VE_beta;
	private double VW_gamma;
	private double K_alpha;
	private double lambda_weighted_prior;
	private double K_lambda_weighted_prior;

	/**
	 * Initialize the new model
	 * @param option
	 * @return
	 */
	public boolean init(SETMWOCmdOption option){
		this.option = option;
		trnModel = new SETMWOModel();

		if (option.est){
			if (!trnModel.initNewModel(option))
				return false;
			trnModel.data.localDictWE.writeStr2IdMap(SETMWO_FP.fpWordMap(option.dpTrain, trnModel.modelName, true));
			trnModel.data.localDictSE.writeStr2IdMap(SETMWO_FP.fpEntityMap(option.dpTrain, trnModel.modelName, true));
		}

		SETMWOFile_Theta.computeTheta(trnModel);
		SETMWOFile_Phi.computePhi(trnModel);
		SETMWOFile_Varphi.computeVarphi(trnModel);

		return true;
	}

	public void estimate(){
		StdOut.infoStdOut("START of Gibbs Sampling (" + trnModel.paramStatic.niters + " iteration). " + TimePrintUtils.getCurrentTimeStr());

		/* 
		 * some pre-calculated values
		 */
		VE_beta = trnModel.paramStatic.VE * trnModel.paramStatic.beta;
		VW_gamma = trnModel.paramStatic.VW * trnModel.paramStatic.gamma;
		K_alpha = trnModel.paramStatic.K * trnModel.paramStatic.alpha;
		lambda_weighted_prior = trnModel.paramStatic.lambda * trnModel.paramStatic.alpha + (1 - trnModel.paramStatic.lambda) * trnModel.paramStatic.sigma;
		K_lambda_weighted_prior = trnModel.paramStatic.K * lambda_weighted_prior;

		/*
		 * run iterations
		 */
		int lastIter = 0;
		for (trnModel.paramStatic.liter = lastIter + 1; trnModel.paramStatic.liter < trnModel.paramStatic.niters + lastIter; trnModel.paramStatic.liter++){
			if(trnModel.paramStatic.liter % 200 == 0){
				System.out.println("Iteration " + trnModel.paramStatic.liter + " ..." + TimePrintUtils.getCurrentTimeStr());
			}

			// for each document
			for (int m = 0; m < trnModel.paramStatic.M; m++){
				// sample for words
				for (int w = 0; w < trnModel.data.docs[m].words.length; w++){
					// sampling the topic corresponding to the current source entity
					int topic = samplingWord(m, w);
					Integer[] topicArr = trnModel.paramRuntime.y_t.get(m);
					topicArr[w] = topic;
					trnModel.paramRuntime.y_t.set(m, topicArr);
				}

				if(trnModel.data.docs[m].entities != null){
					// sample for entities
					for (int e = 0; e < trnModel.data.docs[m].entities.length; e++){
						// sampling the topic corresponding to the current source entity
						int topic = samplingEntity(m, e);
						Integer[] topicArr = trnModel.paramRuntime.z_t.get(m);
						topicArr[e] = topic;
						trnModel.paramRuntime.z_t.set(m, topicArr);
					}
				}
			}// end for each document

			if (option.savestep > 0){
				if (trnModel.paramStatic.liter % option.savestep == 0){
					System.out.println("Saving the model at iteration " + trnModel.paramStatic.liter + " ...");
					SETMWOFile_Theta.computeTheta(trnModel);
					SETMWOFile_Phi.computePhi(trnModel);
					SETMWOModelIO.saveModel(trnModel, option.dpTrain, Conversion.zeroPad(trnModel.paramStatic.liter, 5), true);
				}
			}
		}// end iterations	

		StdOut.infoStdOut("END of Gibbs sampling! Final model " + trnModel.modelName + " saved. " + TimePrintUtils.getCurrentTimeStr());
		SETMWOFile_Theta.computeTheta(trnModel);
		SETMWOFile_Phi.computePhi(trnModel);
		SETMWOFile_Varphi.computeVarphi(trnModel);
		trnModel.paramStatic.liter--;
		SETMWOModelIO.saveModel(trnModel, option.dpTrain, "final", true);
	}

	/**
	 * Do sampling
	 * @param m document number
	 * @param w word number
	 * @return topic id
	 */
	public int samplingWord(int m, int w){
		/*
		 * Get current word information, word itself, its facet and topic associated to srcEntity 
		 */
		int word = trnModel.data.docs[m].words[w];
		int topic = trnModel.paramRuntime.y_t.get(m)[w];

		/*
		 * Remove y_i from the count variable
		 */
		trnModel.paramRuntime.n_w2y[word][topic] -= 1;
		trnModel.paramRuntime.nsum_w2y[topic] -= 1;
		trnModel.paramRuntime.nd_w2y[m][topic] -= 1;
		trnModel.paramRuntime.ndsum_w2y[m] -= 1;

		/*
		 * Calculate probability for each topic
		 */
		for (int k = 0; k < trnModel.paramStatic.K; k++){
			/*
			 * Item 1
			 */
			double item1_numerator = trnModel.paramRuntime.n_w2y[word][k] + trnModel.paramStatic.gamma;
			double item1_denominator = trnModel.paramRuntime.nsum_w2y[k] + VW_gamma;
			double item1_val = item1_numerator / item1_denominator;

			double item2_numerator = 0;
			double item2_denominator = 0;
			if(trnModel.data.docs[m].entities != null){
				/*
				 * Item 2 numerator
				 */
				item2_numerator = lambda_weighted_prior;
				item2_numerator += trnModel.paramStatic.lambda * trnModel.paramRuntime.nd_w2y[m][k];
				int sum_from_salient_entities = 0;
				for (int i = 0; i < trnModel.data.docs[m].entities.length; i++) {
					int entity = trnModel.data.docs[m].entities[i];
					int countUnderTopic_k = trnModel.paramRuntime.n_e2z[entity][k];
					sum_from_salient_entities += countUnderTopic_k;
				}
				item2_numerator += (1 - trnModel.paramStatic.lambda) * (double) sum_from_salient_entities / trnModel.data.docs[m].entities.length;

				/*
				 * Item 2 denominator
				 */
				item2_denominator = K_lambda_weighted_prior + trnModel.paramStatic.lambda * trnModel.paramRuntime.ndsum_w2y[m];
				sum_from_salient_entities = 0;
				for (int i = 0; i < trnModel.data.docs[m].entities.length; i++) {
					int entity = trnModel.data.docs[m].entities[i];
					int countUnderAllTopic = trnModel.paramRuntime.nsum_z2e[entity];
					sum_from_salient_entities += countUnderAllTopic;
				}
				item2_denominator += (1 - trnModel.paramStatic.lambda) * (double) sum_from_salient_entities / trnModel.data.docs[m].entities.length;
			}else{
				/*
				 * Item 2 numerator
				 */
				item2_numerator = trnModel.paramStatic.alpha + trnModel.paramRuntime.nd_w2y[m][k];

				/*
				 * Item 2 denominator
				 */
				item2_denominator = trnModel.paramStatic.K * trnModel.paramStatic.alpha + trnModel.paramRuntime.ndsum_w2y[m];
			}

			/*
			 * final combination
			 */
			trnModel.paramRuntime.p_z[k] = item1_val * item2_numerator / item2_denominator;
		}

		/*
		 * Scaled sample topic because of unnormalized p_z[]
		 */
		for (int k = 1; k < trnModel.paramStatic.K; k++){
			trnModel.paramRuntime.p_z[k] += trnModel.paramRuntime.p_z[k - 1];
		}

		double u = Math.random() * trnModel.paramRuntime.p_z[trnModel.paramStatic.K - 1];

		for (topic = 0; topic < trnModel.paramStatic.K; topic++){
			if (trnModel.paramRuntime.p_z[topic] > u) //sample topic w.r.t distribution p
				break;
		}

		/*
		 * add newly estimated z_t to count variables
		 */
		trnModel.paramRuntime.n_w2y[word][topic] += 1;
		trnModel.paramRuntime.nsum_w2y[topic] += 1;
		trnModel.paramRuntime.nd_w2y[m][topic] += 1;
		trnModel.paramRuntime.ndsum_w2y[m] += 1;

		return topic;
	}

	public int samplingEntity(int m, int n){
		/*
		 * Get current word information, word itself, its facet and topic associated to srcEntity 
		 */
		int entity = trnModel.data.docs[m].entities[n];
		int topic = trnModel.paramRuntime.z_t.get(m)[n];

		/*
		 * Remove z_i from the count variable
		 */
		trnModel.paramRuntime.n_e2z[entity][topic] -= 1;
		trnModel.paramRuntime.nsum_e2z[topic] -= 1;
		trnModel.paramRuntime.nd_e2z[m][topic] -= 1;
		trnModel.paramRuntime.ndsum_e2z[m] -= 1;

		/*
		 * Calculate probability for each topic
		 */
		for (int k = 0; k < trnModel.paramStatic.K; k++){
			/*
			 * Item 1
			 */
			double item1_numerator = trnModel.paramRuntime.n_e2z[entity][k] + trnModel.paramStatic.beta;
			double item1_denominator = trnModel.paramRuntime.nsum_e2z[k] + VE_beta;
			double item1_val = item1_numerator / item1_denominator;

			/*
			 * Item 2
			 */
			double item2_numerator = trnModel.paramRuntime.nd_e2z[m][k] + trnModel.paramStatic.alpha;
			double item2_denominator = trnModel.paramRuntime.ndsum_e2z[m] + K_alpha;
			double item2_val = item2_numerator / item2_denominator;

			/*
			 * final combination
			 */
			trnModel.paramRuntime.p_z[k] = item1_val * item2_val;
		}

		/*
		 * Scaled sample topic because of unnormalized p_z[]
		 */
		for (int k = 1; k < trnModel.paramStatic.K; k++){
			trnModel.paramRuntime.p_z[k] += trnModel.paramRuntime.p_z[k - 1];
		}

		double u = Math.random() * trnModel.paramRuntime.p_z[trnModel.paramStatic.K - 1];

		for (topic = 0; topic < trnModel.paramStatic.K; topic++){
			if (trnModel.paramRuntime.p_z[topic] > u) //sample topic w.r.t distribution p
				break;
		}

		/*
		 * add newly estimated z_t to count variables
		 */
		trnModel.paramRuntime.n_e2z[entity][topic] += 1;
		trnModel.paramRuntime.nsum_e2z[topic] += 1;
		trnModel.paramRuntime.nd_e2z[m][topic] += 1;
		trnModel.paramRuntime.ndsum_e2z[m] += 1;

		return topic;
	}
}
