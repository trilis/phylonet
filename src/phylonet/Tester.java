package phylonet;

import java.util.Scanner;
import java.util.Vector;

import util.HybridizationNetwork;
import util.Newick;
import util.PhyloTree;
import util.Taxon;

public class Tester {

	public static void main(String[] args) {
		Scanner in = new Scanner(System.in);
		int n = in.nextInt();
		Newick newick = new Newick();
		Vector<PhyloTree> input = new Vector<PhyloTree>();
		for (int i = 0; i < n; i++) {
			input.add(newick.newickToPhyloTree(in.next()));
		}
		long t0 = System.currentTimeMillis();
		Vector<Taxon> taxa = new Vector<Taxon>();
		for (Taxon t : newick.getTaxa()) {
			taxa.add(t);
		}
		for (int i = 0;; i++) {
			System.out.println("SEARCHING NETWORKS WITH RETICULATION NUMBER " + i + "...");
			ExhaustiveSearch search = new ExhaustiveSearch(input, i);
			if (search.hasNetworks()) {
				System.out.println("FOUND " + search.getNetworkNumber() + " NETWORK"
						+ (search.getNetworkNumber() == 1 ? "" : "S") + " WITH RETICULATION NUMBER " + i);
				for (HybridizationNetwork network : search.getAllHNetworks()) {
					System.out.println(newick.hybridizationNetworkToNewick(network));
				}
				break;
			}
		}
		System.out.println("WORKED FOR " + (double) (System.currentTimeMillis() - t0) / 1000 + " SECONDS");
		in.close();
	}

}
