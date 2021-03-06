package pl.betoncraft.betonquest.item.typehandler;

import pl.betoncraft.betonquest.item.QuestItem.Existence;

public class HeadOwnerHandler {

    private String owner = null;
    private Existence ownerE = Existence.WHATEVER;

    public HeadOwnerHandler() {
    }

    public void set(final String string) {
        if (string.equalsIgnoreCase("none")) {
            ownerE = Existence.FORBIDDEN;
        } else {
            owner = string;
            ownerE = Existence.REQUIRED;
        }
    }

    public String get() {
        return owner;
    }

    public boolean check(final String string) {
        switch (ownerE) {
            case WHATEVER:
                return true;
            case REQUIRED:
                return string != null && string.equals(owner);
            case FORBIDDEN:
                return string == null;
            default:
                return false;
        }
    }

}
