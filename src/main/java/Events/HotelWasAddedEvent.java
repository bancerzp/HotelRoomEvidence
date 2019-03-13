package Events;

import java.util.EventObject;

public class HotelWasAddedEvent extends EventObject {

    private boolean wasAdded;
    public HotelWasAddedEvent( Object source, boolean wasAdded ) {
        super(source);
        this.wasAdded = wasAdded;
    }

    public boolean getWasAdded() {
        return wasAdded;
    }

}
