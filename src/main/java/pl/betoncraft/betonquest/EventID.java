package pl.betoncraft.betonquest;

import pl.betoncraft.betonquest.config.ConfigPackage;
import pl.betoncraft.betonquest.exceptions.ObjectNotFoundException;

/**
 * @deprecated Use the {@link pl.betoncraft.betonquest.id.EventID} instead, this
 * this will be removed in 2.0 release
 */
// TODO Delete in BQ 2.0.0
@Deprecated
public class EventID extends pl.betoncraft.betonquest.id.EventID {

    public EventID(final ConfigPackage pack, final String identifier) throws ObjectNotFoundException {
        super(pack, identifier);
    }

}
