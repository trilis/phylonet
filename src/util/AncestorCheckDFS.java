package util;

public class AncestorCheckDFS extends DFS {

	private Node descendant;
	private boolean answer;
	
	public AncestorCheckDFS(Node ancestor, Node descendant) {
		this.descendant = descendant;
		dfs(ancestor);
	}
	
	@Override
	public void enter(Node v) {
		if (v == descendant) {
			answer = true;
		}
	}

	@Override
	public void exit(Node v) {
		// TODO Auto-generated method stub
	}
	
	public boolean getAnswer() {
		return answer;
	}

}
