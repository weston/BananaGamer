package org.ggp.base.player.gamer.statemachine.sample;
import java.util.ArrayList;
import java.util.List;

import org.ggp.base.apps.player.detail.DetailPanel;
import org.ggp.base.apps.player.detail.SimpleDetailPanel;
import org.ggp.base.player.gamer.exception.GamePreviewException;
import org.ggp.base.player.gamer.statemachine.StateMachineGamer;
import org.ggp.base.util.game.Game;
import org.ggp.base.util.statemachine.MachineState;
import org.ggp.base.util.statemachine.Move;
import org.ggp.base.util.statemachine.Role;//imported this
import org.ggp.base.util.statemachine.StateMachine;
import org.ggp.base.util.statemachine.exceptions.GoalDefinitionException;
import org.ggp.base.util.statemachine.exceptions.MoveDefinitionException;
import org.ggp.base.util.statemachine.exceptions.TransitionDefinitionException;
import org.ggp.base.util.statemachine.implementation.prover.ProverStateMachine;


//hello


public class BananaTree extends StateMachineGamer{
	long turntime;
	long starttime;
	Move bestMove;
	private static Role opponent;
	private static final int perfectScore = 100;
	private int feasibility = 0;
	private int limit = 2;
	private int probe_count = 4;
	private List<MachineState> stack = new ArrayList();
	@Override
	public void stateMachineMetaGame(long timeout) throws TransitionDefinitionException, MoveDefinitionException, GoalDefinitionException
	{
		for(Role role: getStateMachine().getRoles()){//finds opponent role
			if(role != getRole())
				opponent = role;
		}
	}
	@Override
	public Move stateMachineSelectMove(long timeout) throws TransitionDefinitionException, MoveDefinitionException, GoalDefinitionException
	{
		//System.out.println("test");
		//		long start = System.currentTimeMillis();//taken from RandomGamer.java random gamer
		//		//for nonrandom legal move take code from SampleLegalGamer.java
		//
		//		List<Move> moves = getStateMachine().getLegalMoves(getCurrentState(), getRole());
		//		Move selection = (moves.get(new Random().nextInt(moves.size())));
		//
		//		long stop = System.currentTimeMillis();
		//
		//		notifyObservers(new GamerSelectedMoveEvent(moves, selection, stop - start));
		//		return selection;//end
        
        
        
		long start = System.currentTimeMillis();//start of legal gamer
		//starttime = System.currentTimeMillis();
		turntime = timeout - 2000;
		List<Move> moves = getStateMachine().getLegalMoves(getCurrentState(), getRole());
		Move selection = bestMove = moves.get(0);
		selection = getBestMove(getCurrentState(),turntime + start);
		//long stop = System.currentTimeMillis();
		//notifyObservers(new GamerSelectedMoveEvent(moves, selection, stop - start));
		return selection;
        
	}
	public Move getBestMove(MachineState state,long johnSilvers) throws MoveDefinitionException, TransitionDefinitionException, GoalDefinitionException{
		while(johnSilvers> System.currentTimeMillis()){
			MachineState selectedState = selectNode(getCurrentState());
			int score = montecarlo(selectedState,10);
			ITSATHREELINER(score);
			stack = new ArrayList();
		}
		int score = 1;
		Move bestMove = getStateMachine().getLegalMoves(state,getRole()).get(0);
		List<List<Move>> jointMoves = getStateMachine().getLegalJointMoves(state);
		for (List<Move> jointMove: jointMoves){
			MachineState nextState = getStateMachine().getNextState(state, jointMove);
			if(nextState.stateUtility > score){
				score = nextState.stateUtility;
				bestMove = jointMove.get(0);
			}
		}
		return bestMove;
	}
    
	MachineState selectNode(MachineState state) throws MoveDefinitionException, TransitionDefinitionException {
		if (state.nVisits == 0) {
			stack.add(state);
			return state;
		}
		List<List<Move>> jointMoves = getStateMachine().getLegalJointMoves(state);
		for (List<Move> jointMove: jointMoves){
			MachineState nextState = getStateMachine().getNextState(state, jointMove);
			if (nextState.nVisits == 1) {
				stack.add(nextState);
				return nextState;
			}
		}
		int score = 1;
		MachineState result = state;
		for (List<Move> jointMove: jointMoves){
			MachineState nextState = getStateMachine().getNextState(state, jointMove);
			int selectScore = selectfn(nextState);
			if (selectScore > score) {
				score = selectScore;
				result = nextState;
			}
		}
		stack.add(result);
		return selectNode(result);
	}
    
    
	int selectfn(MachineState state) {
		return (int) (state.stateUtility + Math.sqrt(2*Math.log(stack.get(stack.size() - 1).nVisits/state.nVisits)));
	}
    
