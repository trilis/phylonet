package util;

import java.util.HashMap;
import java.util.HashSet;

public class SubTreeDFS extends DFS {

	private HashSet<Node> parents = new HashSet<Node>();
	private HashMap<Node, Node> nwnodes = new HashMap<Node, Node>();
	private PhyloTree ans = new PhyloTree();
	boolean flag = false;
	private Node oldRoot;
	
	public SubTreeDFS(HashSet<Taxon> taxa, PhyloTree tree) {
		for (Node n : tree.nodes) {
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
			Node nw = new Node(ans);
			if (n.isLeaf()) {
				nw = new Node(ans, n.getTaxon());
			} 
			nwnodes.put(n, nw);
		}
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
				ans.setRoot(nwnodes.get(v));
				oldRoot = v;
			}
		}
		if (flag && parents.contains(v)) {
			v.numberOfVisits++;
			ans.addNode(nwnodes.get(v));
		}
	}

	@Override
	public void exit(Node v) {
		for (Edge e : v.getInEdges()) {
			Node u = e.getStart();
			if (ans.hasNode(nwnodes.get(u)) && ans.hasNode(nwnodes.get(v))) {
				ans.addEdge(nwnodes.get(u), nwnodes.get(v));
			}
			break;
		}
	}
	
	public Node getOldRoot() {
		return oldRoot;
	}
	
	public PhyloTree getAnswer() {
		return ans;
	}

}
