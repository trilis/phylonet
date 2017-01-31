package phylonet;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;

public class PhyloTree extends Graph {

	private Node root;
	
	public PhyloTree() {
		
	}

	public PhyloTree(Graph gr) {
		this.nodes = gr.nodes;
	}
	
	public void setRoot(Node root) {
		this.root = root;
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
		dfs.ans.compress();
		return dfs.ans;
	}
}
