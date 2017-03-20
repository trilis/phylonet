package phylonet_wu;

import util.DFS;
import util.Node;

public class CheckLineageDFS extends DFS {

	private Configuration conf;
	private int coveringCount = 0;
	private boolean isUseful = true;

	public CheckLineageDFS(Configuration conf) {
		this.conf = conf;
	}

	public boolean isCovered(Node v) {
		if (v.isLeaf() && conf.containsTaxon(v.getTaxon())) {
			return true;
		}
		if (!v.isLeaf() && conf.containsNode(v)) {
			return true;
		}
		return false;
	}

	@Override
	public void enter(Node v) {
		if (isCovered(v)) {
			coveringCount++;
		}
		if (v.isLeaf() && coveringCount == 0) {
			isUseful = false;
		}
	}

	@Override
	public void exit(Node v) {
		if (isCovered(v)) {
			coveringCount--;
		}
	}

	public boolean isUseful() {
		return isUseful;
	}

}
