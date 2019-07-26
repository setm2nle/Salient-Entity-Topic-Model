package acw.setmwo.files;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Vector;

import acw.common.utils.file.FileIOUtils;
import acw.setmwo.dao.SETMWODataset;
import acw.setmwo.dao.SETMWODoc;
import acw.setmwo.model.SETMWOModel;

public class SETMWOFile_Tassign {
	
	/**
	 * Write the topic assignment of the model to file
	 * @param setmwoModel
	 * @param fpTassign
	 */
	public static boolean saveModelTAssign(SETMWOModel setmwoModel, String fpTassignW, String fpTassignE){
		try{
			BufferedWriter fwW = new BufferedWriter(new FileWriter(fpTassignW));
			BufferedWriter fwE = new BufferedWriter(new FileWriter(fpTassignE));
			
			//write docs with topic assignments for words
			for (int d = 0; d < setmwoModel.data.M; d++){
				for (int n = 0; n < setmwoModel.data.docs[d].words.length; ++n){
					fwW.write(setmwoModel.data.docs[d].words[n] + ":" + setmwoModel.paramRuntime.y_t.get(d)[n] + " ");
				}
				fwW.write(System.lineSeparator());
				
				if(setmwoModel.data.docs[d].entities != null){
					for (int n = 0; n < setmwoModel.data.docs[d].entities.length; ++n){
						fwE.write(setmwoModel.data.docs[d].entities[n] + ":" + setmwoModel.paramRuntime.z_t.get(d)[n] + " ");
					}
				}else{
					fwE.write("NULL");
				}
				fwE.write(System.lineSeparator());
			}
			
			fwW.close();
			fwE.close();
			return true;
		}
		catch (Exception e){
			System.out.println("Error while saving model tassign: " + e.getMessage());
			e.printStackTrace();
		}
		return false;
	}
	
	public static boolean readTassign(SETMWOModel setmwoModel, String fpTassignW, String fpTassignE){
		try {
			setmwoModel.paramRuntime.z_t = new Vector<Integer[]>();
			setmwoModel.paramRuntime.y_t = new Vector<Integer[]>();
			BufferedReader brTassignW = FileIOUtils.getBufferedReader(fpTassignW);
			BufferedReader brTassignE = FileIOUtils.getBufferedReader(fpTassignE);
			
			String lineStrW, lineStrE;
			setmwoModel.data = new SETMWODataset(setmwoModel.paramStatic.M);
			setmwoModel.data.VWE = setmwoModel.paramStatic.VW;
			setmwoModel.data.VSE = setmwoModel.paramStatic.VE;
			int m = 0;
			while(true){
				lineStrW = brTassignW.readLine();
				lineStrE = brTassignE.readLine();
				if(lineStrW == null || lineStrE == null){
					break;
				}
				
				// read topic assignment of words
				String[] wPairs = lineStrW.split(" ");
				int[] words = new int[wPairs.length];
				Integer[] topicAssignmentW = new Integer[wPairs.length];
				for (int i = 0; i < wPairs.length; i++) {
					String[] entityTopicPair = wPairs[i].split(":");
					Integer entityId = Integer.parseInt(entityTopicPair[0]);
					Integer topic = Integer.parseInt(entityTopicPair[1]);
					words[i] = entityId;
					topicAssignmentW[i] = topic;
				}
				setmwoModel.paramRuntime.y_t.add(topicAssignmentW);
				
				int[] entities = null;
				int entityCount = 0;
				if(lineStrE.equals("NULL")){
					setmwoModel.paramRuntime.z_t.add(null);
				}else{
					String[] pairs = lineStrE.split(" ");
					entityCount = pairs.length;
					entities = new int[pairs.length];
					Integer[] topicAssignmentE = new Integer[pairs.length];
					for (int i = 0; i < pairs.length; i++) {
						String[] entityTopicPair = pairs[i].split(":");
						if(entityTopicPair[0].equals("NULL")){
							System.out.println("line string:" + lineStrE);
						}
						Integer entityId = Integer.parseInt(entityTopicPair[0]);
						Integer topic = Integer.parseInt(entityTopicPair[1]);
						entities[i] = entityId;
						topicAssignmentE[i] = topic;
					}
					setmwoModel.paramRuntime.z_t.add(topicAssignmentE);
				}
				
				// create SETMWO document and add it to data
				SETMWODoc doc = new SETMWODoc(words, entities, words.length, entityCount);
				setmwoModel.data.setDoc(doc, m);
				m++;
			}
			
			brTassignW.close();
			brTassignE.close();
			return true;
		}
		catch (Exception e){
			System.out.println("Error while loading model: " + e.getMessage());
			e.printStackTrace();
		}
		return false;
	}
}
