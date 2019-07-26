package acw.setm.perplexity;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import acw.common.utils.collection.StringIdDualDict;
import acw.common.utils.file.FileIOUtils;
import acw.setm.files.SETMFile_Params;
import acw.setm.files.SETMFile_Perplexity;
import acw.setm.files.SETMFile_Psi;
import acw.setm.files.SETMFile_Rho;
import acw.setm.files.SETMFile_Tassign;
import acw.setm.files.SETMFile_Theta;
import acw.setm.files.SETMFile_Varphi;

public class SETMPerplexityWrapper {
	private SETMPerplexity setmPerplexity;
	private StringIdDualDict dictW;
	private StringIdDualDict dictOE;

	public SETMPerplexityWrapper(String fpParams, String fpRho, String fpVarphiW, String fpPsiE){
		// read params
		SETMFile_Params params = new SETMFile_Params();
		params.readParams(fpParams);
		// read phi, varphiW, psiE
		double[][] rho = SETMFile_Rho.readRho(fpRho, params.K, params.VSE);
		double[][] varphiW = SETMFile_Varphi.readVarphi(fpVarphiW, params.K, params.VW);
		double[][] psiE = SETMFile_Psi.readPsi(fpPsiE, params.K, params.VAE);
		setmPerplexity = new SETMPerplexity(params.K, params.lambda, rho, varphiW, psiE);
	}

	public SETMPerplexityWrapper(String fpParams, String fpRho, String fpVarphiW, String fpPsiE, String fpWMap, String fpOEMap){
		this(fpParams, fpRho, fpVarphiW, fpPsiE);
		dictW = new StringIdDualDict();
		dictW.readStr2IdMap(fpWMap);
		dictOE = new StringIdDualDict();
		dictOE.readStr2IdMap(fpOEMap);
	}

	public double calcPerplexity(String fpTestTheta, String fpTassignSE, String fpTassignW, String fpTassignOE, String fpOutPerplexity) throws IOException{
		BufferedReader brTheta = FileIOUtils.getBufferedReader(fpTestTheta);
		BufferedReader brTassignSE = FileIOUtils.getBufferedReader(fpTassignSE);
		BufferedReader brTassignW = FileIOUtils.getBufferedReader(fpTassignW);
		BufferedReader brTassignOE = FileIOUtils.getBufferedReader(fpTassignOE);
		FileWriter fileWriter = FileIOUtils.getFileWriter(fpOutPerplexity);
		String lineTheta, lineTassignSE, lineTassignW, lineTassignOE;
		while(true){
			lineTheta = brTheta.readLine();
			lineTassignSE = brTassignSE.readLine();
			lineTassignW = brTassignW.readLine();
			lineTassignOE = brTassignOE.readLine();
			if(lineTheta == null || lineTassignSE == null || lineTassignW == null || lineTassignOE == null){
				break;
			}

			// read document topic distribution
			String[] thetaValArr = lineTheta.split(SETMFile_Theta.elementSeperator);
			double[] docTopicDist = new double[thetaValArr.length];
			for (int i = 0; i < docTopicDist.length; i++) {
				docTopicDist[i] = Double.parseDouble(thetaValArr[i]);
			}

			// read salient entities
			int[] salEntities = null;
			if(!lineTassignSE.equals("NULL")){
				String[] tassignValArrSE = lineTassignSE.split(SETMFile_Tassign.elementSeperator);
				salEntities = new int[tassignValArrSE.length];
				for (int i = 0; i < salEntities.length; i++) {
					String[] seTopicArr = tassignValArrSE[i].split(SETMFile_Tassign.pairSeperator);
					if(!seTopicArr[0].equals("")){
						salEntities[i] = Integer.parseInt(seTopicArr[0]);
					}
				}
			}

			// read words
			String[] tassignValArrW = lineTassignW.split(SETMFile_Tassign.elementSeperator);
			int[] words = new int[tassignValArrW.length];
			for (int i = 0; i < words.length; i++) {
				String[] wordTopicArr = tassignValArrW[i].split(SETMFile_Tassign.pairSeperator);
				if(!wordTopicArr[0].equals("")){
					words[i] = Integer.parseInt(wordTopicArr[0]);
				}
			}

			// read observed entities
			int[] obsEntities = null;
			if(!lineTassignOE.equals("NULL")){
				String[] tassignValArrOE = lineTassignOE.split(SETMFile_Tassign.elementSeperator);
				obsEntities = new int[tassignValArrOE.length];
				for (int i = 0; i < obsEntities.length; i++) {
					String[] oeTopicArr = tassignValArrOE[i].split(SETMFile_Tassign.pairSeperator);
					if(!oeTopicArr[0].equals("")){
						obsEntities[i] = Integer.parseInt(oeTopicArr[0]);
					}
				}
			}

			// calculate perplexity

			double[] docPerplexityWELength = setmPerplexity.computeSETMPerplexity4Doc(docTopicDist, salEntities, words, obsEntities);
			fileWriter.write(docPerplexityWELength[0] + " " + (int)docPerplexityWELength[1] + " " + docPerplexityWELength[2] + " " + (int)docPerplexityWELength[3]);
			fileWriter.write(System.lineSeparator());
		}
		brTheta.close();
		brTassignSE.close();
		brTassignW.close();
		brTassignOE.close();
		fileWriter.close();

		double perplexity = SETMFile_Perplexity.calcPerplexity(fpOutPerplexity);
		return perplexity;
	}

