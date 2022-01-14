package code;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Stack;

public abstract class Search_Problem {
	// 5-tuple Search Problem Definition:

	// set of operators
	public static String[] operators = { "carry", "drop", "kill", "takePill", "fly", "up", "down", "right", "left" };

	// initial state
	public static State initialState;

	// state space
	public HashSet<String> state_space = new HashSet<String>();

	public static List<String> stateString = new ArrayList<>();
	public static int[] NeoInfo;
	public static List<String> agentsInfo = new ArrayList<>();
	public static List<String> pillsInfo = new ArrayList<>();
	public static List<String> hostagesInfo = new ArrayList<>();

	public static String path = "";
	public static Stack<Node> stack = new Stack<Node>();

	abstract public Node search(Node root);

	// checks whether the input node is a goal node or not (goal test)
	public boolean isGoal(Node node) {
		for (int i = 0; i < hostagesInfo.size() - 2; i += 2) {
			if (!hostagesInfo.get(i).equals("-1")) {
			}
		}

		if (hostagesInfo.size() == 0)
			return false;

		// if Neo is not standing at TB: not goal
		if (NeoInfo[0] != Matrix.telephoneBoothPosition.getX() || NeoInfo[1] != Matrix.telephoneBoothPosition.getY())
			return false;

		// if at least one hostage is neither killed nor dropped in TB: not goal
		for (int i = 0; i < hostagesInfo.size(); i += 2) {
			if (!(hostagesInfo.get(i).equals("-1") || (hostagesInfo.get(i + 1).equals("2"))))
				return false;
		}

		// calculating number of deaths and kills
		for (int i = 0; i < hostagesInfo.size(); i += 2) {
			if (hostagesInfo.get(i).equals("100"))
				Matrix.numberOfDeaths++;
			if (hostagesInfo.get(i).equals("-1")) {
				Matrix.numberOfDeaths++;
				Matrix.numberOfKills++;
			}

		}

		for (int i = 0; i < agentsInfo.size(); i++) {
			if (agentsInfo.get(i).equals("1"))
				Matrix.numberOfKills++;
		}

		return true;
	}

	// extracts the stateString found in state of the input node into the global
	// temporary
	// attributes "stateString, NeoInfo, agentsInfo, pillsInfo and hostagesInfo"
	public static void updateGameInfo(Node node) {
		stateString = Arrays.asList(node.state.stateString.split(";"));
		NeoInfo = Matrix.convertStringIntArr(stateString.get(0));
		agentsInfo = Arrays.asList(stateString.get(1).split(","));
		pillsInfo = Arrays.asList(stateString.get(2).split(","));
		hostagesInfo = Arrays.asList(stateString.get(3).split(","));
	}

