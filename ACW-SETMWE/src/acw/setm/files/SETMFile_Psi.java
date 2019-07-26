package acw.setm.files;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;

import acw.common.utils.file.FileIOUtils;
import acw.setm.model.SETMModel;

/**
 * \psi is the topic observation-entity distribution of the given SETM model.
 * @author wu-chuan
 *
 */
public class SETMFile_Psi {
	
	/**
	 * Compute \psi
	 * @param trnModel
	 */
	public static void computePsi(SETMModel trnModel){
		double deltaVAE = trnModel.paramStatic.VOE * trnModel.paramStatic.delta;
		for (int k = 0; k < trnModel.paramStatic.K; k++){
			for (int e = 0; e < trnModel.paramStatic.VOE; e++){
				double numerator = trnModel.paramRuntime.n_e2u[e][k] + trnModel.paramStatic.delta;
				double denominator = trnModel.paramRuntime.nsum_e2u[k] + deltaVAE;
				trnModel.paramRuntime.psi[k][e] = numerator / denominator;
			}
		}
	}

	public static double[][] readPsi(String fpPsi, int K, int VAE){
		BufferedReader bReader;
		double[][] psi = new double[K][VAE];
		try {
			int kCurrent = 0;
			bReader = FileIOUtils.getBufferedReader(fpPsi);
			String lineStr;
			while((lineStr = bReader.readLine()) != null){
				String[] vals = lineStr.split(" ");
				double[] dblVals = new double[vals.length];
				for (int i = 0; i < dblVals.length; i++) {
					psi[kCurrent][i] = Double.parseDouble(vals[i]);
				}
				kCurrent++;
			}
			bReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return psi;
	}

	/**
	 * Save word-topic distribution
	 */
	public static boolean saveModelPsi(SETMModel sasModel, String fpPsi){
		try {
			FileWriter fwPsi = FileIOUtils.getFileWriter(fpPsi);

			for (int k = 0; k < sasModel.paramStatic.K; k++){
				for (int e = 0; e < sasModel.paramStatic.VOE; e++){
					fwPsi.write(sasModel.paramRuntime.psi[k][e] + " ");
				}
				fwPsi.write(System.lineSeparator());
			}
			fwPsi.close();
		}
		catch (Exception e){
			System.out.println("Error while saving word-topic distribution:" + e.getMessage());
			e.printStackTrace();
			return false;
		}
		return true;
	}
}
