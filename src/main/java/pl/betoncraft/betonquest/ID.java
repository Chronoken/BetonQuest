package pl.betoncraft.betonquest;

import pl.betoncraft.betonquest.config.ConfigPackage;
import pl.betoncraft.betonquest.exceptions.ObjectNotFoundException;

/**
 * @deprecated Use the {@link pl.betoncraft.betonquest.id.ID} instead,
 * this will be removed in 2.0 release
 */
// TODO Delete in BQ 2.0.0
@Deprecated
@SuppressWarnings("PMD.ShortClassName")
public abstract class ID extends pl.betoncraft.betonquest.id.ID {

    public ID(final ConfigPackage pack, final String identifier) throws ObjectNotFoundException {
        super(pack, identifier);
    }

}
