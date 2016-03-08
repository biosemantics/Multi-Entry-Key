package edu.ucdavis.cs.cfgproject.server;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import au.com.bytecode.opencsv.CSVWriter;
import edu.ucdavis.cs.cfgproject.shared.model.State;
import edu.ucdavis.cs.cfgproject.shared.model.Taxon;
import edu.ucdavis.cs.cfgproject.shared.model.TaxonMatrix;

public class CSVReader {
	
	private char valueSeparator = '|';
	private char columnSeparator = CSVWriter.DEFAULT_SEPARATOR;
	private char quoteCharacter = CSVWriter.DEFAULT_QUOTE_CHARACTER;
	private char escapeCharacter = CSVWriter.DEFAULT_ESCAPE_CHARACTER;
	
	public CSVReader(char columnSeparator, char valueSeparator, char quoteCharacter, char escapeCharacter) {
		this.columnSeparator = columnSeparator;
		this.valueSeparator = valueSeparator;
		this.quoteCharacter = quoteCharacter;
		this.escapeCharacter = escapeCharacter;
	}

	public TaxonMatrix read(String filePath) throws IOException {
		Set<Taxon> taxa = new HashSet<Taxon>();
		try (au.com.bytecode.opencsv.CSVReader reader = new au.com.bytecode.opencsv.CSVReader(new FileReader(filePath), 
				columnSeparator, quoteCharacter, escapeCharacter)) {
			String[] head = reader.readNext();
		    List<String[]> allLines = reader.readAll();
		    for(String[] line : allLines) {
		    	Taxon taxon = new Taxon(line[0]);
		    	for(int i=1; i<line.length; i++)
		    		taxon.setState(head[i], new State(getSplitedValues(line[i])));
		    	taxa.add(taxon);
		    }
		    List<String> characters = new ArrayList<String>(Arrays.asList(head));
		    characters.remove(0);
		    characters.addAll(characters);
		    return new TaxonMatrix(taxa, new HashSet<String>(characters));
		}
	}
	
	public Set<String> getSplitedValues(String multiValue) {
		String[] values = multiValue.split(Pattern.quote(String.valueOf(valueSeparator)));
		Set<String> result = new HashSet<String>();
		for(String value : values)
			result.add(value.trim().toLowerCase());
		return result;
	}
}
