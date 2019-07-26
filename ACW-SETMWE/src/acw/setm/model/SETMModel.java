package acw.setm.model;

import acw.setm.dao.SETMDataset;
import acw.setm.dao.SETMDatasetReader;
import acw.setm.dao.SETMDatasetReaderWithExistingDict;
import acw.setm.param.SETMCmdOption;
import acw.setm.param.SETMParamRuntime;
import acw.setm.param.SETMParamStatic;

public class SETMModel {	
	public SETMParamStatic paramStatic;
	public SETMParamRuntime paramRuntime;
	public boolean training;
	
	// 1. File Names and Paths
	public String modelName;
	public String dpTrain;
	public String dpTest;
	public String fpWTrn;
	public String fpSETrn;
	public String fpOETrn;
	public String fpWInf;
	public String fpSEInf;
	public String fpOEInf;

	public SETMDataset data;  // link to a dataset

	//---------------------------------------------------------------
	//	Init Methods
	//---------------------------------------------------------------
	/**
	 * initialize the model (used in either training or inference)
	 */
	protected boolean init(SETMCmdOption option){
		// Check whether option is null
		if (option == null)
			return false;

		// initialize directory path and input data file names
		dpTrain = option.dpTrain;
		dpTest = option.dpTest;
		
		// initialize model name
		modelName = option.modelName;
		if(option.est){
			training = true;
			fpWTrn = option.fpWTrn;
			fpSETrn = option.fpSETrn;
			fpOETrn = option.fpOETrn;
		}if (option.inf){
			training = false;
			dpTrain = option.dpTrain;
			fpWInf = option.fpWInf;
			fpSEInf = option.fpSEInf;
			fpOEInf = option.fpOEInf;
		}
		
		// initialize static parameters
		paramStatic = new SETMParamStatic();
		paramStatic.initUsingCmdOption(option);
		// initialize runtime parameters
		paramRuntime = new SETMParamRuntime();
		
		return true;
	}

	/**
	 * Init parameters for estimation  (used in training)
	 */
	public boolean initNewModel(SETMCmdOption option){
		
		if (!init(option))
			return false;

		// read dataset
		data = SETMDatasetReader.parse(fpWTrn, fpSETrn, fpOETrn);
		if (data == null){
			System.out.println("Fail to read training data!\n");
			return false;
		}
		
		// read static runtime parameters
		paramStatic.initUsingDataset(data);
		paramRuntime.initUsingStaticParam(paramStatic);
		paramRuntime.randomAssignment2NewData(data, paramStatic.K);
		
		return true;
	}
	
	/**
	 * Initialize parameters for inference  (used in inference)
	 */
	public boolean initNewModelWithExistingModel(SETMCmdOption option, SETMModel trnModel){
		if (!init(option))
			return false;
		
		data = new SETMDatasetReaderWithExistingDict().parse(fpWInf, fpSEInf, fpOEInf, trnModel.data.localDictW, trnModel.data.localDictSE, trnModel.data.localDictOE);
		if (data == null){
			System.out.println("Fail to read dataset!\n");
			return false;
		}

		// read static runtime parameters
		paramStatic.initUsingDataset(data);
		paramRuntime.initUsingStaticParam(paramStatic);
		paramRuntime.initInfUsingSASDataset(data, paramStatic.K);
		
		return true;
	}
	
	/**
	 * Initialize trained model.
	 * The parameters of trained model is not obtained from option directly, but obtained from others file. 
	 */
	public boolean initEstimatedModel(SETMCmdOption option){
		if (!init(option))
			return false;
		
		training = false;
		
		// load model, i.e., read z and trndata
		if (!SETMModelIO.loadModel(this, dpTrain)){
			System.out.println("Fail to load word-topic assignment file of the model!\n");
			return false;
		}
		
		paramStatic.initUsingDataset(data);
		paramRuntime.readCounts(data, paramStatic);
	    
		return true;
	}
}
