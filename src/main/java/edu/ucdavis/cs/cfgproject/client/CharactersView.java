package edu.ucdavis.cs.cfgproject.client;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;
import com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode;
import com.sencha.gxt.widget.core.client.container.HorizontalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.HorizontalLayoutContainer.HorizontalLayoutData;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.form.FieldLabel;

import edu.ucdavis.cs.cfgproject.client.event.SelectStateEvent;
import edu.ucdavis.cs.cfgproject.shared.TaxonMatrixExtractor;
import edu.ucdavis.cs.cfgproject.shared.model.CharacterGain;
import edu.ucdavis.cs.cfgproject.shared.model.TaxonMatrix;

public class CharactersView extends VerticalLayoutContainer {

	private VerticalLayoutContainer verticalLayoutContainer = new VerticalLayoutContainer();
	private EventBus eventBus;
	
	public CharactersView(EventBus eventBus) {
		this.eventBus = eventBus;
		add(new Label("Characters: "), new VerticalLayoutData(1, -1));
		verticalLayoutContainer.setScrollMode(ScrollMode.AUTOY);
		add(verticalLayoutContainer, new VerticalLayoutData(1, 1));
	}
	
	public void setCharacterGains(final TaxonMatrix taxonMatrix, List<CharacterGain> characterGains) {
		verticalLayoutContainer.clear();
		boolean zeroInformationGain = false;
		for(CharacterGain characterGain : characterGains) {
			Set<String> states = TaxonMatrixExtractor.extractCharacterStateValues(taxonMatrix, characterGain.getCharacter());
			Widget widget = createEntry(characterGain, states);
			if(zeroInformationGain == false && characterGain.getInformationGain() == 0.0) {
				HTML horizontalDivider = new HTML("<hr  style=\"width:100%;\" />");
				verticalLayoutContainer.add(horizontalDivider, new VerticalLayoutData(1.0, -1));
				zeroInformationGain = true;
			}
			verticalLayoutContainer.add(widget, new VerticalLayoutData(1.0, -1));
		}
	}

	private Widget createEntry(final CharacterGain characterGain, Set<String> states) {
		//return new Label("test");
		//HorizontalLayoutContainer container = new HorizontalLayoutContainer();
		//HorizontalPanel container = new HorizontalPanel();
		
		/*container.add(new Label(String.valueOf(round(characterGain.getInformationGain(), 2))));
		container.add(new Label(characterGain.getCharacter()));
		
		for (final String state : states) {
			CheckBox checkBox = new CheckBox(state);
			checkBox.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					eventBus.fireEvent(new SelectStateEvent(characterGain.getCharacter(), state));
				}
			});
			container.add(checkBox);
		}
		return container;*/

	    HorizontalPanel horizontalPanel = new HorizontalPanel();
		for (final String state : states) {
			CheckBox checkBox = new CheckBox(state);
			checkBox.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
				@Override
				public void onValueChange(ValueChangeEvent<Boolean> event) {
					if(event.getValue())
						eventBus.fireEvent(new SelectStateEvent(characterGain.getCharacter(), state));
				}
		    });
		    horizontalPanel.add(checkBox);
		}
	 
		FieldLabel result = new FieldLabel(horizontalPanel, characterGain.toString());
		result.setLabelWidth(600);
	    return result;
	}	
	
}
