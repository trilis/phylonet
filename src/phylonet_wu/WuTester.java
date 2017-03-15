package phylonet_wu;

import java.util.Scanner;
import java.util.Vector;

import util.HybridizationNetwork;
import util.Newick;
import util.Node;
import util.PhyloTree;

public class WuTester {

	public static void main(String[] args) {
		Scanner in = new Scanner(System.in);
		Newick newick = new Newick();
		int n = in.nextInt();
		Vector<PhyloTree> input = new Vector<PhyloTree>();
		for (int i = 0; i < n; i++) {
			input.add(newick.newickToPhyloTree(in.next()));
		}
		long t0 = System.currentTimeMillis();
		ConfigurationSearch search = new ConfigurationSearch(input);
		search.buildHybridizationNetworks();
		System.out
				.println("FOUND " + search.getNetworkNumber() + " NETWORK" + (search.getNetworkNumber() == 1 ? "" : "S")
						+ " WITH RETICULATION NUMBER " + search.getReticulationNumber());
		for (HybridizationNetwork hn : search.getNetworks()) {
			System.out.println(newick.hybridizationNetworkToNewick(hn));
		}
		System.out.println("WORKED FOR " + (double) (System.currentTimeMillis() - t0) / 1000 + " SECONDS");
		in.close();
	}
}
