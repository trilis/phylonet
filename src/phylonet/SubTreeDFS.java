package phylonet;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

public class SubTreeDFS extends DFS {

	HashSet<Taxon> taxa;
	HashSet<Node> parents;
	HashMap<Node, Node> nwnodes;
	PhyloTree tree;
	PhyloTree ans = new PhyloTree();
	boolean flag = false;
	
	public SubTreeDFS(HashSet<Taxon> taxa, PhyloTree tree) {
		this.taxa = taxa;
		this.tree = tree;
		for (Node n : tree.nodes) {
			if (n.isLeaf() && taxa.contains(n.getTaxon())) {
				Node v = n;
				parents.add(v);
				while (v.getInDeg() > 0) {
					Iterator<Edge> itr = v.getInEdges();
					v = itr.next().getStart();
					parents.add(v);
				}
			}
		}
		for (Node n : parents) {
			nwnodes.put(n, n.copy(ans));
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
			}
		}
		if (flag && parents.contains(v)) {
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

}
