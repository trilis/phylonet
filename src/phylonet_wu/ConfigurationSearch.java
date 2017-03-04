package phylonet_wu;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Vector;

import util.HybridizationNetwork;
import util.Node;
import util.PhyloTree;
import util.Taxon;

public class ConfigurationSearch {

	Vector<HashSet<Configuration>> allConfigurations = new Vector<HashSet<Configuration>>();
	HashSet<HybridizationNetwork> networks = new HashSet<HybridizationNetwork>();
	HashSet<Configuration> terminal = new HashSet<Configuration>();
	HashMap<Lineage, Node> map = new HashMap<Lineage, Node>();

	public ConfigurationSearch(Vector<PhyloTree> input) {
		HashSet<Configuration> start = new HashSet<Configuration>();
		Configuration startConfiguration = new Configuration(input);
		startConfiguration.doCoalescences(start);
		allConfigurations.add(start);
		for (int i = 0; i < 10; i++) {
			System.out.println(i + " " + allConfigurations.lastElement().size());
			for (Configuration conf : allConfigurations.lastElement()) {
				if (conf.isTerminal()) {
					terminal.add(conf);
				}
			}
			if (terminal.size() > 0) {
				break;
			}
			HashSet<Configuration> nextLevel = new HashSet<Configuration>();
			for (Configuration conf : allConfigurations.lastElement()) {
				HashSet<Configuration> retConfigurations = new HashSet<Configuration>();
				conf.doReticulations(retConfigurations);
				for (Configuration retConf : retConfigurations) {
					retConf.doCoalescences(nextLevel);
				}
			}
			allConfigurations.add(nextLevel);
		}
	}

	public void buildHybridizationNetwork() {
		for (Configuration term : terminal) {
			HybridizationNetwork hn = new HybridizationNetwork();
			Node root = new Node(hn);
			hn.addRoot(root);
			map = new HashMap<Lineage, Node>();
			term.matchLineagesWithRoot(map, root);
			recoverAnswer(term, hn);
			networks.add(hn);
			return;
		}
	}
	
	public void recoverAnswer(Configuration conf, HybridizationNetwork hn) {
		if (conf.getLastEvent() instanceof ReticulationEvent) {
			ReticulationEvent event = (ReticulationEvent)conf.getLastEvent();
			Node n = new Node(hn);
			hn.addNode(n);
			map.put(event.getLineageSource(), n);
			try {
				Taxon t = event.getLineageSource().getTaxon();
				n.setTaxon(t);
			} catch (IllegalArgumentException exc) {};
			hn.addEdge(map.get(event.getLineageTarget1()), n);
			hn.addEdge(map.get(event.getLineageTarget2()), n);
			System.out.println("RET");
			recoverAnswer(event.configurationSource, hn);
		} else if (conf.getLastEvent() instanceof CoalescenceEvent) {
			CoalescenceEvent event = (CoalescenceEvent)conf.getLastEvent();
			Node n1 = new Node(hn);
			Node n2 = new Node(hn);
			try {
				Taxon t = event.getLineageSource1().getTaxon();
				n1.setTaxon(t);
			} catch (IllegalArgumentException exc) {};
			try {
				Taxon t = event.getLineageSource2().getTaxon();
				n2.setTaxon(t);
			} catch (IllegalArgumentException exc) {};
			hn.addNode(n1);
			hn.addNode(n2);
			hn.addEdge(map.get(event.getLineageTarget()), n1);
			hn.addEdge(map.get(event.getLineageTarget()), n2);
			map.put(event.getLineageSource1(), n1);
			map.put(event.getLineageSource2(), n2);
			System.out.println("COA");
			recoverAnswer(event.configurationSource, hn);
		}
	}

}
