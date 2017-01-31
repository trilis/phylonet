package phylonet;

public class Taxon {

	String name;
	
	public Taxon(String name) {
		this.name = name;
	}
	
	@Override
	public boolean equals(Object o) {
		return name.equals(((Taxon)o).name);
	}

}
