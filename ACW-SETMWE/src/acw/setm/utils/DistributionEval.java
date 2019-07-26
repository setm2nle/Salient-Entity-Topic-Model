package acw.setm.utils;

public class DistributionEval {
	public static boolean isDistribution(double[] vals){
		double sum = 0;
		for (int i = 0; i < vals.length; i++) {
			sum += vals[i];
		}
		if(Math.abs(sum - 1.0d) < 0.0000001){
			return true;
		}else{
			return false;
		}
	}
}
