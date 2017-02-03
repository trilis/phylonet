package phylonet;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;

public class AgreementForest {

	private Vector<PhyloTree> trees, trees2, treesInOrder;
	private HashMap<Node, PhyloTree> components;
	private Graph graph;
	
	public AgreementForest (AgreementForest old) {
		HashMap<PhyloTree, PhyloTree> newtrees = new HashMap<PhyloTree, PhyloTree>();
		HashMap<Node, Node> newnodes = new HashMap<Node, Node>();
		for (PhyloTree tree : old.trees) {
			PhyloTree nw = new PhyloTree(tree);
			trees.add(nw);
			newtrees.put(tree, nw);
		}
		for (PhyloTree tree2 : old.trees2) {
			trees2.add(new PhyloTree(tree2));
		}
		for (Node n : old.components.keySet()) {
			Node nw = new Node();
			newnodes.put(n, nw);
			components.put(nw, newtrees.get(components.get(n)));
			graph.addNode(nw);
		}
		for (Node n : old.components.keySet()) {
			Iterator<Edge> iterator = n.getOutEdges();
			while (iterator.hasNext()) {
				graph.addEdge(newnodes.get(n), newnodes.get(iterator.next().getFinish()));
			}
		}
	}

	public AgreementForest(PhyloTree tree1, PhyloTree tree2, Vector<HashSet<Taxon>> partition) {
		BuildAGDFS dfs = new BuildAGDFS(components);
		for (HashSet<Taxon> taxa : partition) {
			PhyloTree t1 = tree1.buildSubGraph(taxa);
			PhyloTree t2 = tree2.buildSubGraph(taxa);
			if (t1.equals(t2)) {
				dfs.putNodes(t1.getOldRoot(), t2.getOldRoot(), t1);
				trees.add(t1);
				trees2.add(t2);
			} else {
				throw new IllegalArgumentException("Partition is illegal");
			}
		}
		dfs.dfs(tree1.getRoot());
		dfs.dfs(tree2.getRoot());
		graph = dfs.getAG();
		if (!graph.isTree()) {
			throw new IllegalArgumentException("Agreement forest is not acyclic");
		}
	}
	
	public boolean isOrderingFull() {
		return treesInOrder.size() == trees.size();
	}
	
	public Vector<Node> getNodesWithNoIncoming() {
		return graph.getNodesWithNoIncoming();
	}
	
	public void delComponent(Node n) {
		graph.delNode(n);
		treesInOrder.add(components.get(n));
		components.remove(n);
	}
	
	public void addComponent(Node n) {
		graph.addNode(n);
		components.put(n, treesInOrder.lastElement());
		treesInOrder.remove(components.get(n));
	}
	
}