	// takes a parent node and an action as inputs and returns a new node if the
	// action is applicable
	// to the parent node, returns null otherwise
	public Node expand(Node parent, int action) {

		// if Neo's damage reaches 100, then he is dead and no new nodes will be created
		if (NeoInfo[2] >= 100)
			return null;

		State state = new State("");

		// calculating number of deaths before applying an action (if one is applicable)
		int death = 0;
		for (int i = 0; i < hostagesInfo.size(); i += 2) {
			if (hostagesInfo.get(i).equals("100") || hostagesInfo.get(i).equals("-1"))
				death++;
		}
		State.death = death;

		String action_string = operators[action];

		// check the ACTION APPLICABILITY
		switch (action_string) {

		case "takePill": {

			String neoPosition = NeoInfo[0] + "," + NeoInfo[1];

			// if Neo is standing at some pill position
			if (Matrix.pillsPosition.get(neoPosition) != null) {

				int pillIndex = Matrix.pillsPosition.get(neoPosition);

				// if this pill is not taken yet
				if (pillsInfo.get(pillIndex).equals("0")) {

					// update pill_isTaken to 1
					pillsInfo.set(pillIndex, "1");

					// decrease the damage of NEO by 20
					NeoInfo[2] = NeoInfo[2] - 20;

					if (NeoInfo[2] <= 0)
						NeoInfo[2] = 0;

					// decrease the damage of all living hostages by 20
					for (int j = 0; j < hostagesInfo.size(); j += 2) {
						if (Integer.parseInt(hostagesInfo.get(j)) > 1 && Integer.parseInt(hostagesInfo.get(j)) <= 99) {
							hostagesInfo.set(j, (Integer.parseInt(hostagesInfo.get(j)) - 20) + "");

							if ((Integer.parseInt(hostagesInfo.get(j)) <= 0))
								hostagesInfo.set(j, "0");
						}
					}

					state.stateString = getNewStateString(NeoInfo, agentsInfo, pillsInfo, hostagesInfo);
					double cost = parent.state.getCostFunction(action);
					return new Node(parent, action, parent.depth + 1, parent.cost + cost, state);
				} else {
					return null;
				}

			}

			else {
				return null;
			}

		}

		case "carry": {
			boolean flag = false;
			
			// check if Neo is standing at some hostage position
			if (Matrix.hostagesPosition.get(NeoInfo[0] + "," + NeoInfo[1]) != null) {
				int i = Matrix.hostagesPosition.get(NeoInfo[0] + "," + NeoInfo[1]);

				// this hostage is not dead or killed
				if (!hostagesInfo.get(i * 2).equals("100") && !hostagesInfo.get(i * 2).equals("-1")
						// this hostage is not carried
						&& hostagesInfo.get(i * 2 + 1).equals("0") 
						// Neo's carriedCount doesn't exceed the capacity
						&& NeoInfo[3] < Matrix.capacity) {

					hostagesInfo.set(i * 2 + 1, "1");
					NeoInfo[3] = NeoInfo[3] + 1;
					flag = true;
				}
			}

			if (!flag)
				return null;
			break;
		}

		case "drop": {
			// if Neo is standing at the TB
			if (Matrix.telephoneBoothPosition.getX() == NeoInfo[0] && Matrix.telephoneBoothPosition.getY() == NeoInfo[1]
					// if Neo is carrying some hostage/s
					&& NeoInfo[3] > 0) {

				// update hostages_isCarried of all carried hostages to 2
				for (int j = 0; j < hostagesInfo.size(); j += 2) {
					if (hostagesInfo.get(j + 1).equals("1")) {
						hostagesInfo.set(j + 1, "2");
					}
				}

				// update carriedCount to zero
				NeoInfo[3] = 0;

				// increase all living non-dropped hostages' damages by 2
				for (int i = 0; i < hostagesInfo.size(); i += 2) {
					if (Integer.parseInt(hostagesInfo.get(i)) < 100 && Integer.parseInt(hostagesInfo.get(i)) > 0
							&& !hostagesInfo.get(i + 1).equals("2"))
						hostagesInfo.set(i, Integer.parseInt(hostagesInfo.get(i)) + 2 + "");

					if (Integer.parseInt(hostagesInfo.get(i)) > 100)
						hostagesInfo.set(i, "100");
				}

				state.stateString = getNewStateString(NeoInfo, agentsInfo, pillsInfo, hostagesInfo);
				double cost = parent.state.getCostFunction(action);
				return new Node(parent, action, parent.depth + 1, parent.cost + cost, state);
			} else {
				return null;
			}

		}

		case "fly": {
			String neoLocation = NeoInfo[0] + "," + NeoInfo[1];

			// if Neo is standing at some pad position
			if (Matrix.padsPositions.get(neoLocation) != null) {
				int[] neoNewLocation = Matrix.convertStringIntArr(Matrix.padsPositions.get(neoLocation));
				
				// updates Neo location to the pad's pair position
				NeoInfo[0] = neoNewLocation[0];
				NeoInfo[1] = neoNewLocation[1];
			} else {
				return null;
			}
			break;

		}

		case "up": {
			// if Neo is standing at (0,y)
			if (NeoInfo[0] == 0)
				return null;

			String neoNewLocation = (NeoInfo[0] - 1) + "," + NeoInfo[1];

			// if a living agent is standing above Neo
			if (Matrix.agentsPosition.get(neoNewLocation) != null) {
				if (agentsInfo.get(Matrix.agentsPosition.get(neoNewLocation)).equals("0"))
					return null;
			}

			// if a dead non-carried hostage is standing above Neo
			if (Matrix.hostagesPosition.get(neoNewLocation) != null) {
				int hostagePosition = Matrix.hostagesPosition.get(neoNewLocation);
				if ((hostagesInfo.get(hostagePosition * 2).equals("100")
						&& hostagesInfo.get(hostagePosition * 2 + 1).equals("0")) ||

				// if a living hostage with damage = 98 OR damage = 99 standing above Neo
						(hostagesInfo.get(hostagePosition * 2 + 1).equals("0")
								&& (hostagesInfo.get(hostagePosition * 2).equals("99")
										|| hostagesInfo.get(hostagePosition * 2).equals("98"))))
					return null;
			}

			NeoInfo[0] = NeoInfo[0] - 1;
			break;
		}

		case "down": {

			// if Neo is standing at (x,n-1)
			if (NeoInfo[0] == Matrix.Height - 1)
				return null;

			String neoNewLocation = (NeoInfo[0] + 1) + "," + NeoInfo[1];

			// if a living agent is standing below Neo
			if (Matrix.agentsPosition.get(neoNewLocation) != null) {
				if (agentsInfo.get(Matrix.agentsPosition.get(neoNewLocation)).equals("0"))
					return null;
			}

			// if a dead non-carried hostage is standing below Neo
			if (Matrix.hostagesPosition.get(neoNewLocation) != null) {
				int hostagePosition = Matrix.hostagesPosition.get(neoNewLocation);
				if ((hostagesInfo.get(hostagePosition * 2).equals("100")
						&& hostagesInfo.get(hostagePosition * 2 + 1).equals("0")) ||

				// if a living hostage with damage = 98 OR damage = 99 standing below Neo
						(hostagesInfo.get(hostagePosition * 2 + 1).equals("0")
								&& (hostagesInfo.get(hostagePosition * 2).equals("99")
										|| hostagesInfo.get(hostagePosition * 2).equals("98"))))
					return null;
			}

			NeoInfo[0] = NeoInfo[0] + 1;
			break;

		}
		// 2 -> Right
		case "right": {
			// if Neo is standing at (0,y)
			if (NeoInfo[1] == Matrix.Width - 1)
				return null;

			String neoNewLocation = NeoInfo[0] + "," + (NeoInfo[1] + 1);

			// if a living agent is standing right to Neo
			if (Matrix.agentsPosition.get(neoNewLocation) != null) {
				if (agentsInfo.get(Matrix.agentsPosition.get(neoNewLocation)).equals("0"))
					return null;
			}

			// if a dead non-carried hostage is standing right to Neo
			if (Matrix.hostagesPosition.get(neoNewLocation) != null) {
				int hostagePosition = Matrix.hostagesPosition.get(neoNewLocation);
				if ((hostagesInfo.get(hostagePosition * 2).equals("100")
						&& hostagesInfo.get(hostagePosition * 2 + 1).equals("0")) ||

				// if a living hostage with damage = 98 OR damage = 99 standing right to Neo
						(hostagesInfo.get(hostagePosition * 2 + 1).equals("0")
								&& (hostagesInfo.get(hostagePosition * 2).equals("99")
										|| hostagesInfo.get(hostagePosition * 2).equals("98"))))
					return null;
			}

			NeoInfo[1] = NeoInfo[1] + 1;
			break;
		}
		// 3 -> Left
		case "left": {
			// if Neo is standing at (m-1,y)
			if (NeoInfo[1] == 0)
				return null;

			String neoNewLocation = NeoInfo[0] + "," + (NeoInfo[1] - 1);

			// if a living agent is standing left to Neo
			if (Matrix.agentsPosition.get(neoNewLocation) != null) {
				if (agentsInfo.get(Matrix.agentsPosition.get(neoNewLocation)).equals("0"))
					return null;
			}

			// if a dead non-carried hostage is standing left to Neo
			if (Matrix.hostagesPosition.get(neoNewLocation) != null) {
				int hostagePosition = Matrix.hostagesPosition.get(neoNewLocation);
				if ((hostagesInfo.get(hostagePosition * 2).equals("100")
						&& hostagesInfo.get(hostagePosition * 2 + 1).equals("0")) ||

				// if a living hostage with damage = 98 OR damage = 99 standing left to Neo
						(hostagesInfo.get(hostagePosition * 2 + 1).equals("0")
								&& (hostagesInfo.get(hostagePosition * 2).equals("99")
										|| hostagesInfo.get(hostagePosition * 2).equals("98"))))
					return null;
			}

			NeoInfo[1] = NeoInfo[1] - 1;
			break;
		}

		// 7 -> Kill
		case "kill": {
			// a1,a2,a3,a4 represents agents in neighboring cells
			int i = -1, a1 = -1, a2 = -1, a3 = -1, a4 = -1;
			// i represents if there is a hostage in neo's location and that hostage damage
			// is 98 or 99 which would convert to an agent
			int ha1 = -1, ha2 = -1, ha3 = -1, ha4 = -1;// hostages converted to agents in neighboring cells
			boolean flag = false;
			if (Matrix.hostagesPosition.get(NeoInfo[0] + "," + NeoInfo[1]) != null) {
				i = Matrix.hostagesPosition.get(NeoInfo[0] + "," + NeoInfo[1]);

			}

			// no hostage found --> i == -1
			// ||
			// hostage found (damage < 98) (carry == 0)
			// ||
			// hostage found (carry == 1)
			// ||
			// hostage found (carry == 2)
			// ||
			// hostage found (damage == -1)

			if ((i == -1)
					|| (i != -1 && (!hostagesInfo.get(i * 2).equals("98") || !hostagesInfo.get(i * 2).equals("99"))
							&& hostagesInfo.get(i * 2 + 1).equals("0"))
					|| (i != -1 && hostagesInfo.get(i * 2 + 1).equals("1"))
					|| (i != -1 && hostagesInfo.get(i * 2 + 1).equals("2"))
					|| (i != -1 && hostagesInfo.get(i * 2).equals("-1"))) {

				if (Matrix.hostagesPosition.get((NeoInfo[0] + 1) + "," + NeoInfo[1]) != null) {
					ha1 = Matrix.hostagesPosition.get((NeoInfo[0] + 1) + "," + NeoInfo[1]) * 2;
					if (hostagesInfo.get(ha1).equals("100") && hostagesInfo.get(ha1 + 1).equals("0")) {
						hostagesInfo.set(ha1, "-1");
						flag = true;
					}
				}

				if (Matrix.hostagesPosition.get((NeoInfo[0] - 1) + "," + NeoInfo[1]) != null) {
					ha2 = Matrix.hostagesPosition.get((NeoInfo[0] - 1) + "," + NeoInfo[1]) * 2;
					if (hostagesInfo.get(ha2).equals("100") && hostagesInfo.get(ha2 + 1).equals("0")) {
						hostagesInfo.set(ha2, "-1");
						flag = true;
					}
				}
				if (Matrix.hostagesPosition.get(NeoInfo[0] + "," + (NeoInfo[1] + 1)) != null) {
					ha3 = Matrix.hostagesPosition.get(NeoInfo[0] + "," + (NeoInfo[1] + 1)) * 2;
					if (hostagesInfo.get(ha3).equals("100") && hostagesInfo.get(ha3 + 1).equals("0")) {
						hostagesInfo.set(ha3, "-1");
						flag = true;
					}
				}
				if (Matrix.hostagesPosition.get(NeoInfo[0] + "," + (NeoInfo[1] - 1)) != null) {
					ha4 = Matrix.hostagesPosition.get(NeoInfo[0] + "," + (NeoInfo[1] - 1)) * 2;
					if (hostagesInfo.get(ha4).equals("100") && hostagesInfo.get(ha4 + 1).equals("0")) {
						hostagesInfo.set(ha4, "-1");
						flag = true;
					}
				}

				if (Matrix.agentsPosition.get((NeoInfo[0] + 1) + "," + NeoInfo[1]) != null) {
					a1 = Matrix.agentsPosition.get((NeoInfo[0] + 1) + "," + NeoInfo[1]);
					if (agentsInfo.get(a1).equals("0")) {
						agentsInfo.set(a1, "1");
						flag = true;
					}

				}
				if (Matrix.agentsPosition.get((NeoInfo[0] - 1) + "," + NeoInfo[1]) != null) {
					a2 = Matrix.agentsPosition.get((NeoInfo[0] - 1) + "," + NeoInfo[1]);
					if (agentsInfo.get(a2).equals("0")) {
						agentsInfo.set(a2, "1");
						flag = true;
					}
				}
				if (Matrix.agentsPosition.get(NeoInfo[0] + "," + (NeoInfo[1] + 1)) != null) {
					a3 = Matrix.agentsPosition.get(NeoInfo[0] + "," + (NeoInfo[1] + 1));
					if (agentsInfo.get(a3).equals("0")) {
						agentsInfo.set(a3, "1");
						flag = true;
					}
				}
				if (Matrix.agentsPosition.get(NeoInfo[0] + "," + (NeoInfo[1] - 1)) != null) {
					a4 = Matrix.agentsPosition.get(NeoInfo[0] + "," + (NeoInfo[1] - 1));
					if (agentsInfo.get(a4).equals("0")) {
						agentsInfo.set(a4, "1");
						flag = true;
					}

				}

			}
			if (!flag)
				return null;
			else {
				NeoInfo[2] = NeoInfo[2] + 20;
				if (NeoInfo[2] >= 100) {
					NeoInfo[2] = 100;
				}
			}

			break;

		}

		default:
			System.out.println("default");
		}

		// increase all living non-dropped hostages' damages by 2
		if (action != 3) {

			for (int i = 0; i < hostagesInfo.size(); i += 2) {
				if (Integer.parseInt(hostagesInfo.get(i)) < 100 && Integer.parseInt(hostagesInfo.get(i)) > 0
						&& !hostagesInfo.get(i + 1).equals("2"))
					hostagesInfo.set(i, Integer.parseInt(hostagesInfo.get(i)) + 2 + "");

				if (Integer.parseInt(hostagesInfo.get(i)) > 100)
					hostagesInfo.set(i, "100");
			}

		}

		double cost = parent.state.getCostFunction(action);
		state.stateString = getNewStateString(NeoInfo, agentsInfo, pillsInfo, hostagesInfo);
		return new Node(parent, action, parent.depth + 1, parent.cost + cost, state);
	}

