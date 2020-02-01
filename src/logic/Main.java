package logic;

import java.util.HashMap;
import java.util.Map;

public class Main {

	public static void main(String[] args) {
		
		// Anzahl der Symbole des SSLP
		int numberofsymbols = 15;
		
		// Erzeuge Array von Nicht-Terminalen 
		NonTerminal[] nonterminals = new NonTerminal[numberofsymbols];
		for (int i = 0; i < numberofsymbols; i++) {
			nonterminals[i] = new NonTerminal(i);
		}		
		
		// Erstelle und fülle Map für Regeln zu Nicht-Terminalen
		Map<NonTerminal, NonTerminal[]> rulesnonterminals = new HashMap<NonTerminal, NonTerminal[]>();
		rulesnonterminals.put(nonterminals[0], new NonTerminal[] {nonterminals[1], nonterminals[14]});
		rulesnonterminals.put(nonterminals[1], new NonTerminal[] {nonterminals[13], nonterminals[2]});
		rulesnonterminals.put(nonterminals[2], new NonTerminal[] {nonterminals[12], nonterminals[3]});
		rulesnonterminals.put(nonterminals[3], new NonTerminal[] {nonterminals[4], nonterminals[12]});
		rulesnonterminals.put(nonterminals[4], new NonTerminal[] {nonterminals[11], nonterminals[5]});
		rulesnonterminals.put(nonterminals[5], new NonTerminal[] {nonterminals[6], nonterminals[11]});
		rulesnonterminals.put(nonterminals[6], new NonTerminal[] {nonterminals[7], nonterminals[10]});
		rulesnonterminals.put(nonterminals[7], new NonTerminal[] {nonterminals[10], nonterminals[8]});
		rulesnonterminals.put(nonterminals[8], new NonTerminal[] {nonterminals[9], nonterminals[9]});
		rulesnonterminals.put(nonterminals[9], new NonTerminal[] {nonterminals[10], nonterminals[10]});
		rulesnonterminals.put(nonterminals[10], new NonTerminal[] {nonterminals[11], nonterminals[11]});
		rulesnonterminals.put(nonterminals[11], new NonTerminal[] {nonterminals[12], nonterminals[12]});
		rulesnonterminals.put(nonterminals[12], new NonTerminal[] {nonterminals[13], nonterminals[14]});
		
		// Erstelle und fülle Map für Regeln zu Terminalen
		Map<NonTerminal, String> rulesterminals = new HashMap<NonTerminal, String>();
		rulesterminals.put(nonterminals[13], "b");
		rulesterminals.put(nonterminals[14], "a");
		
		// Erstelle Grammatik 
		Grammar grammatik = new Grammar(nonterminals[0], rulesnonterminals, rulesterminals, nonterminals);
		
		// Grammatik auflösen
		grammatik.solveGrammar();
		grammatik.printSolvedGrammar();
	
		// Pfade zum Terminal finden
		grammatik.findPathsFromNonTerminalToTerminal();

		// Pfade vom Start finden
		grammatik.findPathsFromStartToNonTerminal();
		
		// Logarithmierte Paare ausgeben
		grammatik.printCompletePaths();
		
		// Centroidpfade finden und ausgeben 
		grammatik.extractCentroidPaths();
		grammatik.printCentroidPaths();
		
		//
		grammatik.sideSymbolsOfCentroidPaths(); 
		grammatik.printSidesOfCentroidPath(); 
		//
		grammatik.calcHeightOfSymbols();
//		grammatik.printHeights();
		//
		grammatik.determineSeperation();
		//
//		grammatik.createNewRules();
	}
}