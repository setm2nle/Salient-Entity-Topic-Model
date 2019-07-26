package acw.setmwo.param;

import java.util.Vector;

import acw.setmwo.dao.SETMWODataset;

public class SETMWOParamRuntime {

	/**
	 * topic distribution of documents
	 * theta[m][k]: the topic distribution of the m-th document
	 */
	public double[][] theta;

	/**
	 * topic token distribution
	 * phi[k][e]: the probability of entity e under topic k. Size: K x VE (the size of entity vocabulary)
	 */
	public double[][] phi;

	/**
	 * topic entity distribution
	 * varphi[k][w]: the probability of word w under topic k. Size: K x VW (the size of word vocabulary)
	 */
	public double[][] varphi;
	
	/**
	 * topic entity distribution
	 * rho[e][k]: the probability of topic k under entity e. Size: VE x K (the size of entity vocabulary)
	 */
	public double[][] rho;

	/**
	 * topic assignments of entities in each document. Size M x [number of entities in the document]
	 */
	public Vector<Integer[]> z_t;
	
	/**
	 * topic assignments of words in each document. Size M x [number of words in the document]
	 */
	public Vector<Integer[]> y_t;

	/****************************************
	 * Counts part 1
	 ****************************************/
	
	/**
	 * n_e2z[e][k]: number of instances of entity e assigned to topic k. Size VE x K
	 */
	public int[][] n_e2z;

	/**
	 * nsum_e2z[k]: total number of instances of all entities assigned to topic k. Size K
	 */
	public int[] nsum_e2z;
	
	/**
	 * nd_e2z[d][k]: the number of entities assigned to topic k in document d. Size M x K.
	 */
	public int[][] nd_e2z;
	
	/**
	 * ndsum_e2z[d]: the number of entities in document d. Size M.
	 */
	public int[] ndsum_e2z;
	
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
	
	/**
	 * nsum_z2e[e]: total number of instances of all topics entity e is assigned to. Size VE
	 */
	public int[] nsum_z2e;
	
	
	/**
	 * K dimensional temporary variable
	 */
	public double [] p_z;

	/****************************************
	 *  Random Initialization
	 ****************************************/

	public void randomAssignment2NewData(SETMWODataset data, int K){
		/*
		 * random assignment of count variables
		 */
		int m, n, topic, word, entity;
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
			 *  initialize topic assignment of entities
			 */
			if(data.docs[m].entities != null){
				int E_d = data.docs[m].entities.length;
				Integer[] eTopics = new Integer[E_d];
				for (n = 0; n < E_d; n++){
					entity = data.docs[m].entities[n];
					// randomly assign a topic to the current token
					topic = (int)Math.floor(Math.random() * K);
					eTopics[n] = topic;
					// update w2y counts
					n_e2z[entity][topic] += 1;
					nsum_e2z[topic] += 1;
					// update document topic counts
					nd_e2z[m][topic] += 1;
					ndsum_e2z[m] += 1;
					nsum_z2e[entity] += 1;
				}
				z_t.set(m, eTopics);
			}else{
				z_t.set(m, null);
			}
			
		}
	}

	/****************************************
	 *  Initialization using existing data
	 ****************************************/

	public void initInfUsingDataset(SETMWODataset data, int K){
		randomAssignment2NewData(data, K);
	}
	
	public void readCounts(SETMWODataset data, SETMWOParamStatic paramStatic){
		allocateSpace4Variables(paramStatic.M, paramStatic.K, paramStatic.VW, paramStatic.VE);
		
		for (int m = 0; m < paramStatic.M; m++){
	    	int N_d = data.docs[m].words.length;
	    	
	    	// assign values for nw, nd, nwsum, and ndsum
	    	for (int n = 0; n < N_d; n++){
	    		int w = data.docs[m].words[n];
	    		int topic = (Integer)y_t.get(m)[n];
	    		
	    		// number of instances of word i assigned to topic j
	    		n_w2y[w][topic] += 1;
	    		// number of words in document i assigned to topic j
	    		nd_w2y[m][topic] += 1;
	    		// total number of words assigned to topic j
	    		nsum_w2y[topic] += 1;
	    	}
	    	// total number of words in document i
	    	ndsum_w2y[m] = N_d;
	    }
	    
	    theta = new double[paramStatic.M][paramStatic.K];
	    phi = new double[paramStatic.K][paramStatic.VW];
	}
	

	/****************************************
	 *  Initialization of Static Parameters
	 ****************************************/

	public void initUsingStaticParam(SETMWOParamStatic paramStatic){
		allocateSpace4Variables(paramStatic.M, paramStatic.K, paramStatic.VW, paramStatic.VE);
		initTopicAssignments(paramStatic.M);
	}

	public void allocateSpace4Variables(int M, int K, int VW, int VE){
		int m, k, w, e;
		
		n_e2z = new int[VE][K];
		for (e = 0; e < VE; e++){
			for (k = 0; k < K; k++){
				n_e2z[e][k] = 0;
			}
		}

		nsum_e2z = new int[K];
		for (k = 0; k < K; k++){
			nsum_e2z[k] = 0;
		}
		
		nd_e2z = new int[M][K];
		for (m = 0; m < M; m++){
			for (k = 0; k < K; k++){
				nd_e2z[m][k] = 0;
			}
		}
		ndsum_e2z = new int[M];
		for (m = 0; m < M; m++){
			ndsum_e2z[m] = 0;
		}
		
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
		
		nsum_z2e = new int[VE];
		for (e = 0; e < VE; e++){
			nsum_z2e[e] = 0;
		}

		p_z = new double[K];

		theta = new double[M][K];
		phi = new double[K][VE];
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
	}
}
