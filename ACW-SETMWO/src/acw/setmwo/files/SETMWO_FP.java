package acw.setmwo.files;

import java.io.File;

public class SETMWO_FP {
	/*
	 * Graphical Model Parameter Files
	 */
	
	public static String fpTheta(String dpIn, String modelName, String modelVersion, boolean trn){
		return dpIn + File.separator + SETMWO_FN.fnTheta(modelName, modelVersion, trn);
	}
	
	public static String fpPhi(String dpIn, String modelName, String modelVersion, boolean trn){
		return dpIn + File.separator + SETMWO_FN.fnPhi(modelName, modelVersion, trn);
	}
	
	public static String fpVarphi(String dpIn, String modelName, String modelVersion, boolean trn){
		return dpIn + File.separator + SETMWO_FN.fnVarphi(modelName, modelVersion, trn);
	}
	
	public static String fpRho(String dpIn, String modelName, String modelVersion, boolean trn){
		return dpIn + File.separator + SETMWO_FN.fnRho(modelName, modelVersion, trn);
	}
	
	public static String fpTassignW(String dpIn, String modelName, String modelVersion, boolean trn){
		return dpIn + File.separator + SETMWO_FN.fnTassignW(modelName, modelVersion, trn);
	}
	
	public static String fpTassignE(String dpIn, String modelName, String modelVersion, boolean trn){
		return dpIn + File.separator + SETMWO_FN.fnTassignE(modelName, modelVersion, trn);
	}
	
	public static String fpParams(String dpIn, String modelName, String modelVersion, boolean trn){
		return dpIn + File.separator + SETMWO_FN.fnParams(modelName, modelVersion, trn);
	}
	
	/*
	 * Dictionary File Names
	 */
	public static String fpWordMap(String dpIn, String modelName, boolean trn){
		return dpIn + File.separator + SETMWO_FN.fnWordMap(modelName, trn);
	}
	
	public static String fpEntityMap(String dpIn, String modelName, boolean trn){
		return dpIn + File.separator + SETMWO_FN.fnEntityMap(modelName, trn);
	}
}
