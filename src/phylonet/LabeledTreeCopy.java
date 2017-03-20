package phylonet;

import java.util.HashMap;
import java.util.HashSet;

import util.Edge;
import util.Node;
import util.PhyloTree;
import util.Taxon;

public class LabeledTreeCopy {

	private PhyloTree newTree = new PhyloTree();
	private HashMap<Node, Node> nwnodes = new HashMap<Node, Node>();;

	public LabeledTreeCopy(PhyloTree old, HashMap<Node, HashSet<Taxon>> labels) {
		for (Node n : old.getNodes()) {
			Node nw = new Node(newTree, n);
			if (n == old.getRoot()) {
				newTree.setRoot(nw);
			}
			nwnodes.put(n, nw);
			newTree.addNode(nw);
			labels.put(nw, labels.get(n));
		}
		for (Node n : old.getNodes()) {
			for (Edge edg : n.getOutEdges()) {
				newTree.addEdge(nwnodes.get(n), nwnodes.get(edg.getFinish()));
			}
		}
	}

	public PhyloTree getAnswer() {
		return newTree;
	}

	public Node getNewNode(Node v) {
		return nwnodes.get(v);
	}
}
