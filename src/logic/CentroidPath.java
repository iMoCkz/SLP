package logic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

public class CentroidPath {
	private static int centroidID = 0;
	private ArrayList<NonTerminal> path;
	private CentroidSideElements leftSideElements, rightSideElements;
	
	public CentroidPath() {
		path = new ArrayList<NonTerminal>();
		leftSideElements = new CentroidSideElements(true);
		rightSideElements = new CentroidSideElements(false);
	}
	
	public CentroidPath(ArrayList<NonTerminal> path) {
		this.path = path;
		leftSideElements = new CentroidSideElements(true);
		rightSideElements = new CentroidSideElements(false);	
	}

	public ArrayList<NonTerminal> getPath() {
		return path;
	}
	
	public CentroidSideElements getLeftSideElements() {
		return leftSideElements;
	}
	
	public CentroidSideElements getRightSideElements() {
		return rightSideElements;
	}
	
	public void add(NonTerminal nonterminal) {
		path.add(nonterminal);
	}
	
	public void add(ArrayList<NonTerminal> centroidOPath) {
		this.path = centroidOPath;
	}
	
	public int size() {
		return path.size();
	}
	
	public NonTerminal get(int index) {
		return path.get(index);
	}
	
	public boolean contains(NonTerminal nonTerminal) {
		return path.contains(nonTerminal);
	}
	
	public void determineSideElements(Map<NonTerminal, NonTerminal[]> rulesNonTerminals) {
		ArrayList<NonTerminal> leftSymbols = new ArrayList<NonTerminal>();
		ArrayList<NonTerminal> rightSymbols = new ArrayList<NonTerminal>();
		for (int symbolIndex = 0; symbolIndex < size() - 1; symbolIndex++) {
			NonTerminal[] sides = rulesNonTerminals.get(get(symbolIndex));
			if (!contains(sides[0])) {
				leftSymbols.add(sides[0]);
			}
			if (!contains(sides[1])) {
				rightSymbols.add(sides[1]);
			}
		}
		leftSideElements.addSideElements(leftSymbols);
		Collections.reverse(rightSymbols);
		rightSideElements.addSideElements(rightSymbols);
	}
	
	public void printSideElements() {
		// left side
		String leftString = String.format("L%s: ", centroidID + 1);
		for (NonTerminal leftSide : leftSideElements.getSideElements()) {
			leftString += String.format("x%s ", leftSide.getId());
		}
		System.out.println(leftString);
		// right side
		String rightString = String.format("R%s: ", centroidID + 1);
		for (NonTerminal rightSide : rightSideElements.getSideElements()) {
			rightString += String.format("x%s ", rightSide.getId());
		}
		System.out.println(rightString);
		System.out.println();
		centroidID++;
	}
	
	public void determineSeperation() {
		cutToShape(leftSideElements);
		cutToShape(rightSideElements);	
		System.out.println();
	}
	
//	public void createNewRules() {
//		leftSideElements.createNewRules();
//		rightSideElements.createNewRules();	
//	}
	
	private void cutToShape(CentroidSideElements sideElements) {
		ArrayList<NonTerminal> sidePath = sideElements.getSideElements();
		// Drehe Liste, wenn es sich um linksseitigen Pfad handelt.
		Collections.reverse(sidePath);
		// Berechne Gewicht der Suffixe
		int[] weightOfSuffixes = new int[sidePath.size()];
		// Erstes Suffix (atomares Suffix) hat keinen Vorgänger
		weightOfSuffixes[0] = sidePath.get(0).getWeight();
		// Berechne Gewicht der einzelnen Suffixe
		for (int suffixIndex = 1; suffixIndex < weightOfSuffixes.length; suffixIndex++) {
			weightOfSuffixes[suffixIndex] = weightOfSuffixes[suffixIndex - 1] + sidePath.get(suffixIndex).getWeight();
		}
		// Logarithmiere Suffix-Höhe
		for (int suffixIndex = 0; suffixIndex < weightOfSuffixes.length; suffixIndex++) {
			weightOfSuffixes[suffixIndex] = (int) Math.ceil(Math.log(weightOfSuffixes[suffixIndex]) / Math.log(2));
			// TESTAUSGABE Suffix-Gewicht
//			printSuffixe(isLeftSide, sidePath, weightOfSuffixes, suffixIndex);
		}
		//
		for (int suffixIndex = 0; suffixIndex < weightOfSuffixes.length; suffixIndex++) {
			if (weightOfSuffixes[suffixIndex] >= weightOfSuffixes[weightOfSuffixes.length - 1]) {
				// Bestimme Aufteilung c & b_1 .. b_m
				ArrayList<NonTerminal> b_m = new ArrayList<NonTerminal>();
				for (int i = suffixIndex - 1; i >= 0; i--) {
					b_m.add(sidePath.get(i));
				}
				sideElements.determineCAndB_m(sidePath.get(suffixIndex), b_m);
				sideElements.printAufteilung();
				break;
			}
		}
	}
	
	private void printSuffixe(boolean isLeftSide, ArrayList<NonTerminal> sidePath, int[] weightOfSuffixes,
			int suffixIndex) {
		String suffix = "";
		if (isLeftSide) {
			for (int i = suffixIndex; i >= 0; i--) {
				suffix += String.format("x%s", sidePath.get(i).getId());
				if (i > 0)
					suffix += " ";
			}
		} else {
			for (int i = 0; i <= suffixIndex; i++) {
				suffix += String.format("x%s", sidePath.get(i).getId());
				if (i < suffixIndex)
					suffix += " ";
			}
		}

		System.out.println(String.format("Suffix: %s | Suffix length: %s | Log_2 Weight: %s", suffix, suffixIndex + 1,
				weightOfSuffixes[suffixIndex]));
	}
	
}
