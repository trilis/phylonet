package phylonet;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;

public class Graph {

	protected HashSet<Node> nodes = new HashSet<Node>();
	
	public Graph() {
		
	}
	
	public Graph(Graph old) {
		HashMap<Node, Node> nwnodes = new HashMap<Node, Node>();
		for (Node n : old.nodes) {
			Node nw = new Node();
			nwnodes.put(n, nw);
			addNode(nw);
		}
		for (Node n : old.nodes) {
			Iterator<Edge> itr = n.getOutEdges();
			while (itr.hasNext()) {
				addEdge(nwnodes.get(n), nwnodes.get(itr.next().getFinish()));
			}
		}
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
		Vector<Edge> toRemove = new Vector<Edge>();
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
		Iterator<Edge> itr = start.getOutEdges();
		while (itr.hasNext()) {
			if (itr.next().getFinish().equals(finish)) {
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
