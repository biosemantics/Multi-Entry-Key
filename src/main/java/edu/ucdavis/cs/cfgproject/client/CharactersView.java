package edu.ucdavis.cs.cfgproject.client;

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
	private VerticalLayoutContainer informationGainContainer = new VerticalLayoutContainer();
	private VerticalLayoutContainer noInformationGainContainer = new VerticalLayoutContainer();
	
	public CharactersView(EventBus eventBus) {
		this.eventBus = eventBus;
		
		AccordionLayoutContainer accordionLayoutContainer = new AccordionLayoutContainer();
		accordionLayoutContainer.setExpandMode(ExpandMode.SINGLE_FILL);
	    AccordionLayoutAppearance appearance = GWT.<AccordionLayoutAppearance> create(AccordionLayoutAppearance.class);
		
		ContentPanel informationGainPanel = new ContentPanel(appearance);
		informationGainPanel.setAnimCollapse(false);
		informationGainPanel.setHeadingText("Information Gain");
		informationGainPanel.add(informationGainContainer);
		accordionLayoutContainer.add(informationGainPanel);
		
	    ContentPanel noInformationGainPanel = new ContentPanel(appearance);
	    noInformationGainPanel.setAnimCollapse(false);
	    noInformationGainPanel.setBodyStyleName("pad-text");
	    noInformationGainPanel.setHeadingText("Settings");
	    noInformationGainPanel.add(noInformationGainContainer);
	    accordionLayoutContainer.add(noInformationGainPanel);
	    
		accordionLayoutContainer.setActiveWidget(informationGainPanel);
		add(accordionLayoutContainer, new VerticalLayoutData(1, 1));
	}
	
	public void setCharacterGains(final TaxonMatrix taxonMatrix, List<CharacterGain> characterGains, Set<CharacterStateValue> selectedCharacterStateValues) {
		informationGainContainer.clear();
		noInformationGainContainer.clear();
		
		for(CharacterGain characterGain : characterGains) {
			Set<String> stateValues = TaxonMatrixExtractor.extractCharacterStateValues(taxonMatrix, characterGain.getCharacter());
			Widget widget = createEntry(characterGain, stateValues, selectedCharacterStateValues);
			if(characterGain.getInformationGain() == 0.0)
				noInformationGainContainer.add(widget, new VerticalLayoutData(1.0, -1));
			else 			
				informationGainContainer.add(widget, new VerticalLayoutData(1.0, -1));
		}
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
