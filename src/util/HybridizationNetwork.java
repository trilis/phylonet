package util;

import java.util.HashMap;
import java.util.Vector;

public class HybridizationNetwork extends Graph {
	private Node root;
	private Vector<PhyloTree> displayedTrees;
	private HashMap<Node, Node> oldNodes;

	public Node getRoot() {
		return root;
	}
	
	public HybridizationNetwork() {
		
	}
	
	public HybridizationNetwork(PhyloTree tree) {
		HashMap<Node, Node> nwnodes = new HashMap<Node, Node>();
		for (Node n : tree.getNodes()) {
			Node nw = new Node(this, n);
			if (n == tree.getRoot()) {
				root = nw;
			}
			nwnodes.put(n, nw);
			addNode(nw);
		}
		for (Node n : tree.getNodes()) {
			for (Edge e : n.getOutEdges()) {
				addEdge(nwnodes.get(n), nwnodes.get(e.getFinish()));
			}
		}
	}

	public void addRoot(Node n) {
		root = n;
		addNode(n);
	}

	private void go(Vector<Node> nodes, int position, PhyloTree tree, HashMap<Node, Node> newNodes) {
		if (position == nodes.size()) {
			tree.compress();
			displayedTrees.add(tree);
			return;
		}
		Node v = nodes.get(position);
		if (!v.isReticulation()) {
			if (v.getInDeg() != 0) {
				Node u = v.getParent();
				tree.addEdge(newNodes.get(u), newNodes.get(v));
			}
			go(nodes, position + 1, tree, newNodes);
			return;
		}
		for (Edge edgeSelected : v.getInEdges()) {
			PhyloTree newTree = new PhyloTree();
			HashMap<Node, Node> newNodes2 = new HashMap<Node, Node>();
			for (Node n : nodes) {
				Node newNode = new Node(newTree, n);
				newTree.addNode(newNode);
				if (n == root) {
					newTree.setRoot(newNode);
				}
				newNodes2.put(n, newNode);
				oldNodes.put(newNode, n);
			}
			for (Node n : tree.getNodes()) {
				for (Edge e : n.getOutEdges()) {
					Node u = oldNodes.get(e.getFinish());
					newTree.addEdge(newNodes2.get(oldNodes.get(n)), newNodes2.get(u));
				}
			}
			newTree.addEdge(newNodes2.get(edgeSelected.getStart()), newNodes2.get(v));
			go(nodes, position + 1, newTree, newNodes2);
		}
	}

	public void countDisplayedTrees() {
		displayedTrees = new Vector<PhyloTree>();
		PhyloTree tree = new PhyloTree();
		HashMap<Node, Node> newNodes = new HashMap<Node, Node>();
		oldNodes = new HashMap<Node, Node>();
		for (Node n : getNodes()) {
			Node newNode = new Node(tree, n);
			tree.addNode(newNode);
			if (n == root) {
				tree.setRoot(newNode);
			}
			newNodes.put(n, newNode);
			oldNodes.put(newNode, n);
		}
		Vector<Node> vecNodes = new Vector<Node>();
		for (Node n : getNodes()) {
			vecNodes.add(n);
		}
		go(vecNodes, 0, tree, newNodes);
	}
	
	public boolean displays(PhyloTree tree) {
		for (PhyloTree pt : displayedTrees) {
			if (tree.isIsomorphicTo(pt)) {
				return true;
			}
		}
		return false;
	}
	
	public Iterable<PhyloTree> getDisplayedTrees() {
		return displayedTrees;
	}
	
	public Node getOldNode(Node v) {
		return oldNodes.get(v);
	}
	
	public void setRoot(Node root) {
		this.root = root;
	}

}
