package acw.setm.files;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.StringTokenizer;

import acw.setm.model.SETMModel;

public class SETMFile_Params {
	public double alpha;
	public double beta;
	public double gamma;
	public double sigma;
	public double delta;
	public double lambda;
	public int K;
	public int M;
	public int VW;
	public int VSE;
	public int VAE;
	
	public static boolean readParams(SETMModel setmModel, String fpParams){
		//open file <model>.others to read:

		try {
			BufferedReader reader = new BufferedReader(new FileReader(fpParams));
			String line;
			while((line = reader.readLine()) != null){
				StringTokenizer tknr = new StringTokenizer(line,"= \t\r\n");

				int count = tknr.countTokens();
				if (count != 2)
					continue;

				String optstr = tknr.nextToken();
				String optval = tknr.nextToken();

				if (optstr.equalsIgnoreCase("alpha")){
					setmModel.paramStatic.alpha = Double.parseDouble(optval);
				}
				else if (optstr.equalsIgnoreCase("beta")){
					setmModel.paramStatic.beta = Double.parseDouble(optval);
				}
				else if (optstr.equalsIgnoreCase("gamma")){
					setmModel.paramStatic.gamma = Double.parseDouble(optval);
				}
				else if (optstr.equalsIgnoreCase("sigma")){
					setmModel.paramStatic.sigma = Double.parseDouble(optval);
				}
				else if (optstr.equalsIgnoreCase("delta")){
					setmModel.paramStatic.delta = Double.parseDouble(optval);
				}
				else if (optstr.equalsIgnoreCase("lambda")){
					setmModel.paramStatic.lambda = Double.parseDouble(optval);
				}
				else if (optstr.equalsIgnoreCase("ntopics")){
					setmModel.paramStatic.K = Integer.parseInt(optval);
				}
				else if (optstr.equalsIgnoreCase("ndocs")){
					setmModel.paramStatic.M = Integer.parseInt(optval);
				}
				else if (optstr.equalsIgnoreCase("nw")){
					setmModel.paramStatic.VW = Integer.parseInt(optval);
				}
				else if (optstr.equalsIgnoreCase("nse")){
					setmModel.paramStatic.VSE = Integer.parseInt(optval);
				}
				else if (optstr.equalsIgnoreCase("nae")){
					setmModel.paramStatic.VOE = Integer.parseInt(optval);
				}
			}

			reader.close();
		}
		catch (Exception e){
			System.out.println("Error while reading other file:" + e.getMessage());
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public boolean readParams(String fpParams){
		//open file <model>.others to read:

		try {
			BufferedReader reader = new BufferedReader(new FileReader(fpParams));
			String line;
			while((line = reader.readLine()) != null){
				StringTokenizer tknr = new StringTokenizer(line,"= \t\r\n");

				int count = tknr.countTokens();
				if (count != 2)
					continue;

				String optstr = tknr.nextToken();
				String optval = tknr.nextToken();

				if (optstr.equalsIgnoreCase("alpha")){
					alpha = Double.parseDouble(optval);
				}
				else if (optstr.equalsIgnoreCase("beta")){
					beta = Double.parseDouble(optval);
				}
				else if (optstr.equalsIgnoreCase("gamma")){
					gamma = Double.parseDouble(optval);
				}
				else if (optstr.equalsIgnoreCase("sigma")){
					sigma = Double.parseDouble(optval);
				}
				else if (optstr.equalsIgnoreCase("delta")){
					delta = Double.parseDouble(optval);
				}
				else if (optstr.equalsIgnoreCase("lambda")){
					lambda = Double.parseDouble(optval);
				}
				else if (optstr.equalsIgnoreCase("ntopics")){
					K = Integer.parseInt(optval);
				}
				else if (optstr.equalsIgnoreCase("ndocs")){
					M = Integer.parseInt(optval);
				}
				else if (optstr.equalsIgnoreCase("nw")){
					VW = Integer.parseInt(optval);
				}
				else if (optstr.equalsIgnoreCase("nse")){
					VSE = Integer.parseInt(optval);
				}
				else if (optstr.equalsIgnoreCase("nae")){
					VAE = Integer.parseInt(optval);
				}
			}

			reader.close();
		}
		catch (Exception e){
			System.out.println("Error while reading other file:" + e.getMessage());
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public static boolean saveModelParams(SETMModel sasModel, String filename){
		try{
			BufferedWriter writer = new BufferedWriter(new FileWriter(filename));

			writer.write("alpha=" + sasModel.paramStatic.alpha + "\n");
			writer.write("beta=" + sasModel.paramStatic.beta + "\n");
			writer.write("gamma=" + sasModel.paramStatic.gamma + "\n");
			writer.write("sigma=" + sasModel.paramStatic.sigma + "\n");
			writer.write("delta=" + sasModel.paramStatic.delta + "\n");
			writer.write("lambda=" + sasModel.paramStatic.lambda + "\n");
			writer.write("ntopics=" + sasModel.paramStatic.K + "\n");
			writer.write("ndocs=" + sasModel.paramStatic.M + "\n");
			writer.write("nw=" + sasModel.paramStatic.VW + "\n");
			writer.write("nse=" + sasModel.paramStatic.VSE + "\n");
			writer.write("nae=" + sasModel.paramStatic.VOE + "\n");

			writer.close();
		}
		catch(Exception e){
			System.out.println("Error while saving model others:" + e.getMessage());
			e.printStackTrace();
			return false;
		}
		return true;
	}
}

