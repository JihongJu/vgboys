package neuroevo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;


public class Genetics {
	private ArrayList<Integer> layout;
	private ArrayList<ArrayList<Double>> populationWeights;//Can be the genotype, can also be mapped from bitmap
	private int populationSize;
	private double mutationChance,crossoverChance;
	private double mutationStandardDev;
	private int layerAmount;
	private Random randomGenerator;
	private int tournamentSize;//How many are compared in each round of the tournament
	private ArrayList<Integer> tournamentHelperList;//A list of all numbers to allow shuffeling and taking the first few as selected for the tournament
	private boolean elitism;//Always keep the best individual between generations?
	private int curGeneration;
	
	public Genetics(ArrayList<Integer> layout,int populationSize){
		mutationStandardDev=0.1;
		curGeneration=0;
		elitism=true;
		randomGenerator = new Random();
		mutationChance=0.2;
		crossoverChance=0;
		tournamentSize=4;
		this.layout = layout;
		this.layerAmount=layout.size();
		this.populationSize=populationSize;
		populationWeights=new ArrayList<ArrayList<Double>>(populationSize);
		tournamentHelperList= new ArrayList<Integer>(populationSize);
		for(int i=0;i<populationSize;i++){
			populationWeights.add(createNewWeights());
			tournamentHelperList.add(i);
		}
		if(tournamentSize>populationSize){
			throw new IllegalArgumentException("Tournament size larger than population size");//Assuming we make that a constructor parameter later
		}
	}
	
	//Creates weight arrays for initial population
	private ArrayList<Double> createNewWeights(){
		ArrayList<Double> weightArray=new ArrayList<Double>();
		for(int layer=0;layer<(layerAmount-1);layer++){
			int nodesCurLayer=layout.get(layer);
			int nodesNextLayer=layout.get(layer+1);
			int connectionAmount=nodesCurLayer*nodesNextLayer;
			for(int connection=0;connection<connectionAmount;connection++){
				double weight = randomGenerator.nextDouble()*2-1;
				weightArray.add(weight);
			}
		}
		return weightArray;
	}
	
	//Gives network weights corresponding to certain index
	//The idea is that a main controller gets networks from the genetics part, it runs them on the game, get a list of scores back
	//And then sends those scores to the genetics part to get a new generation
	public ArrayList<Double> getNetwork(int i){
		return populationWeights.get(i);
	}
	
	//Called after a round of computing scores, this creates a next generation based on the scores for each individual
	public void produceNextGeneration(ArrayList<Double> scores){
		curGeneration+=1;
		if(scores.size()==populationSize){
			ArrayList<ArrayList<Double>> nextPopulationWeights=new ArrayList<ArrayList<Double>>(populationSize);
			int indexStart=0;//Where to start normal generation, depends on elitism
			if(elitism){//Keep room for the best of the previous generation
				indexStart=1;
			}
			for(int i=indexStart;i<populationSize;i++){
				nextPopulationWeights.add(mutate(populationWeights.get(tournamentWinnerIndex(scores))));//Select populationSize individuals
			}
			
			if(elitism){//Add the previous best unaltered to the new generation
				nextPopulationWeights.add(populationWeights.get(highestScoreIndex(scores)));
			}
			populationWeights=nextPopulationWeights;
			return;
		}
		else{
			throw new IllegalArgumentException("Too few scores given to create new generation in"+curGeneration);
		}
		
	}
	
	//TODO nicer implementation of this thing, just rewrite it
	private ArrayList<Double> mutate(ArrayList<Double> currentWeights){
		ArrayList<Double> mutated = new ArrayList<Double>();
		for(int i=0;i<currentWeights.size();i++){
			double mutationAmount=0;
			if(randomGenerator.nextDouble()<mutationChance){
				mutationAmount=randomGenerator.nextGaussian()*mutationStandardDev;
				mutated.add(currentWeights.get(i)+mutationAmount);
			}
			else{
				mutated.add(currentWeights.get(i));
			}
		}
		return mutated;
	}
	
	//Select the individual to propogate through tournament selection
	private int tournamentWinnerIndex(ArrayList<Double> scores){
		Collections.shuffle(tournamentHelperList);
		double maxScore=Double.NEGATIVE_INFINITY;
		int maxScoreIndex=-1;
		for(int i=0;i<tournamentSize;i++){
			int index=tournamentHelperList.get(i);
			double score=scores.get(index);
			if(score>maxScore){
				maxScore=score;
				maxScoreIndex=index;
			}
		}
		if(maxScoreIndex==-1){
			throw new IllegalArgumentException("Could not find best scoring individual in tournament in generation"+curGeneration);
		}
		return maxScoreIndex;
	}
	
	//Gets the index of the current best performer for use in elitism
	private int highestScoreIndex(ArrayList<Double> scores){
		int bestIndex=-1;
		double bestScore=Double.NEGATIVE_INFINITY;
		for(int i=0;i<populationSize;i++){
			double scoreExamined=scores.get(i);
			if(scoreExamined>bestScore){
				bestIndex=i;
				bestScore=scoreExamined;
			}
		}
		if(bestIndex==-1){
			throw new IllegalArgumentException("Could not find best scoring individual in generation"+curGeneration);
		}
		return bestIndex;
	}

}