	void ITSATHREELINER(int score){
		for (MachineState state: stack) {
			state.nVisits++;
			state.stateUtility += score;
		}
	}
    
    
	int evalfn(MachineState state, Role myRole) throws MoveDefinitionException{
		return (int) (0.95 * mobility(state, myRole) + 0.05 * focus(state, myRole));
	}
    
	int mobility(MachineState state, Role myRole) throws MoveDefinitionException{ //IT'S A THREEEEEEE LINERRRRR!!!!!
		int legalmoves = getStateMachine().getLegalMoves(state, myRole).size();
		if (legalmoves + 1 > feasibility) feasibility = legalmoves + 1;
		return legalmoves*100/feasibility;
	}
    
	//montecarlo
	int montecarlo (MachineState state, int count) throws TransitionDefinitionException, MoveDefinitionException, GoalDefinitionException{
		int total = 0;
		int[] notRealDepth = new int[1];
		for (int i=0; i< count; i++){
            MachineState finalState = getStateMachine().performDepthCharge(state, notRealDepth);
            total += getStateMachine().getGoal(finalState,getRole());
		}
		//System.out.println("Montecarlo ouput" + total/count);
		return total/count;
	}
    
	private int[] depth = new int[1];
	int performDepthChargeFromMove(MachineState theState, Move myMove) {
	    StateMachine theMachine = getStateMachine();
	    try {
            MachineState finalState = theMachine.performDepthCharge(theMachine.getRandomNextState(theState, getRole(), myMove), depth);
            return theMachine.getGoal(finalState, getRole());
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
	}
    
	int focus(MachineState state, Role myRole) throws MoveDefinitionException{ //IT'S A THREEEEEEE LINERRRRR!!!!!
		int legalmoves = getStateMachine().getLegalMoves(state, myRole).size();
		if (legalmoves + 1 > feasibility) feasibility = legalmoves + 1;
		return 100 - legalmoves*100/feasibility;
	}
    
	int maxScore(Role myRole,MachineState state, int level) throws MoveDefinitionException, TransitionDefinitionException, GoalDefinitionException{
		if (getStateMachine().isTerminal(state)) {
			return getStateMachine().getGoal(state, getRole());
		}
		List<Move> moves = getStateMachine().getLegalMoves(state, myRole);
		//if (level >= limit) return montecarlo(moves, state, probe_count);//evalfn(state, myRole);//mobility(state, myRole);
		int score = 0;
		for (Move move: moves){
			int result = minScore(getRole(),move,state, level + 1);
            if(result == perfectScore){
                return perfectScore;
            }
			if (result > score) {
				score = result;
			}
		}
		return score;
	}
    
	int minScore(Role myRole,Move myMove, MachineState state, int level) throws MoveDefinitionException, TransitionDefinitionException, GoalDefinitionException{
        
		List<List<Move>> opponentMoves = getStateMachine().getLegalJointMoves(state, myRole, myMove);
		if (opponentMoves.isEmpty())System.out.println("hello world");
        
		int currentLowestScore = perfectScore;
		for(List<Move> opponentMove: opponentMoves){
			//if (opponentMove != myMove){
			//List<Move> jointMove = new ArrayList<Move>();
			//if( myRole == getStateMachine().getRoles().get(0)){
			//	jointMove.add(myMove);
			//	jointMove.add(opponentMove);
			//}else{
			//	jointMove.add(opponentMove);
			//	jointMove.add(myMove);
			//}
			MachineState nextState = getStateMachine().getNextState(state,opponentMove);
			int nextStateScore = maxScore(getRole(),nextState, level + 1);
			//	if(nextStateScore == 0){
			//		return 0;
			//	}
			if(nextStateScore < currentLowestScore){
				currentLowestScore = nextStateScore;
			}
			//}
		}
		return currentLowestScore;
	}
    
    
	@Override
	public String getName() {
		return "Banana";
	}
    
	// This is the default State Machine
	@Override
	public StateMachine getInitialStateMachine() {//hash map from moves to machine states?
		//return new CachedStateMachine(new ProverStateMachine());
		return new ProverStateMachine();
	}
    
	// This is the default Sample Panel
	@Override
	public DetailPanel getDetailPanel() {
		return new SimpleDetailPanel();
	}
    
    
    
	@Override
	public void stateMachineStop() {
		// Sample gamers do no special cleanup when the match ends normally.
	}
    
	@Override
	public void stateMachineAbort() {
		// Sample gamers do no special cleanup when the match ends abruptly.
	}
    
	@Override
	public void preview(Game g, long timeout) throws GamePreviewException {
		// Sample gamers do no game previewing.
	}
}