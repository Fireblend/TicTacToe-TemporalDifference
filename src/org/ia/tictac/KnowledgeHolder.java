package org.ia.tictac;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class KnowledgeHolder {

	private HashMap<String, Double> knowledge;
	private static KnowledgeHolder instance;
	private Random rand;
	
	private static final double ALPHA = 0.3;
	private static final double EXPLORATORY = 10;
	
	public KnowledgeHolder(){
		this.knowledge = new HashMap<String, Double>();
		this.rand = new Random();
	}
	
	public static KnowledgeHolder getInstance(){
		if(instance == null)
			instance = new KnowledgeHolder();
		
		return instance;
	}
	
	private List<String> generateNextStates(String state, char symbolMe){
		ArrayList<String> nextStates = new ArrayList<String>();
		
		for(int i = 0; i < 9; i++)
			if(state.charAt(i) == '0'){
				char[] array = state.toCharArray();
				array[i] = symbolMe;
				nextStates.add(new String(array));
			}
		
		return nextStates;
	}
	
	public String getNextState(String state, char symbolMe, char symbolOther, boolean isTraining){
		List<String> nextStates = generateNextStates(state, symbolMe);
		
		boolean isExploratory = rand.nextInt(100) < EXPLORATORY && isTraining;
		if(isExploratory)
			return nextStates.get(rand.nextInt(nextStates.size()));

		String highestState = nextStates.remove(0);
		double highestVal = getValue(highestState, symbolMe, symbolOther);
		for(String potentialState : nextStates){
			if(getValue(potentialState, symbolMe, symbolOther) > highestVal){
				highestState = potentialState;
				highestVal = getValue(highestState, symbolMe, symbolOther);						
			}
		}
		
		return highestState;
	}
	
	//Determina si ya se acabo el juego
	public boolean isWinningCondition(String state, char symbol){
		//Horizontales
		if(state.charAt(0) == symbol && state.charAt(1) == symbol && state.charAt(2) == symbol)
			return true;
		if(state.charAt(3) == symbol && state.charAt(4) == symbol && state.charAt(5) == symbol)
			return true;
		if(state.charAt(6) == symbol && state.charAt(7) == symbol && state.charAt(8) == symbol)
			return true;
		
		//Verticales
		if(state.charAt(0) == symbol && state.charAt(3) == symbol && state.charAt(6) == symbol)
			return true;
		if(state.charAt(1) == symbol && state.charAt(4) == symbol && state.charAt(7) == symbol)
			return true;
		if(state.charAt(2) == symbol && state.charAt(5) == symbol && state.charAt(8) == symbol)
			return true;
		
		//Diagonales
		if(state.charAt(0) == symbol && state.charAt(4) == symbol && state.charAt(8) == symbol)
			return true;
		if(state.charAt(2) == symbol && state.charAt(4) == symbol && state.charAt(6) == symbol)
			return true;
		
		return false;
	}
	
	//Obtiene el valor de un estado en el knowledge 
	private Double getValue(String state, char symbolMe, char symbolOther){
		if(isWinningCondition(state, symbolMe))
			return 1.0;
		
		if(isWinningCondition(state, symbolOther))
			return 0.0;
		
		if(isGameOver(state))
			return 0.0;
		
		state = lookForSameState(state, symbolMe, symbolOther);
		return knowledge.get(state);
	}
	
	//Obtiene el valor de un estado en el knowledge 
	private Double getValue(String state){
		if(isWinningCondition(state, '1'))
			return 1.0;
		
		if(isWinningCondition(state, '2'))
			return 0.0;
		
		if(isGameOver(state))
			return 0.0;
		
		state = lookForSameState(state, '1', '2');
		return knowledge.get(state);
	}
	
	//Chequea si el estado ya esta en knowledge. Puede estar rotado o en espejo.
	private String lookForSameState(String state, char symbolMe, char symbolOther){
		state = state.replace(symbolMe, '1');
		state = state.replace(symbolOther, '2');
		
		if(knowledge.containsKey(state))
			return state;
		if(knowledge.containsKey(mirrorHoriz(state)))
			return mirrorHoriz(state);
		if(knowledge.containsKey(mirrorVert(state)))
			return mirrorVert(state);

		if(knowledge.containsKey(rotateOnce(state)))
			return rotateOnce(state);
		if(knowledge.containsKey(rotateOnce(rotateOnce(state))))
			return rotateOnce(rotateOnce(state));
		if(knowledge.containsKey(rotateOnce(rotateOnce(rotateOnce(state)))))
			return rotateOnce(rotateOnce(rotateOnce(state)));

		//Si no esta, lo crea y lo devuelve.
		knowledge.put(state, 0.5);
		return state;
	}
	
	//Rota la matriz hacia la derecha
    private static String rotateOnce(String state){
        String out = "";
        out += state.charAt(6);
        out += state.charAt(3);
        out += state.charAt(0);
        out += state.charAt(7);
        out += state.charAt(4);
        out += state.charAt(1);
        out += state.charAt(8);
        out += state.charAt(5);
        out += state.charAt(2);
        return out;
    }

    //Consigue la version espejo vertical del estado
    private static String mirrorVert(String state){
        String out = "";
        out += state.substring(6, 9);
        out += state.substring(3, 6);
        out += state.substring(0, 3);
        return out;
    }

    //Consigue la version espejo horizontal del estado
    private static String mirrorHoriz(String state){
        String out = "";
        out += state.charAt(2);
        out += state.charAt(1); 
        out += state.charAt(0);
        out += state.charAt(5);
        out += state.charAt(4);
        out += state.charAt(3);
        out += state.charAt(8);
        out += state.charAt(7);
        out += state.charAt(6);     
        return out;
    }
    
    public boolean isGameOver(String state){
    	return generateNextStates(state, 'X').size() == 0;
    }
    
    //Actualiza valores en knowledge hash.
    public void updateValues(List<String> states, char symbolMe, char symbolOther){
    	String nextState = lookForSameState(states.remove(states.size()-1), symbolMe, symbolOther);
    	
    	for(int i = states.size()-1; i >= 0; i--){
    		String prevState = lookForSameState(states.get(i), symbolMe, symbolOther);
    		
    		double s = getValue(prevState);
    		double sPrime = getValue(nextState);
    		
    		knowledge.put(prevState, s + ALPHA*(sPrime-s));
    	
    		nextState = prevState;
    	}
    }
    
    // Guarda valores en archivo
    public void storeValues(){
    	
    }
    
    // Extrae valores de archivo
    public void loadValues(){
    	
    }
    
    public void dump(){
    	for(String string : knowledge.keySet()){
    		System.out.println(string + " -> " + knowledge.get(string));
    	}
    }
}
