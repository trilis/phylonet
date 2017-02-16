package phylonet;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Vector;

import util.Node;
import util.PhyloTree;
import util.Taxon;

public class AllAgreementForests {

	private Vector<Vector<Vector<HashSet<Taxon>>>> allPartitions = new Vector<Vector<Vector<HashSet<Taxon>>>>();
	private Vector<Vector<AgreementForest>> allAgreementForests = new Vector<Vector<AgreementForest>>();

	public AllAgreementForests(PhyloTree tree1, PhyloTree tree2, Vector<Taxon> taxa) {
		for (int i = 0; i < taxa.size(); i++) {
			allAgreementForests.add(new Vector<AgreementForest>());
			Vector<Vector<HashSet<Taxon>>> answer = new Vector<Vector<HashSet<Taxon>>>();
			countAllPartitions(i + 1, 0, new HashMap<Integer, Integer>(), taxa, answer);
			allPartitions.add(answer);
			for (Vector<HashSet<Taxon>> partition : answer) {
				try {
					AgreementForest af = new AgreementForest(tree1, tree2, partition);
					countAllOrderings(af, allAgreementForests.lastElement());
				} catch (IllegalArgumentException exc) {};
			}
			if (allAgreementForests.lastElement().size() != 0) {
				return;
			}
		}
	}

	public void countAllOrderings(AgreementForest af, Vector<AgreementForest> answer) {
		Vector<Node> nodes = af.getNodesWithNoIncoming();
		for (Node n : nodes) {
			AgreementForest nwaf = af.copyWithoutNode(n);
			countAllOrderings(nwaf, answer);
		}
		if (af.isOrderingFull()) {
			answer.add(af);
		}
	}

	public void countAllPartitions(int parts, int pos, HashMap<Integer, Integer> map, Vector<Taxon> taxa,
			Vector<Vector<HashSet<Taxon>>> answer) {
		if (pos == taxa.size()) {
			Vector<HashSet<Taxon>> vec = new Vector<HashSet<Taxon>>();
			for (int i = 0; i < parts; i++) {
				vec.add(new HashSet<Taxon>());
			}
			HashSet<Integer> set = new HashSet<Integer>();
			for (int i = 0; i < pos; i++) {
				vec.get(map.get(i)).add(taxa.get(i));
				set.add(map.get(i));
			}
			if (set.size() == parts) {
				answer.addElement(vec);
			}
			return;
		}
		for (int i = 0; i < parts; i++) {
			map.put(pos, i);
			countAllPartitions(parts, pos + 1, map, taxa, answer);
		}
	}

	public Iterable<AgreementForest> getAllAgreementForests() {
		return allAgreementForests.lastElement();
	}

	
}
