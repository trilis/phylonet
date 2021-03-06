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

	private HashSet<AgreementForest> allAgreementForests = new HashSet<AgreementForest>();
	private Vector<HybridizationNetwork> allHNetworks = new Vector<HybridizationNetwork>();

	public ExhaustiveSearch(Vector<PhyloTree> input, int reticulationNumber) {
		SmartAAF aaf = new SmartAAF(input.get(0), input.get(1), reticulationNumber);
		HashMap<AgreementForest, Vector<HashSet<Taxon>>> map = new HashMap<AgreementForest, Vector<HashSet<Taxon>>>();
		HashSet<Vector<HashSet<Taxon>>> taxas1 = new HashSet<Vector<HashSet<Taxon>>>();
		for (AgreementForest af : aaf.getAllAgreementForests()) {
			Vector<HashSet<Taxon>> taxa = new Vector<HashSet<Taxon>>();
			for (int k = 0; k < af.getNumberOfTrees(); k++) {
				taxa.add(af.getTaxaOfTree(k));
			}
			if (!taxas1.contains(taxa)) {
				map.put(af, taxa);
				taxas1.add(taxa);
				allAgreementForests.add(af);
			}
		}
		for (int i = 0; i < input.size(); i++) {
			for (int j = i + 1; j < input.size(); j++) {
				if (i == 0 && j == 1) {
					continue;
				}
				SmartAAF aaf2 = new SmartAAF(input.get(i), input.get(j), reticulationNumber);
				Vector<AgreementForest> toRemove = new Vector<AgreementForest>();
				HashSet<Vector<HashSet<Taxon>>> taxas = new HashSet<Vector<HashSet<Taxon>>>();
				for (AgreementForest af2 : aaf2.getAllAgreementForests()) {
					Vector<HashSet<Taxon>> taxa2 = new Vector<HashSet<Taxon>>();
					for (int k = 0; k < af2.getNumberOfTrees(); k++) {
						taxa2.add(af2.getTaxaOfTree(k));
					}
					taxas.add(taxa2);
				}
				for (AgreementForest af : allAgreementForests) {
					if (!taxas.contains(map.get(af))) {
						toRemove.add(af);
					}
				}
				for (AgreementForest af : toRemove) {
					allAgreementForests.remove(af);
				}
			}
		}
		PhyloTree tree1 = input.get(0);
		HashSet<Taxon> taxaset = new HashSet<Taxon>();
		taxaset.addAll(tree1.getAllTaxa());
		Vector<Taxon> taxa = new Vector<Taxon>();
		taxa.addAll(taxaset);
		countallHNetworks(tree1, input, taxa, reticulationNumber);
	}

	private void countallHNetworks(PhyloTree tree1, Vector<PhyloTree> input, Vector<Taxon> taxa,
			int reticulationNumber) {
		for (AgreementForest af : allAgreementForests) {
			HybridizationNetwork hn = new HybridizationNetwork(tree1);
			HashSet<Taxon> added = new HashSet<Taxon>();
			HashSet<Taxon> allTaxa = new HashSet<Taxon>();
			added.addAll(af.getTaxaOfTree(0));
			allTaxa.addAll(added);
			insertEdges(hn, tree1, 0, af, added, allTaxa, input);
		}
	}

	private void insertEdges(HybridizationNetwork hn, PhyloTree firstTree, int edgesInserted, AgreementForest af,
			HashSet<Taxon> added, HashSet<Taxon> allTaxa, Vector<PhyloTree> input) {
		if (edgesInserted == af.getNumberOfTrees() - 1) {
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
			return;
		}
		hn.countDisplayedTrees();
		HashSet<Taxon> newTaxa = af.getTaxaOfTree(edgesInserted + 1);
		allTaxa.addAll(newTaxa);
		HashSet<Node> targets = getTargetNodes(hn, firstTree, added, newTaxa, allTaxa, input);
		HashSet<Node> sources = getSourceNodes(hn, firstTree, added, newTaxa, allTaxa, af, edgesInserted + 1, input);
		added.addAll(newTaxa);
		for (Node target : targets) {
			for (Node source : sources) {
				if (!target.isAncestorOf(source)) {
					HybridizationNetwork newHN = copyNetworkWithNewEdge(hn, source, target);
					insertEdges(newHN, firstTree, edgesInserted + 1, af, added, allTaxa, input);
				}
			}
		}
		added.removeAll(newTaxa);
		allTaxa.removeAll(newTaxa);
	}

	private HashSet<Node> getTargetNodes(HybridizationNetwork hn, PhyloTree firstTree, HashSet<Taxon> added,
			HashSet<Taxon> newTaxa, HashSet<Taxon> allTaxa, Vector<PhyloTree> input) {
		HashSet<Node> target = new HashSet<Node>();
		Vector<Node> nodes = new Vector<Node>();
		IsomorphismChecker checker = new IsomorphismChecker();
		for (PhyloTree tree : input) {
			if (tree != firstTree) {
				TaxaEmbeddingDFS dfs = new TaxaEmbeddingDFS(newTaxa, tree);
				PhyloTree embeddedTree = dfs.getAnswer();
				checker.countSubtreeTaxa(embeddedTree);
				nodes.add(embeddedTree.getRoot());
			}
		}
		for (PhyloTree tree : hn.getDisplayedTrees()) {
			TaxaEmbeddingDFS dfs = new TaxaEmbeddingDFS(allTaxa, tree);
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
						if (!candidate.hasEdgeToReticulation()) {
							target.add(hn.getOldNode(candidate));
						}
					}
				}
			}
		}
		return target;
	}

	private HashSet<Node> getSourceNodes(HybridizationNetwork hn, PhyloTree firstTree, HashSet<Taxon> added,
			HashSet<Taxon> newTaxa, HashSet<Taxon> allTaxa, AgreementForest af, int currentComponent,
			Vector<PhyloTree> input) {
		HashSet<Node> source = new HashSet<Node>();
		IsomorphismChecker checker = new IsomorphismChecker();
		Vector<Node> siblings = new Vector<Node>();
		for (PhyloTree tree : input) {
			if (tree == firstTree) {
				continue;
			}
			TaxaEmbeddingDFS dfs = new TaxaEmbeddingDFS(allTaxa, tree);
			PhyloTree embeddedTree = dfs.getAnswer();
			checker.countSubtreeTaxa(embeddedTree);
			for (Node n : embeddedTree.getNodes()) {
				if (checker.getSubtreeTaxa(n).equals(newTaxa)) {
					siblings.add(n.getSibling());
					break;
				}
			}
		}
		for (Node vSib : siblings) {
			for (PhyloTree tree : hn.getDisplayedTrees()) {
				TaxaEmbeddingDFS dfs = new TaxaEmbeddingDFS(added, tree);
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
