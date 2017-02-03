package phylonet;

import java.util.HashMap;
import java.util.HashSet;

public class BuildAGDFS extends DFS {

	private HashMap<Node, Node> map;
	private HashSet<Node> gray;
	private HashMap<Node, PhyloTree> components;
	private Graph answer;
	
	public void putNodes(Node node1, Node node2, PhyloTree tree) {
		Node n = new Node();
		answer.addNode(n);
		map.put(node1, n);
		map.put(node2, n);
		components.put(n, tree);
	}
	
	public BuildAGDFS(HashMap<Node, PhyloTree> components) {
		this.components = components;
	}
	
	@Override
	public void enter(Node v) {
		if (map.containsKey(v)) {
			for (Node n : gray) {
				if (!answer.hasEdge(n, v)) {
					answer.addEdge(n, v);
				}
			}
			gray.add(v);
		}
	}

	@Override
	public void exit(Node v) {
		gray.remove(v);
	}
	
	public Graph getAG() {
		return answer;
	}

}
