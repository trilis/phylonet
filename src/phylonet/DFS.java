package phylonet;

import java.util.HashSet;
import java.util.Iterator;

public abstract class DFS {

	public abstract void enter(Node v);
	public abstract void exit(Node v);
	
	private HashSet<Node> used = new HashSet<Node>();
	
	public void dfs(Node v) {
		used.add(v);
		enter(v);
		Iterator<Edge> iterator = v.getOutEdges();
		while (iterator.hasNext()) {
			Edge e = iterator.next();
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
