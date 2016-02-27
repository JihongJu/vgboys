package vgboys;

import java.util.ArrayList;
import java.util.Random;

import core.game.StateObservation;
import core.player.AbstractPlayer;
import ontology.Types;
import tools.ElapsedCpuTimer;


/**Modification on One Step Look-Ahead Controller
 * Thanks to ssamot who wrote the One Step Look-Ahead Controller example
 */
public class Agent extends AbstractPlayer{
	
	protected Random randomGenerator;
	
	
    //Constructor. It must return in 1 second maximum.
    public Agent(StateObservation so, ElapsedCpuTimer elapsedTimer)
    {
        randomGenerator = new Random();
    }

    //Act function. Called every game step, it must return an action in 40 ms maximum.
    public Types.ACTIONS act(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
    	
    	//Initialize best action
    	Types.ACTIONS bestAction = null;
    	//Initialize max reward (Q) found
    	double maxQ = Double.NEGATIVE_INFINITY;
    	//Initialize epsilon
    	double epsilon = 0.5;
    	//Construct Learn State Heuristic
    	LearnStateHeuristic heuristic = new LearnStateHeuristic(stateObs);
    	
    	//TODO Policy Implementation: epsilon-greedy or Softmax
    	//Up-to-now: Look one step ahead with Epsilon-Greedy
    	//Next: 
    	double probRoll = randomGenerator.nextDouble();
    	
    	if (probRoll < epsilon) {
            //Get the available actions in this game.
            ArrayList<Types.ACTIONS> actions = stateObs.getAvailableActions();
            //Determine an index randomly and get the action to return.
            int index = randomGenerator.nextInt(actions.size());
            bestAction = actions.get(index);  		
    	} else {
        	for (Types.ACTIONS action : stateObs.getAvailableActions()) {
        		StateObservation stCopy = stateObs.copy();	//copy current state
        		stCopy.advance(action);						//advance the copied state
        		double Q = heuristic.evaluateState(stCopy);	//
        		
        		System.out.println("Action:" + action + " score:" + Q);
        		if (Q > maxQ) {
        			maxQ = Q;
        			bestAction = action;
        		}
        	}
    	}

    	
        
    	System.out.println("====================");
    	
        //Return the action.
        return bestAction;
    }

}
