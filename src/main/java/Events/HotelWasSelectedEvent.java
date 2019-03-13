package Events;

import java.util.EventObject;

public class HotelWasSelectedEvent extends EventObject {


    private String wasSelected;
    public HotelWasSelectedEvent( Object source, String wasSelected ) {
        super(source);
        this.wasSelected = wasSelected;
    }

    public String getWasSelected() {
        return wasSelected;
    }

}
