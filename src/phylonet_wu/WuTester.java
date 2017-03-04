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
		ConfigurationSearch search = new ConfigurationSearch(input);
		search.buildHybridizationNetwork();
		for (HybridizationNetwork hn : search.networks) {
			System.out.println(newick.hybridizationNetworkToNewick(hn));
			for (Node nd : hn.getNodes()) {
				System.out.println(nd.isReticulation());
			}
		}
		in.close();
	}
}
