package edu.ucdavis.cs.cfgproject.client;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;
import com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.container.AccordionLayoutContainer;
import com.sencha.gxt.widget.core.client.container.AccordionLayoutContainer.AccordionLayoutAppearance;
import com.sencha.gxt.widget.core.client.container.AccordionLayoutContainer.ExpandMode;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.form.FieldLabel;

import edu.ucdavis.cs.cfgproject.client.event.DeselectStateEvent;
import edu.ucdavis.cs.cfgproject.client.event.SelectStateEvent;
import edu.ucdavis.cs.cfgproject.shared.TaxonMatrixExtractor;
import edu.ucdavis.cs.cfgproject.shared.model.CharacterGain;
import edu.ucdavis.cs.cfgproject.shared.model.CharacterStateValue;
import edu.ucdavis.cs.cfgproject.shared.model.TaxonMatrix;

public class CharactersView extends VerticalLayoutContainer {
	
	private EventBus eventBus;
	private AccordionLayoutContainer accordionLayoutContainer  = new AccordionLayoutContainer();
	private AccordionLayoutAppearance appearance = GWT.<AccordionLayoutAppearance> create(AccordionLayoutAppearance.class);
	private VerticalLayoutContainer informationGainContainer = new VerticalLayoutContainer();
	private VerticalLayoutContainer selectedInformationGainContainer = new VerticalLayoutContainer();
	private VerticalLayoutContainer noInformationGainContainer = new VerticalLayoutContainer();
	private ContentPanel informationGainPanel = new ContentPanel(appearance);
	private ContentPanel selectedInformationGainPanel = new ContentPanel(appearance);
	private ContentPanel noInformationGainPanel = new ContentPanel(appearance);
	
	public CharactersView(EventBus eventBus) {
		this.eventBus = eventBus;
		
		accordionLayoutContainer.setExpandMode(ExpandMode.SINGLE_FILL);
		
		informationGainContainer.setScrollMode(ScrollMode.AUTOY);
		informationGainPanel.setHeadingText("Useful Characters for Differentation");
		informationGainPanel.add(informationGainContainer);
		accordionLayoutContainer.add(informationGainPanel);
		
		selectedInformationGainContainer.setScrollMode(ScrollMode.AUTOY);
		selectedInformationGainPanel.setHeadingText("Selected Character States");
		selectedInformationGainPanel.add(selectedInformationGainContainer);
	    accordionLayoutContainer.add(selectedInformationGainPanel);
		
	    noInformationGainContainer.setScrollMode(ScrollMode.AUTOY);
	    noInformationGainPanel.setHeadingText("Useless Characters for Differentiation");
	    noInformationGainPanel.add(noInformationGainContainer);
	    accordionLayoutContainer.add(noInformationGainPanel);
	    
		accordionLayoutContainer.setActiveWidget(informationGainPanel);
		add(accordionLayoutContainer, new VerticalLayoutData(1, 1));
	}
	
	public void setCharacterGains(final TaxonMatrix taxonMatrix, List<CharacterGain> characterGains, Set<CharacterStateValue> selectedCharacterStateValues) {
		informationGainContainer.clear();
		noInformationGainContainer.clear();
		selectedInformationGainContainer.clear();
		Set<String> selectedCharacters = new HashSet<String>();
		for(CharacterStateValue characterStatevalue : selectedCharacterStateValues) 
			selectedCharacters.add(characterStatevalue.getCharacter());
		
		for(CharacterGain characterGain : characterGains) {
			Set<String> stateValues = TaxonMatrixExtractor.extractCharacterStateValues(taxonMatrix, characterGain.getCharacter());
			Widget widget = createEntry(characterGain, stateValues, selectedCharacterStateValues);
			
			if(selectedCharacters.contains(characterGain.getCharacter()))
				selectedInformationGainContainer.add(widget, new VerticalLayoutData(1.0, -1));
			else {
				if(characterGain.getInformationGain() == 0.0)
					noInformationGainContainer.add(widget, new VerticalLayoutData(1.0, -1));
				else 			
					informationGainContainer.add(widget, new VerticalLayoutData(1.0, -1));
			}
		}
		
		if(isActiveInformationGain() && informationGainContainer.getWidgetCount() == 0)
			accordionLayoutContainer.setActiveWidget(selectedInformationGainPanel);
	}
	
	public boolean isActiveInformationGain() {
		return accordionLayoutContainer.getActiveWidget().equals(informationGainPanel);
	}
	
	public boolean isActiveNoInformationGain() {
		return accordionLayoutContainer.getActiveWidget().equals(noInformationGainPanel);
	}
	
	public boolean isActiveSelectedInformationGain() {
		return accordionLayoutContainer.getActiveWidget().equals(selectedInformationGainPanel);
	}

	private Widget createEntry(final CharacterGain characterGain, Set<String> stateValues, Set<CharacterStateValue> selectedCharacterStateValues) {
	    HorizontalPanel horizontalPanel = new HorizontalPanel();
		for (final String stateValue : stateValues) {
			CheckBox checkBox = new CheckBox(stateValue);
			final CharacterStateValue characterStateValue = new CharacterStateValue(characterGain.getCharacter(), stateValue);
			checkBox.setValue(selectedCharacterStateValues.contains(characterStateValue));
			checkBox.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
				@Override
				public void onValueChange(ValueChangeEvent<Boolean> event) {
					if(event.getValue()) 
						eventBus.fireEvent(new SelectStateEvent(characterStateValue));
					else
						eventBus.fireEvent(new DeselectStateEvent(characterStateValue));
				}
		    });
		    horizontalPanel.add(checkBox);
		}
	 
		FieldLabel result = new FieldLabel(horizontalPanel, characterGain.toString());
		result.setLabelWidth(600);
	    return result;
	}
}
