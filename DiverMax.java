package student;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import game.GetOutState;
import game.Tile;
import game.FindState;
import game.SewerDiver;
import game.Node;
import game.NodeStatus;
import game.Edge;

public class DiverMax extends SewerDiver {


    /** Get to the ring in as few steps as possible. Once you get there,
     * you must return from this function in order to pick
     * it up. If you continue to move after finding the ring rather
     * than returning, it will not count.
     * If you return from this function while not standing on top of the ring,
     * it will count as a failure.
     *
     * There is no limit to how many steps you can take, but you will receive
     * a score bonus multiplier for finding the ring in fewer steps.
     *
     * At every step, you know only your current tile's ID and the ID of all
     * open neighbor tiles, as well as the distance to the ring at each of these tiles
     * (ignoring walls and obstacles).
     *
     * In order to get information about the current state, use functions
     * currentLocation(), neighbors(), and distanceToRing() in FindState.
     * You know you are standing on the ring when distanceToRing() is 0.
     *
     * Use function moveTo(long id) in FindState to move to a neighboring
     * tile by its ID. Doing this will change state to reflect your new position.
     *
     * A suggested first implementation that will always find the ring, but likely won't
     * receive a large bonus multiplier, is a depth-first walk. Some
     * modification is necessary to make the search better, in general.*/
    @Override public void findRing(FindState state) {
        //TODO : Find the ring and return.
        // DO NOT WRITE ALL THE CODE HERE. DO NOT MAKE THIS METHOD RECURSIVE.
        // Instead, write your method elsewhere, with a good specification,
        // and call it from this one.
        dfs(state);
        Found = false;
    }

    private HashSet<Long> visited = new HashSet<Long>();

    private Stack<Long> path = new Stack<Long>();

    boolean Found = false;


    /*
     *
     */
	private void dfs(FindState state) {
		long current = state.currentLocation();
		visited.add(current);
		if (Found) return;
		ArrayList<NodeStatus> nbs = new ArrayList<NodeStatus>();
		state.neighbors().forEach(e -> nbs.add(e));
		nbs.sort((a,b) -> a.compareTo(b));
		for (NodeStatus n : nbs) {
			if (Found) {return;}
			if (!visited.contains(n.getId())) {
				path.add(state.currentLocation());
				state.moveTo(n.getId());
				if (state.distanceToRing() == 0) {
					Found = true;
					return;
				}
				dfs(state);
				if (!Found) state.moveTo(path.pop());
				//if (!Found) state.moveTo(current);
			}

		}

		/*
		if (nbs.size() ==1) {
			state.moveTo(nbs.get(0).getId()); dfs(state, nbs.get(0).getId());
		}
		else if (state.neighbors().size() == 2) {
			if (! visited.contains(nbs.get(0).getId())) {
				state.moveTo(nbs.get(0).getId()); dfs(state, nbs.get(0).getId());
			}
			else {
				state.moveTo(nbs.get(1).getId()); dfs(state, nbs.get(1).getId());
			}
		}
		*/



		/*
		Visit u;
		for each neighbor w of u:
			if (w is not visited) dfs(w);
		*/
	}



    /** Get out of the sewer system before the steps are all used, trying to collect
     * as many coins as possible along the way. Your solution must ALWAYS get out
     * before the steps are all used, and this should be prioritized above
     * collecting coins.
     *
     * You now have access to the entire underlying graph, which can be accessed
     * through GetOutState. currentNode() and getExit() will return Node objects
     * of interest, and getNodes() will return a collection of all nodes on the graph.
     *
     * You have to get out of the sewer system in the number of steps given by
     * getStepsRemaining(); for each move along an edge, this number is decremented
     * by the weight of the edge taken.
     *
     * Use moveTo(n) to move to a node n that is adjacent to the current node.
     * When n is moved-to, coins on node n are automatically picked up.
     *
     * You must return from this function while standing at the exit. Failing to
     * do so before steps run out or returning from the wrong node will be
     * considered a failed run.
     *
     * Initially, there are enough steps to get from the starting point to the
     * exit using the shortest path, although this will not collect many coins.
     * For this reason, a good starting solution is to use the shortest path to
     * the exit. */
    @Override public void getOut(GetOutState state) {
        //TODO: Get out of the sewer system before the steps are used up.
        // DO NOT WRITE ALL THE CODE HERE. Instead, write your method elsewhere,
        //with a good specification, and call it from this one.
    	coinGrab(state);
    }

