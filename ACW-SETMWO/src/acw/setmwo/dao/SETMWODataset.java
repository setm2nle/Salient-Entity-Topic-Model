package acw.setmwo.dao;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import acw.common.utils.collection.StringIdDualDict;

public class SETMWODataset {

	/**
	 * separator used to separate document words and entities
	 */
	public static String docWEeparator = " ";

	/**
	 * separator used to separate document salient entities
	 */
	public static String docSESeparator = " ";

	/**
	 * local dictionary for words and entities
	 */
	public StringIdDualDict localDictWE;

	/**
	 * local dictionary for entities
	 */
	public StringIdDualDict localDictSE;

	/**
	 * a list of documents in the dataset
	 */

	public SETMWODoc [] docs;
	/**
	 * number of documents
	 */
	public int M; 

	/**
	 * number of tokens
	 */
	public int VWE;

	/**
	 * number of entities
	 */
	public int VSE;

	public Map<Integer, Integer> lid2gidWE = null;
	public Map<Integer, Integer> lid2gidSE = null;
	public StringIdDualDict globalDictWE = null;
	public StringIdDualDict globalDictSE = null;

	public SETMWODataset(int M){
		localDictWE = new StringIdDualDict();
		localDictSE = new StringIdDualDict();

		this.M = M;
		this.VWE = 0;
		this.VSE = 0;
		docs = new SETMWODoc[M];

		lid2gidWE = new HashMap<Integer, Integer>();
		lid2gidSE = new HashMap<Integer, Integer>();
	}

	/**
	 * set the document at the index idx if idx is greater than 0 and less than M
	 * @param doc document to be set
	 * @param idx index in the document array
	 */	
	public void setDoc(SETMWODoc doc, int idx){
		if (0 <= idx && idx < M){
			docs[idx] = doc;
		}
	}

	public void setDoc(String wordsStr, String seStr, int idx){
		if (0 <= idx && idx < M){
			// read words and entities
			String [] words = wordsStr.split(docWEeparator);
			Vector<Integer> weVec = new Vector<Integer>();

			// read document words
			for (int i = 0; i < words.length; i++) {
				String wordStr = words[i];
				// assign word new id if the word has not been observed before
				int idLocal = localDictWE.getID(wordStr);
				if(idLocal < 0){
					idLocal = localDictWE.addStr(wordStr);
				}
				boolean addWord = true;

				// create mapping to global dictionary
				if (globalDictWE != null){
					//get the global id
					int idGlobal = globalDictWE.getID(wordStr);
					if (idGlobal >= 0){
						lid2gidWE.put(idLocal, idGlobal);
					}else{
						addWord = false;
					}
				}else{
					addWord = false;
				}
				
				if(addWord){
					weVec.add(idLocal);
				}
			}

			// read salient entities
			Vector<Integer> seVec = null;
			if(!seStr.equals("NULL")){
				String [] seEntities = seStr.split(docSESeparator);
				seVec = new Vector<Integer>();
				// read document normal entities
				for (int i = 0; i < seEntities.length; i++) {
					String seEntityStr = seEntities[i];
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
					
					// add if necessary
					if(addSE){
						seVec.add(idLocal);
					}
				}
			}

			SETMWODoc doc = new SETMWODoc(weVec, seVec);
			docs[idx] = doc;
			VWE = localDictWE.str2Id.size();
			VSE = localDictSE.str2Id.size();
		}
	}
}
