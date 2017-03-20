package phylonet_wu;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Vector;

import util.HybridizationNetwork;
import util.IsomorphismChecker;
import util.Node;
import util.PhyloTree;
import util.Taxon;

public class ConfigurationSearch {

	private HashSet<Configuration> allConfigurations = new HashSet<Configuration>();
	private HashSet<HybridizationNetwork> networks = new HashSet<HybridizationNetwork>();
	private HashSet<Configuration> terminal = new HashSet<Configuration>();
	private HashMap<Lineage, Node> map = new HashMap<Lineage, Node>();
	private int reticulationNumber = -1;

	public ConfigurationSearch(Vector<PhyloTree> input) {
		HashSet<Configuration> start = new HashSet<Configuration>();
		Configuration startConfiguration = new Configuration(input);
		startConfiguration.doCoalescences(start);
		allConfigurations.addAll(start);
		for (int i = 0;; i++) {
			reticulationNumber = i;
			for (Configuration conf : allConfigurations) {
				if (conf.isTerminal()) {
					terminal.add(conf);
				}
			}
			if (terminal.size() > 0) {
				break;
			}
			System.out.println("SEARCHING NETWORKS WITH RETICULATION NUMBER " + (i + 1) + "...");
			HashSet<Configuration> nextLevel = new HashSet<Configuration>();
			for (Configuration conf : allConfigurations) {
				HashSet<Configuration> retConfigurations = new HashSet<Configuration>();
				conf.doReticulations(retConfigurations);
				for (Configuration retConf : retConfigurations) {
					retConf.doCoalescences(nextLevel);
				}
			}
			allConfigurations = nextLevel;
		}
	}

	public void buildHybridizationNetworks() {
		for (Configuration term : terminal) {
			HybridizationNetwork hn = new HybridizationNetwork();
			Node root = new Node(hn);
			hn.addRoot(root);
			map = new HashMap<Lineage, Node>();
			term.matchLineagesWithRoot(map, root);
			recoverAnswer(term, hn);
			hn.compress();
			boolean used = false;
			IsomorphismChecker checker = new IsomorphismChecker();
			for (HybridizationNetwork old : networks) {
				if (checker.areNetworksIsomorphic(old, hn)) {
					used = true;
					break;
				}
			}
			if (!used) {
				networks.add(hn);
			}
		}
	}

	private void recoverAnswer(Configuration conf, HybridizationNetwork hn) {
		if (conf.getLastEvent() instanceof ReticulationEvent) {
			ReticulationEvent event = (ReticulationEvent) conf.getLastEvent();
			Node n = new Node(hn);
			hn.addNode(n);
			Node ret = new Node(hn);
			hn.addNode(ret);
			try {
				Taxon t = event.getLineageSource().getTaxon();
				n.setTaxon(t);
			} catch (IllegalArgumentException exc) {
			}
			;
			hn.addEdge(map.get(event.getLineageTarget1()), ret);
			hn.addEdge(map.get(event.getLineageTarget2()), ret);
			hn.addEdge(ret, n);
			map.put(event.getLineageSource(), n);
			recoverAnswer(event.configurationSource, hn);
		} else if (conf.getLastEvent() instanceof CoalescenceEvent) {
			CoalescenceEvent event = (CoalescenceEvent) conf.getLastEvent();
			Node n1 = new Node(hn);
			Node n2 = new Node(hn);
			try {
				Taxon t = event.getLineageSource1().getTaxon();
				n1.setTaxon(t);
			} catch (IllegalArgumentException exc) {
			}
			;
			try {
				Taxon t = event.getLineageSource2().getTaxon();
				n2.setTaxon(t);
			} catch (IllegalArgumentException exc) {
			}
			;
			hn.addNode(n1);
			hn.addNode(n2);
			hn.addEdge(map.get(event.getLineageTarget()), n1);
			hn.addEdge(map.get(event.getLineageTarget()), n2);
			if (map.get(event.getLineageSource1()) == null) {
				map.put(event.getLineageSource1(), n1);
			}
			if (map.get(event.getLineageSource2()) == null) {
				map.put(event.getLineageSource2(), n2);
			}
			recoverAnswer(event.configurationSource, hn);
		}
	}

	public Iterable<HybridizationNetwork> getNetworks() {
		return networks;
	}

	public int getReticulationNumber() {
		return reticulationNumber;
	}

	public int getNetworkNumber() {
		return networks.size();
	}

}
