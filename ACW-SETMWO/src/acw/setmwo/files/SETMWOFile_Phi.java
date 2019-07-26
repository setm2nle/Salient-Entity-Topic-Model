package acw.setmwo.files;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;

import acw.common.utils.file.FileIOUtils;
import acw.setmwo.model.SETMWOModel;

/**
 * \phi is the topic salient-entity distribution of the given SETMWO model.
 * @author wu-chuan
 *
 */
public class SETMWOFile_Phi {
	
	/**
	 * Compute \phi 
	 * @param trnModel
	 */
	public static void computePhi(SETMWOModel trnModel){
		double betaVE = trnModel.paramStatic.VE * trnModel.paramStatic.beta;
		for (int k = 0; k < trnModel.paramStatic.K; k++){
			for (int e = 0; e < trnModel.paramStatic.VE; e++){
				double numerator = trnModel.paramRuntime.n_e2z[e][k] + trnModel.paramStatic.beta;
				double denominator = trnModel.paramRuntime.nsum_e2z[k] + betaVE;
				trnModel.paramRuntime.phi[k][e] = numerator / denominator;
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
	 * Save topic entity distribution
	 */
	public static boolean saveModelPhi(SETMWOModel setmwoModel, String fpPhi){
		try {
			FileWriter fwPhi = FileIOUtils.getFileWriter(fpPhi);

			for (int k = 0; k < setmwoModel.paramStatic.K; k++){
				for (int e = 0; e < setmwoModel.paramStatic.VE; e++){
					fwPhi.write(setmwoModel.paramRuntime.phi[k][e] + " ");
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
