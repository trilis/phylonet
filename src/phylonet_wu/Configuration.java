package phylonet_wu;

import java.util.HashSet;
import java.util.Vector;

import util.Node;
import util.PhyloTree;
import util.Taxon;

public class Configuration {
	private HashSet<Lineage> lineages = new HashSet<Lineage>();
	private Event lastEvent;
	private Vector<PhyloTree> input;
	
	public Configuration(Vector<PhyloTree> input) {
		this.input = input;
		HashSet<Taxon> taxa = new HashSet<Taxon>();
		for (PhyloTree tree : input) {
			taxa.addAll(tree.getAllTaxa());
		}
		for (Taxon t : taxa) {
			lineages.add(new Lineage(t));
		}
	}

	public Configuration(Configuration old) {
		this.input = old.input;
	}
	
	public Configuration configurationWithoutOldLineages(Event event) {
		Configuration newconf = new Configuration(event.configurationSource);
		HashSet<Lineage> forbidden = new HashSet<Lineage>();
		if (event instanceof ReticulationEvent) {
			forbidden.add(((ReticulationEvent)event).getLineageSource());
		} else if (event instanceof CoalescenceEvent){
			forbidden.addAll(((CoalescenceEvent)event).getLineageSources());
		}
		for (Lineage lin : lineages) {
			if (!forbidden.contains(lin)) {
				newconf.lineages.add(lin);
			}
		}
		event.configurationTarget = newconf;
		newconf.lastEvent = event;
		return newconf;
	}

	public Vector<Configuration> doReticulations() {
		Vector<Configuration> newConfigurations = new Vector<Configuration>();
		for (Lineage lin : lineages) {
			Vector<Lineage> newLineages = lin.reticulate();
			ReticulationEvent event = new ReticulationEvent(lin, newLineages.get(0), newLineages.get(1), this, null);
			Configuration conf = this.configurationWithoutOldLineages(event);
			for (Lineage newLin : newLineages) {
				newLin.addReticulationEvent(event);
			}
			conf.lineages.addAll(newLineages);
			newConfigurations.add(conf);
		}
		return newConfigurations;
	}
	
	public void doCoalescences(Vector<Configuration> ans) {
		for (Lineage lin : lineages) {
			for (Lineage lin2 : lineages) {
				if (lin != lin2 && !lin.sharesReticulation(lin2)) {
					Lineage newlin = lin.coalesce(lin2);
					Event event = new CoalescenceEvent(lin, lin2, newlin, this, null);
					Configuration conf = configurationWithoutOldLineages(event);
					conf.lineages.add(newlin);
					if (conf.isUseful()) {
						ans.add(conf);
						conf.doCoalescences(ans);
					}
				}
			}
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

}
