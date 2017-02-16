package util;

import java.util.HashSet;

public abstract class DFS {

	public abstract void enter(Node v);
	public abstract void exit(Node v);
	
	private HashSet<Node> used = new HashSet<Node>();
	
	public void dfs(Node v) {
		used.add(v);
		enter(v);
		for (Edge e : v.getOutEdges()) {
			if (!used.contains(e.getFinish())) {
				dfs(e.getFinish());
			}
		}
		exit(v);
	}
	
	public boolean isUsed(Node v) {
		return used.contains(v);
	}

}
