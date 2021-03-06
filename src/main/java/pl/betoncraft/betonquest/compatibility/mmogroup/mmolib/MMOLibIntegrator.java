package pl.betoncraft.betonquest.compatibility.mmogroup.mmolib;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.compatibility.Integrator;

public class MMOLibIntegrator implements Integrator {

    private final BetonQuest plugin;

    public MMOLibIntegrator() {
        plugin = BetonQuest.getInstance();
    }

    @Override
    public void hook() {
        plugin.registerConditions("mmostat", MMOLibStatCondition.class);
    }

    @Override
    public void reload() {

    }

    @Override
    public void close() {

    }

}
