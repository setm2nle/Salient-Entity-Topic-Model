package acw.setm.representation;

import java.io.File;

import acw.common.utils.arr.DoubleArrUtils;
import acw.common.utils.collection.StringIdDualDict;
import acw.setm.files.SETMFile_Params;
import acw.setm.files.SETMFile_Psi;
import acw.setm.files.SETMFile_Rho;
import acw.setm.files.SETM_FN;

public class SETMWERepresentation {

	/**
	 * salient entity map
	 */
	private static StringIdDualDict seMap;

	/**
	 * observed entity map
	 */
	private static StringIdDualDict oeMap;

	/**
	 * rho in SETM
	 */
	private double[][] rho;

	/**
	 * psi in SETM
	 */
	private double[][] psi;
	
	private int K;
	private int VAE;
	private int VSE;

	public SETMWERepresentation(String dpTrain){
		String setmModelName = "default";
		String version = SETM_FN.FINAL;
		boolean trnLabel = true;
		init(dpTrain, setmModelName, version, trnLabel);
	}

	public void init(String dpTrain, String setmModelName, String version, boolean trnLabel) {
		// file paths related to SETM model
		String fpSETMParams = dpTrain + File.separator + SETM_FN.fnParams(setmModelName, version, trnLabel);
		SETMFile_Params setmFile_Params = new SETMFile_Params();
		setmFile_Params.readParams(fpSETMParams);
		K = setmFile_Params.K;
		VAE = setmFile_Params.VAE;
		VSE = setmFile_Params.VSE;

		String fpModelPsi = dpTrain + File.separator + SETM_FN.fnPsi(setmModelName, version, trnLabel);
		String fpSETMObsEntityMap = dpTrain + File.separator + SETM_FN.fnObservedEntityMap(setmModelName, trnLabel);
		oeMap = new StringIdDualDict();
		oeMap.readStr2IdMap(fpSETMObsEntityMap);
		psi = SETMFile_Psi.readPsi(fpModelPsi, K, VAE);

		String fpModelRho = dpTrain + File.separator + SETM_FN.fnRho(setmModelName, version, trnLabel);
		String fpSETMSalEntityMap = dpTrain + File.separator + SETM_FN.fnSalientEntityMap(setmModelName, trnLabel);
		seMap = new StringIdDualDict();
		seMap.readStr2IdMap(fpSETMSalEntityMap);
		rho = SETMFile_Rho.readRho(fpModelRho, K, VSE);
	}

	public double[] getSERepresentation(String entityMid){
		// obtain entity index
		int entityId = seMap.getID(entityMid);
		if(entityId < VSE && entityId >= 0){
			// obtain entity topic distribution
			double[] entityTopicDist = new double[K];
			for (int k = 0; k < entityTopicDist.length; k++) {
				entityTopicDist[k] = rho[entityId][k];
			}
			return DoubleArrUtils.normalizeEqualOne(entityTopicDist);
		}
		return null;
	}

	public double[] getOERepresentation(String entityMid){
		// obtain entity index
		int entityId = oeMap.getID(entityMid);
		if(entityId < VAE && entityId >= 0){
			// obtain entity topic distribution
			double[] entityTopicDist = new double[K];
			for (int k = 0; k < entityTopicDist.length; k++) {
				entityTopicDist[k] = psi[k][entityId];
			}
			return DoubleArrUtils.normalizeEqualOne(entityTopicDist);
		}
		return null;
	}
	
	public int getDimension(){
		return K;
	}
}
