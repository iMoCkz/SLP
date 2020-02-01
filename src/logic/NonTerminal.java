package logic;

public class NonTerminal {
	
	// ID des Symbols
	private int id;
	// Pfade vom Wurzel
	private int numberOfPathsFromRoot;
	// Pfade zum Terminal
	private int numberOfPathsToTerminal;	
	// 
	private int weight;
	//
	private boolean isFinalWeight = false;
	

	// Konstruktor
	public NonTerminal(int id) {
		this.id = id; 
	}
	
	// Getter und Setter
	public int getId() {
		return id;
	}

	public int getWeight() {
		return weight;
	}
	
	public void setWeight(int height) {
		this.weight = height;
	}
	
	public boolean isFinalWeight() {
		return isFinalWeight;
	}

	public void isFinalWeight(boolean isFinalHeight) {
		this.isFinalWeight = isFinalHeight;
	}
	
	public int getNumberOfPathsFromRoot() {
		return numberOfPathsFromRoot;
	}

	public void setNumberOfPathsFromRoot(int numberOfPathsFromRoot) {
		this.numberOfPathsFromRoot = numberOfPathsFromRoot;
	}

	public int getLogarithmicNumberOfPathsFromRoot() {
		return (int) (Math.log(getNumberOfPathsFromRoot()) / Math.log(2) + 1e-10);
	}

	public int getLogarithmicNumberOfPathsToTerminal() {
		return (int) (Math.log(getNumberOfPathsToTerminal()) / Math.log(2) + 1e-10);
	}

	public int getNumberOfPathsToTerminal() {
		return numberOfPathsToTerminal;
	}

	public void setNumberOfPathsToTerminal(int numberOfPathsToTerminal) {
		this.numberOfPathsToTerminal = numberOfPathsToTerminal;
	}
}
