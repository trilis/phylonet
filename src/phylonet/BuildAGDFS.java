package phylonet;

import java.util.HashMap;
import java.util.HashSet;

import util.DFS;
import util.Graph;
import util.Node;
import util.PhyloTree;

public class BuildAGDFS extends DFS {

	private HashMap<Node, Node> map = new HashMap<Node, Node>();
	private HashSet<Node> gray = new HashSet<Node>();
	private HashMap<Node, PhyloTree> components = new HashMap<Node, PhyloTree>();
	private HashMap<PhyloTree, Node> revComponents = new HashMap<PhyloTree, Node>();
	private Graph answer = new Graph();

	public void putNodes(Node node1, Node node2, PhyloTree tree) {
		Node n = new Node(answer, node1);
		answer.addNode(n);
		map.put(node1, n);
		map.put(node2, n);
		components.put(n, tree);
		revComponents.put(tree, n);
	}

	public BuildAGDFS(HashMap<Node, PhyloTree> components, HashMap<PhyloTree, Node> revComponents) {
		this.revComponents = revComponents;
		this.components = components;
	}

	@Override
	public void enter(Node v) {
		if (map.containsKey(v)) {
			for (Node n : gray) {
				if (!answer.hasEdge(map.get(n), map.get(v))) {
					answer.addEdge(map.get(n), map.get(v));
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