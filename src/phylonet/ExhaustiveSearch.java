package phylonet;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Vector;

import util.Edge;
import util.HybridizationNetwork;
import util.IsomorphismChecker;
import util.Node;
import util.PhyloTree;
import util.TaxaEmbeddingDFS;
import util.Taxon;

public class ExhaustiveSearch {

	private Vector<HybridizationNetwork> allHNetworks = new Vector<HybridizationNetwork>();

	public ExhaustiveSearch(Vector<PhyloTree> input, int reticulationNumber) {
		for (int i = 0; i < input.size(); i++) {
			for (int j = i + 1; j < input.size(); j++) {
				PhyloTree tree1 = input.get(i);
				PhyloTree tree2 = input.get(j);
				HashSet<Taxon> taxaset = new HashSet<Taxon>();
				taxaset.addAll(tree1.getAllTaxa());
				taxaset.addAll(tree2.getAllTaxa());
				Vector<Taxon> taxa = new Vector<Taxon>();
				taxa.addAll(taxaset);
				Vector<HybridizationNetwork> networks = countallHNetworks(tree1, tree2, taxa, reticulationNumber);
				for (HybridizationNetwork hn : networks) {
					hn.compress();
					hn.countDisplayedTrees();
					int displayedNumber = 0;
					for (PhyloTree tree : input) {
						if (hn.displays(tree)) {
							displayedNumber++;
						}
					}
					if (displayedNumber == input.size()) {
						boolean used = false;
						IsomorphismChecker checker = new IsomorphismChecker();
						for (HybridizationNetwork old : allHNetworks) {
							if (checker.areNetworksIsomorphic(old, hn)) {
								used = true;
								break;
							}
						}
						if (!used) {
							allHNetworks.add(hn);
						}
					}
				}
			}
		}
	}

	private Vector<HybridizationNetwork> countallHNetworks(PhyloTree tree1, PhyloTree tree2, Vector<Taxon> taxa,
			int reticulationNumber) {
		Vector<HybridizationNetwork> ans = new Vector<HybridizationNetwork>();
		NaiveAAF aaf = new NaiveAAF(tree1, tree2, taxa, reticulationNumber);
		for (AgreementForest af : aaf.getAllAgreementForests()) {
			HybridizationNetwork hn = new HybridizationNetwork(tree1);
			HashSet<Taxon> added = new HashSet<Taxon>();
			HashSet<Taxon> allTaxa = new HashSet<Taxon>();
			added.addAll(af.getTaxaOfTree(0));
			allTaxa.addAll(added);
			insertEdges(hn, tree2, 0, af, added, allTaxa, ans);
		}
		return ans;
	}

	private void insertEdges(HybridizationNetwork hn, PhyloTree secondTree, int edgesInserted, AgreementForest af,
			HashSet<Taxon> added, HashSet<Taxon> allTaxa, Vector<HybridizationNetwork> ans) {
		if (edgesInserted == af.getNumberOfTrees() - 1) {
			ans.add(hn);
			return;
		}
		hn.countDisplayedTrees();
		HashSet<Taxon> newTaxa = af.getTaxaOfTree(edgesInserted + 1);
		allTaxa.addAll(newTaxa);
		HashSet<Node> targets = getTargetNodes(hn, secondTree, added, newTaxa, allTaxa);
		HashSet<Node> sources = getSourceNodes(hn, secondTree, added, newTaxa, allTaxa, af, edgesInserted + 1);
		added.addAll(newTaxa);
		for (Node target : targets) {
			for (Node source : sources) {
				if (!target.isAncestorOf(source)) {
					HybridizationNetwork newHN = copyNetworkWithNewEdge(hn, source, target);
					insertEdges(newHN, secondTree, edgesInserted + 1, af, added, allTaxa, ans);
				}
			}
		}
		added.remove(newTaxa);
		allTaxa.remove(newTaxa);
	}

	private HashSet<Node> getTargetNodes(HybridizationNetwork hn, PhyloTree secondTree, HashSet<Taxon> added,
			HashSet<Taxon> newTaxa, HashSet<Taxon> allTaxa) {
		HashSet<Node> target = new HashSet<Node>();
		TaxaEmbeddingDFS dfs = new TaxaEmbeddingDFS(newTaxa, secondTree);
		PhyloTree embeddedTree = dfs.getAnswer();
		IsomorphismChecker checker = new IsomorphismChecker();
		checker.countSubtreeTaxa(embeddedTree);
		for (PhyloTree tree : hn.getDisplayedTrees()) {
			dfs = new TaxaEmbeddingDFS(allTaxa, tree);
			PhyloTree tree2 = dfs.getAnswer();
			checker.countSubtreeTaxa(tree2);
			for (Node n : tree2.getNodes()) {
				if (checker.areSubtreesIsomorphic(n, embeddedTree.getRoot())) {
					Node candidate = hn.getOldNode(dfs.getOldNode(n));
					if (!candidate.hasEdgeToReticulation()) {
						target.add(candidate);
					}
				}
			}
		}
		return target;
	}

