package phylonet;

import java.util.HashSet;
import java.util.Vector;

import util.PhyloTree;
import util.Taxon;

public class NaiveAAF {

	private Vector<AgreementForest> allAgreementForests = new Vector<AgreementForest>();
	private PhyloTree tree1, tree2;
	private int[] map;

	public NaiveAAF(PhyloTree tree1, PhyloTree tree2, Vector<Taxon> taxa, int reticulationNumber) {
		this.tree1 = tree1;
		this.tree2 = tree2;
		map = new int[taxa.size()];
		countAllPartitions(reticulationNumber + 1, 0, taxa);
	}

	private void countAllPartitions(int parts, int pos, Vector<Taxon> taxa) {
		if (pos == taxa.size()) {
			Vector<HashSet<Taxon>> vec = new Vector<HashSet<Taxon>>();
			for (int i = 0; i < parts; i++) {
				vec.add(new HashSet<Taxon>());
			}
			HashSet<Integer> set = new HashSet<Integer>();
			for (int i = 0; i < pos; i++) {
				vec.get(map[i]).add(taxa.get(i));
				set.add(map[i]);
			}
			if (set.size() == parts) {
				try {
					allAgreementForests.add(new AgreementForest(tree1, tree2, vec));
				} catch (IllegalArgumentException exc) {};
			}
			return;
		}
		for (int i = 0; i < parts; i++) {
			map[pos] = i;
			countAllPartitions(parts, pos + 1, taxa);
		}
	}

	public Iterable<AgreementForest> getAllAgreementForests() {
		return allAgreementForests;
	}
	
}
