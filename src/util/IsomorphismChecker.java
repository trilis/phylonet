package util;

import java.util.Iterator;

public class IsomorphismChecker {

	public boolean areBinaryTreesIsomorphic(Node root1, Node root2) {
		if (root1 == null && root2 == null) {
			return true;
		}
		if (root1 == null || root2 == null) {
			return false;
		}
		if (root1.isLeaf() && root2.isLeaf()) {
			return (root1.getTaxon().equals(root2.getTaxon()));
		}
		if (root1.getOutDeg() != root2.getOutDeg()) {
			return false;
		}
		Iterator<Edge> itr1 = root1.getOutEdges().iterator();
		Iterator<Edge> itr2 = root2.getOutEdges().iterator();
		Edge e1Left = itr1.next();
		Edge e2Left = itr2.next();
		if (itr1.hasNext()) {
			Edge e1Right = itr1.next();
			Edge e2Right = itr2.next();
			return (areBinaryTreesIsomorphic(e1Left.getFinish(), e2Right.getFinish())
					&& areBinaryTreesIsomorphic(e1Right.getFinish(), e2Left.getFinish()))
					|| (areBinaryTreesIsomorphic(e1Right.getFinish(), e2Right.getFinish()) &&
							areBinaryTreesIsomorphic(e1Left.getFinish(), e2Left.getFinish()));
		}
		return areBinaryTreesIsomorphic(e1Left.getFinish(), e2Left.getFinish());
	}

}
