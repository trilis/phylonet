package util;

public class Edge {
	
	private Node start, finish;
	private Graph graph;
	
	public Edge(Graph graph, Node start, Node finish) {
		this.graph = graph;
		this.start = start;
		this.finish = finish;
	}
	
	public Node getStart() {
		return start;
	}
	
	public Node getFinish() {
		return finish;
	}
	
	public Graph getGraph() {
		return graph;
	}
}
