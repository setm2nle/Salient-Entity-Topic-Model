package acw.setmwo.model;

import acw.common.utils.collection.StringIdDualDict;
import acw.setmwo.files.SETMWOFile_Params;
import acw.setmwo.files.SETMWOFile_Phi;
import acw.setmwo.files.SETMWOFile_Rho;
import acw.setmwo.files.SETMWOFile_Tassign;
import acw.setmwo.files.SETMWOFile_Theta;
import acw.setmwo.files.SETMWOFile_Varphi;
import acw.setmwo.files.SETMWO_FN;
import acw.setmwo.files.SETMWO_FP;

public class SETMWOModelIO {

	public static boolean loadModel(SETMWOModel setmwoModel, String dpTrain){
		String modelName = setmwoModel.modelName;
		String trnModelTag = SETMWO_FN.FINAL;
		
		/*
		 * 1. Read parameters
		 */
		if (!SETMWOFile_Params.readParams(setmwoModel, SETMWO_FP.fpParams(dpTrain, modelName, trnModelTag, true)))
			return false;

		/*
		 * 2. Read topic assignments 
		 */
		if (!SETMWOFile_Tassign.readTassign(setmwoModel, SETMWO_FP.fpTassignW(dpTrain, modelName, trnModelTag, true), SETMWO_FP.fpTassignE(dpTrain, modelName, trnModelTag, true)))
			return false;
		
		/*
		 * 3. read word dictionary and salient entity dictionary
		 */
		StringIdDualDict setmwoDictW = new StringIdDualDict();
		StringIdDualDict setmwoDictE = new StringIdDualDict();
		setmwoDictW.readStr2IdMap(SETMWO_FP.fpWordMap(dpTrain, modelName, true));
		setmwoDictE.readStr2IdMap(SETMWO_FP.fpEntityMap(dpTrain, modelName, true));
		setmwoModel.data.localDictWE = setmwoDictW;
		setmwoModel.data.localDictSE = setmwoDictE;

		return true;
	}

	/**
	 * Save model
	 * @param setmwoModel
	 * @param modelVersionTag
	 * @param trn
	 * @return
	 */
	public static boolean saveModel(SETMWOModel setmwoModel, String dpOut, String modelVersionTag, boolean trn){
		String modelName = setmwoModel.modelName;

		// save theta (source entity specific facet distribution)
		if (!SETMWOFile_Theta.saveModelTheta(setmwoModel, SETMWO_FP.fpTheta(dpOut, modelName, modelVersionTag, trn))){
			return false;
		}

		// save phi (topic entity distribution)
		if (!SETMWOFile_Phi.saveModelPhi(setmwoModel, SETMWO_FP.fpPhi(dpOut, modelName, modelVersionTag, trn))){
			return false;
		}
		
		// save varphi (topic word distribution)
		if (!SETMWOFile_Varphi.saveModelVarphi(setmwoModel, SETMWO_FP.fpVarphi(dpOut, modelName, modelVersionTag, trn))){
			return false;
		}
		
		// save varphi (topic word distribution)
		double[][] rho = SETMWOFile_Rho.computeRho(setmwoModel);
		if (!SETMWOFile_Rho.saveModelRho(rho, SETMWO_FP.fpRho(dpOut, modelName, modelVersionTag, trn))){
			return false;
		}

		// save tassign (topic assignment of all train documents)
		if (!SETMWOFile_Tassign.saveModelTAssign(setmwoModel, SETMWO_FP.fpTassignW(dpOut, modelName, modelVersionTag, trn), SETMWO_FP.fpTassignE(dpOut, modelName, modelVersionTag, trn))){
			return false;
		}

		// save parameter static
		if (!SETMWOFile_Params.saveModelOthers(setmwoModel, SETMWO_FP.fpParams(dpOut, modelName, modelVersionTag, trn))){			
			return false;
		}

		return true;
	}
}
