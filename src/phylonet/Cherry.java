package phylonet;

import java.util.HashSet;

import util.Node;
import util.Taxon;

public class Cherry {
	private HashSet<Taxon> oldLabel1, oldLabel2;
	private HashSet<Taxon> newLabel = new HashSet<Taxon>();
	private Node oldNode1, oldNode2;

	public Cherry(HashSet<Taxon> oldLabel1, HashSet<Taxon> oldLabel2, Node oldNode1, Node oldNode2) {
		this.oldLabel1 = oldLabel1;
		this.oldLabel2 = oldLabel2;
		newLabel.addAll(oldLabel1);
		newLabel.addAll(oldLabel2);
		this.oldNode1 = oldNode1;
		this.oldNode2 = oldNode2;
	}

	public HashSet<Taxon> getOldLabel1() {
		return oldLabel1;
	}

	public HashSet<Taxon> getOldLabel2() {
		return oldLabel2;
	}

	public HashSet<Taxon> getNewLabel() {
		return newLabel;
	}

	public Node getOldNode1() {
		return oldNode1;
	}

	public Node getOldNode2() {
		return oldNode2;
	}
}
