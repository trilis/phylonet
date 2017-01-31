package phylonet;

import java.util.Iterator;
import java.util.Vector;

public class Node {
	
	private Vector<Edge> inEdges = new Vector<Edge>();
	private Vector<Edge> outEdges = new Vector<Edge>();
	private Graph graph;
	private Taxon taxon;
	
	public Node copy(Graph newGraph) {
		Node n = new Node();
		n.taxon = taxon;
		n.graph = newGraph;
		for (Edge e : inEdges) {
			n.inEdges.addElement(new Edge(newGraph, e.getStart(), n));
		}
		for (Edge e : outEdges) {
			n.outEdges.addElement(new Edge(newGraph, n, e.getFinish()));
		}
		return n;
	}
	
	public void addInEdge(Edge edge) {
		inEdges.addElement(edge);
	}
	
	public void addOutEdge(Edge edge) {
		outEdges.addElement(edge);
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
