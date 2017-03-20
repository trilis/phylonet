package phylonet;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Vector;

import util.Edge;
import util.Graph;
import util.IsomorphismChecker;
import util.Node;
import util.PathDFS;
import util.PhyloTree;
import util.TaxaEmbeddingDFS;
import util.Taxon;

public class SmartAAF {

	private Vector<AgreementForest> allAgreementForests = new Vector<AgreementForest>();
	private int reticulationNumber;
	private PhyloTree originalFirstTree, originalSecondTree;
	private HashMap<Node, HashSet<Taxon>> labels = new HashMap<Node, HashSet<Taxon>>();

	public SmartAAF(PhyloTree tree1, PhyloTree tree2, int reticulationNumber) {
		this.reticulationNumber = reticulationNumber;
		this.originalFirstTree = new PhyloTree(tree1);
		this.originalSecondTree = new PhyloTree(tree2);
		PhyloTree newTree1 = new PhyloTree(tree1);
		PhyloTree newTree2 = new PhyloTree(tree2);
		for (Node n : newTree1.getNodes()) {
			if (n.isLeaf()) {
				HashSet<Taxon> taxa = new HashSet<Taxon>();
				taxa.add(n.getTaxon());
				labels.put(n, taxa);
			}
		}
		build(newTree1, new Forest(newTree2), new HashSet<Cherry>());
	}

	private void build(PhyloTree tree, Forest forest, HashSet<Cherry> removedCherries) {
		tree.softCompress();
		if (forest.getNumberOfTrees() > reticulationNumber + 1) {
			return;
		}
		if (getAnyCherry(tree) == null) {
			Forest answer = forest.recoverAnswer(removedCherries);
			Graph graph = new Graph();
			HashMap<Node, TaxaEmbeddingDFS> embeddings1 = new HashMap<Node, TaxaEmbeddingDFS>();
			HashMap<Node, TaxaEmbeddingDFS> embeddings2 = new HashMap<Node, TaxaEmbeddingDFS>();
			HashMap<PhyloTree, Node> treesToNodes = new HashMap<PhyloTree, Node>();
			HashMap<Node, PhyloTree> nodesToTrees = new HashMap<Node, PhyloTree>();
			IsomorphismChecker checker = new IsomorphismChecker();
			for (PhyloTree tree1 : answer.getTrees()) {
				Node nw = new Node(graph);
				graph.addNode(nw);
				checker.countSubtreeTaxa(tree1);
				TaxaEmbeddingDFS dfs1 = new TaxaEmbeddingDFS(checker.getSubtreeTaxa(tree1.getRoot()),
						originalFirstTree);
				TaxaEmbeddingDFS dfs2 = new TaxaEmbeddingDFS(checker.getSubtreeTaxa(tree1.getRoot()),
						originalSecondTree);
				embeddings1.put(nw, dfs1);
				embeddings2.put(nw, dfs2);
				treesToNodes.put(tree1, nw);
				nodesToTrees.put(nw, tree1);
			}
			for (PhyloTree tree1 : answer.getTrees()) {
				for (PhyloTree tree2 : answer.getTrees()) {
					if (tree1 == tree2) {
						continue;
					}
					Node v = treesToNodes.get(tree1);
					Node u = treesToNodes.get(tree2);
					if (embeddings1.get(v).getOldRoot().isAncestorOf(embeddings1.get(u).getOldRoot())
							|| embeddings2.get(v).getOldRoot().isAncestorOf(embeddings2.get(u).getOldRoot())) {
						graph.addEdge(v, u);
					}
				}
			}
			if (graph.isTree()) {
				buildAgreementForests(answer.getNumberOfTrees(), graph, nodesToTrees, new Forest());
			}
			return;
		}
		PhyloTree newTree = new LabeledTreeCopy(tree, labels).getAnswer();
		Vector<Node> isolatedNodes = getIsolatedNodes(newTree, forest);
		if (isolatedNodes.size() > 0) {
			for (Node n : isolatedNodes) {
				newTree.delNode(n);
				build(newTree, forest, removedCherries);
				return;
			}
		}
		Cherry cherry = getAnyCherry(tree);
		Node node1 = forest.findLabel(cherry.getOldLabel1());
		Node node2 = forest.findLabel(cherry.getOldLabel2());
		build(tree, forest.cutEdge(node1.getInEdges().iterator().next()), removedCherries);
		build(tree, forest.cutEdge(node2.getInEdges().iterator().next()), removedCherries);
		if (node1.getGraph() == node2.getGraph()) {
			if (node1.getParent() == node2.getParent()) {
				HashSet<Cherry> newCherries = new HashSet<Cherry>();
				newCherries.addAll(removedCherries);
				newCherries.add(cherry);
				PhyloTree cutTree = eatCherry(tree, cherry);
				Forest newForest = forest.eatCherry(cherry);
				build(cutTree, newForest, newCherries);
			} else {
				PathDFS path = new PathDFS(node1, node2);
				for (Node v : path.getAns()) {
					if (v == node1 || v == node2) {
						continue;
					}
					for (Edge e : v.getOutEdges()) {
						Node u = e.getFinish();
						if (!path.getAns().contains(u)) {
							build(tree, forest.cutEdge(e), removedCherries);
						}
					}
				}
			}
		}
	}

