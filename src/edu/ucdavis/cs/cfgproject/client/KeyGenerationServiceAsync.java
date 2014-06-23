package edu.ucdavis.cs.cfgproject.client;

import java.util.HashMap;
import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.ucdavis.cs.cfgproject.shared.Taxon;

public interface KeyGenerationServiceAsync{
	void retrieveTaxa(List<Taxon> taxa, List<String> characters, AsyncCallback<List<Taxon>> updateTaxaCallback);

	void retrieveCharactersAndIg(List<Taxon> taxa, List<String> characters, AsyncCallback<HashMap<String, String>> updateCharactersCallback);
	
	void retreieveStatesAndSpecies(List<Taxon> taxa, List<String> characters, String character, AsyncCallback<HashMap<String, List<Taxon>>> updateStatesCallback);
}
