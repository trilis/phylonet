package util;

import java.util.HashSet;

import phylonet.AgreementForest;
import phylonet.AllAgreementForests;

public class Newick {
	
	private HashSet<Taxon> taxa = new HashSet<Taxon>();
	
	public String phyloTreeToNewick(PhyloTree tree) {
		if (tree.getRoot().isLeaf()) {
			return tree.getRoot().getTaxon().name + ";";
		}
		return nodeToNewick(tree.getRoot()) + ";";
	}
	
	public String nodeToNewick(Node root) {
		if (root.isLeaf()) {
			return root.getTaxon().name;
		}
		String res = "(";
		for (Edge e : root.getOutEdges()) {
			if (res.length() > 1) {
				res += ',';
			}
			res += nodeToNewick(e.getFinish());
		}
		return res + ")";
	}
	
	public String aafToNewick(AllAgreementForests aaf) {
		String res = "";
		int num = 1;
		for (AgreementForest af : aaf.getAllAgreementForests()) {
			res += "Agreement Forest #" + num + " with " + af.getNumberOfTrees() + " trees\n" + afToNewick(af);
			num++;
		}
		return res;
	}
	
	public String afToNewick(AgreementForest af) {
		String res = "";
		for (PhyloTree tree : af.getOrdering()) {
			res += phyloTreeToNewick(tree) + "\n";
		}
		return res;
	}
	
	public PhyloTree newickToPhyloTree(String s) {
		PhyloTree tree = new PhyloTree();
		tree.setRoot(newickToNode(s.substring(0, s.length() - 1), tree));
		return tree;
	}
	
	public Node newickToNode(String s, PhyloTree tree) {
		if (s.charAt(0) == '(') {
			Node n = new Node(tree);
			tree.addNode(n);
			int pos = 1;
			int prev = 1;
			int bal = 0;
			while (pos != s.length() - 1) {
				while (pos != s.length() - 1 && (bal != 0 || s.charAt(pos) != ',')) {
					if (s.charAt(pos) == '(') {
						bal++;
					}
					if (s.charAt(pos) == ')') {
						bal--;
					}
					pos++;
				}
				Node nx = newickToNode(s.substring(prev, pos), tree);
				tree.addEdge(n, nx);
				prev = pos;
				if (s.charAt(prev) == ',') {
					prev++;
					pos++;
				}
			}
			return n;
		} else {
			taxa.add(new Taxon(s));
			Node n = new Node(tree, new Taxon(s));
			tree.addNode(n);
			return n;
		}
	}
	
	public HashSet<Taxon> getTaxa() {
		return taxa;
	}
}
