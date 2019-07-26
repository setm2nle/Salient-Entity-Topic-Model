/*
 * Copyright (C) 2007 by
 * 
 * 	Xuan-Hieu Phan
 *	hieuxuan@ecei.tohoku.ac.jp or pxhieu@gmail.com
 * 	Graduate School of Information Sciences
 * 	Tohoku University
 * 
 *  Cam-Tu Nguyen
 *  ncamtu@gmail.com
 *  College of Technology
 *  Vietnam National University, Hanoi
 *
 * JGibbsLDA is a free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * by the Free Software Foundation; either version 2 of the License,
 * or (at your option) any later version.
 *
 * JGibbsLDA is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with JGibbsLDA; if not, write to the Free Software Foundation,
 * Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 */

package acw.setm.dao;

import java.util.Vector;

public class SETMDoc {
	/**
	 * all tokens of the document
	 */
	public int[] words;
	
	/**
	 * salient entities of the document
	 */
	public int[] salEntities;
	
	/**
	 * all entities of the document
	 */
	public int[] obsEntities;
	
	/**
	 * the number of words in the document
	 */
	public int wCount;
	
	/**
	 * the number of salient entities in the document
	 */
	public int seCount;
	
	/**
	 * the number of all entities in the document
	 */
	public int oeCount;
	
	public SETMDoc(){
		words = null;
		salEntities = null;
		obsEntities = null;
		wCount = -1;
		seCount = -1;
		oeCount = -1;
	}
	
	public SETMDoc(int[] words, int[] salEntities, int[] obsEntities, int wCount, int seCount, int oeCount){
		this.words = words;
		this.salEntities = salEntities;
		this.obsEntities = obsEntities;
		this.wCount = wCount;
		this.seCount = seCount;
		this.oeCount = oeCount;
	}
	
	public SETMDoc(Vector<Integer> words, Vector<Integer> salEntities, Vector<Integer> obsEntities){
		//
		this.words = new int[words.size()];
		for (int i = 0; i < words.size(); i++) {
			this.words[i] = words.get(i);
		}
		this.wCount = words.size();
		//
		if(salEntities != null){
			this.salEntities = new int[salEntities.size()];
			for (int i = 0; i < salEntities.size(); i++) {
				this.salEntities[i] = salEntities.get(i);
			}
			this.seCount = salEntities.size();
		}else{
			this.salEntities = null;
			this.seCount = 0;
		}
		//
		if(obsEntities != null){
			this.obsEntities = new int[obsEntities.size()];
			for (int i = 0; i < obsEntities.size(); i++) {
				this.obsEntities[i] = obsEntities.get(i);
			}
			this.oeCount = obsEntities.size();
		}else{
			this.oeCount = 0;
			this.obsEntities = null;
		}
	}
}
