package util;

import java.util.HashSet;

public class CheckTreeDFS extends DFS {

	public boolean isTree = true;
	private HashSet<Node> black = new HashSet<Node>();
	private HashSet<Node> gray = new HashSet<Node>();
	
	@Override
	public void enter(Node v) {
		gray.add(v);
		for (Edge e : v.getOutEdges()) {
			if (gray.contains(e.getFinish())) {
				isTree = false;
			}
		}
	}

	@Override
	public void exit(Node v) {
		gray.remove(v);
		black.add(v);
	}

}
