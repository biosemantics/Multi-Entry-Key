package edu.ucdavis.cs.cfgproject.shared.model;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class TaxonMatrix implements Serializable {

	private Collection<Taxon> taxa = new HashSet<Taxon>();
	
	public TaxonMatrix() { }
	
	public TaxonMatrix(Collection<Taxon> taxa) {
		this.taxa = taxa;
	}

	public Collection<Taxon> getTaxa() {
		return taxa;
	}

	public int size() {
		return taxa.size();
	}

	public boolean isEmpty() {
		return taxa.isEmpty();
	}

	public Set<String> getCharacters() {
		if(isEmpty())
			return new HashSet<String>();
		return taxa.iterator().next().getCharacters();
	}
	
}
