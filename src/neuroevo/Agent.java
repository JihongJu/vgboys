package neuroevo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import core.game.Observation;
import core.game.StateObservation;
import core.player.AbstractPlayer;
import ontology.Types;
import ontology.Types.WINNER;
import tools.ElapsedCpuTimer;
import tools.Vector2d;


/**Modification on One Step Look-Ahead Controller
 * Thanks to ssamot who wrote the One Step Look-Ahead Controller example
 */
public class Agent extends AbstractPlayer{
	
	protected Random randomGenerator;
	private Network decisionNetwork;
	
	
    //Constructor. It must return in 1 second maximum.
    public Agent(StateObservation so, ElapsedCpuTimer elapsedTimer)
    {
    	try {
			decisionNetwork=new Network();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        randomGenerator = new Random();
    }

    //Act function. Called every game step, it must return an action in 40 ms maximum.
    public Types.ACTIONS act(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
    	ArrayList<Double> inputs=new ArrayList<Double>();
    	//TODO Look into making this more flexible, right now only one of each type is used as input
    	Vector2d avatarPosition=stateObs.getAvatarPosition();
    	
    	inputs.add(stateObs.getGameScore());
    	inputs.add((double)stateObs.getGameTick());

    	WINNER winner=stateObs.getGameWinner();
    	double winnerStatus;
    	if(winner.equals(WINNER.PLAYER_WINS)){
    		winnerStatus=1;
    	}else if(winner.equals(WINNER.PLAYER_LOSES)){
    		winnerStatus=-1;
    	}else{
    		winnerStatus=0;
    	}
    	inputs.add(winnerStatus);
    	
    	if(stateObs.isGameOver()){
    		inputs.add(1.0);
    	}else{
    		inputs.add(0.0);
    	}
    	
    	//inputs.add((double)stateObs.getAvatarHealthPoints()); //TODO decide if we add this
    	
    	
    	
    	HashMap<Integer,Integer> ownedResources=stateObs.getAvatarResources();
    	ArrayList<Observation>[] immovableObservations=stateObs.getImmovablePositions(avatarPosition);
    	ArrayList<Observation>[] movableObservations=stateObs.getMovablePositions(avatarPosition);
    	ArrayList<Observation>[] NPCObservations=stateObs.getNPCPositions(avatarPosition);
    	ArrayList<Observation>[] selfCreatedObservations=stateObs.getFromAvatarSpritesPositions(avatarPosition);
    	int numResourcesConsidered=5;
    	ArrayList<Double> resourceInfo=new ArrayList<Double>(5);
    	for(int resourceAmount:ownedResources.values()){
    		resourceInfo.add((double) resourceAmount);
    		if(resourceInfo.size()>=numResourcesConsidered){
    			break;
    		}
    	}
    	while(resourceInfo.size()<numResourcesConsidered){
    		resourceInfo.add(0.0);
    	}
    	inputs.addAll(resourceInfo);
    	double immovableDist;
    	double movableDist;
    	double NPCDist;
    	double selfCreatedDist;
    	if(!(immovableObservations==null)){
    		if(immovableObservations[0].size()<1){
    			immovableDist=0;
    		}else{
    			immovableDist=immovableObservations[0].get(0).sqDist;
    		}
    	}else{
    		immovableDist=0;
    	}
    	
    	if(!(movableObservations==null)){
    		if(movableObservations[0].size()<1){
    			movableDist=0;
    		}else{
    			movableDist=movableObservations[0].get(0).sqDist;
    		}
    	}else{
    		movableDist=0;
    	}
    	
    	if(!(NPCObservations==null)){
    		if(NPCObservations[0].size()<1){
    			NPCDist=0;
    		}else{
    			NPCDist=NPCObservations[0].get(0).sqDist;
    		}
    	}else{
    		NPCDist=0;
    	}
    	
    	if(!(selfCreatedObservations==null)){
    		if(selfCreatedObservations[0].size()<1){
    			selfCreatedDist=0;
    		}else{
    			selfCreatedDist=selfCreatedObservations[0].get(0).sqDist;
    		}
    	}else{
    		selfCreatedDist=0;
    	}
    	
    	inputs.add(immovableDist);
    	inputs.add(movableDist);
    	inputs.add(NPCDist);
    	inputs.add(selfCreatedDist);

    	double avatarSpeed=stateObs.getAvatarSpeed();
    	inputs.add(avatarSpeed);

    	
    	
    	
		try {
	    	ArrayList<Double> outputs;
			outputs = decisionNetwork.fire(inputs);
	    	//TODO select best output given inputs (find set of possibles, find from that set the one with the highest score)
	    	ArrayList<Types.ACTIONS> actionsPossible=stateObs.getAvailableActions(true);
	    	double bestScore=Double.NEGATIVE_INFINITY;
	    	Types.ACTIONS bestAction=actionsPossible.get(0);
	    	
	    	//Map actions to their scores and select the best. This maps 0 to NIL, 1 to UP, 2 to RIGHT, 3 to DOWN, 4 to LEFT and 5 to USE
	    	for(Types.ACTIONS actionPossiblitity:actionsPossible){
	    		if(actionPossiblitity.equals(Types.ACTIONS.ACTION_NIL)){
	    			double actionScore=outputs.get(0);
	    			if(actionScore>bestScore){
	    				bestAction=actionPossiblitity;
	    				bestScore=actionScore;
	    			}
	    		}else if(actionPossiblitity.equals(Types.ACTIONS.ACTION_UP)){
	    			double actionScore=outputs.get(1);
	    			if(actionScore>bestScore){
	    				bestAction=actionPossiblitity;
	    				bestScore=actionScore;
	    			}
	    		}else if(actionPossiblitity.equals(Types.ACTIONS.ACTION_RIGHT)){
	    			double actionScore=outputs.get(2);
	    			if(actionScore>bestScore){
	    				bestAction=actionPossiblitity;
	    				bestScore=actionScore;
	    			}
	    		}else if(actionPossiblitity.equals(Types.ACTIONS.ACTION_DOWN)){
	    			double actionScore=outputs.get(3);
					if(actionScore>bestScore){
						bestAction=actionPossiblitity;
						bestScore=actionScore;
					}
	    		}else if(actionPossiblitity.equals(Types.ACTIONS.ACTION_LEFT)){
	    			double actionScore=outputs.get(4);
	    			if(actionScore>bestScore){
	    				bestAction=actionPossiblitity;
	    				bestScore=actionScore;
	    			}
	    		}else if(actionPossiblitity.equals(Types.ACTIONS.ACTION_USE)){
	    			double actionScore=outputs.get(5);
	    			if(actionScore>bestScore){
	    				bestAction=actionPossiblitity;
	    				bestScore=actionScore;
	    			}
	    		}
	    	}
	    	
	        return bestAction;
		} catch (Exception e) {
			// TODO better catch
			e.printStackTrace();
			return Types.ACTIONS.ACTION_NIL;
		}
    }

}
