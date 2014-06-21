package org.ia.tictac;

public class TicTacLogic {

	static TicTacAgent alice;
	static TicTacAgent bob;
	
	public static void main(String[] args){
		// Dos nuevos agentes:
		alice = new TicTacAgent('X', 'O');
		bob = new TicTacAgent('O', 'X');	
		
		// Entrenar 100000 veces
		train(100000);
		
		// Imprima hash
		KnowledgeHolder.getInstance().dump();
	}
	
	// TODO Hace falta la logica del juego. 
	
	// Mostrar un menu:
	// 1. Cargar valores de archivo (la funcion storeValues en KnowledgeHolder)
	// 2. Entrenar X veces (la funcion de train de aca abajo)
	// 3. Guardar valores a archivo (la funcion loadValues en KnowledgeHolder)
	// 4. Jugar contra agente (easy stuff)
	
	// Take it from there.
	
	private static void train(int games){
		// Incializa grid
		String ticTacField = "000000000";
		for(int i = 0; i < games; i++){
			// Mientras no se gane, cada jugador juega.
			while(!KnowledgeHolder.getInstance().isWinningCondition(ticTacField, 'X') &&
					!KnowledgeHolder.getInstance().isWinningCondition(ticTacField, 'O') &&
					!KnowledgeHolder.getInstance().isGameOver(ticTacField)){
				
				ticTacField = alice.play(ticTacField, true);

				// Si el juego termino, quebrar ciclo.
				if(KnowledgeHolder.getInstance().isWinningCondition(ticTacField, 'X') ||
						KnowledgeHolder.getInstance().isWinningCondition(ticTacField, 'O') ||
						KnowledgeHolder.getInstance().isGameOver(ticTacField))
					break;
				
				ticTacField = bob.play(ticTacField, true);
			}
			
			// Actualiza knowledge
			alice.updateKnowledge(KnowledgeHolder.getInstance()
					.isWinningCondition(ticTacField, 'X'), ticTacField);
			bob.updateKnowledge(KnowledgeHolder.getInstance()
					.isWinningCondition(ticTacField, 'O'), ticTacField);
			
			// Resetear para el proximo juego
			ticTacField = "000000000";
		}
		
		KnowledgeHolder.getInstance().dump();
	}
}
