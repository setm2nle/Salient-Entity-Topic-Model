package acw.setm.model;
/*
 * Copyright (C) 2007 by
 * 
 * 	Xuan-Hieu Phan
 *	hieuxuan@ecei.tohoku.ac.jp or pxhieu@gmail.com
 * 	Graduate School of Information Sciences
 * 	Tohoku University
 * 
 *  Cam-Tu Nguyen
 *  ncamtu@gmail.com
 *  College of Technology
 *  Vietnam National University, Hanoi
 *
 * JGibbsLDA is a free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * by the Free Software Foundation; either version 2 of the License,
 * or (at your option) any later version.
 *
 * JGibbsLDA is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with JGibbsLDA; if not, write to the Free Software Foundation,
 * Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 */

import acw.common.utils.stdout.StdOut;
import acw.common.utils.time.TimePrintUtils;
import acw.setm.files.SETMFile_Phi;
import acw.setm.files.SETMFile_Psi;
import acw.setm.files.SETMFile_Rho;
import acw.setm.files.SETMFile_Theta;
import acw.setm.files.SETMFile_Varphi;
import acw.setm.files.SETM_FP;
import acw.setm.param.SETMCmdOption;
import acw.setm.utils.Conversion;

public class SETMEstimator {

	/*
	 * Output model
	 */
	protected SETMModel trnModel;

	/**
	 * input parameters
	 */
	private SETMCmdOption option;

	/**
	 * VToken * \beta
	 */
	private double VSE_beta;
	private double VOE_delta;
	private double VW_gamma;
	private double K_alpha;
	private double lambda_weighted_prior;
	private double K_lambda_weighted_prior;

	/**
	 * Initialize the new model
	 * @param option
	 * @return
	 */
	public boolean init(SETMCmdOption option){
		this.option = option;
		trnModel = new SETMModel();

		if (option.est){
			if (!trnModel.initNewModel(option))
				return false;
			trnModel.data.localDictW.writeStr2IdMap(SETM_FP.fpWordMap(option.dpTrain, trnModel.modelName, true));
			trnModel.data.localDictSE.writeStr2IdMap(SETM_FP.fpSalientEntityMap(option.dpTrain, trnModel.modelName, true));
			trnModel.data.localDictOE.writeStr2IdMap(SETM_FP.fpObservedEntityMap(option.dpTrain, trnModel.modelName, true));
		}

		SETMFile_Theta.computeTheta(trnModel);
		SETMFile_Rho.computeRho(trnModel);
		SETMFile_Phi.computePhi(trnModel);
		SETMFile_Varphi.computeVarphi(trnModel);
		SETMFile_Psi.computePsi(trnModel);

		return true;
	}

	public void estimate(){
		StdOut.infoStdOut("START of Gibbs Sampling (" + trnModel.paramStatic.niters + " iteration). " + TimePrintUtils.getCurrentTimeStr());

		/* 
		 * some pre-calculated values
		 */
		VSE_beta = trnModel.paramStatic.VSE * trnModel.paramStatic.beta;
		VOE_delta = trnModel.paramStatic.VOE * trnModel.paramStatic.delta;
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
				
				// sample for salient entities
				for (int s = 0; s < trnModel.data.docs[m].seCount; s++){
					// sampling the topic corresponding to the current source entity
					int topic = samplingSalEntity(m, s);
					Integer[] topicArr = trnModel.paramRuntime.z_t.get(m);
					topicArr[s] = topic;
					trnModel.paramRuntime.z_t.set(m, topicArr);
				}
				
				// sample for observed entities
				for (int e = 0; e < trnModel.data.docs[m].oeCount; e++){
					// sampling the topic corresponding to the current source entity
					int topic = samplingObsEntity(m, e);
					Integer[] topicArr = trnModel.paramRuntime.u_t.get(m);
					topicArr[e] = topic;
					trnModel.paramRuntime.u_t.set(m, topicArr);
				}
			}// end for each document

