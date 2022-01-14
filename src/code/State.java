package code;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;

public class State {

	public String stateString;

	public static int death = 0;
	public static List<String> stateString1 = new ArrayList<>();
	public static int[] NeoInfo;

// **********************  stateString **********************************

//	NeoX, NeoY, NeoDamage, carriedCount;
//
//	AGENT1_isKilled, .... , AGENTk_isKilled; -- > AGENTi_isKilled = '0' or '1' 
//
//	PILL1_isTaken, .... , PILLk_isTaken; -- > PILLi_isTaken = '0' or '1'
//
//	HOSTAGE1_damage, HOSTAGE1_isCarried , ... , HOSTAGEk_damage , HOSTAGEk_isCarried; 
//
//	--> HOSTAGEi_damage = "32", HOSTAGEi_isCarried = '0' or '1' or '2'
//
//	example:
//	        string = "5,5,0,0;0,0,0,0;0,0;32,0,60,0,24,0,88,0"
//	
//  HOSTAGEi_isCarried = 2 --> hostage dropped in TelephoneBooth 
//  HOSTAGEi_damage = -1 --> dead hostage is killed 

	public State(String stateString) {
		this.stateString = stateString;
	}

	// prints the (m x n) grid according to the extracted information stored in
	// stateString of the state.
	public void visualize() {
		// create a 2D grid with a size of (m * n).
		String[][] grid = new String[Matrix.Height][Matrix.Width];
		for (int i = 0; i < grid.length; ++i) {
			for (int j = 0; j < grid[0].length; ++j) {
				grid[i][j] = "___ ";
			}
		}

		// extract information of the grid found in the stateString.
		List<String> stateStringArray = Arrays.asList(stateString.split(";"));
		int[] NeoInfo = Matrix.convertStringIntArr(stateStringArray.get(0));
		List<String> agentsInfo = Arrays.asList(stateStringArray.get(1).split(","));
		List<String> pillsInfo = Arrays.asList(stateStringArray.get(2).split(","));
		List<String> hostagesInfo = Arrays.asList(stateStringArray.get(3).split(","));

		boolean NEOPrinted = false;

		// assign hostages to their positions in the 2D grid
		for (int i = 0; i < hostagesInfo.size(); i += 2) {

			int[] hostagePositions = Matrix
					.convertStringIntArr(Matrix.convertHashtable(Matrix.hostagesPosition).get(i / 2));

			// if a hostage is dead, carried, or dropped in TB, then ignore
			if (hostagesInfo.get(i).equals("-1") || hostagesInfo.get(i + 1).equals("1")
					|| hostagesInfo.get(i + 1).equals("2"))
				continue;

			if (NeoInfo[0] == hostagePositions[0] && NeoInfo[1] == hostagePositions[1]) {
				grid[NeoInfo[0]][NeoInfo[1]] = "NEO_" + NeoInfo[3] + "+HOS";
				NEOPrinted = true;
			} else {
				grid[hostagePositions[0]][hostagePositions[1]] = "HOS";
			}
		}

		// assign agents to their positions in the 2D grid
		for (int i = 0; i < agentsInfo.size(); i++) {
			// if a agent is killed ignore
			if (agentsInfo.get(i).equals("1"))
				continue;
			int[] agentsPositions = Matrix.convertStringIntArr(Matrix.convertHashtable(Matrix.agentsPosition).get(i));
			grid[agentsPositions[0]][agentsPositions[1]] = "AGE";
		}

		// assign pills to their positions in the 2D grid
		for (int i = 0; i < pillsInfo.size(); i++) {
			// if a pill is taken ignore
			if (pillsInfo.get(i).equals("1"))
				continue;

			int[] pillPositions = Matrix.convertStringIntArr(Matrix.convertHashtable(Matrix.pillsPosition).get(i));

			if (NeoInfo[0] == pillPositions[0] && NeoInfo[1] == pillPositions[1]) {
				grid[NeoInfo[0]][NeoInfo[1]] = "NEO_" + NeoInfo[3] + "+PIL";
				NEOPrinted = true;
			} else {
				grid[pillPositions[0]][pillPositions[1]] = "PIL";
			}
		}

		// assign pads to their positions in the 2D grid
		Enumeration<String> e = Matrix.padsPositions.keys();
		while (e.hasMoreElements()) {

			String key = e.nextElement();
			int[] padsPosition = Matrix.convertStringIntArr(key);

			if (padsPosition[0] == NeoInfo[0] && padsPosition[1] == NeoInfo[1]) {
				grid[NeoInfo[0]][NeoInfo[1]] = "NEO" + NeoInfo[3] + "+PAD";
				NEOPrinted = true;
			} else {
				grid[padsPosition[0]][padsPosition[1]] = "PAD";
			}
		}

		// assign telephone booth and Neo to their positions in the 2D grid
		if (NeoInfo[0] == Matrix.telephoneBoothPosition.getX() && NeoInfo[1] == Matrix.telephoneBoothPosition.getY())
			grid[NeoInfo[0]][NeoInfo[1]] = "NEO_" + NeoInfo[3] + "+TB";
		else {
			if (!NEOPrinted)
				grid[NeoInfo[0]][NeoInfo[1]] = "NEO_" + NeoInfo[3];
			grid[Matrix.telephoneBoothPosition.getX()][Matrix.telephoneBoothPosition.getY()] = "TB";
		}

		String dash = "---------------------------------------------------";

		// printing the 2D grid
		for (int i = 0; i < grid.length; ++i) {
			System.out.println(Arrays.toString(grid[i]));
		}
		System.out.println(dash);

	}

