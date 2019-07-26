package acw.setmwo.param;

import org.kohsuke.args4j.Option;

public class SETMWOCmdOption {
	
	/*
	 * specify the status of the run, i.e. training or inference.
	 */
	@Option(name="-est", usage="Specify whether we want to estimate model from scratch")
	public boolean est = false;
	
	@Option(name="-inf", usage="Specify whether we want to do inference")
	public boolean inf = false;
	
	/*
	 * specify input directory and input files
	 */
	
	@Option(name="-dpTrain", usage="Specify directory")
	public String dpTrain = "";
	
	@Option(name="-dpTest", usage="Specify directory")
	public String dpTest = "";
	
	@Option(name="-wT", usage="Specify train document words data file")
	public String fpWTrn = "";
	
	@Option(name="-eT", usage="Specify train document entities data file")
	public String fpETrn = "";
	
	@Option(name="-wI", usage="Specify test document words data file")
	public String fpWInf = "";
	
	@Option(name="-eI", usage="Specify test document entities data file")
	public String fpEInf = "";
	
	/*
	 * specify model parameters
	 */
	@Option(name="-model", usage="Specify the model name")
	public String modelName = "default";
	
	@Option(name="-alpha", usage="Specify alpha")
	public double alpha = 0.1;
	
	@Option(name="-beta", usage="Specify beta")
	public double beta = 0.1;
	
	@Option(name="-gamma", usage="Specify gamma")
	public double gamma = 0.1;
	
	@Option(name="-sigma", usage="Specify sigma")
	public double sigma = 0.1;
	
	@Option(name="-lambda", usage="Specify lambda")
	public double lambda = 0.5;
	
	@Option(name="-ntopics", usage="Specify the number of topics")
	public int K = 100;
	
	@Option(name="-niters", usage="Specify the number of iterations")
	public int niters = 1000;
	
	@Option(name="-savestep", usage="Specify the number of steps to save the model since the last save")
	public int savestep = 400;
}
