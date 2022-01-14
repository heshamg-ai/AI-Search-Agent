package code;



import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Random;

public abstract class Matrix extends Search_Problem {

	public static int capacity;
	public static int Width;
	public static int Height;
	public static Position telephoneBoothPosition;
	public static Hashtable<String, String> padsPositions;
	public static Hashtable<String, Integer> agentsPosition;
	public static Hashtable<String, Integer> pillsPosition;
	public static Hashtable<String, Integer> hostagesPosition;
	public static int numberOfDeaths = 0;
	public static int numberOfKills = 0;

	abstract public Node search(Node root);

	// converts the key with value of hashtable
	public static Hashtable<Integer, String> convertHashtable(Hashtable<String, Integer> hashtable) {
		Hashtable<Integer, String> newHashtable = new Hashtable<>();

		Enumeration<String> e = hashtable.keys();

		// loop over input hashtable
		while (e.hasMoreElements()) {
			String key = e.nextElement();
			// insert into newHashtable keys and values of input hashtable reversed
			newHashtable.put(hashtable.get(key), key);
		}

		return newHashtable;
	}

	// returns a randomized ArrayList containing all positions from (0, 0) to (m-1,
	// n-1)
	private static ArrayList<Position> generateRandomUniquePositions(int m, int n) {

		ArrayList<Position> allPositions = new ArrayList<>();

		// creates an ArrayList containing all positions from (0, 0) to (m-1, n-1)
		for (int i = 0; i < m; ++i) {
			for (int j = 0; j < n; ++j) {
				allPositions.add(new Position(i, j));
			}
		}
		// shuffles the ArrayList
		Collections.shuffle(allPositions);
		return allPositions;
	}

	// returns array of integers after splitting a string of numbers separated with
	// commas
	public static int[] convertStringIntArr(String str) {
		return Arrays.stream(str.split(",")).mapToInt(Integer::parseInt).toArray();
	}

	// generates the initial state string
	public static String createStateString(String grid) {
		String[] info = grid.split(";");

		// putting in the stateString 'NeoX, NeoY, 0 (neoDamage), 0 (carriedCount);'
		String state = info[2] + ",0,0;";

		int[] hostageCoordinates = convertStringIntArr(info[7]);
		int agentsCount = convertStringIntArr(info[4]).length / 2;
		int pillsCount = convertStringIntArr(info[5]).length / 2;
		int hostagesCount = hostageCoordinates.length / 3;

		// putting in the stateString '0,' agentsCount times indicating that all agents
		// initially alive
		for (int i = 0; i < agentsCount; i++) {
			state += "0";

			if (i != agentsCount - 1)
				state += ",";
		}
		state += ";";

		// putting in the stateString '0,' pillsCount times indicating that all pills
		// initially are not taken
		for (int i = 0; i < pillsCount; i++) {
			state += "0";

			if (i != pillsCount - 1)
				state += ",";
		}
		state += ";";

		// putting in the stateString 'hostageDamage, 0 (isCarried);' of all hostages
		for (int i = 0; i < hostagesCount; i++) {
			state = state + hostageCoordinates[i * 3 + 2] + ",0";

			if (i != hostagesCount - 1)
				state += ",";
		}

		// extracting info from the string grid
		int[] mn = convertStringIntArr(info[0]);
		Width = mn[0];
		Height = mn[1];
		capacity = Integer.parseInt(info[1]);
		telephoneBoothPosition = new Position(convertStringIntArr(info[3])[0], convertStringIntArr(info[3])[1]);

		// filling the pads positions hashtable from string grid
		padsPositions = new Hashtable<>();

		int[] padsCoordinates = convertStringIntArr(info[6]);

		for (int i = 0; i < padsCoordinates.length; i += 4) {
			padsPositions.put(padsCoordinates[i] + "," + padsCoordinates[i + 1],
					padsCoordinates[i + 2] + "," + padsCoordinates[i + 3]);
			padsPositions.put(padsCoordinates[i + 2] + "," + padsCoordinates[i + 3],
					padsCoordinates[i] + "," + padsCoordinates[i + 1]);

		}

		// filling the agents positions array from string grid
		agentsPosition = new Hashtable<String, Integer>();

		int[] agentPositionsTemp = convertStringIntArr(info[4]);

		for (int i = 0; i < agentPositionsTemp.length / 2; i++) {
			agentsPosition.put(agentPositionsTemp[i * 2] + "," + agentPositionsTemp[i * 2 + 1], i);
		}

		// filling the pills positions array from string grid
		pillsPosition = new Hashtable<String, Integer>();

		for (int i = 0; i < convertStringIntArr(info[5]).length; i += 2) {
			pillsPosition.put(convertStringIntArr(info[5])[i] + "," + convertStringIntArr(info[5])[i + 1], i / 2);
		}

		// filling the hostages positions array from string grid
		hostagesPosition = new Hashtable<String, Integer>();
		int c = 0;
		for (int i = 0; i < hostageCoordinates.length; i += 3) {
			hostagesPosition.put(hostageCoordinates[i] + "," + hostageCoordinates[i + 1], c);
			c++;
		}

		return state;
	}

