package phylonet;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

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
					v = tree.getParent(v);
					parents.add(v);
				}
			}
		}
		for (Node n : parents) {
			nwnodes.put(n, new Node(ans, n.getTaxon()));
		}
	}
	
	@Override
	public void enter(Node v) {
		if (!flag) {
			Iterator<Edge> iterator = v.getOutEdges();
			while (iterator.hasNext()) {
				Edge e = iterator.next();
				if (!parents.contains(e.getFinish())) {
					break;
				}
			}
			if (!iterator.hasNext()) {
				flag = true;
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
		Iterator<Edge> itr = v.getInEdges();
		if (itr.hasNext()) {
			Node u = itr.next().getStart();
			if (ans.hasNode(nwnodes.get(u)) && ans.hasNode(nwnodes.get(v))) {
				ans.addEdge(nwnodes.get(u), nwnodes.get(v));
			}
		}
	}
	
	public Node getOldRoot() {
		return oldRoot;
	}
	
	public PhyloTree getAnswer() {
		return ans;
	}

}
