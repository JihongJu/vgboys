import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import core.ArcadeMachine;
import neuroevo.GenticsSNES;
import neuroevo.Network;

/**
 * Created with IntelliJ IDEA.
 * User: Diego
 * Date: 04/10/13
 * Time: 16:29
 * This is a Java port from Tom Schaul's VGDL - https://github.com/schaul/py-vgdl
 */
public class Test
{

    public static void main(String[] args)
    {
        //Available controllers:
    	String sampleRandomController = "controllers.sampleRandom.Agent";
        String sampleOneStepController = "controllers.sampleonesteplookahead.Agent";
        String sampleMCTSController = "controllers.sampleMCTS.Agent";
        String sampleFlatMCTSController = "controllers.sampleFlatMCTS.Agent";
        String sampleOLMCTSController = "controllers.sampleOLMCTS.Agent";
        String sampleGAController = "controllers.sampleGA.Agent";
        String tester = "controllers.Tester.Agent";
        String vgboys = "vgboys.Agent";
        String neuroevo = "neuroevo.Agent";

        //Available Generators
        String randomLevelGenerator = "levelGenerators.randomLevelGenerator.LevelGenerator";
        String geneticGenerator = "levelGenerators.geneticLevelGenerator.LevelGenerator";
        String constructiveLevelGenerator = "levelGenerators.constructiveLevelGenerator.LevelGenerator";
        
        //Available games:
        String gamesPath = "examples/gridphysics/";
        String games[] = new String[]{};
        String generateLevelPath = "examples/generatedLevels/";

        //Training Set 1 (2015; CIG 2014)
        games = new String[]{"aliens", "boulderdash", "butterflies", "chase", "frogs",
                "missilecommand", "portals", "sokoban", "survivezombies", "zelda"};

        //Training Set 2 (2015; Validation CIG 2014)
        //games = new String[]{"camelRace", "digdug", "firestorms", "infection", "firecaster",
        //      "overload", "pacman", "seaquest", "whackamole", "eggomania"};

        //Training Set 3 (2015)
        //games = new String[]{"bait", "boloadventures", "brainman", "chipschallenge",  "modality",
        //                              "painter", "realportals", "realsokoban", "thecitadel", "zenpuzzle"};

        //Training Set 4 (Validation GECCO 2015, Test CIG 2014)
        //games = new String[]{"roguelike", "surround", "catapults", "plants", "plaqueattack",
        //        "jaws", "labyrinth", "boulderchase", "escape", "lemmings"};


        //Training Set 5 (Validation CIG 2015, Test GECCO 2015)
        //games = new String[]{ "solarfox", "defender", "enemycitadel", "crossfire", "lasers",
        //                               "sheriff", "chopper", "superman", "waitforbreakfast", "cakybaky"};

        //Training Set 6 (Validation CEEC 2015)
        //games = new String[]{"lasers2", "hungrybirds" ,"cookmepasta", "factorymanager", "raceBet2",
        //        "intersection", "blacksmoke", "iceandfire", "gymkhana", "tercio"};


        //Other settings
        boolean visuals = false;
//        boolean visuals = false;
        String recordActionsFile = null; //where to record the actions executed. null if not to save.
        int seed = new Random().nextInt();

        //Game and level to play
        int gameIdx = 8;
        int levelIdx = 0; //level names from 0 to 4 (game_lvlN.txt).
        
        String game = gamesPath + games[gameIdx] + ".txt";
        String level1 = gamesPath + games[gameIdx] + "_lvl" + levelIdx +".txt";
        
        String recordLevelFile = generateLevelPath +"geneticLevelGenerator/" + games[gameIdx] + "_lvl0.txt";

        // 1. This starts a game, in a level, played by a human.
        //ArcadeMachine.playOneGame(game, level1, recordActionsFile, seed);
        
        // 2. This plays a game in a level by the controller.
//        ArcadeMachine.runOneGame(game, level1, visuals, sampleMCTSController, recordActionsFile, seed);
//        ArcadeMachine.runOneGame(game, level1, visuals, tester, recordActionsFile, seed);
        int playerIDTemp=0;
        int populationSize=20;
        int iterationMax=100;
        double learningRateMeans=0.05;
        double learningRateSigmas=0.005;
        ArrayList<Integer> test_layout = new ArrayList<Integer>(Arrays.asList(23, 5, 5));
        GenticsSNES SNESWeightGenerator=new GenticsSNES(test_layout,populationSize,learningRateMeans,learningRateSigmas);
        
        Network.setGenetics(SNESWeightGenerator);
        
        //TODO The running of multiple games, sending score to the network and then learning from that.
        ArrayList<Double> bestScoresObtained=new ArrayList<Double>(iterationMax);
        ArrayList<Double> averageScoreObtained=new ArrayList<Double>(iterationMax);
        
        double globalbest = Double.NEGATIVE_INFINITY;

        for(int iter=0;iter<iterationMax;iter++){
        	System.out.println("Starting round:"+iter+" out of:"+iterationMax);
	        ArrayList<Double> scoresPopulation=new ArrayList<Double>(populationSize);
	        double curBest=Double.NEGATIVE_INFINITY;
	        double averageScores=0;
	        for(int individual=0;individual<populationSize;individual++){
	        	//visuals=false;
	        	//if(individual==0){
	        	//	visuals=true;
	        	//}
	        	double[] scoresGiven=ArcadeMachine.runOneGame(game, level1, visuals, neuroevo, recordActionsFile, seed,playerIDTemp);
	        	double relevantScore=scoresGiven[0];
	        	scoresPopulation.add(relevantScore);
	        	averageScores+=relevantScore/populationSize;
	        	if(curBest<relevantScore){
					curBest=relevantScore;
				}
	        	if(globalbest<relevantScore){
	        		globalbest = relevantScore;
	        		Network.saveWeight();
	        	}
	        }
	        System.out.println("best scores in all rounds:" + globalbest);
        	System.out.println("best in this round:"+curBest);
        	System.out.println("Average scores in this round:"+averageScores);

        	bestScoresObtained.add(curBest);
        	averageScoreObtained.add(averageScores);
	        SNESWeightGenerator.produceNextGeneration(scoresPopulation);
        }
        System.out.println("best scores in each iteration:"+bestScoresObtained.toString());
        System.out.println("Average scores in each iteration:"+averageScoreObtained.toString());
        System.out.println("Done");
        //ArcadeMachine.runOneGame(game, level1, true, neuroevo, recordActionsFile, seed,playerIDTemp);
        // 3. This replays a game from an action file previously recorded
        //String readActionsFile = "actionsFile_aliens_lvl0.txt";  //This example is for
        //ArcadeMachine.replayGame(game, level1, visuals, readActionsFile);

        // 4. This plays a single game, in N levels, M times :
        //String level2 = gamesPath + games[gameIdx] + "_lvl" + 1 +".txt";
        //int M = 3;
        //for(int i=0; i<games.length; i++){
        //	game = gamesPath + games[i] + ".txt";
        //	level1 = gamesPath + games[i] + "_lvl" + levelIdx +".txt";
        //	ArcadeMachine.runGames(game, new String[]{level1}, 5, evolutionStrategies, null);
        //}
        
        //5. This starts a game, in a generated level created by a specific level generator
        //if(ArcadeMachine.generateOneLevel(game, geneticGenerator, recordLevelFile)){
        //	ArcadeMachine.playOneGeneratedLevel(game, recordActionsFile, recordLevelFile, seed);
        //}
        
        //6. This plays N games, in the first L levels, M times each. Actions to file optional (set saveActions to true).
        /*int N = 10, L = 1, M = 5;
        boolean saveActions = false;
        String[] levels = new String[L];
        String[] actionFiles = new String[L*M];
        for(int i = 0; i < N; ++i)
        {
            int actionIdx = 0;
            game = gamesPath + games[i] + ".txt";
            for(int j = 0; j < L; ++j){
                levels[j] = gamesPath + games[i] + "_lvl" + j +".txt";
                if(saveActions) for(int k = 0; k < M; ++k)
                    actionFiles[actionIdx++] = "actions_game_" + i + "_level_" + j + "_" + k + ".txt";
            }
            ArcadeMachine.runGames(game, levels, M, kNearestNeighbour, saveActions? actionFiles:null);
        }*/
    }
}
