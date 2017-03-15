package phylonet;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Vector;

import util.Edge;
import util.PhyloTree;
import util.Taxon;
import util.Node;

public class Forest {
	private Vector<PhyloTree> trees = new Vector<PhyloTree>();
	private HashMap<Node, HashSet<Taxon>> labels = new HashMap<Node, HashSet<Taxon>>();

	public Forest() {

	}

	public Forest(PhyloTree tree) {
		trees.add(tree);
		for (Node n : tree.getNodes()) {
			if (n.isLeaf()) {
				HashSet<Taxon> taxa = new HashSet<Taxon>();
				taxa.add(n.getTaxon());
				labels.put(n, taxa);
			}
		}
	}

	public Forest(Forest old) {
		for (PhyloTree tree : old.trees) {
			trees.add(tree);
		}
	}

	public void addTree(PhyloTree tree) {
		trees.add(tree);
	}

	public void delTree(PhyloTree tree) {
		trees.remove(tree);
	}

	public Forest cutEdge(Edge e) {
		PhyloTree old = (PhyloTree) e.getGraph();
		PhyloTree oldTree = copyLabeledTree(old);
		PhyloTree newTree = new PhyloTree();
		newTree.setRoot(copySubTree(e.getFinish(), newTree, oldTree));
		oldTree.compress();
		newTree.compress();
		Forest newForest = new Forest();
		for (PhyloTree tree : getTrees()) {
			if (tree != old) {
				newForest.addTree(tree);
			}
		}
		newForest.addTree(oldTree);
		newForest.addTree(newTree);
		return newForest;
	}

	public Node copySubTree(Node v, PhyloTree newTree, PhyloTree oldTree) {
		Node nw = new Node(newTree, v);
		newTree.addNode(nw);
		labels.put(nw, labels.get(nw));
		Vector<Edge> toRemove = new Vector<Edge>();
		for (Edge e : v.getOutEdges()) {
			Node u = copySubTree(e.getFinish(), newTree, oldTree);
			toRemove.add(e);
			newTree.addEdge(nw, u);
		}
		for (Edge e : toRemove) {
			oldTree.delEdge(e);
		}
		oldTree.delNode(v);
		return nw;
	}

	public Node findLabel(HashSet<Taxon> label) {
		for (PhyloTree tree : getTrees()) {
			for (Node n : tree.getNodes()) {
				if (labels.get(n).equals(label)) {
					return n;
				}
			}
		}
		return null;
	}

	public Forest eatCherry(Cherry cherry) {
		Forest newForest = new Forest(this);
		Node node1 = newForest.findLabel(cherry.getOldLabel1());
		Node node2 = newForest.findLabel(cherry.getOldLabel2());
		newForest.delTree((PhyloTree) node1.getGraph());
		PhyloTree newTree = copyLabeledTree((PhyloTree) node1.getGraph());
		newForest.addTree(newTree);
		newForest.labels.put(node1.getParent(), cherry.getNewLabel());
		newTree.delNode(node1);
		newTree.delNode(node2);
		return newForest;
	}

	public Forest recoverAnswer(HashSet<Cherry> removedCherries) {
		Forest newForest = new Forest(this);
		HashMap<HashSet<Taxon>, Cherry> map = new HashMap<HashSet<Taxon>, Cherry>();
		for (Cherry cherry : removedCherries) {
			map.put(cherry.getNewLabel(), cherry);
		}
		for (PhyloTree tree : newForest.getTrees()) {
			while (true) {
				HashMap<Node, Node> toAdd = new HashMap<Node, Node>();
				for (Node n : tree.getNodes()) {
					if (map.containsKey(labels.get(n))) {
						Node nw1 = new Node(tree);
						Node nw2 = new Node(tree);
						labels.put(nw1, map.get(labels.get(n)).getOldLabel1());
						labels.put(nw2, map.get(labels.get(n)).getOldLabel2());
						toAdd.put(n, nw1);
						toAdd.put(n, nw2);
					}
				}
				if (toAdd.size() == 0) {
					break;
				}
				for (Node n : toAdd.keySet()) {
					tree.addNode(toAdd.get(n));
					tree.addEdge(n, toAdd.get(n));
				}
			}
		}
		return newForest;
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

	public Iterable<PhyloTree> getTrees() {
		return trees;
	}

	public int getNumberOfTrees() {
		return trees.size();
	}

	public HashSet<Taxon> getTaxaOfTree(int treeNumber) {
		return trees.get(treeNumber).getAllTaxa();
	}
	

}
