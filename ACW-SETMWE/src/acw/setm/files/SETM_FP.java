package acw.setm.files;

import java.io.File;

public class SETM_FP {
	/*
	 * Graphical Model Parameter Files
	 */
	
	public static String fpTheta(String dpIn, String modelName, String modelVersion, boolean trn){
		return dpIn + File.separator + SETM_FN.fnTheta(modelName, modelVersion, trn);
	}
	
	public static String fpPhi(String dpIn, String modelName, String modelVersion, boolean trn){
		return dpIn + File.separator + SETM_FN.fnPhi(modelName, modelVersion, trn);
	}
	
	public static String fpVarphi(String dpIn, String modelName, String modelVersion, boolean trn){
		return dpIn + File.separator + SETM_FN.fnVarphi(modelName, modelVersion, trn);
	}
	
	public static String fpPsi(String dpIn, String modelName, String modelVersion, boolean trn){
		return dpIn + File.separator + SETM_FN.fnPsi(modelName, modelVersion, trn);
	}
	
	public static String fpRho(String dpIn, String modelName, String modelVersion, boolean trn){
		return dpIn + File.separator + SETM_FN.fnRho(modelName, modelVersion, trn);
	}
	
	public static String fpTassignW(String dpIn, String modelName, String modelVersion, boolean trn){
		return dpIn + File.separator + SETM_FN.fnTassignW(modelName, modelVersion, trn);
	}
	
	public static String fpTassignSE(String dpIn, String modelName, String modelVersion, boolean trn){
		return dpIn + File.separator + SETM_FN.fnTassignSE(modelName, modelVersion, trn);
	}
	
	public static String fpTassignOE(String dpIn, String modelName, String modelVersion, boolean trn){
		return dpIn + File.separator + SETM_FN.fnTassignOE(modelName, modelVersion, trn);
	}
	
	public static String fpParams(String dpIn, String modelName, String modelVersion, boolean trn){
		return dpIn + File.separator + SETM_FN.fnParams(modelName, modelVersion, trn);
	}
	
	/*
	 * Dictionary File Names
	 */
	public static String fpWordMap(String dpIn, String modelName, boolean trn){
		return dpIn + File.separator + SETM_FN.fnWordMap(modelName, trn);
	}
	
	public static String fpSalientEntityMap(String dpIn, String modelName, boolean trn){
		return dpIn + File.separator + SETM_FN.fnSalientEntityMap(modelName, trn);
	}
	
	public static String fpObservedEntityMap(String dpIn, String modelName, boolean trn){
		return dpIn + File.separator + SETM_FN.fnObservedEntityMap(modelName, trn);
	}
}
