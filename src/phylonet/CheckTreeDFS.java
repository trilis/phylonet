package phylonet;

import java.util.HashSet;
import java.util.Iterator;

public class CheckTreeDFS extends DFS {

	public boolean isTree = true;
	private HashSet<Node> black, gray = new HashSet<Node>();
	
	@Override
	public void enter(Node v) {
		gray.add(v);
		Iterator<Edge> itr = v.getOutEdges();
		while (itr.hasNext()) {
			if (gray.contains(itr.next().getFinish())) {
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
