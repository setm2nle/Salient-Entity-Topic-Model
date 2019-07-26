package acw.setm.dao;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import acw.common.utils.collection.StringIdDualDict;
import acw.common.utils.file.FileContentUtils;

public class SETMDatasetReaderWithExistingDict {
	
	public SETMDataset parse(String fpWords, String fpSalEntities, String fpObsEntities, StringIdDualDict setmDictW, StringIdDualDict setmDictSE, StringIdDualDict setmDictOE){
		SETMDataset setmDataset = null;
		try {
			int M = FileContentUtils.fileLineCounter(fpWords);
			setmDataset = new SETMDataset(M);
			setmDataset.globalDictW = setmDictW;
			setmDataset.globalDictSE = setmDictSE;
			setmDataset.globalDictOE = setmDictOE;
			BufferedReader brW = new BufferedReader(new InputStreamReader(
					new FileInputStream(fpWords), "UTF-8"));
			BufferedReader brSE = new BufferedReader(new InputStreamReader(
					new FileInputStream(fpSalEntities), "UTF-8"));
			BufferedReader brOE = new BufferedReader(new InputStreamReader(
					new FileInputStream(fpObsEntities), "UTF-8"));
			
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
				
				if(lineStrSE.equals("NULL")){
					lineStrSE = null;
				}
				if(lineStrOE.equals("NULL")){
					lineStrOE = null;
				}
				
				setmDataset.setDoc(lineStrW, lineStrSE, lineStrOE, lineNum);
				lineNum++;
			}
			
			setmDataset.VW = setmDataset.localDictW.id2Str.size();
			setmDataset.VSE = setmDataset.localDictSE.id2Str.size();
			setmDataset.VOE = setmDataset.localDictOE.id2Str.size();
			
			brW.close();
			brSE.close();
			brOE.close();
		} catch (Exception e) {
			System.out.println(e.toString() + " in Reading dataset.");
			e.printStackTrace();
		}
		
		return setmDataset;
	}
}
