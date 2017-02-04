package phylonet;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;

public class AllAgreementForests {
	
	Vector<Vector<HashSet<Taxon>>> allPartitions = new Vector<Vector<HashSet<Taxon>>>();
	Vector<AgreementForest> allAgreementForests = new Vector<AgreementForest>();
	
	public AllAgreementForests(PhyloTree tree1, PhyloTree tree2, HashSet<Taxon> taxa) {
		countAllPartitions(taxa, new Vector<HashSet<Taxon>>());
		for (Vector<HashSet<Taxon>> partition : allPartitions) {
			try {
				AgreementForest af = new AgreementForest(tree1, tree2, partition);
				allAgreementForests.addAll(getAllOrderings(af));
			} catch (IllegalArgumentException exc) {};
		}
	}

	public Vector<AgreementForest> getAllOrderings(AgreementForest af) {
		Vector<AgreementForest> answer = new Vector<AgreementForest>();
		Vector<Node> nodes = af.getNodesWithNoIncoming();
		Iterator<Node> itr = nodes.iterator();
		while (itr.hasNext()) {
			Node n = itr.next();
			af.delComponent(n);
			if (itr.hasNext()) {
				AgreementForest nwaf = new AgreementForest(af);
				af.addComponent(n);
				answer.addAll(getAllOrderings(nwaf));
			} else {
				answer.addAll(getAllOrderings(af));
				return answer;
			}
		}
		answer.add(af);
		return answer;
	}
	
	public void countAllPartitions(HashSet<Taxon> taxa, Vector<HashSet<Taxon>> answer) {
		if (taxa.size() == 0) {
			allPartitions.add(answer);
			return;
		}
		HashSet<Taxon> taxa2 = (HashSet<Taxon>)taxa.clone();
		HashSet<Taxon> nw = new HashSet<Taxon>();
		for (Taxon t : taxa) {
			Vector<HashSet<Taxon>> nwans = (Vector<HashSet<Taxon>>)answer.clone();
			taxa2.remove(t);
			nw.add(t);
			nwans.add(nw);
			countAllPartitions(taxa2, nwans);
		}
	}
	
}
