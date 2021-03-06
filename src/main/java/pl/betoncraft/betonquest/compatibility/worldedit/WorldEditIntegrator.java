package pl.betoncraft.betonquest.compatibility.worldedit;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.compatibility.Integrator;


public class WorldEditIntegrator implements Integrator {

    private final BetonQuest plugin;

    public WorldEditIntegrator() {
        plugin = BetonQuest.getInstance();
    }

    @Override
    public void hook() {
        plugin.registerEvents("paste", PasteSchematicEvent.class);
    }

    @Override
    public void reload() {

    }

    @Override
    public void close() {

    }

}
