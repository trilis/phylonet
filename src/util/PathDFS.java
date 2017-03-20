package util;

import java.util.HashMap;
import java.util.HashSet;

public class PathDFS extends DFS {

	private HashSet<Node> ans = new HashSet<Node>();
	private HashMap<Node, Integer> depth = new HashMap<Node, Integer>();

	public PathDFS(Node start, Node finish) {
		ans.add(start);
		ans.add(finish);
		depth.put(((PhyloTree) start.getGraph()).getRoot(), 0);
		dfs(((PhyloTree) start.getGraph()).getRoot());
		while (depth.get(start) > depth.get(finish)) {
			start = start.getParent();
			ans.add(start);
		}
		while (depth.get(start) < depth.get(finish)) {
			finish = finish.getParent();
			ans.add(finish);
		}
		while (start != finish) {
			start = start.getParent();
			finish = finish.getParent();
			ans.add(start);
			ans.add(finish);
		}
	}

	@Override
	public void enter(Node v) {
		for (Edge e : v.getOutEdges()) {
			depth.put(e.getFinish(), depth.get(v) + 1);
		}

	}

	@Override
	public void exit(Node v) {
		// TODO Auto-generated method stub

	}

	public HashSet<Node> getAns() {
		return ans;
	}

}
