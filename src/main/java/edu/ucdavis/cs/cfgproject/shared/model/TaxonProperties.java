package edu.ucdavis.cs.cfgproject.shared.model;

import com.google.gwt.editor.client.Editor.Path;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;

public interface TaxonProperties extends PropertyAccess<Taxon> {

	
	@Path("name")
	ModelKeyProvider<Taxon> key();
	
	@Path("name")
	LabelProvider<Taxon> nameLabel();
	
	@Path("name")
	ValueProvider<Taxon, String> name();
	
}