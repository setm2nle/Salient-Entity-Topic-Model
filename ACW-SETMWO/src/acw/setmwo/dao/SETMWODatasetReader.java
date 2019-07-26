package acw.setmwo.dao;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import acw.common.utils.collection.StringIdDualDict;

public class SETMWODatasetReader {

	public static StringIdDualDict localDictW;
	public static StringIdDualDict localDictE;

	public static SETMWODataset parse(String fpDocWords, String fpDocEntities){
		SETMWODataset setmwoDataset = null;
		localDictW = new StringIdDualDict();
		localDictE = new StringIdDualDict();
		try {
			BufferedReader brW = new BufferedReader(new InputStreamReader(
					new FileInputStream(fpDocWords), "UTF-8"));
			BufferedReader brE = new BufferedReader(new InputStreamReader(
					new FileInputStream(fpDocEntities), "UTF-8"));

			List<SETMWODoc> setmwoDocuments = new ArrayList<SETMWODoc>();
			String lineStrW, lineStrE;
			int lineNum = 0;
			while(true){
				lineStrW = brW.readLine();
				lineStrE = brE.readLine();
				if(lineStrW == null || lineStrE == null){
					if(lineStrW != null || lineStrE != null){
						System.out.println("The number of lines in two files inconsistent.");
					}
					break;
				}

				lineNum++;
				SETMWODoc setmwoDocument = parseTokens(lineStrW, lineStrE);
				setmwoDocuments.add(setmwoDocument);
			}

			int M = lineNum;
			setmwoDataset = new SETMWODataset(M);
			setmwoDataset.localDictWE = localDictW;
			setmwoDataset.localDictSE = localDictE;
			setmwoDataset.VWE = localDictW.id2Str.size();
			setmwoDataset.VSE = localDictE.id2Str.size();
			SETMWODoc[] docs = new SETMWODoc[setmwoDocuments.size()];
			for (int i = 0; i < setmwoDocuments.size(); i++) {
				docs[i] = setmwoDocuments.get(i);
			}
			setmwoDataset.docs = docs;

			brW.close();
			brE.close();
		} catch (Exception e) {
			System.out.println(e.toString() + " in Reading dataset.");
			e.printStackTrace();
		}

		return setmwoDataset;
	}

	private static SETMWODoc parseTokens(String wordsStr, String entitiesStr) {
		String[] wArr = wordsStr.trim().split(SETMWODataset.docWEeparator);
		int[] words = new int[wArr.length];
		
		// add document words into token array
		for (int i = 0; i < wArr.length; i++) {
			String wordStr = wArr[i];
			if(!localDictW.contains(wordStr)){
				localDictW.addStr(wordStr);
			}
			words[i] = localDictW.getID(wordStr);
		}
		
		int[] entities = null;
		int entityCount = 0;
		if(!entitiesStr.equals("NULL")){
			String[] eArr = entitiesStr.trim().split(SETMWODataset.docSESeparator);
			entityCount = eArr.length;
			entities = new int[entityCount];
			// add document normal entities into token array
			for (int i = 0; i < eArr.length; i++) {
				String entityStr = eArr[i];
				if(!localDictE.contains(entityStr)){
					localDictE.addStr(entityStr);
				}
				entities[i] = localDictE.getID(entityStr);
			}
		}
		

		SETMWODoc setmwoDoc = new SETMWODoc(words, entities, wArr.length, entityCount);
		return setmwoDoc;
	}
}
