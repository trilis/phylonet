package util;

import java.util.HashMap;
import java.util.HashSet;

public class PhyloTree extends Graph {

	private Node root;

	public PhyloTree() {

	}

	public PhyloTree(Graph gr, Node root) {
		super(gr);
		this.root = root;
	}

	public PhyloTree(PhyloTree old) {
		HashMap<Node, Node> nwnodes = new HashMap<Node, Node>();
		for (Node n : old.getNodes()) {
			Node nw = new Node(this, n);
			if (n == old.root) {
				root = nw;
			}
			nwnodes.put(n, nw);
			addNode(nw);
		}
		for (Node n : old.getNodes()) {
			for (Edge e : n.getOutEdges()) {
				addEdge(nwnodes.get(n), nwnodes.get(e.getFinish()));
			}
		}
	}

	public void setRoot(Node root) {
		this.root = root;
	}

	public Node getRoot() {
		return root;
	}

	public boolean isIsomorphicTo(PhyloTree tree) {
		IsomorphismChecker checker = new IsomorphismChecker();
		return checker.areBinaryTreesIsomorphic(this, tree);
	}

	@Override
	public String toString() {
		Newick newick = new Newick();
		return newick.phyloTreeToNewick(this);
	}

	public HashSet<Taxon> getAllTaxa() {
		HashSet<Taxon> taxa = new HashSet<Taxon>();
		for (Node n : getNodes()) {
			if (n.isLeaf()) {
				taxa.add(n.getTaxon());
			}
		}
		return taxa;
	}

	public Node getNode(Taxon t) {
		for (Node n : getNodes()) {
			if (n.isLeaf() && n.getTaxon().equals(t)) {
				return n;
			}
		}
		return null;
	}

	public void addFakeTaxon(Taxon rho) {
		Node newRoot = new Node(this);
		Node newLeaf = new Node(this, rho);
		addNode(newLeaf);
		addNode(newRoot);
		addEdge(newRoot, root);
		addEdge(newRoot, newLeaf);
		setRoot(newRoot);
	}

}
