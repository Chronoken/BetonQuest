package pl.betoncraft.betonquest.compatibility.mmogroup.mmoitems;

import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.api.player.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.VariableNumber;
import pl.betoncraft.betonquest.api.QuestEvent;
import pl.betoncraft.betonquest.config.Config;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.exceptions.QuestRuntimeException;
import pl.betoncraft.betonquest.utils.LogUtils;
import pl.betoncraft.betonquest.utils.PlayerConverter;

import java.util.HashMap;
import java.util.logging.Level;

public class MMOItemsGiveEvent extends QuestEvent {

    private final MMOItems mmoPlugin = MMOItems.plugin;

    private final Type itemType;
    private final String itemID;
    private VariableNumber amountVar = new VariableNumber(1);
    private final boolean scale;
    private final boolean notify;
    private final boolean singleStack;

    public MMOItemsGiveEvent(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);

        itemType = mmoPlugin.getTypes().get(instruction.next());
        itemID = instruction.next();

        if (instruction.getInstruction().contains("%") || !instruction.getAllNumbers().isEmpty()) {
            amountVar = instruction.getVarNum();
        }

        scale = instruction.hasArgument("scale");
        singleStack = instruction.hasArgument("singleStack");
        notify = instruction.hasArgument("notify");
    }

    @SuppressWarnings("PMD.PreserveStackTrace")
    @Override
    protected Void execute(final String playerID) throws QuestRuntimeException {
        final Player player = PlayerConverter.getPlayer(playerID);
        int amount = amountVar.getInt(playerID);

        final ItemStack mmoItem;
        if (scale) {
            mmoItem = mmoPlugin.getItem(itemType, itemID, PlayerData.get(player.getUniqueId()));
        } else {
            mmoItem = mmoPlugin.getItem(itemType, itemID);
        }

        if (notify) {
            try {
                Config.sendNotify(instruction.getPackage().getName(), playerID, "items_given",
                        new String[]{mmoItem.getItemMeta().getDisplayName(), String.valueOf(amount)},
                        "items_given,info");
            } catch (final QuestRuntimeException exception) {
                try {
                    LogUtils.getLogger().log(Level.WARNING, "The notify system was unable to play a sound for the 'items_given' category in '" + instruction.getEvent().getFullID() + "'. Error was: '" + exception.getMessage() + "'");
                } catch (final InstructionParseException exep) {
                    throw new QuestRuntimeException(exep);
                }
            }
        }

        while (amount > 0) {

            final int stackSize;
            if (singleStack) {
                stackSize = Math.min(amount, 64);
            } else {
                stackSize = 1;
            }

            mmoItem.setAmount(stackSize);
            final HashMap<Integer, ItemStack> left = player.getInventory().addItem(mmoItem);
            for (final Integer leftNumber : left.keySet()) {
                final ItemStack itemStack = left.get(leftNumber);
                player.getWorld().dropItem(player.getLocation(), itemStack);
            }
            amount -= stackSize;
        }
        return null;
    }
}
