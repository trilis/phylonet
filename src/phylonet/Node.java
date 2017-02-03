package phylonet;

import java.util.HashSet;
import java.util.Iterator;

public class Node {
	
	private HashSet<Edge> inEdges = new HashSet<Edge>();
	private HashSet<Edge> outEdges = new HashSet<Edge>();
	private Graph graph;
	private Taxon taxon;
	
	public Node copy(Graph newGraph) {
		Node n = new Node();
		n.taxon = taxon;
		n.graph = newGraph;
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
	
	public Iterator<Edge> getInEdges() {
		return inEdges.iterator();
	}
	
	public Iterator<Edge> getOutEdges() {
		return outEdges.iterator();
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
}
