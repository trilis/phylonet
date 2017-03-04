package util;

import java.util.HashSet;

public class CheckTreeDFS extends DFS {
	
	private boolean isTree = true;
	private HashSet<Node> black = new HashSet<Node>();
	private HashSet<Node> gray = new HashSet<Node>();
	
	public CheckTreeDFS(Graph graph) {
		for (Node n : graph.getNodes()) {
			if (!isUsed(n)) {
				dfs(n);
			}
		}
	}
	
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
	
	public boolean isTree() {
		return isTree;
	}

}
