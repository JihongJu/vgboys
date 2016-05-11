package neuroevo;
import java.util.ArrayList;

//Very much temporary to test correct functioning of genetic algorithm on fake problem (score is sum of weights)
public class TestGeneticsTemp {

	public static void main(String[] args) {
		ArrayList<Integer> fakeLayout=new ArrayList<Integer>();
		fakeLayout.add(5);
		fakeLayout.add(3);
		fakeLayout.add(5);
		int populationSize=20;
		Genetics testGenetics= new Genetics(fakeLayout,populationSize);
		int iterationMax=20;
		ArrayList<Double> bestScores=new ArrayList<Double>();
		ArrayList<Double> averageScores=new ArrayList<Double>();
		for(int iter=0;iter<iterationMax;iter++){
			ArrayList<Double> scores=new ArrayList<Double>();
			double curBest=Double.MIN_VALUE;
			for(int i=0;i<populationSize;i++){
				ArrayList<Double> currentWeights=testGenetics.getNetwork(i);
				double score=0;
				for(int j=0;j<currentWeights.size();j++){
					score+=currentWeights.get(j);
				}
				if(curBest<score){
					curBest=score;
				}
				scores.add(score);
			}
			System.out.println(curBest);
			testGenetics.produceNextGeneration(scores);
		}

	}

}
