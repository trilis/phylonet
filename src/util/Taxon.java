package util;

public class Taxon {

	public final String name;
	
	public Taxon(String name) {
		this.name = name;
	}
	
	@Override
	public int hashCode() {
		return name.hashCode();
	}
	
	@Override
	public boolean equals(Object o) {
		return name.equals(((Taxon)o).name);
	}

	@Override
	public String toString() {
		return name;
	}
}
