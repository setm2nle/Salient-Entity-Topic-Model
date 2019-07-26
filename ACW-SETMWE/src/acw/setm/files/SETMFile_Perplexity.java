package acw.setm.files;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import acw.common.topicmodel.perplexity.PerplextiyCalculator;
import acw.common.utils.file.FileIOUtils;

public class SETMFile_Perplexity {
	
	public static void write2File(FileWriter fwPerplexity, double docPerplexityW, int wordLength, double docPerplexityE, int entityLength) throws IOException{
		fwPerplexity.write(docPerplexityW + " " + wordLength + " " + docPerplexityE + " " + entityLength);
		fwPerplexity.write(System.lineSeparator());
	}

	public static double calcPerplexity(String fpPerplexity, List<Integer> targetLines) throws IOException{
		double probSum = 0;
		int probCount = 0;
		BufferedReader brPerp = FileIOUtils.getBufferedReader(fpPerplexity);
		String lineStr;
		int lineNum = 0;
		boolean includeCurrentItem = false;
		while((lineStr = brPerp.readLine()) != null){
			if(targetLines != null){
				if(targetLines.contains(lineNum)){
					includeCurrentItem =  true;
				}
			}else{
				includeCurrentItem = true;
			}
			// add up perplexity if the current item should be included
			if(includeCurrentItem){
				String[] vals = lineStr.split(" ");
				if(vals.length == 4){
					double perplexityW = Double.parseDouble(vals[0]);
					int wordLength = Integer.parseInt(vals[1]);
					double perplexityE = Double.parseDouble(vals[2]);
					int entityLength = Integer.parseInt(vals[3]);
					probSum += perplexityW + perplexityE;
					probCount += wordLength + entityLength;
				}
			}
		}
		return PerplextiyCalculator.calcPerplexity(probSum, probCount);
	}
	
	public static double calcPerplexity(String fpPerplexity) throws IOException{
		return calcPerplexity(fpPerplexity, null);
	}
}
