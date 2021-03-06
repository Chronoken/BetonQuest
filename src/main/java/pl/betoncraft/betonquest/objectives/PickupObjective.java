package pl.betoncraft.betonquest.objectives;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.Instruction.Item;
import pl.betoncraft.betonquest.api.Objective;
import pl.betoncraft.betonquest.config.Config;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.exceptions.QuestRuntimeException;
import pl.betoncraft.betonquest.utils.LogUtils;
import pl.betoncraft.betonquest.utils.PlayerConverter;

import java.util.logging.Level;

public class PickupObjective extends Objective implements Listener {

    private final Item[] pickupItems;

    private final int amount;

    private final boolean notify;

    public PickupObjective(final Instruction instruction) throws InstructionParseException {
        super(instruction);

        template = PickupData.class;

        pickupItems = instruction.getItemList();

        amount = instruction.getInt(instruction.getOptional("amount"), 1);
        notify = instruction.hasArgument("notify");
    }

    @EventHandler(ignoreCancelled = true)
    public void onPickup(final EntityPickupItemEvent event) {
        if (isInvalidItem(event.getItem().getItemStack()) || !(event.getEntity() instanceof Player)) {
            return;
        }

        final Player player = (Player) event.getEntity();
        final String playerID = PlayerConverter.getID(player);

        if (!containsPlayer(playerID) || !checkConditions(playerID)) {
            return;
        }

        final PickupData playerData = getPickupData(playerID);
        playerData.pickup();

        if (playerData.isFinished()) {
            completeObjective(playerID);
            return;
        }

        if (notify) {
            try {
                Config.sendNotify(instruction.getPackage().getName(), playerID, "items_to_pickup", new String[]{Integer.toString(playerData.getAmount())}, "items_to_pickup,info");
            } catch (final QuestRuntimeException exception) {
                try {
                    LogUtils.getLogger().log(Level.WARNING, "The notify system was unable to play a sound for the 'items_to_pickup' category in '" + instruction.getObjective().getFullID() + "'. Error was: '" + exception.getMessage() + "'");
                } catch (final InstructionParseException exep) {
                    LogUtils.logThrowableReport(exep);
                }
            }
        }
    }

    private boolean isInvalidItem(final ItemStack itemStack) {
        for (final Item item : pickupItems) {
            if (item.isItemEqual(itemStack)) {
                return false;
            }
        }
        return true;
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
        return Integer.toString(amount);
    }

    @Override
    public String getProperty(final String name, final String playerID) {
        switch (name.toLowerCase()) {
            case "left":
                return Integer.toString(getPickupData(playerID).getAmount());
            case "amount":
                return Integer.toString(amount);
            default:
                return "";
        }
    }

    public static class PickupData extends ObjectiveData {

        private int amount;

        public PickupData(final String instruction, final String playerID, final String objID) {
            super(instruction, playerID, objID);
            amount = Integer.parseInt(instruction);
        }

        private void pickup() {
            amount--;
            update();
        }

        private boolean isFinished() {
            return amount <= 0;
        }

        private int getAmount() {
            return amount;
        }

        @Override
        public String toString() {
            return Integer.toString(amount);
        }

    }

    private PickupData getPickupData(final String playerID) {
        return (PickupData) dataMap.get(playerID);
    }
}
