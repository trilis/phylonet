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
		labels = old.labels;
	}

	public void addTree(PhyloTree tree) {
		trees.add(tree);
	}

	public void delTree(PhyloTree tree) {
		trees.remove(tree);
	}

	public Forest cutEdge(Edge e) {
		PhyloTree old = (PhyloTree) e.getGraph();
		LabeledTreeCopy copy = new LabeledTreeCopy(old, labels);
		PhyloTree oldTree = copy.getAnswer();
		PhyloTree newTree = new PhyloTree();
		newTree.setRoot(copySubTree(copy.getNewNode(e.getFinish()), newTree, oldTree, true));
		oldTree.softCompress();
		newTree.softCompress();
		Forest newForest = new Forest();
		for (PhyloTree tree : getTrees()) {
			if (tree != old) {
				newForest.addTree(tree);
			}
		}
		newForest.labels = labels;
		newForest.addTree(oldTree);
		newForest.addTree(newTree);
		return newForest;
	}

	private Node copySubTree(Node v, PhyloTree newTree, PhyloTree oldTree, boolean isRoot) {
		Node nw = new Node(newTree, v);
		newTree.addNode(nw);
		labels.put(nw, labels.get(v));
		Vector<Node> toRemove = new Vector<Node>();
		for (Edge e : v.getOutEdges()) {
			Node u = copySubTree(e.getFinish(), newTree, oldTree, false);
			toRemove.add(e.getFinish());
			newTree.addEdge(nw, u);
		}
		for (Node n : toRemove) {
			oldTree.delNode(n);
		}
		if (isRoot) {
			oldTree.delNode(v);
		}
		return nw;
	}

	public Node findLabel(HashSet<Taxon> label) {
		for (PhyloTree tree : getTrees()) {
			for (Node n : tree.getNodes()) {
				if (n.isLeaf() && labels.get(n).equals(label)) {
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
		LabeledTreeCopy copy = new LabeledTreeCopy((PhyloTree) node1.getGraph(), labels);
		PhyloTree newTree = copy.getAnswer();
		newForest.addTree(newTree);
		newForest.labels.put(copy.getNewNode(node1.getParent()), cherry.getNewLabel());
		newTree.delEdge(copy.getNewNode(node1).getInEdges().iterator().next());
		newTree.delEdge(copy.getNewNode(node2).getInEdges().iterator().next());
		newTree.delNode(copy.getNewNode(node1));
		newTree.delNode(copy.getNewNode(node2));
		return newForest;
	}

	public Forest recoverAnswer(HashSet<Cherry> removedCherries) {
		Forest newForest = new Forest();
		for (PhyloTree tree : getTrees()) {
			newForest.addTree(new LabeledTreeCopy(tree, labels).getAnswer());
		}
		HashMap<HashSet<Taxon>, Cherry> map = new HashMap<HashSet<Taxon>, Cherry>();
		for (Cherry cherry : removedCherries) {
			map.put(cherry.getNewLabel(), cherry);
		}
		for (PhyloTree tree : newForest.getTrees()) {
			while (true) {
				HashMap<Node, Node> toAdd = new HashMap<Node, Node>();
				for (Node n : tree.getNodes()) {
					if (n.isLeaf() && map.containsKey(labels.get(n))) {
						Node nw1 = new Node(tree);
						Node nw2 = new Node(tree);
						labels.put(nw1, map.get(labels.get(n)).getOldLabel1());
						if (map.get(labels.get(n)).getOldLabel1().size() == 1) {
							nw1.setTaxon(map.get(labels.get(n)).getOldLabel1().iterator().next());
						}
						labels.put(nw2, map.get(labels.get(n)).getOldLabel2());
						if (map.get(labels.get(n)).getOldLabel2().size() == 1) {
							nw2.setTaxon(map.get(labels.get(n)).getOldLabel2().iterator().next());
						}
						toAdd.put(nw1, n);
						toAdd.put(nw2, n);
					}
				}
				if (toAdd.size() == 0) {
					break;
				}
				for (Node n : toAdd.keySet()) {
					tree.addNode(n);
					tree.addEdge(toAdd.get(n), n);
				}
			}
		}
		return newForest;
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
