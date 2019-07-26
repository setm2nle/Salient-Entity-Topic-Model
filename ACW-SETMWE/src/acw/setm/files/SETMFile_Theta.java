package acw.setm.files;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;

import acw.common.utils.file.FileIOUtils;
import acw.setm.model.SETMModel;

public class SETMFile_Theta {
	public static final String elementSeperator = " ";
	
	/**
	 * Compute theta distribution (source entity specific facet distribution)
	 */
//	public static void computeTheta(SETMModel trnModel){
//		double K_alpha = trnModel.paramStatic.K * trnModel.paramStatic.alpha;
//		for (int m = 0; m < trnModel.paramStatic.M; m++){
//			for (int k = 0; k < trnModel.paramStatic.K; k++){
//				double numerator = trnModel.paramRuntime.nd_s2z[m][k] + trnModel.paramStatic.alpha;
//				numerator += trnModel.paramRuntime.nd_w2y[m][k] + trnModel.paramStatic.alpha;
//				double denominator = trnModel.paramRuntime.ndsum_s2z[m] + K_alpha;
//				denominator += trnModel.paramRuntime.ndsum_w2y[m] + K_alpha;
//				trnModel.paramRuntime.theta[m][k] = numerator / denominator;
//			}
//		}
//	}
	
	public static void computeTheta(SETMModel trnModel){
		double K_alpha = trnModel.paramStatic.K * trnModel.paramStatic.alpha;
		for (int m = 0; m < trnModel.paramStatic.M; m++){
			for (int k = 0; k < trnModel.paramStatic.K; k++){
				double numerator = trnModel.paramRuntime.nd_w2y[m][k] + trnModel.paramStatic.alpha;
				double denominator = trnModel.paramRuntime.ndsum_w2y[m] + K_alpha;
				trnModel.paramRuntime.theta[m][k] = numerator / denominator;
			}
		}
	}
	
	public static double[][] readTheta(String fpTheta, int M, int K){
		BufferedReader bReader;
		double[][] theta = new double[M][K];
		try {
			int mCurrent = 0;
			bReader = FileIOUtils.getBufferedReader(fpTheta);
			String lineStr;
			while((lineStr = bReader.readLine()) != null){
				String[] vals = lineStr.split(elementSeperator);
				double[] dblVals = new double[vals.length];
				for (int i = 0; i < dblVals.length; i++) {
					theta[mCurrent][i] = Double.parseDouble(vals[i]);
				}
				mCurrent++;
			}
			bReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return theta;
	}
	
	/**
	 * Save theta (topic distribution) for this model
	 */
	public static boolean saveModelTheta(SETMModel sasModel, String fpTheta){
		try{
			FileWriter fwTheta = FileIOUtils.getFileWriter(fpTheta);
			
			for (int m = 0; m < sasModel.paramStatic.M; m++){
				for (int j = 0; j < sasModel.paramStatic.K; j++){
					fwTheta.write(sasModel.paramRuntime.theta[m][j] + elementSeperator);
				}
				fwTheta.write(System.lineSeparator());
			}
			fwTheta.close();
		}
		catch (Exception e){
			System.out.println("Error while saving topic distribution file for this model: " + e.getMessage());
			e.printStackTrace();
			return false;
		}
		return true;
	}
}
