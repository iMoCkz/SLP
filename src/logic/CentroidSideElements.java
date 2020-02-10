package logic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class CentroidSideElements {
	private ArrayList<NonTerminal> sideElements;
	private NonTerminal c;
	private ArrayList<NonTerminal> b_m;
	private ArrayList<NonTerminal> a_k;
	private boolean isLeftPath;
	private int newRulesId;
	private Map<NonTerminal, NonTerminal[]> newRules_a;
	private Map<NonTerminal, NonTerminal[]> newRules_b;
	private int grammarSize;
	private static int SRuleNr = 1;
	private static int PRuleNr = 1;
	private LinkedHashMap<NonTerminal, NonTerminal[]> newRules_S;	
	private LinkedHashMap<NonTerminal, NonTerminal[]> newRules_P;

	public CentroidSideElements(int grammarSize, boolean isLeftPath) {
		this.grammarSize = grammarSize;
		this.isLeftPath = isLeftPath;
		this.newRules_a = new LinkedHashMap<NonTerminal, NonTerminal[]>();
		this.newRules_b = new LinkedHashMap<NonTerminal, NonTerminal[]>();
		this.newRules_S = new LinkedHashMap<NonTerminal, NonTerminal[]>();
		this.newRules_P = new LinkedHashMap<NonTerminal, NonTerminal[]>();
	}

	public LinkedHashMap<NonTerminal, NonTerminal[]> getNewRules_S() {
		return newRules_S;
	}

	public LinkedHashMap<NonTerminal, NonTerminal[]> getNewRules_P() {
		return newRules_P;
	}
	
	public void addSideElements(ArrayList<NonTerminal> sideElements) {
		this.sideElements = sideElements;
	}

	public int size() {
		return sideElements.size();
	}

	public ArrayList<NonTerminal> getElements() {
		return sideElements;
	}

	public void determineCAndB_m(NonTerminal c, ArrayList<NonTerminal> b_m) {
		this.c = c;
		this.b_m = b_m;
		//
		this.a_k = new ArrayList<NonTerminal>();
		for (NonTerminal symbol : sideElements) {
			if (!this.c.equals(symbol) && !b_m.contains(symbol)) {
				this.a_k.add(symbol);
			}
		}
//		if (this.isLeftPath)
		Collections.reverse(this.a_k);
	}

	public void printAufteilung() {
		// a_k's ausgeben
		String a_k = "";
		for (int i = 0; i < this.a_k.size(); i++) {
			a_k += String.format("x%s", this.a_k.get(i).getId());
			if (i < this.a_k.size() - 1) {
				a_k += " ";
			}
		}
		// b_m's ausgeben
		String b_m = "";
		for (int i = 0; i < this.b_m.size(); i++) {
			b_m += String.format("x%s", this.b_m.get(i).getId());
			if (i < this.b_m.size() - 1) {
				b_m += " ";
			}
		}
		System.out.println(String.format("a_k = %s | c = x%s | b_m = %s", a_k, this.c.getId(), b_m));

		createNewRules();
		System.out.println();
	}

	public void createNewRules() {
		newRulesId = 1;
		// Regeln aus a_k
		for (int i = 1; i <= this.a_k.size() - 1; i += 2) {
			this.newRules_a.put(new NonTerminal(newRulesId),
					new NonTerminal[] { this.a_k.get(i - 1), this.a_k.get(i) });
		}	
		if (this.a_k.size() % 2 != 0) {
			this.newRules_a.put(new NonTerminal(newRulesId), new NonTerminal[] { this.a_k.get(this.a_k.size() - 1) });
		}
//		ausgabe2();
		// Regel aus b_m
		newRulesId = 1;
		for (int i = 0; i < this.b_m.size(); i++) {
			ArrayList<NonTerminal> rules = new ArrayList<NonTerminal>();
			for (int j = i; j < this.b_m.size(); j++) {
				rules.add(this.b_m.get(j));
			}
			newRules_b.put(new NonTerminal(newRulesId), rules.toArray(NonTerminal[]::new));
//			ausgabe(rules);
			if (i == 0) {
				rules.add(0, this.c);
				newRules_b.put(new NonTerminal(newRulesId), rules.toArray(NonTerminal[]::new));
//				ausgabe(rules);
			}
		}
	}

	public void createNewSPRules() {	
		Collections.reverse(sideElements);

		for (int i = 0; i < sideElements.size(); i++) {
			String test = "";
			ArrayList<NonTerminal> rules = new ArrayList<NonTerminal>();
			if (isLeftPath) {
				test = String.format("S%s -> ", SRuleNr++);
				for (int j = i; j < sideElements.size(); j++){
					rules.add(sideElements.get(j));
					test += String.format("x%s ", sideElements.get(j).getId());
				}
				newRules_S.put(new NonTerminal(newRulesId), rules.toArray(NonTerminal[]::new));

			} else {
				test = String.format("P%s -> ", PRuleNr++);
				for (int j = sideElements.size() - 1; j >= i; j--){
					rules.add(sideElements.get(j));
					test += String.format("x%s ", sideElements.get(j).getId());
				}
				newRules_P.put(new NonTerminal(newRulesId), rules.toArray(NonTerminal[]::new));
			}					
			System.out.println(test);
		}
	}
	
	private void ausgabe() {
		for (NonTerminal symbol : newRules_b.keySet()) {
			System.out.println(symbol.getId());
		}
	}

	private void ausgabe(ArrayList<NonTerminal> list) {
		String ausgabe = "";
		for (int i = 0; i < list.size(); i++) {
			ausgabe += String.format("x%s", list.get(i).getId());
			if (i < list.size() - 1) {
				ausgabe += " ";
			}
		}
		System.out.println(String.format("V%s: %s", newRulesId++, ausgabe));
	}
	
	private void ausgabe2() {
		for (NonTerminal key : this.newRules_a.keySet())
			System.out.println(key.getId());
	}

	
}