			if (option.savestep > 0){
				if (trnModel.paramStatic.liter % option.savestep == 0){
					System.out.println("Saving the model at iteration " + trnModel.paramStatic.liter + " ...");
					SETMFile_Theta.computeTheta(trnModel);
					SETMFile_Phi.computePhi(trnModel);
					SETMModelIO.saveModel(trnModel, option.dpTrain, Conversion.ZeroPad(trnModel.paramStatic.liter, 5), true);
				}
			}
		}// end iterations	

		StdOut.infoStdOut("END of Gibbs sampling! Final model " + trnModel.modelName + " saved. " + TimePrintUtils.getCurrentTimeStr());
		SETMFile_Theta.computeTheta(trnModel);
		SETMFile_Phi.computePhi(trnModel);
		SETMFile_Varphi.computeVarphi(trnModel);
		SETMFile_Psi.computePsi(trnModel);
		SETMFile_Rho.computeRho(trnModel);
		trnModel.paramStatic.liter--;
		SETMModelIO.saveModel(trnModel, option.dpTrain, "final", true);
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
			if(trnModel.data.docs[m].seCount > 0){
				/*
				 * Item 2 numerator and denominator
				 */
				item2_numerator = lambda_weighted_prior + trnModel.paramStatic.lambda * trnModel.paramRuntime.nd_w2y[m][k];
				item2_denominator = K_lambda_weighted_prior + trnModel.paramStatic.lambda * trnModel.paramRuntime.ndsum_w2y[m];
				int sum_from_salient_entities_numerator = 0;
				int sum_from_salient_entities_denominator = 0;
				for (int i = 0; i < trnModel.data.docs[m].seCount; i++) {
					int salEntity = trnModel.data.docs[m].salEntities[i];
					//
					int countUnderTopic_k = trnModel.paramRuntime.n_s2z[salEntity][k];
					sum_from_salient_entities_numerator += countUnderTopic_k;
					//
					int countUnderAllTopic = trnModel.paramRuntime.nsum_z2s[salEntity];
					sum_from_salient_entities_denominator += countUnderAllTopic;
				}
				item2_numerator += (1 - trnModel.paramStatic.lambda) * (double) sum_from_salient_entities_numerator / trnModel.data.docs[m].seCount;
				item2_denominator += (1 - trnModel.paramStatic.lambda) * (double) sum_from_salient_entities_denominator / trnModel.data.docs[m].seCount;
			}else{
				/*
				 * Item 2 numerator and denominator
				 */
				item2_numerator = trnModel.paramStatic.alpha + trnModel.paramRuntime.nd_w2y[m][k];
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
	
	public int samplingSalEntity(int m, int n){
		/*
		 * Get current word information, word itself, its facet and topic associated to srcEntity 
		 */
		int salEntity = trnModel.data.docs[m].salEntities[n];
		int topic = trnModel.paramRuntime.z_t.get(m)[n];

		/*
		 * Remove z_i from the count variable
		 */
		trnModel.paramRuntime.n_s2z[salEntity][topic] -= 1;
		trnModel.paramRuntime.nsum_s2z[topic] -= 1;
		trnModel.paramRuntime.nd_s2z[m][topic] -= 1;
		trnModel.paramRuntime.ndsum_s2z[m] -= 1;

		/*
		 * Calculate probability for each topic
		 */
		for (int k = 0; k < trnModel.paramStatic.K; k++){
			/*
			 * Item 1
			 */
			double item1_numerator = trnModel.paramRuntime.n_s2z[salEntity][k] + trnModel.paramStatic.beta;
			double item1_denominator = trnModel.paramRuntime.nsum_s2z[k] + VSE_beta;
			double item1_val = item1_numerator / item1_denominator;
			
			/*
			 * Item 2
			 */
			double item2_numerator = trnModel.paramRuntime.nd_s2z[m][k] + trnModel.paramStatic.alpha;
			double item2_denominator = trnModel.paramRuntime.ndsum_s2z[m] + K_alpha;
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
		trnModel.paramRuntime.n_s2z[salEntity][topic] += 1;
		trnModel.paramRuntime.nsum_s2z[topic] += 1;
		trnModel.paramRuntime.nd_s2z[m][topic] += 1;
		trnModel.paramRuntime.ndsum_s2z[m] += 1;

		return topic;
	}
	
	public int samplingObsEntity(int m, int e){
		int obsEntity = trnModel.data.docs[m].obsEntities[e];
		int topic = trnModel.paramRuntime.u_t.get(m)[e];

		/*
		 * Remove y_i from the count variable
		 */
		trnModel.paramRuntime.n_e2u[obsEntity][topic] -= 1;
		trnModel.paramRuntime.nsum_e2u[topic] -= 1;
		trnModel.paramRuntime.nd_e2u[m][topic] -= 1;
		trnModel.paramRuntime.ndsum_e2u[m] -= 1;

		/*
		 * Calculate probability for each topic
		 */
		for (int k = 0; k < trnModel.paramStatic.K; k++){
			/*
			 * Item 1
			 */
			double item1_numerator = trnModel.paramRuntime.n_e2u[obsEntity][k] + trnModel.paramStatic.delta;
			double item1_denominator = trnModel.paramRuntime.nsum_e2u[k] + VOE_delta;
			double item1_val = item1_numerator / item1_denominator;

			double item2_numerator = 0;
			double item2_denominator = 0;
			if(trnModel.data.docs[m].seCount > 0){
				/*
				 * Item 2 numerator
				 */
				item2_numerator = lambda_weighted_prior + trnModel.paramStatic.lambda * trnModel.paramRuntime.nd_e2u[m][k];
				item2_denominator = K_lambda_weighted_prior + trnModel.paramStatic.lambda * trnModel.paramRuntime.ndsum_e2u[m];
				int sum_from_salient_entities_numerator = 0;
				int sum_from_salient_entities_denominator = 0;
				for (int i = 0; i < trnModel.data.docs[m].seCount; i++) {
					int salEntity = trnModel.data.docs[m].salEntities[i];
					//
					int countUnderTopic_k = trnModel.paramRuntime.n_s2z[salEntity][k];
					sum_from_salient_entities_numerator += countUnderTopic_k;
					//
					int countUnderAllTopic = trnModel.paramRuntime.nsum_z2s[salEntity];
					sum_from_salient_entities_denominator += countUnderAllTopic;
				}
				item2_numerator += (1 - trnModel.paramStatic.lambda) * (double) sum_from_salient_entities_numerator / trnModel.data.docs[m].seCount;
				item2_denominator += (1 - trnModel.paramStatic.lambda) * (double) sum_from_salient_entities_denominator / trnModel.data.docs[m].seCount;
			}else{
				/*
				 * Item 2 numerator
				 */
				item2_numerator = trnModel.paramStatic.alpha + trnModel.paramRuntime.nd_e2u[m][k];
				
				/*
				 * Item 2 denominator
				 */
				item2_denominator = trnModel.paramStatic.K * trnModel.paramStatic.alpha + trnModel.paramRuntime.ndsum_e2u[m];
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
		trnModel.paramRuntime.n_e2u[obsEntity][topic] += 1;
		trnModel.paramRuntime.nsum_e2u[topic] += 1;
		trnModel.paramRuntime.nd_e2u[m][topic] += 1;
		trnModel.paramRuntime.ndsum_e2u[m] += 1;

		return topic;
	}
}
