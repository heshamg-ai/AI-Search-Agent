 	package code;

 	public class Node {
 		public static long expandedNodes = 0;
 		public Node parent;
 		public int action;
 		public int depth;
 		public double cost;
 		public State state;

 		public Node(Node parent, int action, int depth, double cost, State state) {
 			this.parent = parent;
 			this.action = action;
 			this.depth = depth;
 			this.cost = cost;
 			this.state = state;
 		}
 	}
