package acw.setm.param;

import java.util.Vector;

import acw.setm.dao.SETMDataset;

public class SETMParamRuntime {

	/**
	 * topic distribution of documents
	 * theta[m][k]: the topic distribution of the m-th document
	 */
	public double[][] theta;

	/**
	 * topic token distribution
	 * phi[k][s]: the probability of salient entity s under topic k. Size: K x VSE (the size of salient entity vocabulary)
	 */
	public double[][] phi;

	/**
	 * topic word distribution
	 * varphi[k][w]: the probability of word w under topic k. Size: K x VW (the size of word vocabulary)
	 */
	public double[][] varphi;
	
	/**
	 * topic salient entity distribution
	 * rho[s][k]: the probability of topic k under entity s. Size: VSE x K
	 */
	public double[][] rho;
	
	/**
	 * topic all entity distribution
	 * rho[k][e]: the probability of topic k under entity e. Size: VAE x K (the size of entity vocabulary)
	 */
	public double[][] psi;

	/**
	 * topic assignments of words in each document. Size M x [number of words in the document]
	 */
	public Vector<Integer[]> y_t;
	
	/**
	 * topic assignments of salient entities in each document. Size M x [number of salient entities in the document]
	 */
	public Vector<Integer[]> z_t;
	
	/**
	 * topic assignments of all entities in each document. Size M x [number of all entities in the document]
	 */
	public Vector<Integer[]> u_t;

	/****************************************
	 * Counts part 1
	 ****************************************/
	
	/**
	 * n_s2z[s][k]: number of instances of salient entity s assigned to topic k. Size VSE x K
	 */
	public int[][] n_s2z;

	/**
	 * nsum_s2z[k]: total number of instances of salient entities assigned to topic k. Size K
	 */
	public int[] nsum_s2z;
	
	/**
	 * nd_s2z[d][k]: the number of salient entities assigned to topic k in document d. Size M x K.
	 */
	public int[][] nd_s2z;
	
	/**
	 * ndsum_s2z[d]: the number of salient entities in document d. Size M.
	 */
	public int[] ndsum_s2z;
	
	/****************************************
	 * Counts part 2
	 ****************************************/
	
	/**
	 * n_w2y[w][k]: number of instances of word w assigned to topic k. Size VW x K
	 */
	public int[][] n_w2y;

	/**
	 * nsum_w2y[k]: total number of instances of all words assigned to topic k. Size K
	 */
	public int[] nsum_w2y;
	
	/**
	 * nd_w2y[d][k]: the number of words assigned to topic k in document d. Size M x K.
	 */
	public int[][] nd_w2y;
	
	/**
	 * ndsum_w2y[d]: the number of words in document d. Size M.
	 */
	public int[] ndsum_w2y;
	
	/****************************************
	 * Counts part 3
	 ****************************************/
	/**
	 * n_e2u[e][k]: number of instances of entity e assigned to topic k. Size VAE x K
	 */
	public int[][] n_e2u;

	/**
	 * nsum_e2u[k]: total number of instances of all entities assigned to topic k. Size K
	 */
	public int[] nsum_e2u;
	
	/**
	 * nd_e2u[d][k]: the number of all entities assigned to topic k in document d. Size M x K.
	 */
	public int[][] nd_e2u;
	
	/**
	 * ndsum_e2u[d]: the number of all entities in document d. Size M.
	 */
	public int[] ndsum_e2u;
	
	/**
	 * nsum_z2s[s]: total number of instances of all topics salient entity s is assigned to. Size VSE
	 */
	public int[] nsum_z2s;
	
	/**
	 * K dimensional temporary variable
	 */
	public double [] p_z;

	/****************************************
	 *  Random Initialization
	 ****************************************/

	//	public void initInfUsingEFMDataset(EFTMDataset data, int F, int K){
	//		randomAssignment2NewData(data, F, K);
	//	}

	public void randomAssignment2NewData(SETMDataset data, int K){
		/*
		 * random assignment of count variables
		 */
		int m, n, topic, word, salEntity, allEntity;
		for (m = 0; m < data.M; m++){
			/*
			 *  initialize topic assignment of words
			 */
			int N_d = data.docs[m].words.length;
			Integer[] wTopics = new Integer[N_d];
			for (n = 0; n < N_d; n++){
				word = data.docs[m].words[n];
				// randomly assign a topic to the current token
				topic = (int)Math.floor(Math.random() * K);
				wTopics[n] = topic;
				// update w2y counts
				n_w2y[word][topic] += 1;
				nsum_w2y[topic] += 1;
				// update document topic counts
				nd_w2y[m][topic] += 1;
				ndsum_w2y[m] += 1;
			}
			y_t.set(m, wTopics);
			
			/*
			 *  initialize topic assignment of salient entities
			 */
			if(data.docs[m].seCount > 0){
				int S_d = data.docs[m].seCount;
				Integer[] seTopics = new Integer[S_d];
				for (n = 0; n < S_d; n++){
					salEntity = data.docs[m].salEntities[n];
					// randomly assign a topic to the current token
					topic = (int)Math.floor(Math.random() * K);
					seTopics[n] = topic;
					// update w2y counts
					n_s2z[salEntity][topic] += 1;
					nsum_s2z[topic] += 1;
					// update document topic counts
					nd_s2z[m][topic] += 1;
					ndsum_s2z[m] += 1;
					nsum_z2s[salEntity] += 1;
				}
				z_t.set(m, seTopics);
			}else{
				z_t.set(m, null);
			}
			
			/*
			 *  initialize topic assignment of all entities
			 */
			if(data.docs[m].oeCount > 0){
				int E_d = data.docs[m].obsEntities.length;
				Integer[] oeTopics = new Integer[E_d];
				for (n = 0; n < E_d; n++){
					allEntity = data.docs[m].obsEntities[n];
					// randomly assign a topic to the current token
					topic = (int)Math.floor(Math.random() * K);
					oeTopics[n] = topic;
					// update w2y counts
					n_e2u[allEntity][topic] += 1;
					nsum_e2u[topic] += 1;
					// update document topic counts
					nd_e2u[m][topic] += 1;
					ndsum_e2u[m] += 1;
				}
				u_t.set(m, oeTopics);
			}else{
				u_t.set(m, null);
			}
		}
	}

