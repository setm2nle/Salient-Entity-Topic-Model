package acw.setmwo.files;

public class SETMWO_FN {
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
	
	public static String fnRho(String modelName, String modelVersion, boolean trn){
		return modelName + "-" + getTrnTag(trn) + "-" + modelVersion + ".rho";
	}
	
	public static String fnTassignW(String modelName, String modelVersion, boolean trn){
		return modelName + "-" + getTrnTag(trn) + "-" + modelVersion + ".tassignW";
	}
	
	public static String fnTassignE(String modelName, String modelVersion, boolean trn){
		return modelName + "-" + getTrnTag(trn) + "-" + modelVersion + ".tassignE";
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
	
	public static String fnEntityMap(String modelName, boolean trn){
		return modelName + "-" + getTrnTag(trn) + ".entity-dict";
	}
}
