package Events;


import java.util.EventObject;

public class HotelWasDeletedEvent extends EventObject {


    private boolean wasDeleted;
    private String idHotel;
    public HotelWasDeletedEvent( Object source, boolean wasDeleted,String idHotel ) {
        super(source);
        this.wasDeleted = wasDeleted;
        this.idHotel = idHotel;
    }

    public boolean getWasDeleted() {
        return wasDeleted;
    }

    public String getIdHotel() {
        return idHotel;
    }


}
