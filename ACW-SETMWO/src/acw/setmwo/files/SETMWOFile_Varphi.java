package acw.setmwo.files;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;

import acw.common.utils.file.FileIOUtils;
import acw.setmwo.model.SETMWOModel;

public class SETMWOFile_Varphi {
	
	/**
	 * Compute \varphi (topic token distribution) of the given SETMWO model. 
	 * @param trnModel
	 */
	public static void computeVarphi(SETMWOModel trnModel){
		double gammaVW = trnModel.paramStatic.VW * trnModel.paramStatic.gamma;
		for (int k = 0; k < trnModel.paramStatic.K; k++){
			for (int w = 0; w < trnModel.paramStatic.VW; w++){
				double numerator = trnModel.paramRuntime.n_w2y[w][k] + trnModel.paramStatic.gamma;
				double denominator = trnModel.paramRuntime.nsum_w2y[k] + gammaVW;
				trnModel.paramRuntime.varphi[k][w] = numerator / denominator;
			}
		}
	}

	public static double[][] readVarphi(String fpVarphi, int K, int VW){
		BufferedReader bReader;
		double[][] varphi = new double[K][VW];
		try {
			int kCurrent = 0;
			bReader = FileIOUtils.getBufferedReader(fpVarphi);
			String lineStr;
			while((lineStr = bReader.readLine()) != null){
				String[] vals = lineStr.split(" ");
				double[] dblVals = new double[vals.length];
				for (int i = 0; i < dblVals.length; i++) {
					varphi[kCurrent][i] = Double.parseDouble(vals[i]);
				}
				kCurrent++;
			}
			bReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return varphi;
	}

	/**
	 * Save word-topic distribution
	 */
	public static boolean saveModelVarphi(SETMWOModel setmwoModel, String fpVarphi){
		try {
			FileWriter fwVarphi = FileIOUtils.getFileWriter(fpVarphi);

			for (int k = 0; k < setmwoModel.paramStatic.K; k++){
				for (int w = 0; w < setmwoModel.paramStatic.VW; w++){
					fwVarphi.write(setmwoModel.paramRuntime.varphi[k][w] + " ");
				}
				fwVarphi.write(System.lineSeparator());
			}
			fwVarphi.close();
		}
		catch (Exception e){
			System.out.println("Error while saving word-topic distribution:" + e.getMessage());
			e.printStackTrace();
			return false;
		}
		return true;
	}
}
