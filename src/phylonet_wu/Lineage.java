package phylonet_wu;

import java.util.HashSet;
import java.util.Vector;

import util.Edge;
import util.Node;
import util.PhyloTree;
import util.Taxon;

public class Lineage {
	private HashSet<Node> nodes = new HashSet<Node>();
	private HashSet<Taxon> taxa = new HashSet<Taxon>();
	private boolean isVanishable = false;
	private boolean isInitial = false;
	private HashSet<ReticulationEvent> reticulationEvents = new HashSet<ReticulationEvent>();
	private Vector<PhyloTree> input;

	public Lineage(Lineage lin) {
		input = lin.input;
	}

	public Lineage(Taxon t, Vector<PhyloTree> input) {
		taxa.add(t);
		this.input = input;
		this.isInitial = true;
	}

	private void addNodesFromLineage(Lineage lin) {
		for (Node n : lin.nodes) {
			this.nodes.add(n);
		}
		for (Taxon t : lin.taxa) {
			this.taxa.add(t);
		}
	}

	public Vector<Lineage> reticulate() {
		Lineage newLin1 = new Lineage(this);
		newLin1.addNodesFromLineage(this);
		newLin1.isVanishable = true;
		Lineage newLin2 = new Lineage(this);
		newLin2.addNodesFromLineage(this);
		newLin2.isVanishable = true;
		Vector<Lineage> vec = new Vector<Lineage>();
		vec.add(newLin1);
		vec.add(newLin2);
		return vec;
	}

	private Node getParentForCoalescence(Node n, Lineage lin) {
		if (n.getInDeg() != 0) {
			Node parent = n.getParent();
			for (Edge e : parent.getOutEdges()) {
				Node u = e.getFinish();
				if (u != n && ((u.isLeaf() && lin.containsTaxon(u.getTaxon())) || !u.isLeaf() && lin.containsNode(u))) {
					return parent;
				}
			}
		}
		throw new IllegalArgumentException("Parent of n has no sons in lin");
	}

	public Lineage coalesce(Lineage lin) {
		Lineage newlin = new Lineage(lin);
		for (ReticulationEvent event : this.reticulationEvents) {
			newlin.addReticulationEvent(event);
		}
		for (ReticulationEvent event : lin.reticulationEvents) {
			newlin.addReticulationEvent(event);
		}
		for (Node n : this.nodes) {
			try {
				newlin.nodes.add(getParentForCoalescence(n, lin));
			} catch (IllegalArgumentException exc) {
			}
			;
		}
		for (Taxon t : this.taxa) {
			for (PhyloTree tree : input) {
				try {
					newlin.nodes.add(getParentForCoalescence(tree.getNode(t), lin));
				} catch (IllegalArgumentException exc) {
				}
				;
			}
		}
		if (newlin.nodes.size() == 0) {
			throw new IllegalArgumentException("Coalescence is redundant");
		}
		if (this.isVanishable) {
			newlin.addNodesFromLineage(lin);
		}
		if (lin.isVanishable) {
			newlin.addNodesFromLineage(this);
		}
		if (this.isVanishable && lin.isVanishable) {
			newlin.isVanishable = true;
		}
		return newlin;
	}

	public void addReticulationEvent(ReticulationEvent event) {
		reticulationEvents.add(event);
	}

	public boolean containsNode(Node n) {
		return nodes.contains(n);
	}

	public boolean containsTaxon(Taxon t) {
		return taxa.contains(t);
	}

	public Taxon getTaxon() {
		if (!isInitial) {
			throw new IllegalArgumentException();
		}
		for (Taxon t : taxa) {
			return t;
		}
		throw new IllegalArgumentException();
	}

	@Override
	public String toString() {
		String res = "Nodes:";
		for (Node n : nodes) {
			res += n.toString() + " ";
		}
		res += "Taxa:";
		for (Taxon t : taxa) {
			res += t.toString() + " ";
		}
		return res;
	}
}
