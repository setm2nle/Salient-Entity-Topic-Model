package acw.setmwo.files;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.StringTokenizer;

import acw.setmwo.model.SETMWOModel;

public class SETMWOFile_Params {
	public double alpha;
	public double beta;
	public double gamma;
	public double sigma;
	public double lambda;
	public int K;
	public int M;
	public int VW;
	public int VE;
	
	public static boolean readParams(SETMWOModel setmwoModel, String otherFilePath){
		//open file <model>.others to read:

		try {
			BufferedReader reader = new BufferedReader(new FileReader(otherFilePath));
			String line;
			while((line = reader.readLine()) != null){
				StringTokenizer tknr = new StringTokenizer(line,"= \t\r\n");

				int count = tknr.countTokens();
				if (count != 2)
					continue;

				String optstr = tknr.nextToken();
				String optval = tknr.nextToken();

				if (optstr.equalsIgnoreCase("alpha")){
					setmwoModel.paramStatic.alpha = Double.parseDouble(optval);
				}
				else if (optstr.equalsIgnoreCase("beta")){
					setmwoModel.paramStatic.beta = Double.parseDouble(optval);
				}
				else if (optstr.equalsIgnoreCase("gamma")){
					setmwoModel.paramStatic.gamma = Double.parseDouble(optval);
				}
				else if (optstr.equalsIgnoreCase("sigma")){
					setmwoModel.paramStatic.sigma = Double.parseDouble(optval);
				}
				else if (optstr.equalsIgnoreCase("ntopics")){
					setmwoModel.paramStatic.K = Integer.parseInt(optval);
				}
				else if (optstr.equalsIgnoreCase("ndocs")){
					setmwoModel.paramStatic.M = Integer.parseInt(optval);
				}
				else if (optstr.equalsIgnoreCase("nw")){
					setmwoModel.paramStatic.VW = Integer.parseInt(optval);
				}
				else if (optstr.equalsIgnoreCase("ne")){
					setmwoModel.paramStatic.VE = Integer.parseInt(optval);
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
				else if (optstr.equalsIgnoreCase("ne")){
					VE = Integer.parseInt(optval);
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

	public static boolean saveModelOthers(SETMWOModel setmwoModel, String filename){
		try{
			BufferedWriter writer = new BufferedWriter(new FileWriter(filename));

			writer.write("alpha=" + setmwoModel.paramStatic.alpha + "\n");
			writer.write("beta=" + setmwoModel.paramStatic.beta + "\n");
			writer.write("gamma=" + setmwoModel.paramStatic.gamma + "\n");
			writer.write("sigma=" + setmwoModel.paramStatic.sigma + "\n");
			writer.write("lambda=" + setmwoModel.paramStatic.lambda + "\n");
			writer.write("ntopics=" + setmwoModel.paramStatic.K + "\n");
			writer.write("ndocs=" + setmwoModel.paramStatic.M + "\n");
			writer.write("nw=" + setmwoModel.paramStatic.VW + "\n");
			writer.write("ne=" + setmwoModel.paramStatic.VE + "\n");

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

