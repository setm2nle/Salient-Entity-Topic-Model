package acw.setmwo.dao;

import java.util.Vector;

public class SETMWODoc {
	/**
	 * all tokens of the document
	 */
	public int[] words;
	
	/**
	 * all salient entities of the document
	 */
	public int[] entities;
	
	/**
	 * the number of words in the document
	 */
	public int wCount;
	/**
	 * the number of entities in the document
	 */
	public int eCount;
	
	public SETMWODoc(){
		words = null;
		entities = null;
		wCount = -1;
		eCount = -1;
	}
	
	public SETMWODoc(int[] words, int[] entities, int wCount, int eCount){
		this.words = words;
		this.entities = entities;
		this.wCount = wCount;
		this.eCount = eCount;
	}
	
	public SETMWODoc(Vector<Integer> words, Vector<Integer> entities){
		//
		this.words = new int[words.size()];
		for (int i = 0; i < words.size(); i++) {
			this.words[i] = words.get(i);
		}
		this.wCount = words.size();
		//
		if(entities != null){
			this.entities = new int[entities.size()];
			for (int i = 0; i < entities.size(); i++) {
				this.entities[i] = entities.get(i);
			}
			this.eCount = entities.size();
		}else{
			this.entities = null;
			this.eCount = 0;
		}
	}
}
