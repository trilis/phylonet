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
	
	public Node(Graph graph, Taxon taxon) {
		this.graph = graph;
		this.taxon = taxon;
	}
	
	public Node copy(Graph newGraph) {
		Node n = new Node(newGraph);
		n.taxon = taxon;
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
}
