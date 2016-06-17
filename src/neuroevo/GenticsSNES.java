package neuroevo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;


public class GenticsSNES {
	private ArrayList<Integer> layout;
	private ArrayList<ArrayList<Double>> populationWeights,populationNormalSamples;//Can be the genotype, can also be mapped from bitmap
	private int populationSize;
	private int layerAmount;
	private Random randomGenerator;
	private int curGeneration;
	private ArrayList<Double> weightMean,weightSigma;
	private int weightAmount;
	private double learningRateMeans,learningRateSigmas;
	private int nextPopToSend;
	
	public GenticsSNES(ArrayList<Integer> layout,int populationSize, double learningRateMeans,double learningRateSigmas){
		this.learningRateMeans=learningRateMeans;
		this.learningRateSigmas=learningRateSigmas;
		curGeneration=0;
		nextPopToSend=0;
		randomGenerator = new Random();
		this.layout = layout;
		this.layerAmount=layout.size();
		this.populationSize=populationSize;
		populationWeights=new ArrayList<ArrayList<Double>>(populationSize);
		weightAmount=0;
		for(int layer=0;layer<(layerAmount-1);layer++){
			weightAmount+=layout.get(layer)*layout.get(layer+1);
		}
		//TODO define good start values
		double startMean=0;
		double startSigma=0.4;
		weightMean=new ArrayList<Double>(Collections.nCopies(weightAmount, startMean));
		weightSigma=new ArrayList<Double>(Collections.nCopies(weightAmount, startSigma));
		
		sampleNewPopulation();
	}
	
	
	//Samples a new population from current parameters
	private void sampleNewPopulation(){
		ArrayList<ArrayList<Double>> newPopulation=new ArrayList<ArrayList<Double>>(populationSize);
		ArrayList<ArrayList<Double>> newPopulationNormalSamples=new ArrayList<ArrayList<Double>>(populationSize);
		for(int i=0;i<populationSize;i++){
			ArrayList<Double> weightArray=new ArrayList<Double>();
			ArrayList<Double> normalSamplesArray=new ArrayList<Double>();
			for(int j=0;j<weightAmount;j++){
				double normalSample=randomGenerator.nextGaussian();
				normalSamplesArray.add(normalSample);
				double newWeight=weightMean.get(j)+normalSample*weightSigma.get(j);
				weightArray.add(newWeight);
			}
			newPopulationNormalSamples.add(normalSamplesArray);
			newPopulation.add(weightArray);
		}
		populationNormalSamples=newPopulationNormalSamples;
		populationWeights=newPopulation;
	}
	
	
	
	
	//Gives network weights corresponding to certain index
	//The idea is that a main controller gets networks from the genetics part, it runs them on the game, get a list of scores back
	//And then sends those scores to the genetics part to get a new generation
	public ArrayList<Double> getNetwork(int i){
		return populationWeights.get(i);
	}
	
	
	//For getting the next weights to the network in the agent creation
	public ArrayList<Double> getNextNetwork(){
		if(nextPopToSend>populationSize){
			try {
				throw new Exception("Too many new networks requested");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		ArrayList<Double> weightsToSend=populationWeights.get(nextPopToSend);
		nextPopToSend=nextPopToSend+1;
		return weightsToSend;
	}
	
	public ArrayList<Integer> getLayout(){
		return layout;
	}
	
	//Called after a round of computing scores, this creates a next generation based on the scores for each individual
	public void produceNextGeneration(ArrayList<Double> scores){
		nextPopToSend=0;
		curGeneration+=1;
		if(scores.size()==populationSize){
			//Double[] utilities=getUtilitiesScoreNormalised(scores);
			//Double[] utilities=getUtilitiesExponential(scores,1.0);
			//Double[] utilities=getUtilitiesScoreItself(scores);
			//Double[] utilities=getUtilitiesRankedSigmoid(scores);
			Double[] utilities=getUtilitiesLinear(scores);


			Double[] weightGradients=new Double[weightAmount];
			Double[] sigmaGradients=new Double[weightAmount];
			Arrays.fill(weightGradients,0.0);
			Arrays.fill(sigmaGradients,0.0);
			for(int i=0;i<populationSize;i++){
				double curUtil=utilities[i];
				ArrayList<Double> curSamplesArray=populationNormalSamples.get(i);
				for(int j=0;j<weightAmount;j++){
					weightGradients[j]+=curUtil*(curSamplesArray.get(j));
					sigmaGradients[j]+=curUtil*(curSamplesArray.get(j)*curSamplesArray.get(j)-1);
				}
			}
			
			for(int i=0;i<weightAmount;i++){
				//learningRateSigmas
				double newMean=weightMean.get(i)+learningRateMeans*weightSigma.get(i)*weightGradients[i];
				double newSigma=weightSigma.get(i)*Math.exp(0.5*learningRateSigmas*sigmaGradients[i]);
				weightMean.set(i, newMean);
				weightSigma.set(i, newSigma);
			}
			sampleNewPopulation();
			return;
		}
		else{
			throw new IllegalArgumentException("Too few scores given to create new generation in"+curGeneration);
		}
	}
	
	/*
	private Double[] getUtilitiesLinear(ArrayList<Double> scores){
		IndexSortingComparator comparator = new IndexSortingComparator(scores);
		Integer[] indexes = comparator.createIndexArray();
		Arrays.sort(indexes, comparator);
		Double[] utilities=new Double[populationSize];
		for(int i=0;i<populationSize;i++){
			utilities[indexes[i]]=(double) (populationSize-i)/populationSize;
		}
		return utilities;
	}
	
	private Double[] getUtilitiesExponential(ArrayList<Double> scores,double powValue){
		IndexSortingComparator comparator = new IndexSortingComparator(scores);
		Integer[] indexes = comparator.createIndexArray();
		Arrays.sort(indexes, comparator);
		Double[] utilities=new Double[populationSize];
		double divisionValue=Math.pow(1.0*populationSize,powValue);
		for(int i=0;i<populationSize;i++){
			utilities[indexes[i]]=(double) Math.pow(1.0*(populationSize-i), powValue)/divisionValue;
		}
		return utilities;
	}
	
	private Double[] getUtilitiesRankedSigmoid(ArrayList<Double> scores){
		IndexSortingComparator comparator = new IndexSortingComparator(scores);
		Integer[] indexes = comparator.createIndexArray();
		Arrays.sort(indexes, comparator);
		Double[] utilities=new Double[populationSize];
		for(int i=0;i<populationSize;i++){
			utilities[indexes[i]]=(double) 1/(1+Math.exp(-(0.5*populationSize-i)/populationSize));
		}
		return utilities;
	}
	*/
	
	private Double[] getUtilitiesLinear(ArrayList<Double> scores){
		Double[] rank=getRanking(scores);
		Double[] utilities=new Double[populationSize];
		for(int i=0;i<populationSize;i++){
			utilities[i]=(double) (rank[i])/populationSize;
		}
		return utilities;
	}
	
	private Double[] getUtilitiesExponential(ArrayList<Double> scores,double powValue){
		Double[] rank=getRanking(scores);
		Double[] utilities=new Double[populationSize];
		double divisionValue=Math.pow(1.0*populationSize,powValue);
		for(int i=0;i<populationSize;i++){
			utilities[i]=(double) Math.pow(1.0*(rank[i]), powValue)/divisionValue;
		}
		return utilities;
	}
	
	private Double[] getUtilitiesRankedSigmoid(ArrayList<Double> scores){
		Double[] rank=getRanking(scores);
		Double[] utilities=new Double[populationSize];
		for(int i=0;i<populationSize;i++){
			utilities[i]=(double) 1/(1+Math.exp(-(rank[i]-0.5*populationSize)/populationSize));
		}
		return utilities;
	}
	
	private Double[] getUtilitiesScoreNormalised(ArrayList<Double> scores){
		//IndexSortingComparator comparator = new IndexSortingComparator(scores);
		//Integer[] indexes = comparator.createIndexArray();
		//Arrays.sort(indexes, comparator);
		double maximumVal=Collections.max(scores);
		double minVal=Collections.min(scores);
		double spread=maximumVal-minVal;
		double multiplicationFactor;
		if(spread>0){
			multiplicationFactor=1/spread;
		}else{
			multiplicationFactor=0.0;
		}
		Double[] utilities=new Double[populationSize];
		for(int i=0;i<populationSize;i++){
			utilities[i]=populationSize*(scores.get(i)-minVal)*multiplicationFactor;
		}
		return utilities;
	}
	
	private Double[] getUtilitiesScoreItself(ArrayList<Double> scores){
		Double[] utilities=new Double[populationSize];
		for(int i=0;i<populationSize;i++){
			utilities[i]=scores.get(i);
		}
		return utilities;
	}
	
	private Double[] getRanking(ArrayList<Double> scores){
		//This function is just here to allow one place to change in case another ranking method is going to be used
		return getRankingEqualsGetOriginal(scores);
	}
	
	private Double[] getRankingEqualsGetWorst(ArrayList<Double> scores){
		//This function is just here to allow one place to change in case another ranking method is going to be used
		IndexSortingComparator comparator = new IndexSortingComparator(scores);
		Integer[] indexes = comparator.createIndexArray();
		Arrays.sort(indexes, comparator);
		Double[] rank=new Double[populationSize];
		rank[indexes[populationSize-1]]=1.0;
		double prevRank=1.0;
		double prevVal=scores.get(indexes[populationSize-1]);
		
		
		for(int i=populationSize-2;i>=0;i--){
			if(scores.get(indexes[i])==prevVal){
				rank[indexes[i]]=prevRank;
			}
			else{
				rank[indexes[i]]=(double) populationSize-i;
				prevRank=(double) populationSize-i;
				prevVal=(double) (populationSize-i);
			}
			prevVal=scores.get(indexes[i]);
		}
		return rank;
	}
	
	private Double[] getRankingEqualsGetOriginal(ArrayList<Double> scores){
		//This function is just here to allow one place to change in case another ranking method is going to be used
		IndexSortingComparator comparator = new IndexSortingComparator(scores);
		Integer[] indexes = comparator.createIndexArray();
		Arrays.sort(indexes, comparator);
		Double[] rank=new Double[populationSize];
		rank[indexes[populationSize-1]]=1.0;		
		for(int i=populationSize-2;i>=0;i--){
			rank[indexes[i]]=(double) populationSize-i;
		}
		return rank;
	}

	
}
