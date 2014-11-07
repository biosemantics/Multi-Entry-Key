package edu.ucdavis.cs.cfgproject.server;

import java.io.FileReader;
import java.io.IOException;
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
	private char columnSeparator = ';';
	
	public CSVReader(char columnSeparator, char valueSeparator) {
		this.columnSeparator = columnSeparator;
		this.valueSeparator = valueSeparator;
	}

	public TaxonMatrix read(String filePath) throws IOException {
		Set<Taxon> taxa = new HashSet<Taxon>();
		au.com.bytecode.opencsv.CSVReader reader = new au.com.bytecode.opencsv.CSVReader(new FileReader(filePath), columnSeparator, CSVWriter.NO_QUOTE_CHARACTER);
		
		String[] head = reader.readNext();
	    List<String[]> allLines = reader.readAll();
	    for(String[] line : allLines) {
	    	Taxon taxon = new Taxon(line[0]);
	    	for(int i=1; i<line.length; i++)
	    		taxon.setState(head[i], new State(getSplitedValues(line[i])));
	    	taxa.add(taxon);
	    }
	    return new TaxonMatrix(taxa);
	}
	
	public Set<String> getSplitedValues(String multiValue) {
		return new HashSet<String>(Arrays.asList(multiValue.split(Pattern.quote(String.valueOf(valueSeparator)))));
	}
}
