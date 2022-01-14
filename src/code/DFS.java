package code;

import java.util.Stack;

public class DFS extends Search_Problem {
	@Override
	public Node search(Node root) {
		Stack<Node> nodesStack = new Stack<>();
		nodesStack.push(root);
		return DFSHelper(nodesStack, true, 0);
	}
	
	
	public Node DFSHelper(Stack<Node> stackNodes, boolean dfs, int iter) {

		while (true) {
			if (stackNodes.isEmpty()) {
				return null;
			}
			
			Node front = stackNodes.pop();
			updateGameInfo(front);	
			if (isGoal(front)) {
				return front;
			}
			if (!dfs && front.depth >= iter) {
				continue;
			}
			
			Node.expandedNodes++;
			
			// the action order is reversed due to the stack (start with the last action).
			for (int i = 8; i >=0; i--) { 
				updateGameInfo(front);
				Node child = expand(front, i);
				if(child != null) {
					if (!state_space.contains(stateStringDamageUpdater(child.state.stateString))) {
						state_space.add(stateStringDamageUpdater(child.state.stateString));
						stackNodes.push(child);
					}
					
				}

			}
		}

	}
}
