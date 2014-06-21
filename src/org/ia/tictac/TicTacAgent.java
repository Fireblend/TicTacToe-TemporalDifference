package org.ia.tictac;

import java.util.ArrayList;
import java.util.List;

public class TicTacAgent {
	private char symbolMe;
	private char symbolOther;
	private List<String> moves;
	
	public TicTacAgent(char symbolMe, char symbolOther){
		this.symbolMe = symbolMe;
		this.symbolOther = symbolOther;
		this.moves = new ArrayList<String>();
	}
	
	public String play(String currentState, boolean isTraining){
		String nextState = KnowledgeHolder.getInstance()
				.getNextState(currentState, symbolMe, symbolOther, isTraining);
		
		// Agregar estado anterior (movida oponente) y nueva (mi movida)
		moves.add(currentState);
		moves.add(nextState);
		
		return nextState;
	}
	
	public void updateKnowledge(boolean won, String lastState){
		// Si se perdio, hace falta agregar el ultimo estado
		if(!won)
			moves.add(lastState);

		// Actualizar estados
		KnowledgeHolder.getInstance().updateValues(moves, symbolMe, symbolOther);
		
		// Se supone que ya que estamos actualizando estados es porque termino el juego
		this.moves.clear();
	}
}