package acw.setm.dao;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import acw.common.utils.collection.StringIdDualDict;

public class SETMDataset {

	/**
	 * separator used to separate document words
	 */
	public static String docWSeparator = " ";

	/**
	 * separator used to separate salient entities
	 */
	public static String docSESeparator = " ";

	/**
	 * separator used to separate observed entities
	 */
	public static String docOESeparator = " ";

	/**
	 * local dictionary for words
	 */
	public StringIdDualDict localDictW;

	/**
	 * local dictionary for entities
	 */
	public StringIdDualDict localDictSE;

	public StringIdDualDict localDictOE;

	/**
	 * a list of documents in the dataset
	 */
	public SETMDoc [] docs;
	/**
	 * number of documents
	 */
	public int M; 
	/**
	 * number of tokens
	 */
	public int VW;
	/**
	 * number of salient entities
	 */
	public int VSE;

	/**
	 * number of observed entities
	 */
	public int VOE;

	public Map<Integer, Integer> lid2gidW = null;
	public Map<Integer, Integer> lid2gidSE = null;
	public Map<Integer, Integer> lid2gidOE = null;
	public StringIdDualDict globalDictW = null;
	public StringIdDualDict globalDictSE = null;
	public StringIdDualDict globalDictOE = null;

	public SETMDataset(int M){
		localDictW = new StringIdDualDict();
		localDictSE = new StringIdDualDict();
		localDictOE = new StringIdDualDict();

		this.M = M;
		this.VW = 0;
		this.VSE = 0;
		this.VOE = 0;
		docs = new SETMDoc[M];

		lid2gidW = new HashMap<Integer, Integer>();
		lid2gidSE = new HashMap<Integer, Integer>();
		lid2gidOE = new HashMap<Integer, Integer>();
	}

	/**
	 * set the document at the index idx if idx is greater than 0 and less than M
	 * @param doc document to be set
	 * @param idx index in the document array
	 */	
	public void setDoc(SETMDoc doc, int idx){
		if (0 <= idx && idx < M){
			docs[idx] = doc;
		}
	}

	public void setDoc(String wordsStr, String salEntitiesStr, String obsEntitiesStr, int idx){
		if (0 <= idx && idx < M){
			// read words and entities
			String [] words = wordsStr.split(docWSeparator);
			Vector<Integer> wordsVec = new Vector<Integer>();

			// read document words
			for (int i = 0; i < words.length; i++) {
				String wordStr = words[i];
				// assign word new id if the word has not been observed before
				int idLocal = localDictW.getID(wordStr);
				if (idLocal < 0){
					idLocal = localDictW.addStr(wordStr);
				}
				boolean addW = true;

				// create mapping to global dictionary
				if (globalDictW != null){
					//get the global id
					int idGlobal = globalDictW.getID(wordStr);
					if (idGlobal >= 0){
						lid2gidW.put(idLocal, idGlobal);
					}else{
						addW = false;
					}
				}else{
					addW = false;
				}
				
				// add if necessary
				if(addW){
					wordsVec.add(idLocal);
				}
			}

			Vector<Integer> salEntitiesVec = null;
			if(salEntitiesStr != null){
				String [] salEntities = salEntitiesStr.split(docSESeparator);
				salEntitiesVec = new Vector<Integer>();
				// read document salient entities
				for (int i = 0; i < salEntities.length; i++) {
					String seEntityStr = salEntities[i];
					if(seEntityStr.equals("NULL")){
						continue;
					}
					// assign entity new id if the word has not been observed before
					int idLocal = localDictSE.getID(seEntityStr);
					if (idLocal < 0){
						idLocal = localDictSE.addStr(seEntityStr);
					}
					boolean addSE = true;
					
					// create mapping to global dictionary
					if (globalDictSE != null){
						//get the global id
						int idGlobal = globalDictSE.getID(seEntityStr);
						if (idGlobal >= 0){
							lid2gidSE.put(idLocal, idGlobal);
						}else{
							addSE = false;
						}
					}else{
						addSE = false;
					}
					
					//
					if(addSE){
						salEntitiesVec.add(idLocal);
					}
				}
			}

			Vector<Integer> obsEntitiesVec = null;
			if(obsEntitiesStr != null){
				String [] obsEntities = obsEntitiesStr.split(docOESeparator);
				obsEntitiesVec = new Vector<Integer>();
				// read document all entities
				for (int i = 0; i < obsEntities.length; i++) {
					String oeEntityStr = obsEntities[i];
					if(oeEntityStr.equals("NULL")){
						continue;
					}
					// assign entity new id if the word has not been observed before
					int idLocal = localDictOE.getID(oeEntityStr);
					if (idLocal < 0){
						idLocal = localDictOE.addStr(oeEntityStr);
					}
					boolean addOE = true;
					
					// create mapping to global dictionary
					if (globalDictOE != null){
						//get the global id
						int idGlobal = globalDictOE.getID(oeEntityStr);
						if (idGlobal >= 0){
							lid2gidOE.put(idLocal, idGlobal);
						}else{
							addOE = false;
						}
					}else{
						addOE = false;
					}
					
					// 
					if(addOE){
						obsEntitiesVec.add(idLocal);
					}
				}
			}

			SETMDoc doc = new SETMDoc(wordsVec, salEntitiesVec, obsEntitiesVec);
			docs[idx] = doc;
			VW = localDictW.str2Id.size();
			VSE = localDictSE.str2Id.size();
			VOE = localDictOE.str2Id.size();
		}
	}
}
