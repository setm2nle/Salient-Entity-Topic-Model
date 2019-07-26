package acw.setm.perplexity;

import java.io.IOException;

/*
 * Perplexity calculation for SETM
 */
public class SETMPerplexity {

	private int K; // number of topics in SETM
	private double lambda;
	private double[][] rho; // salient-entity topic distribution in SETM
	private double[][] varphiW; // topic word distribution in SETM
	private double[][] psiE; // topic observed-entity distribution in SETM

	public SETMPerplexity(int K, double lambda, double[][] rho, double[][] varphiW, double[][] psiE){
		this.K = K;
		this.lambda = lambda;
		this.rho = rho;
		this.varphiW = varphiW;
		this.psiE = psiE;
	}

	public double[] computeSETMPerplexity4Doc(double[] docTopicDist, int[] salEntities, int[] words, int[] observedEntities) throws IOException{
		/*
		 * calculate the average entity topic distribution
		 */
		double[] Phi = new double[K];
		if(salEntities != null){
			/*
			 * Combine with document topic distribution
			 */
			double[] entityTopicDistSum = new double[K];
			for (int i = 0; i < entityTopicDistSum.length; i++) {
				entityTopicDistSum[i] = 0;
			}
			for (int i = 0; i < salEntities.length; i++) {
				int entity = salEntities[i];
				double[] entityTopicDist = rho[entity];
				for (int k = 0; k < K; k++) {
					entityTopicDistSum[k] += entityTopicDist[k];
				}
			}
			for (int i = 0; i < entityTopicDistSum.length; i++) {
				entityTopicDistSum[i] = entityTopicDistSum[i] / salEntities.length;
			}
			for (int k = 0; k < K; k++) {
				Phi[k] = lambda * docTopicDist[k] + (1 - lambda) * entityTopicDistSum[k];
			}
		}else{
			Phi = docTopicDist;
		}

		//perplexity of words
		double wordProbSum = 0.0;
		int wordCount = 0;
		if(words != null){
			wordCount = words.length;
			for (int i = 0; i < words.length; i++) {
				int word = words[i];
				double wordProbability = 0.0;
	
				// for each topic
				for (int k = 0; k < K; k++) {
					// obtain the probability of the current word under the given topic
					double p_w_z = varphiW[k][word];
					// obtain the probability of the given topic in the current document
					double p_z_Phi = Phi[k];
					// obtain: p(z|d) * p(w|z)
					wordProbability += p_w_z * p_z_Phi;
				}
				wordProbSum += wordProbability;
			}
			if(wordProbSum > words.length){
				System.out.println("ERROR: sum more than words length.! " + wordProbSum + "," + words.length);
			}
		}

		double entityProbSum = 0.0;
		int oeCount = 0;
		if(observedEntities != null){
			oeCount = observedEntities.length;
			for(int i = 0; i < observedEntities.length; i++) {
				int entity = observedEntities[i];
				double entityProbability = 0.0;

				// for each topic
				for (int k = 0; k < K; k++) {
					// obtain the probability of the current entity under the given topic
					double p_e_z = psiE[k][entity];
					// obtain the probability of the given topic in the current document
					double p_z_Phi = Phi[k];
					// obtain: p(z|\Phi) * p(w|z)
					entityProbability += p_e_z * p_z_Phi;
				}
				entityProbSum += entityProbability;
			}
		}

		double[] docPerplexityWELength = new double[4];
		docPerplexityWELength[0] = wordProbSum;
		docPerplexityWELength[1] = wordCount;
		docPerplexityWELength[2] = entityProbSum;
		docPerplexityWELength[3] = oeCount;
		return docPerplexityWELength;
	}
}
