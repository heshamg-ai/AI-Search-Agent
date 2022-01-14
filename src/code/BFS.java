package code;

import java.util.LinkedList;
import java.util.Queue;

public class BFS extends Search_Problem {
	@Override
	public Node search(Node root) {
		Queue<Node> nodes = new LinkedList<>();
		nodes.add(root);
		
		while (true) {
			if (nodes.size() == 0) {
				return null;
			} else {
				Node front = nodes.poll();
				updateGameInfo(front);	
				
				if (isGoal(front) ) {
					//System.out.println(front);
					return front;
				} else {
					Node.expandedNodes++;

					for (int i = 0; i < 9; ++i) {
						updateGameInfo(front);	
						
						Node child = expand(front, i);
						if(child != null) {
							if (!state_space.contains(stateStringDamageUpdater(child.state.stateString))) {
								state_space.add(stateStringDamageUpdater(child.state.stateString));
								nodes.add(child);
							}	
						}
					}
				}
			}
		}
		
		
	}

	

}
