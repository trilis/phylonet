package phylonet;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;

public class PhyloTree extends Graph {

	private Node root, oldRoot;
	
	public PhyloTree() {
		
	}
	
	public PhyloTree(Graph gr) {
		this.nodes = gr.nodes;
	}
	
	public PhyloTree(PhyloTree old) {
		HashMap<Node, Node> nwnodes = new HashMap<Node, Node>();
		for (Node n : old.nodes) {
			Node nw = new Node(this, n.getTaxon());
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
	
	public void setRoot(Node root) {
		this.root = root;
	}
	
	public Node getRoot() {
		return root;
	}
	
	public void compress() {
		Vector<Node> toRemove = new Vector<Node>();
		for (Node n : nodes) {
			if (n.getInDeg() == 1 && n.getOutDeg() == 1) {
				Iterator<Edge> iterator = n.getInEdges();
				Edge in = iterator.next();
				iterator = n.getOutEdges();
				Edge out = iterator.next();
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

	public PhyloTree buildSubGraph(HashSet<Taxon> taxa) {
		SubTreeDFS dfs = new SubTreeDFS(taxa, this);
		dfs.dfs(root);
		oldRoot = dfs.getOldRoot();
		PhyloTree subGraph = dfs.getAnswer();
		subGraph.compress();
		return subGraph;
	}
	
	public Node getParent(Node v) {
		Iterator<Edge> itr = v.getInEdges();
		return itr.next().getStart();
	}
	
	public boolean isIsomorphicTo(PhyloTree tree) {
		IsomorphismChecker checker = new IsomorphismChecker();
		return checker.areBinaryTreesIsomorphic(root, tree.getRoot());
	}
	
	public Node getOldRoot() {
		return oldRoot;
	}

}
