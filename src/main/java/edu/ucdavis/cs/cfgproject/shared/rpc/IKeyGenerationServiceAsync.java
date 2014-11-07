package edu.ucdavis.cs.cfgproject.shared.rpc;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.ucdavis.cs.cfgproject.shared.model.CharacterGain;
import edu.ucdavis.cs.cfgproject.shared.model.TaxonMatrix;


public interface IKeyGenerationServiceAsync{
	
	public void getTaxonMatrix(String input, AsyncCallback<TaxonMatrix> callback);

	public void getCharacterGains(TaxonMatrix taxonMatrix, AsyncCallback<List<CharacterGain>> callback);

}
