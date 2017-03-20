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

public class AlternativeExhaustiveSearch {

	private Vector<Vector<PhyloTree>> allPermutations = new Vector<Vector<PhyloTree>>();
	private Vector<HybridizationNetwork> allHNetworks = new Vector<HybridizationNetwork>();

	private void countAllPermutations(Vector<PhyloTree> input, Vector<PhyloTree> ans) {
		if (ans.size() == input.size()) {
			allPermutations.add(ans);
			return;
		}
		for (PhyloTree tree : input) {
			if (!ans.contains(tree)) {
				Vector<PhyloTree> newAns = new Vector<PhyloTree>();
				newAns.addAll(ans);
				newAns.add(tree);
				countAllPermutations(input, newAns);
			}
		}
	}

	public AlternativeExhaustiveSearch(Vector<PhyloTree> input, int reticulationNumber) {
		countAllPermutations(input, new Vector<PhyloTree>());
		for (Vector<PhyloTree> permutation : allPermutations) {
			HybridizationNetwork start = new HybridizationNetwork(permutation.get(0));
			Vector<Vector<HybridizationNetwork>> networks = new Vector<Vector<HybridizationNetwork>>();
			networks.add(new Vector<HybridizationNetwork>());
			networks.get(0).add(start);
			for (int i = 1; i < permutation.size(); i++) {
				networks.add(new Vector<HybridizationNetwork>());
				for (HybridizationNetwork hn : networks.get(i - 1)) {
					hn.countDisplayedTrees();
					for (PhyloTree displayedTree : hn.getDisplayedTrees()) {
						displayedTree.compress();
						SmartAAF aaf = new SmartAAF(permutation.get(i), displayedTree, 0);
						for (int k = 1;; k++) {
							if (((Vector<AgreementForest>) aaf.getAllAgreementForests()).size() > 0) {
								break;
							}
							aaf = new SmartAAF(permutation.get(i), displayedTree, k);
						}
						for (AgreementForest af : aaf.getAllAgreementForests()) {
							HashSet<Taxon> added = new HashSet<Taxon>();
							HashSet<Taxon> allTaxa = new HashSet<Taxon>();
							added.addAll(af.getTaxaOfTree(0));
							allTaxa.addAll(added);
							insertEdges(hn, permutation.get(i), 0, af, added, allTaxa, reticulationNumber,
									networks.lastElement());
						}
					}
				}
			}
			if (networks.lastElement().size() > 0) {
				for (HybridizationNetwork hn : networks.lastElement()) {
					int displayedNumber = 0;
					hn.countDisplayedTrees();
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

	private void insertEdges(HybridizationNetwork hn, PhyloTree secondTree, int edgesInserted, AgreementForest af,
			HashSet<Taxon> added, HashSet<Taxon> allTaxa, int reticulationNumber, Vector<HybridizationNetwork> ans) {
		if (hn.getReticulationNumber() > reticulationNumber) {
			return;
		}
		if (edgesInserted == af.getNumberOfTrees() - 1) {
			hn.compress();
			boolean used = false;
			IsomorphismChecker checker = new IsomorphismChecker();
			for (HybridizationNetwork old : ans) {
				if (checker.areNetworksIsomorphic(old, hn)) {
					used = true;
					break;
				}
			}
			if (!used) {
				ans.add(hn);
			}
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
					insertEdges(newHN, secondTree, edgesInserted + 1, af, added, allTaxa, reticulationNumber, ans);
				}
			}
		}
		added.removeAll(newTaxa);
		allTaxa.removeAll(newTaxa);
	}

	private HashSet<Node> getTargetNodes(HybridizationNetwork hn, PhyloTree secondTree, HashSet<Taxon> added,
			HashSet<Taxon> newTaxa, HashSet<Taxon> allTaxa) {
		HashSet<Node> target = new HashSet<Node>();
		Vector<Node> nodes = new Vector<Node>();
		IsomorphismChecker checker = new IsomorphismChecker();
		TaxaEmbeddingDFS dfs = new TaxaEmbeddingDFS(newTaxa, secondTree);
		PhyloTree embeddedTree = dfs.getAnswer();
		checker.countSubtreeTaxa(embeddedTree);
		nodes.add(embeddedTree.getRoot());
		for (PhyloTree tree : hn.getDisplayedTrees()) {
			dfs = new TaxaEmbeddingDFS(allTaxa, tree);
			PhyloTree tree2 = dfs.getAnswer();
			checker.countSubtreeTaxa(tree2);
			for (Node n : tree2.getNodes()) {
				boolean flag = false;
				for (Node root : nodes) {
					if (checker.areSubtreesIsomorphic(n, root)) {
						flag = true;
					}
				}
				if (flag) {
					Node start = dfs.getOldNode(n);
					while (start.getOutDeg() == 1
							&& start.getOutEdges().iterator().next().getFinish().getOutDeg() == 1) {
						start = start.getOutEdges().iterator().next().getFinish();
					}
					Vector<Node> candidates = new Vector<Node>();
					candidates.add(start);
					while (candidates.lastElement().getInDeg() == 1
							&& candidates.lastElement().getParent().getOutDeg() == 1
							&& candidates.lastElement().getParent().getInDeg() == 1) {
						candidates.add(candidates.lastElement().getParent());
					}
					for (Node candidate : candidates) {
						if (!hn.getOldNode(candidate).hasEdgeToReticulation()) {
							target.add(hn.getOldNode(candidate));
						}
					}
				}
			}
		}
		return target;
	}

	private HashSet<Node> getSourceNodes(HybridizationNetwork hn, PhyloTree secondTree, HashSet<Taxon> added,
			HashSet<Taxon> newTaxa, HashSet<Taxon> allTaxa, AgreementForest af, int currentComponent) {
		HashSet<Node> source = new HashSet<Node>();
		IsomorphismChecker checker = new IsomorphismChecker();
		Vector<Node> siblings = new Vector<Node>();
		TaxaEmbeddingDFS dfs = new TaxaEmbeddingDFS(allTaxa, secondTree);
		PhyloTree embeddedTree = dfs.getAnswer();
		checker.countSubtreeTaxa(embeddedTree);
		for (Node n : embeddedTree.getNodes()) {
			if (checker.getSubtreeTaxa(n).equals(newTaxa)) {
				siblings.add(n.getSibling());
				break;
			}
		}
		for (Node vSib : siblings) {
			for (PhyloTree tree : hn.getDisplayedTrees()) {
				dfs = new TaxaEmbeddingDFS(added, tree);
				PhyloTree tree2 = dfs.getAnswer();
				checker.countSubtreeTaxa(tree2);
				checker.countSubtreeTaxa(tree);
				for (Node n : tree2.getNodes()) {
					if (checker.areSubtreesIsomorphic(n, vSib)) {
						Vector<Node> candidates = new Vector<Node>();
						Node start = dfs.getOldNode(n);
						while (start.getOutDeg() == 1
								&& start.getOutEdges().iterator().next().getFinish().getOutDeg() == 1) {
							start = start.getOutEdges().iterator().next().getFinish();
						}
						candidates.add(start);
						while (candidates.lastElement().getInDeg() == 1
								&& candidates.lastElement().getParent().getOutDeg() == 1
								&& candidates.lastElement().getParent().getInDeg() == 1) {
							candidates.add(candidates.lastElement().getParent());
						}
						for (Node candidate : candidates) {
							if (hn.getOldNode(candidate).getInDeg() == 1) {
								source.add(hn.getOldNode(candidate));
								try {
									Node candidate2 = candidate.getSibling();
									for (Node nInSubTree : tree.getNodes()) {
										if (candidate2.isAncestorOf(nInSubTree)) {
											for (int i = currentComponent; i < af.getNumberOfTrees(); i++) {
												if (checker.getSubtreeTaxa(nInSubTree).equals(af.getTaxaOfTree(i))) {
													Node candidate3 = hn.getOldNode(nInSubTree);
													if (candidate3.getInDeg() == 1) {
														source.add(candidate3);
													}
												}
											}
										}
									}
								} catch (IllegalArgumentException exc) {
								}
							}
						}
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
		Node new4 = new Node(hn);
		hn.addNode(new4);
		hn.addEdge(new1, new4);
		hn.addEdge(new4, new2);
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
