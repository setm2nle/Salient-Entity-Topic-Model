package acw.setm.files;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;

import acw.common.utils.file.FileIOUtils;
import acw.setm.model.SETMModel;

public class SETMFile_Rho {
	
	public static double[][] computeRho(SETMModel trnModel){
		double[][] rho = new double[trnModel.paramStatic.VSE][trnModel.paramStatic.K];
		double sigmaVSE = trnModel.paramStatic.VSE * trnModel.paramStatic.sigma;
		for (int s = 0; s < trnModel.paramStatic.VSE; s++) {
			for (int k = 0; k < trnModel.paramStatic.K; k++) {
				double numerator = trnModel.paramRuntime.n_s2z[s][k] + trnModel.paramStatic.sigma;
				double denominator = trnModel.paramRuntime.nsum_z2s[s] + sigmaVSE;
				rho[s][k] = numerator / denominator;
			}
		}
		return rho;
	}

	public static double[][] readRho(String fpRho, int K, int VSE){
		BufferedReader bReader;
		double[][] rho = new double[VSE][K];
		try {
			int tokenCurrent = 0;
			bReader = FileIOUtils.getBufferedReader(fpRho);
			String lineStr;
			while((lineStr = bReader.readLine()) != null){
				String[] vals = lineStr.split(" ");
				double[] dblVals = new double[vals.length];
				for (int k = 0; k < dblVals.length; k++) {
					rho[tokenCurrent][k] = Double.parseDouble(vals[k]);
				}
				tokenCurrent++;
			}
			bReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return rho;
	}

	/**
	 * Save word-topic distribution
	 */
	public static boolean saveModelRho(double[][] rho, String fpRho){
		try {
			FileWriter fwRho = FileIOUtils.getFileWriter(fpRho);

			for (int e = 0; e < rho.length; e++){
				for (int k = 0; k < rho[0].length; k++){
					fwRho.write(rho[e][k] + " ");
				}
				fwRho.write(System.lineSeparator());
			}
			fwRho.close();
		}
		catch (Exception e){
			System.out.println("Error while saving word-topic distribution:" + e.getMessage());
			e.printStackTrace();
			return false;
		}
		return true;
	}
}
