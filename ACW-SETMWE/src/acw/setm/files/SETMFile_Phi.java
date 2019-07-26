package acw.setm.files;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;

import acw.common.utils.file.FileIOUtils;
import acw.setm.model.SETMModel;

/**
 * \phi is the topic salient-entity distribution of the given SETM model
 * @author wu-chuan
 *
 */
public class SETMFile_Phi {
	
	/**
	 * Compute \phi.
	 * @param trnModel
	 */
	public static void computePhi(SETMModel trnModel){
		double betaVE = trnModel.paramStatic.VSE * trnModel.paramStatic.beta;
		for (int k = 0; k < trnModel.paramStatic.K; k++){
			for (int s = 0; s < trnModel.paramStatic.VSE; s++){
				double numerator = trnModel.paramRuntime.n_s2z[s][k] + trnModel.paramStatic.beta;
				double denominator = trnModel.paramRuntime.nsum_s2z[k] + betaVE;
				trnModel.paramRuntime.phi[k][s] = numerator / denominator;
			}
		}
	}

	public static double[][] readPhi(String fpPhi, int K, int VE){
		BufferedReader bReader;
		double[][] phi = new double[K][VE];
		try {
			int kCurrent = 0;
			bReader = FileIOUtils.getBufferedReader(fpPhi);
			String lineStr;
			while((lineStr = bReader.readLine()) != null){
				String[] vals = lineStr.split(" ");
				double[] dblVals = new double[vals.length];
				for (int i = 0; i < dblVals.length; i++) {
					phi[kCurrent][i] = Double.parseDouble(vals[i]);
				}
				kCurrent++;
			}
			bReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return phi;
	}

	/**
	 * Save word-topic distribution
	 */
	public static boolean saveModelPhi(SETMModel sasModel, String fpPhi){
		try {
			FileWriter fwPhi = FileIOUtils.getFileWriter(fpPhi);

			for (int k = 0; k < sasModel.paramStatic.K; k++){
				for (int e = 0; e < sasModel.paramStatic.VSE; e++){
					fwPhi.write(sasModel.paramRuntime.phi[k][e] + " ");
				}
				fwPhi.write(System.lineSeparator());
			}
			fwPhi.close();
		}
		catch (Exception e){
			System.out.println("Error while saving word-topic distribution:" + e.getMessage());
			e.printStackTrace();
			return false;
		}
		return true;
	}
}
