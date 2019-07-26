package acw.setm.param;

import acw.setm.dao.SETMDataset;

public class SETMParamStatic {
	public String modelName;	// name of the model
	// model parameters
	public int M; //dataset size (i.e., number of docs)
	public int VW; //vocabulary token size
	public int VSE; //vocabulary salient size
	public int VOE; //vocabulary all entity size
	public int K; // number of topics
	public double alpha, beta, gamma, sigma, delta, lambda; //model hyperparameters

	// 3. Internal Runtime Variables
	public int liter;
	public int niters; //number of Gibbs sampling iteration
	public int savestep; //saving period
	
	public SETMParamStatic(){
		// run information
		modelName = "def-model"; // default model name
		niters = 2000;
		savestep = 1000;
		// dataset parameters
		M = 0;
		VW = 0;
		VSE = 0;
		VOE = 0;
		// hyperparameters
		K = 0;
		alpha = 0.1;
		beta = 0.1;
		gamma = 0.1;
		sigma = 0.1;
		delta = 0.1;
		lambda = 0.5;
	}
	
	public void initUsingCmdOption(SETMCmdOption option){
		modelName = option.modelName;
		// M, VW, VDE, VSE are counts to be calculated in the process of reading dataset
		K = option.K;

		/*
		 * default values of model parameters
		 */
		alpha = option.alpha;
		if (alpha < 0.0){
			alpha = 0.1;
		}
		beta = option.beta;
		if(beta < 0.0){
			beta = 0.1;
		}
		gamma = option.gamma;
		if(gamma < 0.0){
			gamma = 0.1;
		}
		sigma = option.sigma;
		if(sigma < 0.0){
			sigma = 0.1;
		}
		delta = option.delta;
		if(delta < 0.0){
			delta = 0.1;
		}
		lambda = option.lambda;
		if(lambda< 0.0){
			lambda = 0.5;
		}
		
		// run parameters
		liter = 0;
		niters = option.niters;
		savestep = option.savestep;
	}
	
	public void initUsingDataset(SETMDataset data){
		M = data.M;
		VW = data.VW;
		VSE = data.VSE;
		VOE = data.VOE;
	}
}
