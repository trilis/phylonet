package phylonet;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Vector;

import util.Edge;
import util.Graph;
import util.Node;
import util.PathDFS;
import util.PhyloTree;
import util.Taxon;

public class SmartAAF {

	private Vector<AgreementForest> allAgreementForests = new Vector<AgreementForest>();
	private int reticulationNumber;
	private HashMap<Node, HashSet<Taxon>> labels;

	public SmartAAF(PhyloTree tree1, PhyloTree tree2, int reticulationNumber) {
		this.reticulationNumber = reticulationNumber;
		for (Node n : tree1.getNodes()) {
			if (n.isLeaf()) {
				HashSet<Taxon> taxa = new HashSet<Taxon>();
				taxa.add(n.getTaxon());
				labels.put(n, taxa);
			}
		}
		build(tree1, new Forest(tree2), new HashSet<Cherry>());
	}

	private void build(PhyloTree tree, Forest forest, HashSet<Cherry> removedCherries) {
		if (forest.getNumberOfTrees() > reticulationNumber + 1) {
			return;
		}
		if (tree.getSize() == 1) {
			Forest answer = forest.recoverAnswer(removedCherries);
			Graph ADG = new Graph();
			
			return;
		}
		PhyloTree newTree = copyLabeledTree(tree);
		Vector<Node> isolatedNodes = getIsolatedNodes(newTree, forest);
		if (isolatedNodes.size() > 0) {
			for (Node n : isolatedNodes) {
				newTree.delNode(n);
			}
			build(newTree, forest, removedCherries);
			return;
		}
		Cherry cherry = getAnyCherry(tree);
		Node node1 = forest.findLabel(cherry.getOldLabel1());
		Node node2 = forest.findLabel(cherry.getOldLabel2());
		build(tree, forest.cutEdge(node1.getInEdges().iterator().next()), removedCherries);
		build(tree, forest.cutEdge(node2.getInEdges().iterator().next()), removedCherries);
		if (node1.getGraph() == node2.getGraph()) {
			if (node1.getParent() == node2.getParent()) {
				HashSet<Cherry> newCherries = new HashSet<Cherry>();
				newCherries.addAll(removedCherries);
				newCherries.add(cherry);
				PhyloTree cutTree = eatCherry(tree, cherry);
				Forest newForest = forest.eatCherry(cherry);
				build(cutTree, newForest, newCherries);
			} else {
				PathDFS path = new PathDFS(node1, node2);
				for (Node v : path.getAns()) {
					if (v == node1 || v == node2) {
						continue;
					}
					for (Edge e : v.getOutEdges()) {
						Node u = e.getFinish();
						if (!path.getAns().contains(u)) {
							build(tree, forest.cutEdge(e), removedCherries);
						}
					}
				}
			}
		}
	}

	private PhyloTree copyLabeledTree(PhyloTree old) {
		HashMap<Node, Node> nwnodes = new HashMap<Node, Node>();
		PhyloTree newTree = new PhyloTree();
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
		return newTree;
	}

	private Vector<Node> getIsolatedNodes(PhyloTree tree, Forest forest) {
		Vector<Node> ans = new Vector<Node>();
		for (Node n : tree.getNodes()) {
			if (n.isLeaf() && forest.findLabel(labels.get(n)).getGraph().getSize() == 1) {
				ans.add(n);
			}
		}
		return ans;
	}

	private Cherry getAnyCherry(PhyloTree tree) {
		for (Node n : tree.getNodes()) {
			if (!n.isLeaf()) {
				Vector<Node> oldNodes = new Vector<Node>();
				for (Edge e : n.getOutEdges()) {
					if (e.getFinish().isLeaf()) {
						oldNodes.add(e.getFinish());
					}
				}
				if (oldNodes.size() == 2) {
					return new Cherry(labels.get(oldNodes.get(0)), labels.get(oldNodes.get(1)),
							oldNodes.get(0), oldNodes.get(1));
				}
			}
		}
		return null;
	}
	
	public PhyloTree eatCherry(PhyloTree tree, Cherry cherry) {
		PhyloTree newTree = copyLabeledTree(tree);
		Vector<Node> toRemove = new Vector<Node>();
		for (Node n : newTree.getNodes()) {
			if (labels.get(n).equals(cherry.getOldLabel1())) {
				toRemove.add(n);
				labels.put(n.getParent(), cherry.getNewLabel());
			}
		}
		for (Node n : toRemove) {
			newTree.delNode(n);
		}
		return newTree;
	}

}
