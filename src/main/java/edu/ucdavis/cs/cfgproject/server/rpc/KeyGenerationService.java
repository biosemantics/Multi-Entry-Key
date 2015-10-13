package edu.ucdavis.cs.cfgproject.server.rpc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Random;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.ucdavis.cs.cfgproject.server.CSVReader;
import edu.ucdavis.cs.cfgproject.server.Configuration;
import edu.ucdavis.cs.cfgproject.server.InformationGainCalculator;
import edu.ucdavis.cs.cfgproject.shared.model.CharacterGain;
import edu.ucdavis.cs.cfgproject.shared.model.State;
import edu.ucdavis.cs.cfgproject.shared.model.Taxon;
import edu.ucdavis.cs.cfgproject.shared.model.TaxonMatrix;
import edu.ucdavis.cs.cfgproject.shared.rpc.IKeyGenerationService;

public class KeyGenerationService extends RemoteServiceServlet implements IKeyGenerationService {

	private CSVReader reader = new CSVReader(Configuration.columnSeparator, Configuration.valueSeparator, Configuration.quoteCharacter, 
			Configuration.escapeCharacter);
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
	
	public static void main(String[] args) {
		TaxonMatrix taxonMatrix = generateRandomTaxonMatrix();
		KeyGenerationService service = new KeyGenerationService();
		
		final long startTime = System.currentTimeMillis();
		List<CharacterGain> characterGains = service.getCharacterGains(taxonMatrix);
		final long endTime = System.currentTimeMillis();
		System.out.println("Total execution time: " + (endTime - startTime) );
		
	}

	private static TaxonMatrix generateRandomTaxonMatrix() {
		// TODO generate your random matrix here
		
		List<Taxon> taxa = new LinkedList<Taxon>();
		
		/*
		Taxon a = new Taxon("a");
		Taxon b = new Taxon("b");
		Taxon c = new Taxon("c");
		taxa.add(a);
		taxa.add(b);
		taxa.add(c);
		a.setState("c1", new State(new HashSet<String>(Arrays.asList("1", "0"))));
		a.setState("c2", new State(new HashSet<String>(Arrays.asList("0"))));
		b.setState("c1", new State(new HashSet<String>(Arrays.asList("0"))));
		b.setState("c2", new State(new HashSet<String>(Arrays.asList("1"))));
		c.setState("c1", new State(new HashSet<String>(Arrays.asList("0"))));
		c.setState("c2", new State(new HashSet<String>(Arrays.asList("1"))));
		*/
		
		
		final int TAXON_SIZE = 4;
		final int CHARACTER_SIZE = 1;
		
		for (int i=0; i<TAXON_SIZE; i++) {
			String taxonName = "t" + i;
			Taxon t = new Taxon(taxonName);
//			System.out.print("Taxon " + taxonName);
			
			for (int j=0; j<CHARACTER_SIZE; j++) {
				String characterName = "c" + j;
				Random random = new Random();
//				String value = Integer.toString(random.nextInt(1));
//				t.setState(characterName, new State(new HashSet<String>(Arrays.asList(value))));
//				System.out.print(" States are ");
				int s1 = random.nextInt(10);
				int s2 = random.nextInt(10);
//				System.out.println(s1 + "," + s2);
				t.setState(characterName, new State(new HashSet<String>(Arrays.asList(
						String.valueOf(s1),
                        String.valueOf(s2)
                        ))));
			}
			taxa.add(t);
		}
		
		
		TaxonMatrix sample = new TaxonMatrix(taxa);
		return sample;
	}
	
}
