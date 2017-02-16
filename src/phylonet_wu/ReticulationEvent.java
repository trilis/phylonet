package phylonet_wu;

public class ReticulationEvent extends Event {
	private Lineage lineageSource, lineageTarget1, lineageTarget2;

	public ReticulationEvent(Lineage lineageSource, Lineage lineageTarget1, Lineage lineageTarget2,
			Configuration configurationSource, Configuration configurationTarget) {
		this.lineageSource = lineageSource;
		this.lineageTarget1 = lineageTarget1;
		this.lineageTarget2 = lineageTarget2;
		this.configurationSource = configurationSource;
		this.configurationTarget = configurationTarget;
	}
	
	public Lineage getLineageSource() {
		return lineageSource;
	}
}
