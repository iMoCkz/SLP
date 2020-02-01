package logic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

public class Grammar {

	private NonTerminal grammarStartSymbol;
	private Map<NonTerminal, NonTerminal[]> rulesNonTerminals;
	private Map<NonTerminal, String> rulesTerminals;
	private NonTerminal[] nonTerminals;
	public String grammarSolution;
	public ArrayList<CentroidPath> centroidPaths;
	public ArrayList<ArrayList<NonTerminal>> LeftSidesCentroidPath, RightSidesCentroidPath;

	// Konstruktor
	public Grammar(NonTerminal startSymbol, Map<NonTerminal, NonTerminal[]> rulesNonTerminals,
			Map<NonTerminal, String> rulesTerminals, NonTerminal[] nonTerminals) {
		this.grammarStartSymbol = startSymbol;
		this.rulesNonTerminals = rulesNonTerminals;
		this.rulesTerminals = rulesTerminals;
		this.nonTerminals = nonTerminals;
		// Startsymbol hat immer nur 1 Weg zu sich selbst
		this.grammarStartSymbol.setNumberOfPathsFromRoot(1);
		// Endsymbol vor Terminal hat immer nur 1 Weg zu sich selbst
		for (int i = rulesNonTerminals.size(); i < nonTerminals.length; i++) {
			nonTerminals[i].setNumberOfPathsToTerminal(1);
			nonTerminals[i].setWeight(1);
			nonTerminals[i].isFinalWeight(true);
		}
		centroidPaths = new ArrayList<CentroidPath>();
		LeftSidesCentroidPath = new ArrayList<ArrayList<NonTerminal>>();
		RightSidesCentroidPath = new ArrayList<ArrayList<NonTerminal>>();
	}

	public void solveGrammar() {
		grammarSolution = solveGrammarRecursively(grammarStartSymbol);
	}

	// Pfade zum Terminalsymbol finden
	public void findPathsFromNonTerminalToTerminal() {
		for (NonTerminal symbol : rulesNonTerminals.keySet()) {
			symbol.setNumberOfPathsToTerminal(findPathsToTerminalRecursively(symbol, 0));
		}
	}

	// Pfade vom Startsymbol finden
	public void findPathsFromStartToNonTerminal() {
		for (NonTerminal symbol : nonTerminals) {
			if (!symbol.equals(grammarStartSymbol)) {
				findPathsFromStartRecursively(grammarStartSymbol, symbol, 0);
			}
		}
	}

	// Grammatik berechnen
	private String solveGrammarRecursively(NonTerminal startSymbol) {
		String output = "";
		if (rulesNonTerminals.containsKey(startSymbol)) {
			NonTerminal[] nonterminalsymbols = rulesNonTerminals.get(startSymbol);
			for (int nonterminalindex = 0; nonterminalindex < nonterminalsymbols.length; nonterminalindex++) {
				output += solveGrammarRecursively(rulesNonTerminals.get(startSymbol)[nonterminalindex]);
			}
		} else {
			if (rulesTerminals.containsKey(startSymbol)) {
				output += rulesTerminals.get(startSymbol);
			}
		}
		return output;
	}

	// Berechne Pfade zum Terminalsymbol
	private int findPathsToTerminalRecursively(NonTerminal startSymbol, int countOfPaths) {
		int current_paths = countOfPaths;
		if (!rulesTerminals.containsKey(startSymbol)) {
			NonTerminal[] nonterminalsymbols = rulesNonTerminals.get(startSymbol);
			for (int nonterminalindex = 0; nonterminalindex < nonterminalsymbols.length; nonterminalindex++) {
				current_paths = findPathsToTerminalRecursively(rulesNonTerminals.get(startSymbol)[nonterminalindex],
						current_paths);
			}
		} else {
			current_paths++;
		}
		return current_paths;
	}

