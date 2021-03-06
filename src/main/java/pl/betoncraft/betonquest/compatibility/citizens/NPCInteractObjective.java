package pl.betoncraft.betonquest.compatibility.citizens;

import net.citizensnpcs.api.event.NPCRightClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.api.Objective;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.utils.PlayerConverter;

/**
 * Player has to right click the NPC
 */
public class NPCInteractObjective extends Objective implements Listener {

    private final int npcId;
    private final boolean cancel;

    public NPCInteractObjective(final Instruction instruction) throws InstructionParseException {
        super(instruction);
        template = ObjectiveData.class;
        npcId = instruction.getInt();
        if (npcId < 0) {
            throw new InstructionParseException("ID cannot be negative");
        }
        cancel = instruction.hasArgument("cancel");
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onNPCClick(final NPCRightClickEvent event) {
        final String playerID = PlayerConverter.getID(event.getClicker());
        if (event.getNPC().getId() != npcId || !containsPlayer(playerID)) {
            return;
        }
        if (checkConditions(playerID)) {
            if (cancel) {
                event.setCancelled(true);
            }
            completeObjective(playerID);
        }
    }

    @Override
    public void start() {
        Bukkit.getPluginManager().registerEvents(this, BetonQuest.getInstance());
    }

    @Override
    public void stop() {
        HandlerList.unregisterAll(this);
    }

    @Override
    public String getDefaultDataInstruction() {
        return "";
    }

    @Override
    public String getProperty(final String name, final String playerID) {
        return "";
    }

}