	private Vector<Node> getIsolatedNodes(PhyloTree tree, Forest forest) {
		Vector<Node> ans = new Vector<Node>();
		for (Node n : tree.getNodes()) {
			if (n.isLeaf() && forest.findLabel(labels.get(n)).getGraph().numberOfLeaves() == 1) {
				ans.add(n);
			}
		}
		return ans;
	}

	private Cherry getAnyCherry(PhyloTree tree) {
		for (Node n : tree.getNodes()) {
			if (!n.isLeaf()) {
				Vector<Node> oldNodes = new Vector<Node>();
				for (Edge e : n.getOutEdges()) {
					if (e.getFinish().isLeaf()) {
						oldNodes.add(e.getFinish());
					}
				}
				if (oldNodes.size() == 2) {
					return new Cherry(labels.get(oldNodes.get(0)), labels.get(oldNodes.get(1)), oldNodes.get(0),
							oldNodes.get(1));
				}
			}
		}
		return null;
	}

	private PhyloTree eatCherry(PhyloTree tree, Cherry cherry) {
		PhyloTree newTree = new LabeledTreeCopy(tree, labels).getAnswer();
		Vector<Node> toRemove = new Vector<Node>();
		for (Node n : newTree.getNodes()) {
			if (n.isLeaf() && labels.get(n).equals(cherry.getOldLabel1())) {
				toRemove.add(n);
				labels.put(n.getParent(), cherry.getNewLabel());
			}
			if (n.isLeaf() && labels.get(n).equals(cherry.getOldLabel2())) {
				toRemove.add(n);
			}
		}
		for (Node n : toRemove) {
			newTree.delNode(n);
		}
		return newTree;
	}

	private void buildAgreementForests(int k, Graph graph, HashMap<Node, PhyloTree> nodesToTrees, Forest addedTrees) {
		if (addedTrees.getNumberOfTrees() == k) {
			allAgreementForests.add(new AgreementForest(addedTrees));
			return;
		}
		for (Node n : graph.getNodes()) {
			if (n.getInDeg() == 0) {
				Forest newTrees = new Forest(addedTrees);
				newTrees.addTree(nodesToTrees.get(n));
				buildAgreementForests(k, graphWithoutNode(graph, n, nodesToTrees), nodesToTrees, newTrees);
			}
		}
	}

	private Graph graphWithoutNode(Graph old, Node v, HashMap<Node, PhyloTree> nodesToTrees) {
		Graph graph = new Graph();
		HashMap<Node, Node> nwnodes = new HashMap<Node, Node>();
		for (Node n : old.getNodes()) {
			if (n == v) {
				continue;
			}
			Node nw = new Node(graph, n);
			nwnodes.put(n, nw);
			nodesToTrees.put(nw, nodesToTrees.get(n));
			graph.addNode(nw);
		}
		for (Node n : old.getNodes()) {
			for (Edge e : n.getOutEdges()) {
				if (n != v && e.getFinish() != v) {
					graph.addEdge(nwnodes.get(n), nwnodes.get(e.getFinish()));
				}
			}
		}
		return graph;
	}

	public Iterable<AgreementForest> getAllAgreementForests() {
		return allAgreementForests;
	}

}