	private HashSet<Node> getSourceNodes(HybridizationNetwork hn, PhyloTree secondTree, HashSet<Taxon> added,
			HashSet<Taxon> newTaxa, HashSet<Taxon> allTaxa, AgreementForest af, int currentComponent) {
		HashSet<Node> source = new HashSet<Node>();
		TaxaEmbeddingDFS dfs = new TaxaEmbeddingDFS(allTaxa, secondTree);
		PhyloTree embeddedTree = dfs.getAnswer();
		IsomorphismChecker checker = new IsomorphismChecker();
		checker.countSubtreeTaxa(embeddedTree);
		Node vSib = null;
		for (Node n : embeddedTree.getNodes()) {
			if (checker.getSubtreeTaxa(n).equals(newTaxa)) {
				vSib = n.getSibling();
				break;
			}
		}
		for (PhyloTree tree : hn.getDisplayedTrees()) {
			dfs = new TaxaEmbeddingDFS(added, tree);
			PhyloTree tree2 = dfs.getAnswer();
			checker.countSubtreeTaxa(tree2);
			checker.countSubtreeTaxa(tree);
			for (Node n : tree2.getNodes()) {
				if (checker.areSubtreesIsomorphic(n, vSib)) {
					Node candidate = hn.getOldNode(dfs.getOldNode(n));
					try {
						Node candidate2 = dfs.getOldNode(n).getSibling();
						for (Node nInSubTree : tree.getNodes()) {
							if (candidate2.isAncestorOf(nInSubTree)) {
								for (int i = currentComponent; i < af.getNumberOfTrees(); i++) {
									if (checker.getSubtreeTaxa(nInSubTree).equals(af.getTaxaOfTree(i))) {
										Node candidate3 = hn.getOldNode(nInSubTree);
										if (!candidate3.isReticulation()) {
											source.add(hn.getOldNode(nInSubTree));
										}
									}
								}
							}
						}
					} catch (IllegalArgumentException exc) {}
					if (!candidate.isReticulation()) {
						source.add(candidate);
					}
				}
			}
		}
		return source;
	}
	
	private HybridizationNetwork copyNetworkWithNewEdge(HybridizationNetwork old, Node source, Node target) {
		HybridizationNetwork hn = new HybridizationNetwork();
		HashMap<Node, Node> nwnodes = new HashMap<Node, Node>();
		for (Node n : old.getNodes()) {
			Node nw = new Node(hn, n);
			if (n == old.getRoot()) {
				hn.setRoot(nw);
			}
			nwnodes.put(n, nw);
			hn.addNode(nw);
		}
		for (Node n : old.getNodes()) {
			for (Edge e : n.getOutEdges()) {
				hn.addEdge(nwnodes.get(n), nwnodes.get(e.getFinish()));
			}
		}
		source = nwnodes.get(source);
		target = nwnodes.get(target);
		Node new1 = new Node(hn);
		Node new2 = new Node(hn);
		for (Edge e : source.getInEdges()) {
			Node parent = e.getStart();
			hn.delEdge(e);
			hn.addNode(new1);
			hn.addEdge(parent, new1);
			hn.addEdge(new1, source);
			break;
		}
		if (target.getInDeg() == 1) {
			for (Edge e : target.getInEdges()) {
				Node parent = e.getStart();
				Node new3 = new Node(hn);
				hn.delEdge(e);
				hn.addNode(new2);
				hn.addNode(new3);
				hn.addEdge(parent, new3);
				hn.addEdge(new3, new2);
				hn.addEdge(new2, target);
				break;
			}
		} else {
			new2 = target;
		}
		hn.addEdge(new1, new2);
		return hn;
	}

	public Iterable<HybridizationNetwork> getAllHNetworks() {
		return allHNetworks;
	}

	public int getNetworkNumber() {
		return allHNetworks.size();
	}

	public boolean hasNetworks() {
		return getNetworkNumber() > 0;
	}
}