	// generates the new state string from all global temporary attributes
	public String getNewStateString(int[] NeoInfo, List<String> agentsInfo, List<String> pillsInfo,
			List<String> hostagesInfo) {
		String newState = "";

		// concatenate NeoInfo on newState
		for (int i = 0; i < NeoInfo.length; i++) {
			if (i != NeoInfo.length - 1)
				newState += NeoInfo[i] + ",";
			else
				newState += NeoInfo[i];
		}
		newState += ";";

		// concatenate agentsInfo on newState
		for (int i = 0; i < agentsInfo.size(); i++) {
			if (i != agentsInfo.size() - 1)
				newState += agentsInfo.get(i) + ",";
			else
				newState += agentsInfo.get(i);
		}
		newState += ";";

		// concatenate pillsInfo on newState
		for (int i = 0; i < pillsInfo.size(); i++) {
			if (i != pillsInfo.size() - 1)
				newState += pillsInfo.get(i) + ",";
			else
				newState += pillsInfo.get(i);
		}
		newState += ";";

		// concatenate hostagesInfo on newState
		for (int i = 0; i < hostagesInfo.size(); i++) {
			if (i != hostagesInfo.size() - 1)
				newState += hostagesInfo.get(i) + ",";
			else
				newState += hostagesInfo.get(i);
		}

		return newState;
	}

