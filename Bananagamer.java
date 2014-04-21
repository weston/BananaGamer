package org.ggp.base.player.gamer.statemachine.sample;

import java.util.List;

import org.ggp.base.apps.player.detail.DetailPanel;
import org.ggp.base.apps.player.detail.SimpleDetailPanel;
import org.ggp.base.player.gamer.event.GamerSelectedMoveEvent;
import org.ggp.base.player.gamer.exception.GamePreviewException;
import org.ggp.base.player.gamer.statemachine.StateMachineGamer;
import org.ggp.base.util.game.Game;
import org.ggp.base.util.statemachine.Move;
import org.ggp.base.util.statemachine.StateMachine;
import org.ggp.base.util.statemachine.exceptions.GoalDefinitionException;
import org.ggp.base.util.statemachine.exceptions.MoveDefinitionException;
import org.ggp.base.util.statemachine.exceptions.TransitionDefinitionException;
import org.ggp.base.util.statemachine.implementation.prover.ProverStateMachine;

/**
 * SampleGamer is a simplified version of the StateMachineGamer, dropping some
 * advanced functionality so the example gamers can be presented concisely.
 * This class implements 7 of the 8 core functions that need to be implemented
 * for any gamer.
 *
 * If you want to quickly create a gamer of your own, extend this class and
 * add the last core function : public Move stateMachineSelectMove(long timeout)
 */
//test to see if this works
public abstract class BananaGamer extends StateMachineGamer
{
	@Override
	public void stateMachineMetaGame(long timeout) throws TransitionDefinitionException, MoveDefinitionException, GoalDefinitionException
	{
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
	}
	@Override
	public Move stateMachineSelectMove(long timeout) throws TransitionDefinitionException, MoveDefinitionException, GoalDefinitionException
	{
		//		long start = System.currentTimeMillis();//taken from RandomGamer.java random gamer
		//		//for nonrandom legal move take code from SampleLegalGamer.java
		//
		//		List<Move> moves = getStateMachine().getLegalMoves(getCurrentState(), getRole());
		//		Move selection = (moves.get(new Random().nextInt(moves.size())));
		//
		//		long stop = System.currentTimeMillis();
		//
		//		notifyObservers(new GamerSelectedMoveEvent(moves, selection, stop - start));
		//		return selection;


		long start = System.currentTimeMillis();//start of legal gamer
		List<Move> moves = getStateMachine().getLegalMoves(getCurrentState(), getRole());
		Move selection = moves.get(0);
		long stop = System.currentTimeMillis();
		notifyObservers(new GamerSelectedMoveEvent(moves, selection, stop - start));
		return selection;

	}


	/** This will currently return "SampleGamer"
	 * If you are working on : public abstract class MyGamer extends SampleGamer
	 * Then this function would return "MyGamer"
	 */
	@Override
	public String getName() {
		return "BananaGamer";
	}

	// This is the default State Machine
	@Override
	public StateMachine getInitialStateMachine() {//hash map from moves to machine states?
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
