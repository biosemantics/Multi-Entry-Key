package edu.ucdavis.cs.cfgproject.shared.rpc;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import edu.ucdavis.cs.cfgproject.shared.model.CharacterGain;
import edu.ucdavis.cs.cfgproject.shared.model.TaxonMatrix;

@RemoteServiceRelativePath("keyGeneration")
public interface IKeyGenerationService extends RemoteService{
	
	public TaxonMatrix getTaxonMatrix(String input) throws Exception;

	public List<CharacterGain> getCharacterGains(TaxonMatrix taxonMatrix) throws Exception;
	
}