	// inserts in a stack all nodes from the goal node to the root
	public static void updateStack(Node goal) {
		if (goal == null)
			return;
		else {
			stack.add(goal);
			updateStack(goal.parent);
		}
	}

	// pops out all nodes stored in the stack and storing their actions in a string,
	// resulting in
	// path of solution found along with number of deaths, kills and expanded nodes
	public static void updatePath(boolean visualize) {
		// first node popped is the root node
		Node n = ((Node) stack.pop());

		// if visualize is set to true, then print the initial grid
		if (visualize) {
			n.state.visualize();
		}

		// Keep popping out nodes from the stack until reaching the goal node
		while (!stack.isEmpty()) {
			n = ((Node) stack.pop());
			String operator = operators[n.action];

			// Concatenate each popped node’s action into path
			path += operator + ",";

			// if visualize is set to true, then print each popped node's corresponding grid
			if (visualize) {
				System.out.println("Action taken: " + operator);
				n.state.visualize();
			}
		}

		// Concatenate numberOfDeaths, numberOfKills and expandedNodes into path
		if (path.length() > 1)
			path = path.substring(0, path.length() - 1);
		path += ";" + Matrix.numberOfDeaths + ";" + Matrix.numberOfKills + ";" + Node.expandedNodes;
	}

	// updating the hostage damages in the state string
	// hostage dead = 1, hostage alive = 0, hostage dead = 2
	public static String stateStringDamageUpdater(String stateString) {

		// copy NeoInfo, agentsInfo and pillsInfo from stateString into a new string
		String[] info = stateString.split(";");
		String result = info[0] + ";" + info[1] + ";" + info[2] + ";";
		String[] hostageInfo = info[3].split(",");

		// update only hostagesInfo
		for (int i = 0; i < hostageInfo.length; i += 2) {
			// if hostage is living, then updates its damage to 0
			int hostage = 0;

			// if hostage is dead, then updates its damage to 1
			if (hostageInfo[i].equals("100"))
				hostage = 1;

			// if hostage is dead and killed, then updates its damage to 2
			else if (hostageInfo[i].equals("-1"))
				hostage = 2;

			result += hostage + "," + hostageInfo[i + 1];
			result += ",";
		}

		return result.substring(0, result.length() - 1);
	}

}
