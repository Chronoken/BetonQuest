package pl.betoncraft.betonquest.compatibility.placeholderapi;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.config.Config;
import pl.betoncraft.betonquest.utils.PlayerConverter;

public class BetonQuestPlaceholder extends PlaceholderExpansion {

    public BetonQuestPlaceholder() {
        super();
    }

    /**
     * Persist through reloads
     *
     * @return true to persist through reloads
     */
    @Override
    public boolean persist() {
        return true;
    }

    /**
     * We can always register
     *
     * @return Always true since it's an internal class.
     */
    @Override
    public boolean canRegister() {
        return true;
    }

    /**
     * Name of person who created the expansion
     *
     * @return The name of the author as a String.
     */
    @Override
    public String getAuthor() {
        return BetonQuest.getInstance().getDescription().getAuthors().toString();
    }

    /**
     * The identifier for PlaceHolderAPI to link to this expansion
     *
     * @return The identifier in {@code %<identifier>_<value>%} as String.
     */
    @Override
    public String getIdentifier() {
        return "betonquest";
    }

    /**
     * Version of the expansion
     *
     * @return The version as a String.
     */
    @Override
    public String getVersion() {
        return BetonQuest.getInstance().getDescription().getVersion();
    }

    /**
     * A placeholder request has occurred and needs a value
     *
     * @param player     A {@link org.bukkit.entity.Player Player}.
     * @param identifier A String containing the identifier/value.
     * @return possibly-null String of the requested identifier.
     */
    @Override
    public String onPlaceholderRequest(final Player player, final String identifier) {
        final String pack;
        final String placeholderIdentifier;
        final int index = identifier.indexOf(':');
        if (index == -1) {
            pack = Config.getDefaultPackage().getName();
            placeholderIdentifier = identifier;
        } else {
            pack = identifier.substring(0, index);
            placeholderIdentifier = identifier.substring(index + 1);
        }
        return BetonQuest.getInstance().getVariableValue(pack, '%' + placeholderIdentifier + '%', PlayerConverter.getID(player));
    }

}
