package acw.setm.launcher;

import acw.setm.model.SETMEstimator;
import acw.setm.model.SETMInferencer;
import acw.setm.param.SETMCmdOption;

public class SETMLauncher {
	
	public static void estimate(SETMCmdOption option){
		option.est = true;
		SETMEstimator estimator = new SETMEstimator();
		estimator.init(option);
		estimator.estimate();
	}
	
	public static void inference(SETMCmdOption option){
		option.inf = true;
		SETMInferencer inferencer = new SETMInferencer();
		inferencer.init(option);
		inferencer.inference();
	}
}
