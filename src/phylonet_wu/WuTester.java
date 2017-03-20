package phylonet_wu;

import java.util.Scanner;
import java.util.Vector;

import util.HybridizationNetwork;
import util.Newick;
import util.PhyloTree;
import util.Taxon;

public class WuTester {

	public static void main(String[] args) {
		Scanner in = new Scanner(System.in);
		int n = in.nextInt();
		Newick newick = new Newick();
		Taxon rho = new Taxon("#");
		Vector<PhyloTree> input = new Vector<PhyloTree>();
		for (int i = 0; i < n; i++) {
			input.add(newick.newickToPhyloTree(in.next()));
			input.lastElement().addFakeTaxon(rho);
		}
		long t0 = System.currentTimeMillis();
		Vector<Taxon> taxa = new Vector<Taxon>();
		for (Taxon t : newick.getTaxa()) {
			taxa.add(t);
		}
		taxa.add(rho);
		ConfigurationSearch search = new ConfigurationSearch(input);
		search.buildHybridizationNetworks();
		System.out
				.println("FOUND " + search.getNetworkNumber() + " NETWORK" + (search.getNetworkNumber() == 1 ? "" : "S")
						+ " WITH RETICULATION NUMBER " + search.getReticulationNumber());
		for (HybridizationNetwork network : search.getNetworks()) {
			network.killFakeTaxon(rho);
			System.out.println(newick.hybridizationNetworkToNewick(network));
		}
		System.out.println("WORKED FOR " + (double) (System.currentTimeMillis() - t0) / 1000 + " SECONDS");
		in.close();
	}

}
