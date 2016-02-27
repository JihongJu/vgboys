package vgboys;

import java.util.ArrayList;

import controllers.Heuristics.StateHeuristic;
import core.game.Observation;
import core.game.StateObservation;
import ontology.Types;
import tools.Vector2d;

/**
 * Learn state heuristic
 * Up-to-now: Copied from SimpleStateHeuristic.java by ssamot
 * Next: Implement Reinforcement Learning
*/
public class LearnStateHeuristic extends StateHeuristic {
	public LearnStateHeuristic(StateObservation stateObs) {
		
	}
	
	public double evaluateState(StateObservation stateObs) {		
		// Get Avatar Positions
		Vector2d avatarPosition = stateObs.getAvatarPosition();
		ArrayList<Observation>[] npcPositions = stateObs.getNPCPositions(avatarPosition);
		ArrayList<Observation>[] portalPositions = stateObs.getPortalsPositions(avatarPosition);
		
		// Win and Lose Utility
		double won = 0;
		if (stateObs.getGameWinner() == Types.WINNER.PLAYER_WINS) {
			won = 1000000000;
		} else if (stateObs.getGameWinner() == Types.WINNER.PLAYER_LOSES) {
			return -999999999;
		}
		
		double minDistance = Double.POSITIVE_INFINITY;	//The distance to the closest NPC
		int npcCounter = 0;								//Number of NPCs alive
		
		if (npcPositions != null) {
			for (ArrayList<Observation> npcs : npcPositions) {
				if (npcs.size() > 0) {
					minDistance = npcs.get(0).sqDist;	//Get the squared distance to the closest NPC
					npcCounter += npcs.size();
				}
			}
		}
		
		// If there is no portal available
		if (portalPositions == null) {
			double score = 0;
			if (npcCounter == 0) {
				score = stateObs.getGameScore() + won*1000000000;
			} else {
				score = -minDistance / 100.0 + (-npcCounter) * 100.0 + stateObs.getGameScore() + won*1000000000;
			}
			
			return score;
		}
		// Else
		Vector2d minObjectPortal = null;						//The closest Portal
		double minDistancePortal = Double.POSITIVE_INFINITY;	//The distance to the closest portal
		for (ArrayList<Observation> portals : portalPositions) {
			if (portals.size() > 0) {
				minObjectPortal = portals.get(0).position;
				minDistancePortal = portals.get(0).sqDist;
			}
		}
		
		double score = 0;
		if (minObjectPortal == null) {
			score = stateObs.getGameScore() + won*1000000000;
		} else {
			score = -minDistancePortal * 10.0 + stateObs.getGameScore() + won*100000000;
		}
		
		return score;
	}
}
