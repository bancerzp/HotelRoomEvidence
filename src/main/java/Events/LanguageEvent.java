package Events;

import java.util.EventObject;

public class LanguageEvent extends EventObject {


    private String language;
    public LanguageEvent( Object source, String language ) {
        super(source);
        this.language = language;
    }

    public String getLanguage() {
        return language;
    }

}