	// returns a double number representing the cost calculated to a specific input
	// action.
	public double getCostFunction(int action) {
		int numberOfDeath = 0;
		int numberOfKills = 0;

		for (int i = 0; i < Matrix.hostagesInfo.size(); i += 2) {
			if (Matrix.hostagesInfo.get(i).equals("100"))
				numberOfDeath++;
			if (Matrix.hostagesInfo.get(i).equals("-1")) {
				numberOfDeath++;
				numberOfKills++;
			}
		}

		for (int i = 0; i < Matrix.agentsInfo.size(); i++) {
			if (Matrix.agentsInfo.get(i).equals("1"))
				numberOfKills++;
		}

		numberOfDeath = numberOfDeath - death;

		numberOfDeath = numberOfDeath * 2000;
		numberOfKills = numberOfKills * 1000;

		int cost = 0;

		switch (action) {
		case 5: {
			cost = 4;
			break;
		}
		case 6: {
			cost = 4;
			break;
		}
		case 8: {
			cost = 4;
			break;
		}
		case 7: {
			cost = 4;
			break;
		}
		case 1: {
			cost = 2;
			break;
		}
		case 0: {
			cost = 1;
			break;
		}
		case 3: {
			cost = 0;
			break;
		}
		case 2: {
			cost = numberOfKills;
			break;
		}
		case 4: {
			cost = 4;
			break;
		}
		default:
			throw new IllegalArgumentException("Unexpected value: " + action);
		}

		return cost + numberOfDeath;
	}

	// returns a double number representing the first heuristic calculated for a
	// specific node.
	public double getHeuristicValueOne(Node node) {
		double dx = Math.abs(getNeoX(node) - getBoothX());
		double dy = Math.abs(getNeoY(node) - getBoothY());
		return 4 * (dx + dy);
	}

	public static int getNeoX(Node node) {
		stateString1 = Arrays.asList(node.state.stateString.split(";"));
		NeoInfo = Matrix.convertStringIntArr(stateString1.get(0));
		return NeoInfo[0];

	}

	public static int getNeoY(Node node) {
		stateString1 = Arrays.asList(node.state.stateString.split(";"));
		NeoInfo = Matrix.convertStringIntArr(stateString1.get(0));
		return NeoInfo[1];
	}

	public static int getBoothX() {
		return Matrix.telephoneBoothPosition.getX();
	}

	public static int getBoothY() {
		return Matrix.telephoneBoothPosition.getY();
	}
	public double getEucDis(int[] p1, int[] p2) {
		int dis1 = p1[0] - p2[0];
		int dis2 = p1[1] - p2[1];
		return Math.sqrt(Math.pow(dis1, 2) + Math.pow(dis2, 2));
	}
	// returns a double number representing the second heuristic calculated for specific node.
	public double getHeuristicValueTwo(Node node) {
		int[] NeoPosition = { getNeoX(node), getNeoY(node) };
		int[] TelephoneBoothPosition = { getBoothX(), getBoothY() };

		return getEucDis(NeoPosition, TelephoneBoothPosition) * 4;
	}



}
