package util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Vector;

public class IsomorphismChecker {

	private HashMap<Node, HashSet<Taxon>> subtreeTaxa = new HashMap<Node, HashSet<Taxon>>();
	private HashMap<Vector<Node>, Boolean> memorization = new HashMap<Vector<Node>, Boolean>();

	public void countSubtreeTaxa(PhyloTree tree) {
		SubtreeTaxaDFS dfs = new SubtreeTaxaDFS();
		dfs.dfs(tree.getRoot());
		subtreeTaxa.putAll(dfs.getMap());
	}

	public void countSubtreeTaxa(HybridizationNetwork hn) {
		SubtreeTaxaDFS dfs = new SubtreeTaxaDFS();
		dfs.dfs(hn.getRoot());
		subtreeTaxa.putAll(dfs.getMap());
	}

	public boolean areBinaryTreesIsomorphic(PhyloTree tree1, PhyloTree tree2) {
		countSubtreeTaxa(tree1);
		countSubtreeTaxa(tree2);
		return areSubtreesIsomorphic(tree1.getRoot(), tree2.getRoot());
	}

	public boolean areSubtreesIsomorphic(Node root1, Node root2) {
		Vector<Node> vec = new Vector<Node>();
		vec.add(root1);
		vec.add(root2);
		if (memorization.containsKey(vec)) {
			return memorization.get(vec);
		}
		memorization.put(vec, false);
		if (root1 == null && root2 == null) {
			memorization.put(vec, true);
			return true;
		}
		if (root1 == null || root2 == null) {
			return false;
		}
		if (!haveSameTaxa(root1, root2)) {
			return false;
		}
		if (root1.isLeaf() && root2.isLeaf()) {
			memorization.put(vec, root1.getTaxon().equals(root2.getTaxon()));
			return (root1.getTaxon().equals(root2.getTaxon()));
		}
		if (root1.getOutDeg() != root2.getOutDeg()) {
			return false;
		}
		if (root1.getOutDeg() == 1) {
			Boolean ans = areSubtreesIsomorphic(root1.getOutEdges().iterator().next().getFinish(),
					root2.getOutEdges().iterator().next().getFinish());
			memorization.put(vec, ans);
			return ans;
		}
		Vector<Node> children1 = new Vector<Node>();
		Vector<Node> children2 = new Vector<Node>();
		for (Edge e : root1.getOutEdges()) {
			children1.add(e.getFinish());
		}
		for (Edge e : root2.getOutEdges()) {
			children2.add(e.getFinish());
		}
		for (int i = 0; i < 2; i++) {
			Node v = children1.get(i);
			Node u = children2.get(0);
			Node vv = children1.get(1 - i);
			Node uu = children2.get(1);
			if (haveSameTaxa(v, u)) {
				Boolean ans = areSubtreesIsomorphic(v, u) && areSubtreesIsomorphic(vv, uu);
				memorization.put(vec, ans);
				return ans;
			}
		}
		return false;
	}

	public HashSet<Taxon> getSubtreeTaxa(Node v) {
		return subtreeTaxa.get(v);
	}

	public boolean haveSameTaxa(Node v, Node u) {
		HashSet<Taxon> taxa1 = subtreeTaxa.get(v);
		HashSet<Taxon> taxa2 = subtreeTaxa.get(u);
		return taxa1.equals(taxa2);
	}

	public boolean areNetworksIsomorphic(HybridizationNetwork hn1, HybridizationNetwork hn2) {
		countSubtreeTaxa(hn1);
		countSubtreeTaxa(hn2);
		return areSubtreesIsomorphic(hn1.getRoot(), hn2.getRoot());
	}

}