	public double calcPerplexityForWE(String fpTestTheta, String fpDocW, String fpDocOE, String fpOutPerplexity) throws IOException{
		BufferedReader brTheta = FileIOUtils.getBufferedReader(fpTestTheta);
		BufferedReader brDocW = null, brDocOE = null;
		if(fpDocW != null){
			brDocW = FileIOUtils.getBufferedReader(fpDocW);
		}
		if(fpDocOE != null){
			brDocOE = FileIOUtils.getBufferedReader(fpDocOE);
		}
		FileWriter fileWriter = FileIOUtils.getFileWriter(fpOutPerplexity);
		String lineTheta, lineDocW = "", lineDocOE = "";
		while(true){
			lineTheta = brTheta.readLine();
			if(brDocW != null){
				lineDocW = brDocW.readLine();
			}
			if(brDocOE != null){
				lineDocOE = brDocOE.readLine();
			}
			if(lineTheta == null){
				break;
			}

			// read document topic distribution
			String[] thetaValArr = lineTheta.split(SETMFile_Theta.elementSeperator);
			double[] docTopicDist = new double[thetaValArr.length];
			for (int i = 0; i < docTopicDist.length; i++) {
				docTopicDist[i] = Double.parseDouble(thetaValArr[i]);
			}

			// read observed entities
			int[] words = null;
			if(brDocW != null){
				if(!lineDocW.equals("NULL")){
					List<Integer> wordList = new ArrayList<Integer>();
					String[] wordStrArr = lineDocW.split(" ");
					for (int i = 0; i < wordStrArr.length; i++) {
						int oeId = dictW.getID(wordStrArr[i]);
						if(oeId >= 0){
							wordList.add(oeId);
						}
					}
					words = new int[wordList.size()];
					for (int i = 0; i < words.length; i++) {
						words[i] = wordList.get(i);
					}
				}
			}

			// read observed entities
			int[] obsEntities = null;
			if(brDocOE != null){
				if(!lineDocOE.equals("NULL")){
					List<Integer> oeList = new ArrayList<Integer>();
					String[] oeStrArr = lineDocOE.split(" ");
					for (int i = 0; i < oeStrArr.length; i++) {
						int oeId = dictOE.getID(oeStrArr[i]);
						if(oeId >= 0){
							oeList.add(oeId);
						}
					}
					obsEntities = new int[oeList.size()];
					for (int i = 0; i < obsEntities.length; i++) {
						obsEntities[i] = oeList.get(i);
					}
				}
			}

			// calculate perplexity

			double[] docPerplexityWELength = setmPerplexity.computeSETMPerplexity4Doc(docTopicDist, null, words, obsEntities);
			fileWriter.write(docPerplexityWELength[0] + " " + (int)docPerplexityWELength[1] + " " + docPerplexityWELength[2] + " " + (int)docPerplexityWELength[3]);
			fileWriter.write(System.lineSeparator());
		}
		brTheta.close();
		if(brDocW != null){
			brDocW.close();
		}
		if(brDocOE != null){
			brDocOE.close();
		}
		fileWriter.close();

		double perplexity = SETMFile_Perplexity.calcPerplexity(fpOutPerplexity);
		return perplexity;
	}
}
