package acw.setm.files;

public class SETM_FN {
	public static final String FINAL = "final";
	
	/*
	 * Graphical Model Parameter Files
	 */
	public static String getTrnTag(boolean trn){
		if(trn){
			return "train";
		}else{
			return "test";
		}
	}
	
	public static String fnTheta(String modelName, String modelVersion, boolean trn){
		return modelName + "-" + getTrnTag(trn) + "-" + modelVersion + ".theta";
	}
	
	public static String fnPhi(String modelName, String modelVersion, boolean trn){
		return modelName + "-" + getTrnTag(trn) + "-" + modelVersion + ".phi";
	}
	
	public static String fnVarphi(String modelName, String modelVersion, boolean trn){
		return modelName + "-" + getTrnTag(trn) + "-" + modelVersion + ".varphi";
	}
	
	public static String fnPsi(String modelName, String modelVersion, boolean trn){
		return modelName + "-" + getTrnTag(trn) + "-" + modelVersion + ".psi";
	}
	
	public static String fnRho(String modelName, String modelVersion, boolean trn){
		return modelName + "-" + getTrnTag(trn) + "-" + modelVersion + ".rho";
	}
	
	public static String fnTassignW(String modelName, String modelVersion, boolean trn){
		return modelName + "-" + getTrnTag(trn) + "-" + modelVersion + ".tassignW";
	}
	
	public static String fnTassignSE(String modelName, String modelVersion, boolean trn){
		return modelName + "-" + getTrnTag(trn) + "-" + modelVersion + ".tassignSE";
	}
	
	public static String fnTassignOE(String modelName, String modelVersion, boolean trn){
		return modelName + "-" + getTrnTag(trn) + "-" + modelVersion + ".tassignOE";
	}
	
	public static String fnParams(String modelName, String modelVersion, boolean trn){
		return modelName + "-" + getTrnTag(trn) + "-" + modelVersion + ".params";
	}
	
	/*
	 * Dictionary File Names
	 */
	
	public static String fnWordMap(String modelName, boolean trn){
		return modelName + "-" + getTrnTag(trn) + ".word-dict";
	}
	
	public static String fnSalientEntityMap(String modelName, boolean trn){
		return modelName + "-" + getTrnTag(trn) + ".sal-entity-dict";
	}
	
	public static String fnObservedEntityMap(String modelName, boolean trn){
		return modelName + "-" + getTrnTag(trn) + ".obs-entity-dict";
	}
}
