package code;

import java.util.Comparator;
import java.util.PriorityQueue;

public class ASTAR extends Search_Problem {
	public static String greedyType = "";

	@Override
	public Node search(Node root) {
		Comparator<Node> asCom;

		if (greedyType.equals("AS1")) {
			asCom = Comparator.comparingDouble(n -> (n.state.getHeuristicValueOne(n) + n.cost));

		} else {
			asCom = Comparator.comparingDouble(n -> (n.state.getHeuristicValueTwo(n) + n.cost));
		}

		PriorityQueue<Node> asPQ = new PriorityQueue<>(asCom);
		asPQ.add(root);

		return rawPQFn(asPQ);
	}

	public void setGreedy(String greedyType) {
		this.greedyType = greedyType;
	}

	public Node rawPQFn(PriorityQueue<Node> pq) {

		while (true) {
			if (pq.size() == 0) {
				return null;
			} else {
				Node front = pq.poll();
				updateGameInfo(front);
				if (isGoal(front)) {
					return front;
				} else {
					Node.expandedNodes++;
					for (int i = 0; i < 9; ++i) {
						updateGameInfo(front);
						Node child = expand(front, i);
						if (child != null) {
							if (!state_space.contains(stateStringDamageUpdater(child.state.stateString))) {
								state_space.add(stateStringDamageUpdater(child.state.stateString));
								pq.add(child);
							}

						}
					}

				}
			}
		}

	}

}