package acw.setmwo.model;

import acw.setmwo.dao.SETMWODataset;
import acw.setmwo.dao.SETMWODatasetReader;
import acw.setmwo.dao.SETMWODatasetReaderWithExistingDict;
import acw.setmwo.param.SETMWOCmdOption;
import acw.setmwo.param.SETMWOParamRuntime;
import acw.setmwo.param.SETMWOParamStatic;

public class SETMWOModel {	
	public SETMWOParamStatic paramStatic;
	public SETMWOParamRuntime paramRuntime;
	public boolean training;
	
	// 1. File Names and Paths
	public String modelName;
	public String dpTrain;
	public String dpTest;
	public String fpWTrn;
	public String fpETrn;
	public String fpWInf;
	public String fpEInf;

	public SETMWODataset data;  // link to a dataset

	/**
	 * initialize the model (used in either training or inference)
	 */
	protected boolean init(SETMWOCmdOption option){
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
			fpETrn = option.fpETrn;
		}if (option.inf){
			training = false;
			dpTrain = option.dpTrain;
			fpWInf = option.fpWInf;
			fpEInf = option.fpEInf;
		}
		
		// initialize static parameters
		paramStatic = new SETMWOParamStatic();
		paramStatic.initUsingCmdOption(option);
		// initialize runtime parameters
		paramRuntime = new SETMWOParamRuntime();
		
		return true;
	}

	/**
	 * Init parameters for estimation  (used in training)
	 */
	public boolean initNewModel(SETMWOCmdOption option){
		
		if (!init(option))
			return false;

		// read dataset
		data = SETMWODatasetReader.parse(fpWTrn, fpETrn);
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
	public boolean initNewModelWithExistingModel(SETMWOCmdOption option, SETMWOModel trnModel){
		if (!init(option))
			return false;
		
		data = new SETMWODatasetReaderWithExistingDict().parse(fpWInf, fpEInf, trnModel.data.localDictWE, trnModel.data.localDictSE);
		if (data == null){
			System.out.println("Fail to read dataset!\n");
			return false;
		}

		// read static runtime parameters
		paramStatic.initUsingDataset(data);
		paramRuntime.initUsingStaticParam(paramStatic);
		paramRuntime.initInfUsingDataset(data, paramStatic.K);
		
		return true;
	}
	
	/**
	 * Initialize trained model.
	 * The parameters of trained model is not obtained from option directly, but obtained from others file. 
	 */
	public boolean initEstimatedModel(SETMWOCmdOption option){
		if (!init(option))
			return false;
		
		training = false;
		
		// load model, i.e., read z and trndata
		if (!SETMWOModelIO.loadModel(this, dpTrain)){
			System.out.println("Fail to load word-topic assignment file of the model!\n");
			return false;
		}
		
		paramStatic.initUsingDataset(data);
		paramRuntime.readCounts(data, paramStatic);
	    
		return true;
	}
}
