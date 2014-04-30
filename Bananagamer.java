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





public class Minimax extends StateMachineGamer{

	private static Role opponent;
	private static final int perfectScore = 100;
	private int feasibility = 0;
	private int limit = 4;
	private static final double mobilityContribution = 0.95;
	private static final double focusContribution = 0.05;
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



		//long start = System.currentTimeMillis();//start of legal gamer
		List<Move> moves = getStateMachine().getLegalMoves(getCurrentState(), getRole());
		Move selection = moves.get(0);
		selection = getBestMove(getCurrentState());
		//long stop = System.currentTimeMillis();
		//notifyObservers(new GamerSelectedMoveEvent(moves, selection, stop - start));
		return selection;

	}
	public Move getBestMove(MachineState state) throws MoveDefinitionException, TransitionDefinitionException, GoalDefinitionException{
		List<Move> moves = getStateMachine().getLegalMoves(getCurrentState(),getRole());
		Move moveSave = moves.get(0);
		int score = 0;
		for(Move move: moves){
			int result = minScore(getRole(),move,state, 1);
			if(result == perfectScore){
				return move;
			}
			if(result > score){
				moveSave = move;
				score = result;
			}
		}
		return moveSave;
	}

	int evalfn(MachineState state, Role myRole) throws MoveDefinitionException{
		return (int) (mobilityContribution * mobility(state, myRole) + focusContribution * focus(state, myRole));
	}

	int mobility(MachineState state, Role myRole) throws MoveDefinitionException{ //IT'S A THREEEEEEE LINERRRRR!!!!!
		int legalmoves = getStateMachine().getLegalMoves(state, myRole).size();
		if (legalmoves + 1 > feasibility) feasibility = legalmoves + 1;
		return legalmoves*100/feasibility;
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
		if (level >= limit) return evalfn(state, myRole);//mobility(state, myRole);
		List<Move> moves = getStateMachine().getLegalMoves(state, myRole);
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
		return "BananaHeuristic";
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