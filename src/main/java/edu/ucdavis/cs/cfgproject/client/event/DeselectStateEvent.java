package edu.ucdavis.cs.cfgproject.client.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import edu.ucdavis.cs.cfgproject.client.event.DeselectStateEvent.DeselectStateHandler;
import edu.ucdavis.cs.cfgproject.shared.model.CharacterStateValue;

public class DeselectStateEvent extends GwtEvent<DeselectStateHandler> {

	public interface DeselectStateHandler extends EventHandler {
		void onSelect(DeselectStateEvent event);
	}
	
	public static Type<DeselectStateHandler> TYPE = new Type<DeselectStateHandler>();

	private CharacterStateValue characterStateValue;
	
	public DeselectStateEvent(CharacterStateValue characterStateValue) {
		this.characterStateValue = characterStateValue;
	}

	@Override
	public Type<DeselectStateHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(DeselectStateHandler handler) {
		handler.onSelect(this);
	}

	public CharacterStateValue getCharacterStateValue() {
		return characterStateValue;
	}
	
}