	// Berechne Pfade vom Startsymbol
	private void findPathsFromStartRecursively(NonTerminal startSymbol, NonTerminal endSymbol, int countOfPaths) {
		int current_paths = countOfPaths;
		if (startSymbol.getId() != endSymbol.getId()) {
			if (rulesNonTerminals.containsKey(startSymbol)) {
				NonTerminal[] nonterminalsymbols = rulesNonTerminals.get(startSymbol);
				for (int nonterminalindex = 0; nonterminalindex < nonterminalsymbols.length; nonterminalindex++) {
					findPathsFromStartRecursively(rulesNonTerminals.get(startSymbol)[nonterminalindex], endSymbol,
							current_paths);
				}
			}
		} else {
			endSymbol.setNumberOfPathsFromRoot(endSymbol.getNumberOfPathsFromRoot() + 1);
		}
	}

	// Pfade vom Startsymbol ausgeben
	public void printPathsFromStartToNonTerminal() {
		System.out.println("Non terminal and his paths from start symbol:");
		for (NonTerminal symbol : nonTerminals) {
			System.out.println(String.format("x%d has %d path(s) from start symbol.", symbol.getId(),
					symbol.getNumberOfPathsFromRoot()));
		}
		System.out.println();
	}

	// Pfade zum Terminal ausgeben
	public void printPathsFromNonTerminalToTerminal() {
		System.out.println("Non terminal and his paths to terminal:");
		for (NonTerminal symbol : nonTerminals) {
			System.out.println(String.format("x%d has %d path(s) from start symbol.", symbol.getId(),
					symbol.getNumberOfPathsToTerminal()));
		}
		System.out.println();
	}

	// Grammatik berechnen
	public void printSolvedGrammar() {
		System.out.println(String.format("Solved grammar: %s\n", grammarSolution));
	}

	// Berechnete Paare ausgeben
	public void printCompletePaths() {
		System.out.println("The symmetric centroid path decomposition results: ");
		for (NonTerminal symbol : nonTerminals) {
			System.out.print(String.format("x%d -> (%d, %d) -> ", symbol.getId(), symbol.getNumberOfPathsFromRoot(),
					symbol.getNumberOfPathsToTerminal()));
			// Logarithmieren der Paare
			int log1 = (int) (Math.log(symbol.getNumberOfPathsFromRoot()) / Math.log(2) + 1e-10);
			int log2 = (int) (Math.log(symbol.getNumberOfPathsToTerminal()) / Math.log(2) + 1e-10);
			System.out.println("(" + log1 + ", " + log2 + ")");
		}
		System.out.println();
	}

	// Logarithmierte Paare ausgeben
	public void printLogarithmicPathValues() {
		for (NonTerminal nonterminal : nonTerminals) {
			System.out.println(String.format("x%s: (%s, %s)", nonterminal.getId(),
					nonterminal.getLogarithmicNumberOfPathsFromRoot(),
					nonterminal.getLogarithmicNumberOfPathsToTerminal()));
		}
		System.out.println();
	}

	// Alle Centroidpfade herausfinden
	public void extractCentroidPaths() {
		// Liste aller Variablen, die zum Centroidpfad gehören können
		ArrayList<NonTerminal> nonVisitedSymbols = new ArrayList<>(Arrays.asList(nonTerminals));
		// Liste der Variablen durchgehen
		while (nonVisitedSymbols.size() > 0) {
			// Centroidpfad ist eine Liste von Nichtterminalen
			CentroidPath possibleCentroidPath = new CentroidPath();
			NonTerminal topSymbol = nonVisitedSymbols.get(0);
			possibleCentroidPath.add(topSymbol);
			// Rekursive Suche der Centroidpfade
			extractCentroidPathRecursively(topSymbol, possibleCentroidPath);
			// Entferne Symbole von 'nonVisitedSymbols', die nicht zum Centroidpfad gehören
			nonVisitedSymbols.removeAll(possibleCentroidPath.getPath());
			// Füge mögliche Centroidpfade zur Liste 'centroidPaths'.
			centroidPaths.add(possibleCentroidPath);
		}
		// Pfade mit der Länge 1 werden entfernt
		clearCentroidPaths();
	}

