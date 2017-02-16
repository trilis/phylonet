package util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Vector;

public class PhyloTree extends Graph {

	private Node root, oldRoot;
	
	public PhyloTree() {
		
	}
	
	public PhyloTree(Node root) {
		this.root = root;
	}
	
	public PhyloTree(Graph gr) {
		this.nodes = gr.nodes;
	}
	
	public PhyloTree(PhyloTree old) {
		HashMap<Node, Node> nwnodes = new HashMap<Node, Node>();
		for (Node n : old.nodes) {
			Node nw = new Node(this);
			if (n.isLeaf()) {
				nw = new Node(this, n.getTaxon());
			} 
			if (n == old.root) {
				root = nw;
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

	public PhyloTree buildSubGraph(HashSet<Taxon> taxa) {
		SubTreeDFS dfs = new SubTreeDFS(taxa, this);
		dfs.dfs(root);
		PhyloTree subGraph = dfs.getAnswer();
		subGraph.oldRoot = dfs.getOldRoot();
		subGraph.compress();
		return subGraph;
	}
	
	
	public boolean isIsomorphicTo(PhyloTree tree) {
		IsomorphismChecker checker = new IsomorphismChecker();
		return checker.areBinaryTreesIsomorphic(root, tree.getRoot());
	}
	
	public Node getOldRoot() {
		return oldRoot;
	}
	
	@Override
	public String toString() {
		Newick newick = new Newick();
		return newick.phyloTreeToNewick(this);
	}
	
	public HashSet<Taxon> getAllTaxa() {
		HashSet<Taxon> taxa = new HashSet<Taxon>();
		for (Node n : nodes) {
			if (n.isLeaf()) {
				taxa.add(n.getTaxon());
			}
		}
		return taxa;
	}
	
	public Node getNode(Taxon t) {
		for (Node n : nodes) {
			if (n.isLeaf() && n.getTaxon().equals(t)) {
				return n;
			}
		}
		return null;
	}

}
