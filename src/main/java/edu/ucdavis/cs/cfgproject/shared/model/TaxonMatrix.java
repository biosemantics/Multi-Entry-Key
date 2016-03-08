package edu.ucdavis.cs.cfgproject.shared.model;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class TaxonMatrix implements Serializable {

	private Collection<Taxon> taxa = new HashSet<Taxon>();
	private Collection<String> characters = new HashSet<String>();
	
	public TaxonMatrix() { }
	
	public TaxonMatrix(Collection<Taxon> taxa, Collection<String> characters) {
		this.taxa = taxa;
		this.characters = characters;
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

	public Collection<String> getCharacters() {
		return characters;
	}
	
}
