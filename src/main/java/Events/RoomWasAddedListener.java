package Events;

import java.sql.SQLException;

public interface RoomWasAddedListener {

    void roomWasAdded(RoomWasAddedEvent event) throws SQLException;

}
