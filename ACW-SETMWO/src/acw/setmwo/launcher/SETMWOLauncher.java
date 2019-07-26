package acw.setmwo.launcher;

import acw.setmwo.model.SETMWOEstimator;
import acw.setmwo.model.SETMWOInferencer;
import acw.setmwo.param.SETMWOCmdOption;

public class SETMWOLauncher {
	
	public static void estimate(SETMWOCmdOption option){
		option.est = true;
		SETMWOEstimator estimator = new SETMWOEstimator();
		estimator.init(option);
		estimator.estimate();
	}
	
	public static void inference(SETMWOCmdOption option){
		option.inf = true;
		SETMWOInferencer inferencer = new SETMWOInferencer();
		inferencer.init(option);
		inferencer.inference();
	}
}
