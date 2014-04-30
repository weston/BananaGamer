
import java.util.ArrayList;
import java.util.List;

import org.ggp.base.apps.player.detail.DetailPanel;
import org.ggp.base.apps.player.detail.SimpleDetailPanel;
import org.ggp.base.player.gamer.event.GamerSelectedMoveEvent;
import org.ggp.base.player.gamer.exception.GamePreviewException;
import org.ggp.base.player.gamer.statemachine.StateMachineGamer;
import org.ggp.base.util.game.Game;
import org.ggp.base.util.statemachine.MachineState;
import org.ggp.base.util.statemachine.Move;
import org.ggp.base.util.statemachine.Role;//imported this
import org.ggp.base.util.statemachine.StateMachine;
import org.ggp.base.util.statemachine.cache.CachedStateMachine;
import org.ggp.base.util.statemachine.exceptions.GoalDefinitionException;
import org.ggp.base.util.statemachine.exceptions.MoveDefinitionException;
import org.ggp.base.util.statemachine.exceptions.TransitionDefinitionException;
import org.ggp.base.util.statemachine.implementation.prover.ProverStateMachine;





public class BananaGamer extends StateMachineGamer
{

	private static Role opponent;
	private static final int perfectScore = 100;
	private int alpha = 0;
	private int beta = 100;
	@Override
	public void stateMachineMetaGame(long timeout) throws TransitionDefinitionException, MoveDefinitionException, GoalDefinitionException
	{
		System.out.println(getCurrentState());
		//		long currentTime = System.currentTimeMillis();
		//		long finishBy = timeout - 1000;
		//
		//		StateMachine stateMachine = getStateMachine();
		//		MachineState rootState = getCurrentState();
		//		stateMachine.getInitialState();
		//
		//		MachineState currentState;
		//		int nbStatesExplored = 0;
		//		while(true){
		//			currentState = rootState;
		//			boolean isTerminal = stateMachine.isTerminal(currentState);
		//			while(!isTerminal){
		//				currentState = stateMachine.getRandomNextState(currentState);
		//				isTerminal = stateMachine.isTerminal(currentState);
		//				nbStatesExplored++;
		//			}
		//			if(System.currentTimeMillis() > finishBy)
		//				break;
		//		}
		//		System.out.println("Metagaming done. Nb states explored " + nbStatesExplored);
		for(Role role: getStateMachine().getRoles()){//finds opponent role
			if(role != getRole())
				opponent = role;
		}
	}
	@Override
	public Move stateMachineSelectMove(long timeout) throws TransitionDefinitionException, MoveDefinitionException, GoalDefinitionException
	{
		long start = System.currentTimeMillis();//start of legal gamer
		List<Move> moves = getStateMachine().getLegalMoves(getCurrentState(), getRole());
		Move selection = getBestMove(getCurrentState(),moves);
		long stop = System.currentTimeMillis();
		notifyObservers(new GamerSelectedMoveEvent(moves, selection, stop - start));
		return selection;

	}
	public Move getBestMove(MachineState state, List<Move> moves) throws MoveDefinitionException, TransitionDefinitionException, GoalDefinitionException{
		int indexSave = 0;
		int score = 0;
		alpha = 0;
		beta = 100;
		System.out.println("getBestMove");
		for(int i = 0; i < moves.size(); i++){
			int result = minScore(getRole(),moves.get(i),state);
			System.out.println(result + "this is the result "+ moves.get(i) +" this is the move");
			if(result == perfectScore){
				return moves.get(i);
			}
			if(result > score){
				indexSave = i;
				score = result;
			}
			System.out.println(score + "This is the score");
		}
		return moves.get(indexSave);
	}

	int maxScore(Role myRole,MachineState state) throws MoveDefinitionException, TransitionDefinitionException, GoalDefinitionException{
		if (getStateMachine().isTerminal(state)) {
			return getStateMachine().getGoal(state, getRole());
		}
		List<Move> moves = getStateMachine().getLegalMoves(state, getRole());
		for (int i = 0; i < moves.size(); i++){
			int result = minScore(getRole(),moves.get(i),state);//minmax
			alpha = Math.max(alpha, result);
			if (alpha>=beta) {
				return beta;
			}
		}
		return alpha;
	}

	int minScore(Role myRole,Move myMove, MachineState state) throws MoveDefinitionException, TransitionDefinitionException, GoalDefinitionException{
		List<Move> opponentMoves = getStateMachine().getLegalMoves(state,opponent);
		//int currentLowestScore = perfectScore; //minmax
		for(int i = 0; i < opponentMoves.size(); i++){
			Move opponentMove = opponentMoves.get(i);
			List<Move> jointMove = new ArrayList<Move>();
			if( myRole == getStateMachine().getRoles().get(0)){
				jointMove.add(myMove);
				jointMove.add(opponentMove);
			}else{
				jointMove.add(opponentMove);
				jointMove.add(myMove);
			}
			//System.out.println("min");
			MachineState nextState = getStateMachine().getNextState(state,jointMove);
			int nextStateScore = maxScore(getRole(),nextState);
			beta = Math.min(beta, nextStateScore);
			if (beta <= alpha) return alpha;
//			//if(nextStateScore == 0){//minmax
//			//	return 0;
//			//}
			//if(nextStateScore < currentLowestScore){//minmax
			//	currentLowestScore = nextStateScore;
			//}
		}
		//return currentLowestScore;//minmax
		return beta;
	}

	@Override
	public String getName() {
		return "BananaGamer";
	}

	// This is the default State Machine
	@Override
	public StateMachine getInitialStateMachine() {//hash map from moves to machine states?
		return new CachedStateMachine(new ProverStateMachine());
		//return new ProverStateMachine();
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