package util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Vector;

public class Graph {

	public HashSet<Node> nodes = new HashSet<Node>();
	
	public Graph() {
		
	}
	
	public Graph(Graph old) {
		HashMap<Node, Node> nwnodes = new HashMap<Node, Node>();
		for (Node n : old.nodes) {
			Node nw = new Node(this);
			if (n.isLeaf()) {
				nw = new Node(this, n.getTaxon());
			} 
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
		CheckTreeDFS dfs = new CheckTreeDFS();
		for (Node n : nodes) {
			if (!dfs.isUsed(n)) {
				dfs.dfs(n);
			}
		}
		return dfs.isTree;
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
	
}
