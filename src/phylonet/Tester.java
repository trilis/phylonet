package phylonet;

import java.util.Scanner;
import java.util.Vector;

import util.Newick;
import util.PhyloTree;
import util.Taxon;

public class Tester {

	public static void main(String[] args) {
		Scanner in = new Scanner(System.in);
		Newick newick = new Newick();
		PhyloTree tree1 = newick.newickToPhyloTree(in.next());
		PhyloTree tree2 = newick.newickToPhyloTree(in.next());
		Vector<Taxon> taxa = new Vector<Taxon>();
		for (Taxon t : newick.getTaxa()) {
			taxa.add(t);
		}
		AllAgreementForests aaf = new AllAgreementForests(tree1, tree2, taxa);
		System.out.print(newick.aafToNewick(aaf));
		in.close();
	}

}