    private HashSet<Node> coinSearched = new HashSet<Node>();
    private Stack<Node> search_path = new Stack<Node>();

    /*
     * return Node of nearest unvisited neighbor. return null if all
     * neighbors visited.
     **/
    private Node nearest(GetOutState state, Set<Node> nbs) {
    	Node closest = null;
    	int min = 100;
    	for (Node n : nbs) {
    		if (n.getEdge(state.currentNode()).length < min && !coinSearched.contains(n)) {
    			closest = n;
    			min = n.getEdge(state.currentNode()).length;
    		}
    	}
    	return closest;
    }

    /*
     * return Node of a neighbor with coins on it. if no neighbor has coins,
     * call 'nearest' and return Node of nearest unvisited neighbor.
     * return null if all neighbors visited.
     ***/
    private Node nearestCoin(GetOutState state, Set<Node> nbs) {
    	HashSet<Node> visitedGetOut = new HashSet<Node>();
    	Node closest = null;
    	for (Node n : nbs) {
    		//if (n.getTile().coins()>0 && !coinSearched.contains(n)) {
    		if (n.getTile().coins()>0) {
    			closest = n;
    		}
    	}
    	/**/
    	// bad seed -8209986781353465035
    	if (closest == null) {
    		for (Node n : nbs) {
	    		for (Node nNbs : n.getNeighbors()) {
	    			if (nNbs.getTile().coins()>0 && n.getEdge(nNbs).length <20 && !visitedGetOut.contains(nNbs)&& !visitedGetOut.contains(n)) {
	        			closest = n;
	        			if (closest.getEdge(state.currentNode()).length >30 /*nearest(state, nbs).getEdge(state.currentNode()).length*/)
	        				closest=nearest(state, nbs);
		        		visitedGetOut.add(nNbs);
		        		visitedGetOut.add(n);
		        		//System.out.println("nNbs");
	        		}
	    		}
    		}
    	}
    	/**/
    	//if (closest == null) closest = nearest(state, nbs);
    	return closest;
    }

    private void coinGrab(GetOutState state) {
    	int margin = 30;	// changed from 20 to 30 because ran out of steps in seed 7171721301712947436
    	List<Node> retlist = Paths.shortestPath(state.currentNode(), state.getExit());
    	//System.out.println(Paths.pathDistance(retlist) + " current distance to exit");
    	//System.out.println(state.stepsLeft() + " steps left");
    	if (Paths.pathDistance(retlist) + margin > state.stepsLeft()) {
    		retlist.remove(state.currentNode());
    		retlist.forEach(n -> state.moveTo(n));
    		return;
    	}


    	coinSearched.add(state.currentNode());
    	Set<Node> nbs = state.currentNode().getNeighbors();
    	Node closest = nearestCoin(state, nbs);
    	if (closest!=null) {
    		search_path.add(state.currentNode());
    		// this commented-out stuff, when included, prevents the player
    		// from running out of steps. Usually.
    		// It works by finding out whether moving to the nearest neighbor
    		// would place the player "too far away".
    		// However, I don't feel like I implemented it very neatly.
    		// A better solution might check all neighbors, starting with the
    		// one returned by nearestCoin.

    		/*
    		retlist.add(0, closest);
    		int hypotheticalDistance = Paths.pathDistance(retlist);
    		int hypotheticalStepsLeft = state.stepsLeft() - closest.getEdge(state.currentNode()).length;
    		if (hypotheticalDistance + margin > hypotheticalStepsLeft) {
    			retlist.remove(state.currentNode());
    			retlist.remove(closest);
        		retlist.forEach(n -> state.moveTo(n));
        		return;
    		} else {
    		*/
        		state.moveTo(closest);
        	/*
    		}
    		*/
    	}

    	else {
    		state.moveTo(search_path.pop());
    	}
    	coinGrab(state);

    }

}
