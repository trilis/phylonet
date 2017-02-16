package phylonet_wu;

import java.util.HashSet;

public class CoalescenceEvent extends Event {
	private Lineage lineageSource1, lineageSource2, lineageTarget;

	public CoalescenceEvent(Lineage lineageSource1, Lineage lineageSource2, Lineage lineageTarget,
			Configuration configurationSource, Configuration configurationTarget) {
		this.lineageSource1 = lineageSource1;
		this.lineageSource2 = lineageSource2;
		this.lineageTarget = lineageTarget;
		this.configurationSource = configurationSource;
		this.configurationTarget = configurationTarget;
	}
	
	public HashSet<Lineage> getLineageSources() {
		HashSet<Lineage> ans = new HashSet<Lineage>();
		ans.add(lineageSource1);
		ans.add(lineageSource2);
		return ans;
	}
}
