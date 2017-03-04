package util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Vector;

public class Graph {

	private HashSet<Node> nodes = new HashSet<Node>();
	
	public Graph() {
		
	}
	
	public Graph(Graph old) {
		HashMap<Node, Node> nwnodes = new HashMap<Node, Node>();
		for (Node n : old.nodes) {
			Node nw = new Node(this, n);
			nwnodes.put(n, nw);
			addNode(nw);
		}
		for (Node n : old.nodes) {
			for (Edge e : n.getOutEdges()) {
				addEdge(nwnodes.get(n), nwnodes.get(e.getFinish()));
			}
		}
	}
	
	public void addNode(Node node) {
		if (!node.getGraph().equals(this)) {
			throw new RuntimeException("Node from other graph");
		}
		nodes.add(node);
	}
	
	public void delNode(Node node) {
		if (!node.getGraph().equals(this)) {
			throw new RuntimeException("Node from other graph");
		}
		Vector<Edge> toRemove = new Vector<Edge>();
		for (Edge e : node.getInEdges()) {
			e.getStart().delOutEdge(e);
			toRemove.add(e);
		}
		for (Edge e : toRemove) {
			node.delInEdge(e);
		}
		toRemove.clear();
		for (Edge e : node.getOutEdges()) {
			e.getFinish().delInEdge(e);
			toRemove.add(e);
		}
		for (Edge e : toRemove) {
			node.delOutEdge(e);
		}
		nodes.remove(node);
	}
	
	public void addEdge(Node start, Node finish) {
		if (!start.getGraph().equals(this) || !finish.getGraph().equals(this)) {
			throw new RuntimeException("Node from other graph");
		}
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
	
	public boolean isTree() {
		CheckTreeDFS dfs = new CheckTreeDFS(this);
		return dfs.isTree();
	}
	
	public boolean hasEdge(Node start, Node finish) {
		if (!start.getGraph().equals(this) || !finish.getGraph().equals(this)) {
			throw new RuntimeException("Node from other graph");
		}
		for (Edge e : start.getOutEdges()) {
			if (e.getFinish().equals(finish)) {
				return true;
			}
		}
		return false;
	}
	
	public Vector<Node> getNodesWithNoIncoming() {
		Vector<Node> answer = new Vector<Node>();
		for (Node n : nodes) {
			if (n.getInDeg() == 0) {
				answer.add(n);
			}
		}
		return answer;
	}
	
	public void compress() {
		Vector<Node> toRemove = new Vector<Node>();
		for (Node n : nodes) {
			Node p = n;
			while (p.getInDeg() == 1 && p.getOutDeg() == 0 && p.getTaxon() == null) {
				Node pp = p.getParent();
				delEdge(p.getInEdges().iterator().next());
				toRemove.add(p);
				p = pp;
			}
		}
		for (Node n : nodes) {
			if (n.getInDeg() == 1 && n.getOutDeg() == 1) {
				Edge in = null;
				for (Edge e : n.getInEdges()) {
					in = e;
					break;
				}
				Edge out = null;
				for (Edge e : n.getOutEdges()) {
					out = e;
					break;
				}
				in.getStart().delOutEdge(in);
				out.getFinish().delInEdge(out);
				addEdge(in.getStart(), out.getFinish());
				toRemove.addElement(n);
			}
		}
		for (Node n : toRemove) {
			nodes.remove(n);
		}
	}
	
	public Iterable<Node> getNodes() {
		return nodes;
	}
	
}
