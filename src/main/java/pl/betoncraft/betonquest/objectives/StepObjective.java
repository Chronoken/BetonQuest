package pl.betoncraft.betonquest.objectives;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.api.Objective;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.exceptions.QuestRuntimeException;
import pl.betoncraft.betonquest.utils.BlockSelector;
import pl.betoncraft.betonquest.utils.LogUtils;
import pl.betoncraft.betonquest.utils.PlayerConverter;
import pl.betoncraft.betonquest.utils.location.CompoundLocation;

import java.util.logging.Level;

/**
 * The player must step on the pressure plate
 */
public class StepObjective extends Objective implements Listener {
    private static final BlockSelector PRESSURE_PLATE_SELECTOR = getPressurePlateSelector();

    private final CompoundLocation loc;

    private static BlockSelector getPressurePlateSelector() {
        try {
            return new BlockSelector("*_PRESSURE_PLATE");
        } catch (InstructionParseException exception) {
            LogUtils.logThrowableReport(exception);
        }
        return null;
    }

    public StepObjective(final Instruction instruction) throws InstructionParseException {
        super(instruction);
        template = ObjectiveData.class;
        loc = instruction.getLocation();
    }

    @EventHandler(ignoreCancelled = true)
    public void onStep(final PlayerInteractEvent event) {
        // Only fire the event for the main hand to avoid that the event is triggered two times.
        if (event.getHand() == EquipmentSlot.OFF_HAND && event.getHand() != null) {
            return; // off hand packet, ignore.
        }
        if (event.getAction() != Action.PHYSICAL) {
            return;
        }
        if (event.getClickedBlock() == null) {
            return;
        }
        try {
            final String playerID = PlayerConverter.getID(event.getPlayer());
            final Block block = loc.getLocation(playerID).getBlock();
            if (!event.getClickedBlock().equals(block)) {
                return;
            }

            if (PRESSURE_PLATE_SELECTOR == null || !PRESSURE_PLATE_SELECTOR.match(block.getBlockData().getMaterial())) {
                return;
            }
            if (!containsPlayer(playerID)) {
                return;
            }
            // player stepped on the pressure plate
            if (checkConditions(playerID)) {
                completeObjective(playerID);
            }
        } catch (QuestRuntimeException e) {
            LogUtils.getLogger().log(Level.WARNING, "Error while handling '" + instruction.getID() + "' objective: " + e.getMessage());
            LogUtils.logThrowable(e);
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
        if (name.equalsIgnoreCase("location")) {
            final Block block;
            try {
                block = loc.getLocation(playerID).getBlock();
            } catch (QuestRuntimeException e) {
                LogUtils.getLogger().log(Level.WARNING, "Error while getting location property in '" + instruction.getID() + "' objective: "
                        + e.getMessage());
                LogUtils.logThrowable(e);
                return "";
            }
            return "X: " + block.getX() + ", Y: " + block.getY() + ", Z: " + block.getZ();
        }
        return "";
    }
}