	// returns the grid string

	public static String genGrid() {

		Random rand = new Random();

		// randomizing grid of m x n in range 5*5 and 15*15
		int m = rand.nextInt(11) + 5;
		int n = rand.nextInt(11) + 5;

		// randomizing hostages count
		int hostagesCount = rand.nextInt(8) + 3;

		// randomizing pills count
		int pillsCount = rand.nextInt(hostagesCount) + 1;

		// randomizing Neo carry limit c<=4
		int c = rand.nextInt(4) + 1;

		// calculating pads count
		int padsCount = m * n / 4;
		if (padsCount % 2 != 0) {
			padsCount++;
		}

		// calculating agents count
		int agentsCount = hostagesCount * 2;

		ArrayList<Position> allPositions = generateRandomUniquePositions(m, n);
		Position neoP = allPositions.get(0);
		Position telephoneBoothPosition = allPositions.get(1);

		// creating a string grid containing: 'M, N; capacity; NeoX, NeoY; TB_X, TB_Y;
		String grid = String.format("%d,%d;%d;%d,%d;%d,%d;", m, n, c, neoP.x, neoP.y, telephoneBoothPosition.x,
				telephoneBoothPosition.y);

		// concatenating into string grid all agents positions
		for (int i = 0; i < agentsCount; i++) {
			Position temp = allPositions.get(i + 2);
			if (i == agentsCount - 1)
				grid += String.format("%d,%d;", temp.x, temp.y);
			else
				grid += String.format("%d,%d,", temp.x, temp.y);

		}

		// concatenating into string grid all pills positions
		for (int i = 0; i < pillsCount; i++) {
			Position temp = allPositions.get(i + 2 + agentsCount);

			if (i == pillsCount - 1)
				grid += String.format("%d,%d;", temp.x, temp.y);
			else
				grid += String.format("%d,%d,", temp.x, temp.y);
		}

		// concatenating into string grid all pads positions
		for (int i = 0; i < padsCount; i++) {
			Position temp = allPositions.get(i + 2 + agentsCount + pillsCount);
			Position temp2 = allPositions.get(i + 2 + agentsCount + pillsCount + 1);

			if (i == padsCount - 2) {
				grid += String.format("%d,%d,%d,%d,", temp.x, temp.y, temp2.x, temp2.y);
				grid += String.format("%d,%d,%d,%d;", temp2.x, temp2.y, temp.x, temp.y);
				break;
			} else
				grid += String.format("%d,%d,%d,%d,", temp.x, temp.y, temp2.x, temp2.y);
			grid += String.format("%d,%d,%d,%d,", temp2.x, temp2.y, temp.x, temp.y);
			i++;
		}

		// concatenating into string grid all hostages positions along with the
		// randomized damage for each ranging from (1, 99)
		for (int i = 0; i < hostagesCount; i++) {
			Position temp = allPositions.get(i + 2 + agentsCount + pillsCount + padsCount);
			int damage = rand.nextInt(99) + 1;
			if (i == hostagesCount - 1)
				grid += String.format("%d,%d,%d", temp.x, temp.y, damage);
			else
				grid += String.format("%d,%d,%d,", temp.x, temp.y, damage);

		}

		return grid;

	}

