package acw.setm.files;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Vector;

import acw.common.utils.file.FileIOUtils;
import acw.setm.dao.SETMDataset;
import acw.setm.dao.SETMDoc;
import acw.setm.model.SETMModel;

public class SETMFile_Tassign {
	public static final String elementSeperator = " ";
	public static final String pairSeperator = ":";
	
	/**
	 * Write the topic assignment of the model to file
	 * @param setmModel
	 * @param fpTassign
	 */
	public static boolean saveModelTAssign(SETMModel setmModel, String fpTassignW, String fpTassignSE, String fpTassignOE){
		try{
			BufferedWriter fwW = new BufferedWriter(new FileWriter(fpTassignW));
			BufferedWriter fwSE = new BufferedWriter(new FileWriter(fpTassignSE));
			BufferedWriter fwOE = new BufferedWriter(new FileWriter(fpTassignOE));
			
			//write docs with topic assignments for words
			for (int d = 0; d < setmModel.data.M; d++){
				for (int n = 0; n < setmModel.data.docs[d].words.length; ++n){
					fwW.write(setmModel.data.docs[d].words[n] + pairSeperator + setmModel.paramRuntime.y_t.get(d)[n] + elementSeperator);
				}
				fwW.write(System.lineSeparator());
				
				if(setmModel.data.docs[d].seCount > 0){
					for (int n = 0; n < setmModel.data.docs[d].seCount; ++n){
						fwSE.write(setmModel.data.docs[d].salEntities[n] + pairSeperator + setmModel.paramRuntime.z_t.get(d)[n] + elementSeperator);
					}
				}else{
					fwSE.write("NULL");
				}
				fwSE.write(System.lineSeparator());
				
				if(setmModel.data.docs[d].oeCount > 0){
					for (int n = 0; n < setmModel.data.docs[d].oeCount; ++n){
						fwOE.write(setmModel.data.docs[d].obsEntities[n] + pairSeperator + setmModel.paramRuntime.u_t.get(d)[n] + elementSeperator);
					}
				}else{
					fwOE.write("NULL");
				}
				fwOE.write(System.lineSeparator());
			}
			
			fwW.close();
			fwSE.close();
			fwOE.close();
			return true;
		}
		catch (Exception e){
			System.out.println("Error while saving model tassign: " + e.getMessage());
			e.printStackTrace();
		}
		return false;
	}
	
	public static boolean readTassign(SETMModel setmModel, String fpTassignW, String fpTassignSE, String fpTassignOE){
		try {
			setmModel.paramRuntime.y_t = new Vector<Integer[]>();
			setmModel.paramRuntime.z_t = new Vector<Integer[]>();
			setmModel.paramRuntime.u_t = new Vector<Integer[]>();
			BufferedReader brTassignW = FileIOUtils.getBufferedReader(fpTassignW);
			BufferedReader brTassignSE = FileIOUtils.getBufferedReader(fpTassignSE);
			BufferedReader brTassignOE = FileIOUtils.getBufferedReader(fpTassignOE);
			
			String lineStrW, lineStrSE, lineStrOE;
			setmModel.data = new SETMDataset(setmModel.paramStatic.M);
			setmModel.data.VW = setmModel.paramStatic.VW;
			setmModel.data.VSE = setmModel.paramStatic.VSE;
			setmModel.data.VOE = setmModel.paramStatic.VOE;
			int m = 0;
			while(true){
				lineStrW = brTassignW.readLine();
				lineStrSE = brTassignSE.readLine();
				lineStrOE = brTassignOE.readLine();
				if(lineStrW == null || lineStrSE == null || lineStrOE == null){
					break;
				}
				
				// read topic assignment of words
				String[] wPairs = lineStrW.split(elementSeperator);
				int[] words = new int[wPairs.length];
				Integer[] topicAssignmentW = new Integer[wPairs.length];
				for (int i = 0; i < wPairs.length; i++) {
					String[] entityTopicPair = wPairs[i].split(pairSeperator);
					Integer entityId = Integer.parseInt(entityTopicPair[0]);
					Integer topic = Integer.parseInt(entityTopicPair[1]);
					words[i] = entityId;
					topicAssignmentW[i] = topic;
				}
				setmModel.paramRuntime.y_t.add(topicAssignmentW);
				
				int[] salEntities = null;
				int seCount = 0;
				if(lineStrSE.equals("NULL")){
					setmModel.paramRuntime.z_t.add(null);
				}else{
					// read topic assignment of salient entities
					String[] sePairs = lineStrSE.split(elementSeperator);
					salEntities = new int[sePairs.length];
					seCount = sePairs.length;
					Integer[] topicAssignmentSE = new Integer[sePairs.length];
					for (int i = 0; i < sePairs.length; i++) {
						String[] seTopicPair = sePairs[i].split(pairSeperator);
						Integer seId = Integer.parseInt(seTopicPair[0]);
						Integer topic = Integer.parseInt(seTopicPair[1]);
						salEntities[i] = seId;
						topicAssignmentSE[i] = topic;
					}
					setmModel.paramRuntime.z_t.add(topicAssignmentSE);
				}
				
				int[] obsEntities = null;
				int oeCount = 0;
				// read topic assignment of all entities
				if(lineStrOE.equals("NULL")){
					setmModel.paramRuntime.u_t.add(null);
				}else{
					String[] oePairs = lineStrOE.split(elementSeperator);
					obsEntities = new int[oePairs.length];
					oeCount = oePairs.length;
					Integer[] topicAssignmentOE = new Integer[oePairs.length];
					for (int i = 0; i < oePairs.length; i++) {
						String[] oeTopicPair = oePairs[i].split(pairSeperator);
						Integer oeId = Integer.parseInt(oeTopicPair[0]);
						Integer topic = Integer.parseInt(oeTopicPair[1]);
						obsEntities[i] = oeId;
						topicAssignmentOE[i] = topic;
					}
					setmModel.paramRuntime.u_t.add(topicAssignmentOE);
				}
				
				// create SETM document and add it to data
				SETMDoc doc = new SETMDoc(words, salEntities, obsEntities, words.length, seCount, oeCount);
				setmModel.data.setDoc(doc, m);
				m++;
			}
			
			brTassignW.close();
			brTassignSE.close();
			brTassignOE.close();
			return true;
		}
		catch (Exception e){
			System.out.println("Error while loading model: " + e.getMessage());
			e.printStackTrace();
		}
		return false;
	}
}
