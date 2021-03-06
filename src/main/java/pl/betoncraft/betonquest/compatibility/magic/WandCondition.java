package pl.betoncraft.betonquest.compatibility.magic;

import com.elmakers.mine.bukkit.api.magic.MagicAPI;
import com.elmakers.mine.bukkit.api.wand.LostWand;
import com.elmakers.mine.bukkit.api.wand.Wand;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.VariableNumber;
import pl.betoncraft.betonquest.api.Condition;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.exceptions.QuestRuntimeException;
import pl.betoncraft.betonquest.utils.PlayerConverter;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Checks if the player is holding a wand.
 */
public class WandCondition extends Condition {

    private final MagicAPI api;
    private final CheckType type;
    private final Map<String, VariableNumber> spells = new HashMap<>();
    private final String name;
    private final VariableNumber amount;

    public WandCondition(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);
        final String string = instruction.next();
        switch (string) {
            case "hand":
                type = CheckType.IN_HAND;
                break;
            case "inventory":
                type = CheckType.IN_INVENTORY;
                break;
            case "lost":
                type = CheckType.IS_LOST;
                break;
            default:
                throw new InstructionParseException("Unknown check type '" + string + "'");
        }
        final String[] array = instruction.getArray(instruction.getOptional("spells"));
        if (array != null) {
            for (final String spell : array) {
                VariableNumber level = new VariableNumber(1);
                if (spell.contains(":")) {
                    final String[] spellParts = spell.split(":");
                    try {
                        level = new VariableNumber(instruction.getPackage().getName(), spellParts[1]);
                    } catch (InstructionParseException e) {
                        throw new InstructionParseException("Could not parse spell level", e);
                    }
                    this.spells.put(spellParts[0], level);
                } else {
                    throw new InstructionParseException("Incorrect spell format");
                }
            }
        }
        name = instruction.getOptional("name");
        api = (MagicAPI) Bukkit.getPluginManager().getPlugin("Magic");
        amount = instruction.getVarNum(instruction.getOptional("amount"));
    }

    @Override
    protected Boolean execute(final String playerID) throws QuestRuntimeException {
        final Player player = PlayerConverter.getPlayer(playerID);
        int heldAmount;

        switch (type) {
            case IS_LOST:
                for (final LostWand lost : api.getLostWands()) {
                    final Player owner = Bukkit.getPlayer(UUID.fromString(lost.getOwnerId()));
                    if (owner == null) {
                        continue;
                    }
                    if (owner.equals(player)) {
                        return true;
                    }
                }
                return false;
            case IN_HAND:
                ItemStack wandItem = null;
                wandItem = player.getInventory().getItemInMainHand();
                if (!api.isWand(wandItem)) {
                    return false;
                }
                final Wand wand1 = api.getWand(wandItem);
                return checkWand(wand1, playerID);
            case IN_INVENTORY:
                heldAmount = 0;
                for (final ItemStack item : player.getInventory().getContents()) {
                    if (item == null) {
                        continue;
                    }
                    if (api.isWand(item)) {
                        final Wand wand2 = api.getWand(item);
                        if (checkWand(wand2, playerID)) {
                            heldAmount += item.getAmount();
                            if (amount == null || heldAmount >= amount.getInt(playerID)) {
                                return true;
                            }
                        }
                    }
                }
                return false;
            default:
                return false;
        }
    }

    /**
     * Checks if the given wand meets specified name and spells conditions.
     *
     * @param wand wand to check
     * @return true if the wand meets the conditions, false otherwise
     * @throws QuestRuntimeException
     */
    private boolean checkWand(final Wand wand, final String playerID) throws QuestRuntimeException {
        if (name != null && !wand.getTemplateKey().equalsIgnoreCase(name)) {
            return false;
        }
        if (!spells.isEmpty()) {
            spell:
            for (final String spell : spells.keySet()) {
                final int level = spells.get(spell).getInt(playerID);
                for (final String wandSpell : wand.getSpells()) {
                    if (wandSpell.toLowerCase().startsWith(spell.toLowerCase()) && wand.getSpellLevel(spell) >= level) {
                        continue spell;
                    }
                }
                return false;
            }
        }
        return true;
    }

    private enum CheckType {
        IS_LOST, IN_HAND, IN_INVENTORY
    }

}
