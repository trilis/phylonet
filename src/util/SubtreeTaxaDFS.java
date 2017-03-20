package util;

import java.util.HashMap;
import java.util.HashSet;

public class SubtreeTaxaDFS extends DFS {

	private HashMap<Node, HashSet<Taxon>> map = new HashMap<Node, HashSet<Taxon>>();

	@Override
	public void enter(Node v) {
	}

	@Override
	public void exit(Node v) {
		HashSet<Taxon> taxa = new HashSet<Taxon>();
		if (v.isLeaf() && v.getTaxon() != null) {
			taxa.add(v.getTaxon());
		} else {
			for (Edge e : v.getOutEdges()) {
				Node u = e.getFinish();
				taxa.addAll(map.get(u));
			}
		}
		map.put(v, taxa);
	}

	public HashMap<Node, HashSet<Taxon>> getMap() {
		return map;
	}
}
