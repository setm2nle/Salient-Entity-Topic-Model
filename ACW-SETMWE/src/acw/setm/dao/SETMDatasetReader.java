package acw.setm.dao;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import acw.common.utils.collection.StringIdDualDict;

public class SETMDatasetReader {

	public static StringIdDualDict localDictW;
	public static StringIdDualDict localDictSE;
	public static StringIdDualDict localDictOE;

	public static SETMDataset parse(String fpDocWords, String fpDocSEntities, String fpDocOEntities){
		SETMDataset setmDataset = null;
		localDictW = new StringIdDualDict();
		localDictSE = new StringIdDualDict();
		localDictOE = new StringIdDualDict();
		try {
			BufferedReader brW = new BufferedReader(new InputStreamReader(
					new FileInputStream(fpDocWords), "UTF-8"));
			BufferedReader brSE = new BufferedReader(new InputStreamReader(
					new FileInputStream(fpDocSEntities), "UTF-8"));
			BufferedReader brOE = new BufferedReader(new InputStreamReader(
					new FileInputStream(fpDocOEntities), "UTF-8"));

			List<SETMDoc> setmDocuments = new ArrayList<SETMDoc>();
			String lineStrW, lineStrSE, lineStrOE;
			int lineNum = 0;
			while(true){
				lineStrW = brW.readLine();
				lineStrSE = brSE.readLine();
				lineStrOE = brOE.readLine();
				if(lineStrW == null || lineStrSE == null || lineStrOE == null){
					if(lineStrW != null || lineStrSE != null || lineStrOE != null){
						System.out.println("The number of lines in two files inconsistent.");
					}
					break;
				}

				lineNum++;
				SETMDoc setmDocument = parseTokens(lineStrW, lineStrSE, lineStrOE);
				setmDocuments.add(setmDocument);
			}

			int M = lineNum;
			setmDataset = new SETMDataset(M);
			setmDataset.localDictW = localDictW;
			setmDataset.localDictSE = localDictSE;
			setmDataset.localDictOE = localDictOE;
			setmDataset.VW = localDictW.id2Str.size();
			setmDataset.VSE = localDictSE.id2Str.size();
			setmDataset.VOE = localDictOE.id2Str.size();
			SETMDoc[] docs = new SETMDoc[setmDocuments.size()];
			for (int i = 0; i < setmDocuments.size(); i++) {
				docs[i] = setmDocuments.get(i);
			}
			setmDataset.docs = docs;

			brW.close();
			brSE.close();
			brOE.close();
		} catch (Exception e) {
			System.out.println(e.toString() + " in Reading dataset.");
			e.printStackTrace();
		}

		return setmDataset;
	}

	private static SETMDoc parseTokens(String wordsStr, String salEntitiesStr, String obsEntitiesStr) {
		String[] wArr = wordsStr.trim().split(SETMDataset.docWSeparator);
		int[] words = new int[wArr.length];
		// add document words into token array
		for (int i = 0; i < wArr.length; i++) {
			String wordStr = wArr[i];
			if(!localDictW.contains(wordStr)){
				localDictW.addStr(wordStr);
			}
			words[i] = localDictW.getID(wordStr);
		}
		
		int[] salEntities = null;
		int seCount = 0;
		if(!salEntitiesStr.equals("NULL")){
			String[] seArr = salEntitiesStr.trim().split(SETMDataset.docSESeparator);
			seCount = seArr.length;
			salEntities = new int[seArr.length];
			// add document salient entities into token array
			for (int i = 0; i < seArr.length; i++) {
				String salEntityStr = seArr[i];
				if(!localDictSE.contains(salEntityStr)){
					localDictSE.addStr(salEntityStr);
				}
				salEntities[i] = localDictSE.getID(salEntityStr);
			}
		}
		
		int[] obsEntities = null;
		int oeCount = 0;
		if(!obsEntitiesStr.equals("NULL")){
			String[] oeArr = obsEntitiesStr.trim().split(SETMDataset.docOESeparator);
			oeCount = oeArr.length;
			obsEntities = new int[oeArr.length];
			// add document observed entities into token array
			for (int i = 0; i < oeArr.length; i++) {
				String obsEntityStr = oeArr[i];
				if(!localDictOE.contains(obsEntityStr)){
					localDictOE.addStr(obsEntityStr);
				}
				obsEntities[i] = localDictOE.getID(obsEntityStr);
			}
		}

		SETMDoc setmDoc = new SETMDoc(words, salEntities, obsEntities, wArr.length, seCount, oeCount);
		return setmDoc;
	}
}
