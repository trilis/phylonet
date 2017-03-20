package util;

import java.util.HashMap;
import java.util.HashSet;

public class TaxaEmbeddingDFS extends DFS {

	private HashSet<Node> parents = new HashSet<Node>();
	private HashMap<Node, Node> nwNodes = new HashMap<Node, Node>();
	private HashMap<Node, Node> oldNodes = new HashMap<Node, Node>();
	private PhyloTree ans = new PhyloTree();
	private boolean flag = false;

	public TaxaEmbeddingDFS(HashSet<Taxon> taxa, PhyloTree tree) {
		for (Node n : tree.getNodes()) {
			if (n.isLeaf() && taxa.contains(n.getTaxon())) {
				Node v = n;
				parents.add(v);
				while (v.getInDeg() > 0) {
					v = v.getParent();
					parents.add(v);
				}
			}
		}
		for (Node n : parents) {
			Node nw = new Node(ans, n);
			nwNodes.put(n, nw);
			oldNodes.put(nw, n);
		}
		dfs(tree.getRoot());
	}

	@Override
	public void enter(Node v) {
		if (!flag) {
			boolean breaked = false;
			for (Edge e : v.getOutEdges()) {
				if (!parents.contains(e.getFinish())) {
					breaked = true;
					break;
				}
			}
			if (parents.contains(v) && !breaked) {
				flag = true;
				ans.setRoot(nwNodes.get(v));
			}
		}
		if (flag && parents.contains(v)) {
			v.numberOfVisits++;
			ans.addNode(nwNodes.get(v));
		}
	}

	@Override
	public void exit(Node v) {
		for (Edge e : v.getInEdges()) {
			Node u = e.getStart();
			if (ans.hasNode(nwNodes.get(u)) && ans.hasNode(nwNodes.get(v))) {
				ans.addEdge(nwNodes.get(u), nwNodes.get(v));
			}
			break;
		}
	}

	public PhyloTree getAnswer() {
		ans.compress();
		return ans;
	}

	public Node getOldNode(Node v) {
		return oldNodes.get(v);
	}

	public Node getOldRoot() {
		return oldNodes.get(ans.getRoot());
	}
}
