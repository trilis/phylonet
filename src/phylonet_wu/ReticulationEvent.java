package phylonet_wu;

public class ReticulationEvent extends Event {
	private Lineage lineageSource, lineageTarget1, lineageTarget2;

	public ReticulationEvent(Lineage lineageSource, Lineage lineageTarget1, Lineage lineageTarget2,
			Configuration configurationSource) {
		this.lineageSource = lineageSource;
		this.lineageTarget1 = lineageTarget1;
		this.lineageTarget2 = lineageTarget2;
		this.configurationSource = configurationSource;
	}
	
	public Lineage getLineageSource() {
		return lineageSource;
	}
	
	public Lineage getLineageTarget1() {
		return lineageTarget1;
	}
	
	public Lineage getLineageTarget2() {
		return lineageTarget2;
	}
}
