package util;

import java.util.Vector;

public class Node {

	private Vector<Edge> inEdges = new Vector<Edge>();
	private Vector<Edge> outEdges = new Vector<Edge>();
	private Graph graph;
	private Taxon taxon;
	public int numberOfVisits = 0;

	public Node(Graph graph) {
		this.graph = graph;
	}

	public Node(Graph graph, Node old) {
		this.graph = graph;
		if (old.isLeaf()) {
			this.taxon = old.getTaxon();
		}
	}

	public Node(Graph graph, Taxon t) {
		this.graph = graph;
		this.taxon = t;
	}

	public Node copy(Graph newGraph) {
		Node n = new Node(newGraph, this);
		for (Edge e : inEdges) {
			n.inEdges.add(new Edge(newGraph, e.getStart(), n));
		}
		for (Edge e : outEdges) {
			n.outEdges.add(new Edge(newGraph, n, e.getFinish()));
		}
		return n;
	}

	public void addInEdge(Edge edge) {
		inEdges.add(edge);
	}

	public void addOutEdge(Edge edge) {
		outEdges.add(edge);
	}

	public void delInEdge(Edge edge) {
		inEdges.remove(edge);
	}

	public void delOutEdge(Edge edge) {
		outEdges.remove(edge);
	}

	public Graph getGraph() {
		return graph;
	}

	public Iterable<Edge> getInEdges() {
		return inEdges;
	}

	public Iterable<Edge> getOutEdges() {
		return outEdges;
	}

	public int getInDeg() {
		return inEdges.size();
	}

	public int getOutDeg() {
		return outEdges.size();
	}

	public boolean isLeaf() {
		return getOutDeg() == 0;
	}

	public Taxon getTaxon() {
		return taxon;
	}

	public Node getParent() {
		return inEdges.get(0).getStart();
	}

	public boolean isReticulation() {
		return this.getInDeg() > 1;
	}

	public void setTaxon(Taxon t) {
		taxon = t;
	}

	@Override
	public String toString() {
		Newick newick = new Newick();
		return "(node) " + newick.nodeToNewick(this);
	}

	public boolean isAncestorOf(Node v) {
		AncestorCheckDFS dfs = new AncestorCheckDFS(this, v);
		return dfs.getAnswer();
	}

	public Node getSibling() {
		if (this.inEdges.size() == 0) {
			throw new IllegalArgumentException("No sibling");
		}
		Node parent = this.getParent();
		for (Edge e : parent.outEdges) {
			if (e.getFinish() != this) {
				return e.getFinish();
			}
		}
		throw new IllegalArgumentException("No sibling");
	}

	public boolean hasEdgeToReticulation() {
		for (Edge e : outEdges) {
			if (e.getFinish().isReticulation()) {
				return true;
			}
		}
		return false;
	}
}
