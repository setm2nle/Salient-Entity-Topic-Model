package acw.setmwo.dao;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import acw.common.utils.collection.StringIdDualDict;
import acw.common.utils.file.FileContentUtils;

public class SETMWODatasetReaderWithExistingDict {
	
	public SETMWODataset parse(String fpWords, String fpEntities, StringIdDualDict setmwoDictW, StringIdDualDict setmwoDictE){
		SETMWODataset setmwoDataset = null;
		try {
			int M = FileContentUtils.fileLineCounter(fpWords);
			setmwoDataset = new SETMWODataset(M);
			setmwoDataset.globalDictWE = setmwoDictW;
			setmwoDataset.globalDictSE = setmwoDictE;
			BufferedReader brW = new BufferedReader(new InputStreamReader(
					new FileInputStream(fpWords), "UTF-8"));
			BufferedReader brE = new BufferedReader(new InputStreamReader(
					new FileInputStream(fpEntities), "UTF-8"));
			
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
				
				setmwoDataset.setDoc(lineStrW, lineStrE, lineNum);
				lineNum++;
			}
			
			setmwoDataset.VWE = setmwoDataset.localDictWE.id2Str.size();
			setmwoDataset.VSE = setmwoDataset.localDictSE.id2Str.size();
			
			brW.close();
			brE.close();
		} catch (Exception e) {
			System.out.println(e.toString() + " in Reading dataset.");
			e.printStackTrace();
		}
		
		return setmwoDataset;
	}
}
