package Events;

import java.util.EventObject;

public class PlafEvent extends EventObject {


    private int plaf;

    public PlafEvent(Object source, int plaf) {
        super(source);
        this.plaf = plaf;
    }

    public int getPlaf() {
        return plaf;
    }

}
