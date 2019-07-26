package acw.setmwo.files;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;

import acw.common.utils.file.FileIOUtils;
import acw.setmwo.model.SETMWOModel;

/**
 * \rho is the entity topic distribution, which is used to generate topics given salient entities. 
 * @author wu-chuan
 *
 */
public class SETMWOFile_Rho {
	
	public static double[][] computeRho(SETMWOModel trnModel){
		double[][] rho = new double[trnModel.paramStatic.VE][trnModel.paramStatic.K];
		double sigmaVE = trnModel.paramStatic.VE * trnModel.paramStatic.sigma;
		for (int e = 0; e < trnModel.paramStatic.VE; e++) {
			for (int k = 0; k < trnModel.paramStatic.K; k++) {
				double numerator = trnModel.paramRuntime.n_e2z[e][k] + trnModel.paramStatic.sigma;
				double denominator = trnModel.paramRuntime.nsum_z2e[e] + sigmaVE;
				rho[e][k] = numerator / denominator;
			}
		}
		return rho;
	}

	public static double[][] readRho(String fpVarphi, int K, int VE){
		BufferedReader bReader;
		double[][] rho = new double[VE][K];
		try {
			int tokenCurrent = 0;
			bReader = FileIOUtils.getBufferedReader(fpVarphi);
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
