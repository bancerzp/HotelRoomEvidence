package Events;

import javax.swing.*;
import java.io.FileNotFoundException;
import java.io.IOException;

public interface PlafListener {
    void changePlaf(PlafEvent event) throws ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException, IOException;
}
