package edu.ucdavis.cs.cfgproject.server.rpc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.ucdavis.cs.cfgproject.server.CSVReader;
import edu.ucdavis.cs.cfgproject.server.Configuration;
import edu.ucdavis.cs.cfgproject.server.InformationGainCalculator;
import edu.ucdavis.cs.cfgproject.shared.model.CharacterGain;
import edu.ucdavis.cs.cfgproject.shared.model.TaxonMatrix;
import edu.ucdavis.cs.cfgproject.shared.rpc.IKeyGenerationService;

public class KeyGenerationService extends RemoteServiceServlet implements IKeyGenerationService {

	private CSVReader reader = new CSVReader(Configuration.columnSeparator, Configuration.valueSeparator);
	private InformationGainCalculator informationGainCalculator = new InformationGainCalculator();
	
	@Override
	public TaxonMatrix getTaxonMatrix(String input) throws Exception {
		return reader.read(input);
	}

	@Override
	public List<CharacterGain> getCharacterGains(TaxonMatrix taxonMatrix) {
		Set<String> characters = taxonMatrix.getCharacters();
		List<CharacterGain> result = new ArrayList<CharacterGain>(characters.size());
		for (String character : characters)
			result.add(new CharacterGain(character, informationGainCalculator.calculateNDisc1Ig(taxonMatrix, character)));
		Collections.sort(result);
		return result;
	}
	
}
