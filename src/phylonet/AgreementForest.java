package phylonet;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Vector;

import util.Edge;
import util.Graph;
import util.Node;
import util.PhyloTree;
import util.TaxaEmbeddingDFS;
import util.Taxon;

public class AgreementForest extends Forest {

	public AgreementForest(PhyloTree tree1, PhyloTree tree2, Vector<HashSet<Taxon>> partition) {
		HashMap<Node, PhyloTree> components = new HashMap<Node, PhyloTree>();
		HashMap<PhyloTree, Node> revComponents = new HashMap<PhyloTree, Node>();
		Graph graph = new Graph();
		Vector<PhyloTree> trees2 = new Vector<PhyloTree>();
		BuildAGDFS dfs = new BuildAGDFS(components, revComponents);
		for (Node n : tree1.getNodes()) {
			n.numberOfVisits = 0;
		}
		for (Node n : tree2.getNodes()) {
			n.numberOfVisits = 0;
		}
		for (HashSet<Taxon> taxa : partition) {
			TaxaEmbeddingDFS subdfs1 = new TaxaEmbeddingDFS(taxa, tree1);
			PhyloTree subGraph1 = subdfs1.getAnswer();
			subGraph1.compress();
			TaxaEmbeddingDFS subdfs2 = new TaxaEmbeddingDFS(taxa, tree2);
			PhyloTree subGraph2 = subdfs2.getAnswer();
			subGraph2.compress();
			if (subGraph1.isIsomorphicTo(subGraph2)) {
				dfs.putNodes(subdfs1.getOldNode(subGraph1.getRoot()), subdfs2.getOldNode(subGraph2.getRoot()),
						subGraph1);
				addTree(subGraph1);
				trees2.add(subGraph2);
			} else {
				throw new IllegalArgumentException("Partition is illegal");
			}
		}
		for (Node n : tree1.getNodes()) {
			if (n.numberOfVisits > 1) {
				throw new IllegalArgumentException("Partition is not node disjoint");
			}
		}
		for (Node n : tree2.getNodes()) {
			if (n.numberOfVisits > 1) {
				throw new IllegalArgumentException("Partition is not node disjoint");
			}
		}
		dfs.dfs(tree1.getRoot());
		dfs.dfs(tree2.getRoot());
		graph = dfs.getAG();
		if (!graph.isTree()) {
			throw new IllegalArgumentException("Agreement forest is not acyclic");
		}
		for (PhyloTree tree : getTrees()) {
			Node v = revComponents.get(tree);
			if (v.getInDeg() != 0) {
				throw new IllegalArgumentException("Ordering is not acyclic");
			}
			Vector<Edge> toDel = new Vector<Edge>();
			for (Edge e : v.getOutEdges()) {
				toDel.add(e);
			}
			for (Edge e : toDel) {
				graph.delEdge(e);
			}
		}
	}

	public AgreementForest(Forest forest) {
		super(forest);
	}

}