	/****************************************
	 *  Initialization using existing data
	 ****************************************/

	public void initInfUsingSASDataset(SETMDataset data, int K){
		randomAssignment2NewData(data, K);
	}

	/****************************************
	 *  Initialization of Static Parameters
	 ****************************************/

	public void initUsingStaticParam(SETMParamStatic paramStatic){
		allocateSpace4Variables(paramStatic.M, paramStatic.K, paramStatic.VW, paramStatic.VSE, paramStatic.VOE);
		initTopicAssignments(paramStatic.M);
	}
	
	public void readCounts(SETMDataset data, SETMParamStatic paramStatic){
		allocateSpace4Variables(paramStatic.M, paramStatic.K, paramStatic.VW, paramStatic.VSE, paramStatic.VOE);
		
		for (int m = 0; m < paramStatic.M; m++){
	    	int N_d = data.docs[m].words.length;
	    	
	    	// assign values for nw, nd, nwsum, and ndsum
	    	for (int n = 0; n < N_d; n++){
	    		int w = data.docs[m].words[n];
	    		int topic = (Integer)y_t.get(m)[n];
				// update w2y counts
				n_w2y[w][topic] += 1;
				nsum_w2y[topic] += 1;
				// update document topic counts
				nd_w2y[m][topic] += 1;
				ndsum_w2y[m] += 1;
	    	}
	    	// total number of words in document i
	    	ndsum_w2y[m] = N_d;
	    	
	    	if(data.docs[m].oeCount > 0){
				int E_d = data.docs[m].oeCount;
				Integer[] oeTopics = new Integer[E_d];
				for (int n = 0; n < E_d; n++){
					int oe = data.docs[m].obsEntities[n];
					int topic = (Integer)u_t.get(m)[n];
					// randomly assign a topic to the current token
					// update w2y counts
					n_e2u[oe][topic] += 1;
					nsum_e2u[topic] += 1;
					// update document topic counts
					nd_e2u[m][topic] += 1;
					ndsum_e2u[m] += 1;
				}
				u_t.set(m, oeTopics);
			}else{
				u_t.set(m, null);
			}
	    	
	    }
	    
	    theta = new double[paramStatic.M][paramStatic.K];
	    phi = new double[paramStatic.K][paramStatic.VW];
	}

	public void allocateSpace4Variables(int M, int K, int VW, int VSE, int VAE){
		int m, k, w, s, e;
		
		/*
		 * counts related to salient entities
		 */
		n_s2z = new int[VSE][K];
		for (s = 0; s < VSE; s++){
			for (k = 0; k < K; k++){
				n_s2z[s][k] = 0;
			}
		}

		nsum_s2z = new int[K];
		for (k = 0; k < K; k++){
			nsum_s2z[k] = 0;
		}
		
		nd_s2z = new int[M][K];
		for (m = 0; m < M; m++){
			for (k = 0; k < K; k++){
				nd_s2z[m][k] = 0;
			}
		}
		ndsum_s2z = new int[M];
		for (m = 0; m < M; m++){
			ndsum_s2z[m] = 0;
		}
		
		/*
		 * counts related to all entities
		 */
		n_e2u = new int[VAE][K];
		for (e = 0; e < VAE; e++){
			for (k = 0; k < K; k++){
				n_e2u[e][k] = 0;
			}
		}

		nsum_e2u = new int[K];
		for (k = 0; k < K; k++){
			nsum_e2u[k] = 0;
		}
		
		nd_e2u = new int[M][K];
		for (m = 0; m < M; m++){
			for (k = 0; k < K; k++){
				nd_e2u[m][k] = 0;
			}
		}
		ndsum_e2u = new int[M];
		for (m = 0; m < M; m++){
			ndsum_e2u[m] = 0;
		}
		
		/*
		 * counts related to words
		 */
		n_w2y = new int[VW][K];
		for (w = 0; w < VW; w++){
			for (k = 0; k < K; k++){
				n_w2y[w][k] = 0;
			}
		}

		nsum_w2y = new int[K];
		for (k = 0; k < K; k++){
			nsum_w2y[k] = 0;
		}
		
		nd_w2y = new int[M][K];
		for (m = 0; m < M; m++){
			for (k = 0; k < K; k++){
				nd_w2y[m][k] = 0;
			}
		}
		ndsum_w2y = new int[M];
		for (m = 0; m < M; m++){
			ndsum_w2y[m] = 0;
		}
		
		nsum_z2s = new int[VSE];
		for (s = 0; s < VSE; s++){
			nsum_z2s[s] = 0;
		}

		p_z = new double[K];

		theta = new double[M][K];
		phi = new double[K][VSE];
		psi = new double[K][VAE];
		varphi = new double[K][VW];
	}
	
	public void initTopicAssignments(int M){
		z_t = new Vector<Integer[]>();
		for (int m = 0; m < M; m++) {
			z_t.add(null);
		}
		
		y_t = new Vector<Integer[]>();
		for (int m = 0; m < M; m++) {
			y_t.add(null);
		}
		
		u_t = new Vector<Integer[]>();
		for (int m = 0; m < M; m++) {
			u_t.add(null);
		}
	}
}
