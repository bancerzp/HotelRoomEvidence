package Events;

import java.util.EventObject;

public class RoomWasAddedEvent extends EventObject {

    private boolean wasAdded;

    public RoomWasAddedEvent(Object source, boolean wasAdded) {
        super(source);
        this.wasAdded = wasAdded;
    }

    public boolean getWasAdded() {
        return wasAdded;
    }

}
