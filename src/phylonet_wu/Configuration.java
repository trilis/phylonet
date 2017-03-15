package phylonet_wu;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Vector;

import util.Node;
import util.PhyloTree;
import util.Taxon;

public class Configuration {
	private LinkedHashSet<Lineage> lineages = new LinkedHashSet<Lineage>();
	private Event lastEvent;
	private Vector<PhyloTree> input;

	public Configuration(Vector<PhyloTree> input) {
		this.input = input;
		HashSet<Taxon> taxa = new HashSet<Taxon>();
		for (PhyloTree tree : input) {
			taxa.addAll(tree.getAllTaxa());
		}
		for (Taxon t : taxa) {
			lineages.add(new Lineage(t, input));
		}
	}

	public Configuration(Configuration old) {
		this.input = old.input;
	}

	public Configuration configurationWithoutOldLineages(Event event) {
		Configuration newconf = new Configuration(event.configurationSource);
		HashSet<Lineage> forbidden = new HashSet<Lineage>();
		if (event instanceof ReticulationEvent) {
			forbidden.add(((ReticulationEvent) event).getLineageSource());
		} else if (event instanceof CoalescenceEvent) {
			forbidden.addAll(((CoalescenceEvent) event).getLineageSources());
		}
		for (Lineage lin : lineages) {
			if (!forbidden.contains(lin)) {
				newconf.lineages.add(lin);
			}
		}
		newconf.lastEvent = event;
		return newconf;
	}

	public void doReticulations(HashSet<Configuration> ans) {
		for (Lineage lin : lineages) {
			Vector<Lineage> newLineages = lin.reticulate();
			ReticulationEvent event = new ReticulationEvent(lin, newLineages.get(0), newLineages.get(1), this);
			Configuration conf = this.configurationWithoutOldLineages(event);
			for (Lineage newLin : newLineages) {
				newLin.addReticulationEvent(event);
			}
			conf.lineages.addAll(newLineages);
			ans.add(conf);
		}
	}

	public void doCoalescences(HashSet<Configuration> ans) {
		boolean flag = false;
		for (Lineage lin : lineages) {
			for (Lineage lin2 : lineages) {
				if (lin == lin2) {
					break;
				}
				if (!lin.sharesReticulation(lin2)) {
					try {
						Lineage newlin = lin.coalesce(lin2);
						Event event = new CoalescenceEvent(lin, lin2, newlin, this);
						Configuration conf = configurationWithoutOldLineages(event);
						conf.lineages.add(newlin);
						if (conf.isUseful()) {
							flag = true;
							conf.doCoalescences(ans);
						}
					} catch (IllegalArgumentException exc) {};
				}
			}
		}
		if (!flag) {
			ans.add(this);
		}
	}

	public boolean containsTaxon(Taxon t) {
		for (Lineage lin : lineages) {
			if (lin.containsTaxon(t)) {
				return true;
			}
		}
		return false;
	}

	public boolean containsNode(Node n) {
		for (Lineage lin : lineages) {
			if (lin.containsNode(n)) {
				return true;
			}
		}
		return false;
	}

	public boolean isUseful() {
		CheckLineageDFS dfs = new CheckLineageDFS(this);
		for (PhyloTree tree : input) {
			dfs.dfs(tree.getRoot());
		}
		return dfs.isUseful();
	}

	public boolean isTerminal() {
		return isUseful() && lineages.size() == 1;
	}
	
	public Event getLastEvent() {
		return lastEvent;
	}
	
	public void matchLineagesWithRoot(HashMap<Lineage, Node> map, Node root) {
		for (Lineage lin : lineages) {
			map.put(lin, root);
		}
	}
	
	@Override
	public String toString() {
		String res = "Lineages:\n";
		for (Lineage lin : lineages) {
			res += lin.toString() + "\n";
		}
		return res;
	}

}