	// Berechnen der Centroidpfade
	private void extractCentroidPathRecursively(NonTerminal startSymbol, CentroidPath currentCentroidPath) {
		if (rulesNonTerminals.containsKey(startSymbol)) {
			NonTerminal[] nonterminalsymbols = rulesNonTerminals.get(startSymbol);
			for (int nonterminalindex = 0; nonterminalindex < nonterminalsymbols.length; nonterminalindex++) {
				// Vergleich der Paare
				if (startSymbol.getLogarithmicNumberOfPathsFromRoot() == nonterminalsymbols[nonterminalindex]
						.getLogarithmicNumberOfPathsFromRoot()
						&& startSymbol.getLogarithmicNumberOfPathsToTerminal() == nonterminalsymbols[nonterminalindex]
								.getLogarithmicNumberOfPathsToTerminal()) {
					currentCentroidPath.add(nonterminalsymbols[nonterminalindex]);
					extractCentroidPathRecursively(nonterminalsymbols[nonterminalindex], currentCentroidPath);
				}
			}
		}
	}

	// Centroidpfade ohne Länge 1
	private void clearCentroidPaths() {
		ArrayList<CentroidPath> clearedCentroidPaths = new ArrayList<CentroidPath>();
		for (CentroidPath centroidPath : centroidPaths) {
			// Falls Centroidpfad Länge 1 hat, dann entferne diesen
			if (centroidPath.size() != 1) {
				clearedCentroidPaths.add(centroidPath);
			}
		}
		centroidPaths = clearedCentroidPaths;
	}

	// Centroidpfade werden ausgegeben
	public void printCentroidPaths() {
		System.out.println(String.format("Count of symmetric centroid paths: %s", centroidPaths.size()));
		int counter = 1;
		// Liste der Centroidpfade durchgehen
		for (CentroidPath centroidPath : centroidPaths) {
			String centroidPathStr = String.format("Centroid path %s: ", counter);
			// Symbole im Centroidpfad
			for (NonTerminal symbol : centroidPath.getPath()) {
				centroidPathStr += String.format("x%s ", symbol.getId());
			}
			System.out.println(centroidPathStr);
			counter++;
		}
	}

	public void sideSymbolsOfCentroidPaths() {
		for (CentroidPath centroidPath : centroidPaths) {
			centroidPath.determineSideElements(rulesNonTerminals);
		}
	}

	public void printSidesOfCentroidPath() {
		for (CentroidPath centroidPath : centroidPaths) {
			centroidPath.printSideElements();
		}
	}

	public void calcHeightOfSymbols() {
		ArrayList<NonTerminal> symbolsNonFinalHeights = new ArrayList<>(Arrays.asList(nonTerminals));
		symbolsNonFinalHeights.removeAll(rulesTerminals.keySet());

		while (symbolsNonFinalHeights.size() > 0) {
			ArrayList<NonTerminal> symbolsToRemove = new ArrayList<NonTerminal>();
			for (NonTerminal nonTerminal : symbolsNonFinalHeights) {
				NonTerminal[] nonterminalsymbols = rulesNonTerminals.get(nonTerminal);
				if (nonterminalsymbols != null) {
					if (nonterminalsymbols[0].isFinalWeight() && nonterminalsymbols[1].isFinalWeight()) {
						nonTerminal.setWeight(nonterminalsymbols[0].getWeight() + nonterminalsymbols[1].getWeight());
						nonTerminal.isFinalWeight(true);
						symbolsToRemove.add(nonTerminal);
					}
				}
			}
			symbolsNonFinalHeights.removeAll(symbolsToRemove);
		}
	}

	public void printHeights() {
		for (NonTerminal symbol : nonTerminals) {
			System.out.println(String.format("Symbol %s has a depth of %s.", symbol.getId(), symbol.getWeight()));
		}
		System.out.println();
	}

	public void determineSeperation() {
		for (CentroidPath centroidPath : centroidPaths) {
			centroidPath.determineSeperation();			
		}
	}
	
//	public void createNewRules() {
//		for (CentroidPath centroidPath : centroidPaths) {
//			centroidPath.createNewRules();		
//		}
//	}
	
}