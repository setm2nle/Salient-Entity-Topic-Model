package acw.setm.model;

import acw.common.utils.collection.StringIdDualDict;
import acw.setm.files.SETMFile_Params;
import acw.setm.files.SETMFile_Phi;
import acw.setm.files.SETMFile_Psi;
import acw.setm.files.SETMFile_Rho;
import acw.setm.files.SETMFile_Tassign;
import acw.setm.files.SETMFile_Theta;
import acw.setm.files.SETMFile_Varphi;
import acw.setm.files.SETM_FN;
import acw.setm.files.SETM_FP;

public class SETMModelIO {

	public static boolean loadModel(SETMModel sasModel, String dpTrain){
		String modelName = sasModel.modelName;
		String trnModelTag = SETM_FN.FINAL;

		/*
		 *  read others file to obtain static parameters
		 */
		if (!SETMFile_Params.readParams(sasModel, SETM_FP.fpParams(dpTrain, modelName, trnModelTag, true)))
			return false;

		if (!SETMFile_Tassign.readTassign(sasModel, SETM_FP.fpTassignW(dpTrain, modelName, trnModelTag, true), SETM_FP.fpTassignSE(dpTrain, modelName, trnModelTag, true), SETM_FP.fpTassignOE(dpTrain, modelName, trnModelTag, true)))
			return false;

		/*
		 * read word, document entity, source entity dictionaries
		 */
		StringIdDualDict setmDictW = new StringIdDualDict();
		StringIdDualDict setmDictSE = new StringIdDualDict();
		StringIdDualDict setmDictOE = new StringIdDualDict();
		setmDictW.readStr2IdMap(SETM_FP.fpWordMap(dpTrain, modelName, true));
		setmDictSE.readStr2IdMap(SETM_FP.fpSalientEntityMap(dpTrain, modelName, true));
		setmDictOE.readStr2IdMap(SETM_FP.fpObservedEntityMap(dpTrain, modelName, true));
		sasModel.data.localDictW = setmDictW;
		sasModel.data.localDictSE = setmDictSE;
		sasModel.data.localDictOE = setmDictOE;

		return true;
	}

	/**
	 * Save model
	 * @param sasModel
	 * @param modelVersionTag
	 * @param trn
	 * @return
	 */
	public static boolean saveModel(SETMModel sasModel, String dpOut, String modelVersionTag, boolean trn){
		String modelName = sasModel.modelName;

		// save theta (source entity specific facet distribution)
		if (!SETMFile_Theta.saveModelTheta(sasModel, SETM_FP.fpTheta(dpOut, modelName, modelVersionTag, trn))){
			return false;
		}

		// save phi (topic entity distribution)
		if (!SETMFile_Phi.saveModelPhi(sasModel, SETM_FP.fpPhi(dpOut, modelName, modelVersionTag, trn))){
			return false;
		}
		
		// save varphi (topic word distribution)
		if (!SETMFile_Varphi.saveModelVarphi(sasModel, SETM_FP.fpVarphi(dpOut, modelName, modelVersionTag, trn))){
			return false;
		}
		
		// save psi (topic observed entity distribution)
		if (!SETMFile_Psi.saveModelPsi(sasModel, SETM_FP.fpPsi(dpOut, modelName, modelVersionTag, trn))){
			return false;
		}
		
		// save varphi (topic word distribution)
		double[][] rho = SETMFile_Rho.computeRho(sasModel);
		if (!SETMFile_Rho.saveModelRho(rho, SETM_FP.fpRho(dpOut, modelName, modelVersionTag, trn))){
			return false;
		}

		// save tassign (topic assignment of all train documents)
		if (!SETMFile_Tassign.saveModelTAssign(sasModel, SETM_FP.fpTassignW(dpOut, modelName, modelVersionTag, trn), SETM_FP.fpTassignSE(dpOut, modelName, modelVersionTag, trn), SETM_FP.fpTassignOE(dpOut, modelName, modelVersionTag, trn))){
			return false;
		}

		// save parameter static
		if (!SETMFile_Params.saveModelParams(sasModel, SETM_FP.fpParams(dpOut, modelName, modelVersionTag, trn))){			
			return false;
		}

		return true;
	}
}
