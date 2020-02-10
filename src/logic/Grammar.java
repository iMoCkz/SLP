package logic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class Grammar {

	private NonTerminal grammarStartSymbol;
	private Map<NonTerminal, NonTerminal[]> rulesNonTerminals;
	private Map<NonTerminal, String> rulesTerminals;
	private NonTerminal[] nonTerminals;
	public String grammarSolution;
	public ArrayList<CentroidPath> centroidPaths;
	public ArrayList<ArrayList<NonTerminal>> LeftSidesCentroidPath, RightSidesCentroidPath;
	private Map<NonTerminal, NonTerminal[]> reducedRules;
	private int newRulesCounter = 0;
	private String grammarName;


	// Konstruktor
	public Grammar(String grammarName, NonTerminal startSymbol, Map<NonTerminal, NonTerminal[]> rulesNonTerminals,
			Map<NonTerminal, String> rulesTerminals, NonTerminal[] nonTerminals) {
		this.grammarName = grammarName;
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
		reducedRules = new HashMap<NonTerminal, NonTerminal[]>();
	}

	public Map<NonTerminal, NonTerminal[]> getReducedRules() {
		return reducedRules;
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
		System.out.println(String.format("Solved grammar %s: %s\n", grammarName, grammarSolution));
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
			CentroidPath possibleCentroidPath = new CentroidPath(nonTerminals.length);
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
	
	public void createNewRules() {
		for (CentroidPath centroidPath : centroidPaths) {
			centroidPath.abc();	
			System.out.println();
		}
	}
	
	public void createRulesFromSPConnection() {
		for (CentroidPath centroidPath : centroidPaths) {
			// Center Non Terminal
			NonTerminal centerNonTerminal = centroidPath.getPath().get(centroidPath.getPath().size() - 1);
			// S-Rules
			ArrayList<NonTerminal[]> SRules = new ArrayList<NonTerminal[]>();
			ArrayList<NonTerminal> leftSides = centroidPath.getLeftSideElements().getElements();
			for (int centroidElementIndex = 0; centroidElementIndex < centroidPath.size(); centroidElementIndex++) {
				NonTerminal[] leftRightElements = rulesNonTerminals.get(centroidPath.getPath().get(centroidElementIndex));
				int i = 0;
				for (NonTerminal[] nonTerminals : centroidPath.getLeftSideElements().getNewRules_S().values()) {
					if (Arrays.deepEquals(leftSides.toArray(NonTerminal[]::new), nonTerminals)) {
						SRules.add(nonTerminals);
//						System.out.println(String.format("S%s", ++i));
						break;
					} else {
						i++;
					}
				}	
				// Entferne Element, wenn 
				if (leftSides.contains(leftRightElements[0])) {
					leftSides.remove(leftRightElements[0]);
				}
			}
			// P-Rules	
			ArrayList<NonTerminal[]> PRules = new ArrayList<NonTerminal[]>();
			ArrayList<NonTerminal> rightSides = centroidPath.getRightSideElements().getElements();
			Collections.reverse(rightSides);
			for (int centroidElementIndex = 0; centroidElementIndex < centroidPath.size(); centroidElementIndex++) {
				NonTerminal[] leftRightElements = rulesNonTerminals.get(centroidPath.getPath().get(centroidElementIndex));
				int i = 0;
				for (NonTerminal[] nonTerminals : centroidPath.getRightSideElements().getNewRules_P().values()) {
					if (Arrays.deepEquals(rightSides.toArray(NonTerminal[]::new), nonTerminals)) {
						PRules.add(nonTerminals);
//						System.out.println(String.format("P%s", ++i));
						break;
					} else {
						i++;
					}
				}	
				
				if (rightSides.contains(leftRightElements[1])) {
					rightSides.remove(leftRightElements[1]);
				}
			}
			createRules(SRules, PRules, centerNonTerminal);			
			System.out.println();
		}
		addRulesBeyondCentroidPath();
	}
	
	private void createRules(ArrayList<NonTerminal[]> SRules, ArrayList<NonTerminal[]> PRules, NonTerminal centerNonTerminal) {
		int maxSOrPRulesSize = Math.max(SRules.size(), PRules.size());
		for (int spRulesIndex = 0; spRulesIndex < maxSOrPRulesSize; spRulesIndex++) {
			ArrayList<NonTerminal> newCompleteRule = new ArrayList<NonTerminal>();
			// S-Rules
			if (spRulesIndex < SRules.size()) {
				newCompleteRule.addAll(new ArrayList<>(Arrays.asList(SRules.get(spRulesIndex))));
			}
			// Last element from centroid
			newCompleteRule.add(centerNonTerminal);
			// P-Rules
			if (spRulesIndex < PRules.size()) {
				newCompleteRule.addAll(new ArrayList<>(Arrays.asList(PRules.get(spRulesIndex))));
			}
			printNonTerminalArrays(newRulesCounter, newCompleteRule);
			reducedRules.put(new NonTerminal(newRulesCounter++), newCompleteRule.toArray(NonTerminal[]::new));
		}
		reducedRules.put(centerNonTerminal, rulesNonTerminals.get(centerNonTerminal));		
	}
	
	private void addRulesBeyondCentroidPath() {
		for (NonTerminal nonTerminal : nonTerminals) {
			if (!containedInCentroid(nonTerminal) && rulesNonTerminals.keySet().contains(nonTerminal)) {
				reducedRules.put(nonTerminal, rulesNonTerminals.get(nonTerminal));
			}
		}
	}
	
	public Grammar SLPGrammar() {	
		// NonTerminalRules
		Set<NonTerminal> nonTsSet = reducedRules.keySet();
		NonTerminal[] nonTs = nonTsSet.toArray(new NonTerminal[nonTsSet.size()]);
		// TerminalRules
		Set<NonTerminal> TsSet = rulesTerminals.keySet();
		NonTerminal[] Ts = TsSet.toArray(new NonTerminal[TsSet.size()]);
		// Concatenate to obtain all terminal symbols
		NonTerminal[] allTerminals = extendNonTerminals(nonTs, Ts);
		// Sort terminals by id
		Arrays.sort(allTerminals, Comparator.comparing(NonTerminal::getId));

		return new Grammar("H", allTerminals[0], reducedRules, rulesTerminals, allTerminals);
	}
	
	private NonTerminal[] extendNonTerminals(NonTerminal[] currentNonTs, NonTerminal[] currentTs) {
		NonTerminal[] completeArray = new NonTerminal[currentNonTs.length + currentTs.length];
		int terminalCounter = 0;
		for (int i = 0; i < completeArray.length; i++) {			
			completeArray[i] = i < currentNonTs.length ? currentNonTs[i] :  currentTs[terminalCounter++];
		}
		return completeArray;
	}
	
	private boolean containedInCentroid(NonTerminal nonTerminal) {
		for (CentroidPath centroid : centroidPaths) {
			if (centroid.getPath().contains(nonTerminal)) {
				return true;
			}
		}		
		return false;
	}
	
	private void ausgabe(ArrayList<NonTerminal> test) {
		String bla = "";
		for (NonTerminal sym : test) {
			bla += sym.getId() + " ";
		}
		System.out.println(bla);
	}
	
	public static void printNonTerminalArrays(int ruleNr, ArrayList<NonTerminal> nonTs) {
		String rule = "";
		for (int i = 0; i < nonTs.size(); i++) {
			rule += String.format("x%s ", nonTs.get(i).getId());
		}
		System.out.println(String.format("x%s -> %s", ruleNr, rule));
	}

	public void printNonterminals() {
		for (NonTerminal nT : nonTerminals) {
			System.out.println(nT.getId());
		}
	}
	
	public void printGrammarRules() {
		System.out.println(String.format("Grammar %s's rules are:", grammarName));
		for (NonTerminal nT : nonTerminals) {
			if (rulesNonTerminals.containsKey(nT)) {
				NonTerminal[] nTs = rulesNonTerminals.get(nT);
				String rule = String.format("x%s -> ", nT.getId());			
				for (int i = 0; i < nTs.length; i++) {
					rule += String.format("x%s ", nTs[i].getId());
				}
				System.out.println(rule);
			}			
		}
		for (NonTerminal nT : nonTerminals) {
			if (rulesTerminals.containsKey(nT)) {
				System.out.println(String.format("x%s -> %s", nT.getId(), rulesTerminals.get(nT)));
			}
		}
		System.out.println();
	}
}