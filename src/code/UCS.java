package code;

import java.util.Comparator;
import java.util.PriorityQueue;

public class UCS extends Search_Problem {

	@Override
	public Node search(Node root) {
		Comparator<Node> ucsComparator = Comparator.comparingDouble(n -> n.cost);
		PriorityQueue<Node> ucsPQ = new PriorityQueue<>(ucsComparator);
		ucsPQ.add(root);
		return rawPQFn(ucsPQ);
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
					for (int i = 0; i < 9; i++) {
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
