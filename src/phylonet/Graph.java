package phylonet;

import java.util.HashSet;
import java.util.Iterator;

public class Graph {

	protected HashSet<Node> nodes = new HashSet<Node>();
	
	public Graph copy() {
		Graph gr = new Graph();
		for (Node n : nodes) {
			gr.nodes.add(n.copy(gr));
		}
		return gr;
	}
	
	public void addNode(Node node) {
		nodes.add(node);
	}
	
	public void delNode(Node node) {
		if (!node.getGraph().equals(this)) {
			throw new RuntimeException("Node from other graph");
		}
		Iterator<Edge> inEdges = node.getInEdges();
		Iterator<Edge> outEdges = node.getOutEdges();
		HashSet<Edge> toRemove = new HashSet<Edge>();
		while (inEdges.hasNext()) {
			Edge e = inEdges.next();
			e.getStart().delOutEdge(e);
			toRemove.add(e);
		}
		for (Edge e : toRemove) {
			node.delInEdge(e);
		}
		toRemove.clear();
		while (outEdges.hasNext()) {
			Edge e = outEdges.next();
			e.getFinish().delInEdge(e);
			toRemove.add(e);
		}
		for (Edge e : toRemove) {
			node.delOutEdge(e);
		}
		nodes.remove(node);
	}
	
	public void addEdge(Node start, Node finish) {
		Edge edge = new Edge(this, start, finish);
		start.addOutEdge(edge);
		finish.addInEdge(edge);
	}
	
	public void delEdge(Edge edge) {
		if (!edge.getGraph().equals(this)) {
			throw new RuntimeException("Edge from other graph");
		}
		edge.getStart().delOutEdge(edge);
		edge.getFinish().delInEdge(edge);
	}
	
	public boolean hasNode(Node node) {
		return nodes.contains(node);
	}
	
}