	// returns string path of the solution if one is found
	public static String solve(String grid, String strategy, boolean visualize) {
		// re-initializing static variables
		numberOfDeaths = 0;
		numberOfKills = 0;
		Node.expandedNodes = 0;
		path = "";

		// creating the problem's root node with its state containing stateString
		String state = createStateString(grid);
		initialState = new State(state);
		Node root = new Node(null, -1, 0, 0, initialState);

		Node goalNode = null;

		// instantiate a different class, depending on which input strategy is
		// specified, and calling search method on its instance in order to find a
		// solution to the problem.
		switch (strategy) {
		case "BF": {
			BFS bfs = new BFS();
			goalNode = bfs.search(root);
			break;
		}
		case "DF": {
			DFS dfs = new DFS();
			goalNode = dfs.search(root);
			break;

		}
		case "ID": {
			IDS ids = new IDS();
			goalNode = ids.search(root);
			break;
		}
		case "UC": {
			UCS ucs = new UCS();
			goalNode = ucs.search(root);
			break;
		}
		case "GR1": {
			GRS grs = new GRS();
			grs.setGreedy("GR1");
			goalNode = grs.search(root);
			break;

		}
		case "GR2": {
			GRS grs = new GRS();
			grs.setGreedy("GR2");
			goalNode = grs.search(root);
			break;
		}
		case "AS1": {
			ASTAR astar = new ASTAR();
			astar.setGreedy("AS1");
			goalNode = astar.search(root);
			break;
		}
		case "AS2": {
			ASTAR astar = new ASTAR();
			astar.setGreedy("AS2");
			goalNode = astar.search(root);
			break;

		}
		default:
			System.out.println("Invalid Strategy!");
		}

		// checks if no goal node found, then return 'No Solution'
		if (goalNode == null)
			return "No Solution";

		updateStack(goalNode);
		updatePath(visualize);

		return path;
	}

	


	public static void main(String[] args) {
		
		long startTime = System.currentTimeMillis();
		
		String grid0 = "5,5;2;3,4;1,2;0,3,1,4;2,3;4,4,0,2,0,2,4,4;2,2,91,2,4,62";
		String grid1 = "5,5;1;1,4;1,0;0,4;0,0,2,2;3,4,4,2,4,2,3,4;0,2,32,0,1,38";
		String grid2 = "5,5;2;3,2;0,1;4,1;0,3;1,2,4,2,4,2,1,2,0,4,3,0,3,0,0,4;1,1,77,3,4,34";
		String grid3 = "5,5;1;0,4;4,4;0,3,1,4,2,1,3,0,4,1;4,0;2,4,3,4,3,4,2,4;0,2,98,1,2,98,2,2,98,3,2,98,4,2,98,2,0,1";
		String grid4 = "5,5;1;0,4;4,4;0,3,1,4,2,1,3,0,4,1;4,0;2,4,3,4,3,4,2,4;0,2,98,1,2,98,2,2,98,3,2,98,4,2,98,2,0,98,1,0,98";
		String grid5 = "5,5;2;0,4;3,4;3,1,1,1;2,3;3,0,0,1,0,1,3,0;4,2,54,4,0,85,1,0,43";
		String grid6 = "5,5;2;3,0;4,3;2,1,2,2,3,1,0,0,1,1,4,2,3,3,1,3,0,1;2,4,3,2,3,4,0,4;4,4,4,0,4,0,4,4;1,4,57,2,0,46";
		String grid7 = "5,5;3;1,3;4,0;0,1,3,2,4,3,2,4,0,4;3,4,3,0,4,2;1,4,1,2,1,2,1,4,0,3,1,0,1,0,0,3;4,4,45,3,3,12,0,2,88";
		String grid8 = "5,5;2;4,3;2,1;2,0,0,4,0,3,0,1;3,1,3,2;4,4,3,3,3,3,4,4;4,0,17,1,2,54,0,0,46,4,1,22";
		String grid9 = "5,5;2;0,4;1,4;0,1,1,1,2,1,3,1,3,3,3,4;1,0,2,4;0,3,4,3,4,3,0,3;0,0,30,3,0,80,4,4,80";
		String grid10 = "5,5;4;1,1;4,1;2,4,0,4,3,2,3,0,4,2,0,1,1,3,2,1;4,0,4,4,1,0;2,0,0,2,0,2,2,0;0,0,62,4,3,45,3,3,39,2,3,40";
		

		com.sun.management.OperatingSystemMXBean osBebo = (com.sun.management.OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
		
		
		System.out.println("path: " + solve(grid0, "ID", false));
		System.out.println("expanded nodes: " + Node.expandedNodes);
		System.out.println();
		
		 double cpuload=osBebo.getProcessCpuLoad()*100;
	 	 System.out.println("CPU usage: "+ cpuload+ " %");
		
		long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;
        System.out.println("Time Taken: "+elapsedTime*0.001+" sec");
		
		  // Get the Java runtime
        Runtime runtime = Runtime.getRuntime();
        // Run the garbage collector
        runtime.gc();
        // Calculate the used memory
        long memory = runtime.totalMemory() - runtime.freeMemory();
 
        System.out.println("RAM Usage: "+ ((double)memory/runtime.totalMemory())*100+" %");
        
	}
}
