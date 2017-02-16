package phylonet;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Vector;

import util.Edge;
import util.Graph;
import util.Node;
import util.PhyloTree;
import util.Taxon;

public class AgreementForest {

	private Vector<PhyloTree> trees = new Vector<PhyloTree>();
	private Vector<PhyloTree> trees2 = new Vector<PhyloTree>();
	private Vector<PhyloTree> treesInOrder = new Vector<PhyloTree>();
	private HashMap<Node, PhyloTree> components = new HashMap<Node, PhyloTree>();
	private Graph graph = new Graph();
	
	public AgreementForest() {
		
	}
	
	public AgreementForest (AgreementForest old) {
		HashMap<PhyloTree, PhyloTree> newtrees = new HashMap<PhyloTree, PhyloTree>();
		HashMap<Node, Node> newnodes = new HashMap<Node, Node>();
		for (PhyloTree tree : old.trees) {
			PhyloTree nw = new PhyloTree(tree);
			trees.add(nw);
			newtrees.put(tree, nw);
		}
		for (PhyloTree tree2 : old.trees2) {
			trees2.add(new PhyloTree(tree2));
		}
		for (PhyloTree tree : old.treesInOrder) {
			treesInOrder.add(newtrees.get(tree));
		}
		for (Node n : old.components.keySet()) {
			Node nw = new Node(graph);
			if (n.isLeaf()) {
				nw = new Node(graph, n.getTaxon());
			} 
			newnodes.put(n, nw);
			components.put(nw, newtrees.get(old.components.get(n)));
			graph.addNode(nw);
		}
		for (Node n : old.components.keySet()) {
			for (Edge e : n.getOutEdges()) {
				graph.addEdge(newnodes.get(n), newnodes.get(e.getFinish()));
			}
		}
	}

	public AgreementForest(PhyloTree tree1, PhyloTree tree2, Vector<HashSet<Taxon>> partition) {
		BuildAGDFS dfs = new BuildAGDFS(components);
		for (Node n : tree1.nodes) {
			n.numberOfVisits = 0;
		}
		for (Node n : tree2.nodes) {
			n.numberOfVisits = 0;
		}
		for (HashSet<Taxon> taxa : partition) {
			PhyloTree t1 = tree1.buildSubGraph(taxa);
			PhyloTree t2 = tree2.buildSubGraph(taxa);
			if (t1.isIsomorphicTo(t2)) {
				dfs.putNodes(t1.getOldRoot(), t2.getOldRoot(), t1);
				trees.add(t1);
				trees2.add(t2);
			} else {
				throw new IllegalArgumentException("Partition is illegal");
			}
		}

		for (Node n : tree1.nodes) {
			if (n.numberOfVisits > 1) {
				throw new IllegalArgumentException("Partition is not node disjoint");
			}
		}
		for (Node n : tree2.nodes) {
			if (n.numberOfVisits > 1) {
				throw new IllegalArgumentException("Partition is not node disjoint");
			}
		}
		dfs.dfs(tree1.getRoot());
		dfs.dfs(tree2.getRoot());
		graph = dfs.getAG();
		if (!graph.isTree()) {
			throw new IllegalArgumentException("Agreement forest is not acyclic");
		}
	}
	
	public boolean isOrderingFull() {
		return treesInOrder.size() == trees.size();
	}
	
	public Vector<Node> getNodesWithNoIncoming() {
		return graph.getNodesWithNoIncoming();
	}
	
	public AgreementForest copyWithoutNode(Node v) {
		AgreementForest nwaf = new AgreementForest();
		HashMap<PhyloTree, PhyloTree> newtrees = new HashMap<PhyloTree, PhyloTree>();
		HashMap<Node, Node> newnodes = new HashMap<Node, Node>();
		for (PhyloTree tree : this.trees) {
			PhyloTree nw = new PhyloTree(tree);
			nwaf.trees.add(nw);
			newtrees.put(tree, nw);
		}
		for (PhyloTree tree2 : this.trees2) {
			nwaf.trees2.add(new PhyloTree(tree2));
		}
		for (PhyloTree tree : this.treesInOrder) {
			nwaf.treesInOrder.add(newtrees.get(tree));
		}
		for (Node n : this.components.keySet()) {
			Node nw = new Node(nwaf.graph);
			if (n.isLeaf()) {
				nw = new Node(nwaf.graph, n.getTaxon());
			}
			if (n != v) {
				newnodes.put(n, nw);
				nwaf.components.put(nw, newtrees.get(this.components.get(n)));
				nwaf.graph.addNode(nw);
			} else {
				nwaf.treesInOrder.add(newtrees.get(this.components.get(n)));
			}
		}
		for (Node n : this.components.keySet()) {
			for (Edge e : n.getOutEdges()) {
				Node u = e.getFinish();
				if (n != v && n != u) {
					nwaf.graph.addEdge(newnodes.get(n), newnodes.get(u));
				}
			}
		}
		return nwaf;
	}
	
	public void delComponent(Node n) {
		graph.delNode(n);
		treesInOrder.add(components.get(n));
		components.remove(n);
	}
	
	public void addComponent(Node n) {
		graph.addNode(n);
		components.put(n, treesInOrder.lastElement());
		treesInOrder.remove(components.get(n));
	}
	
	public Iterable<PhyloTree> getOrdering() {
		return treesInOrder;
	}
	
	public int getNumberOfTrees() {
		return trees.size();
	}
	
}
