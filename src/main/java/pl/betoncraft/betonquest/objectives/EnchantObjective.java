package pl.betoncraft.betonquest.objectives;

import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.api.Objective;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.item.QuestItem;
import pl.betoncraft.betonquest.utils.PlayerConverter;

import java.util.List;
import java.util.Locale;

/**
 * Requires the player to enchant an item.
 */
public class EnchantObjective extends Objective implements Listener {

    private final QuestItem item;
    private final List<EnchantmentData> enchantments;

    public EnchantObjective(final Instruction instruction) throws InstructionParseException {
        super(instruction);
        template = ObjectiveData.class;
        item = instruction.getQuestItem();
        enchantments = instruction.getList(EnchantmentData::convert);
        if (enchantments.isEmpty()) {
            throw new InstructionParseException("Not enough arguments");
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onEnchant(final EnchantItemEvent event) {
        final String playerID = PlayerConverter.getID(event.getEnchanter());
        if (!containsPlayer(playerID)) {
            return;
        }
        if (!item.compare(event.getItem())) {
            return;
        }
        for (final EnchantmentData enchant : enchantments) {
            if (!event.getEnchantsToAdd().keySet().contains(enchant.getEnchantment())
                    || event.getEnchantsToAdd().get(enchant.getEnchantment()) < enchant.getLevel()) {
                return;
            }
        }
        if (checkConditions(playerID)) {
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

    public static class EnchantmentData {

        private final Enchantment enchantment;
        private final int level;

        public EnchantmentData(final Enchantment enchantment, final int level) {
            this.enchantment = enchantment;
            this.level = level;
        }

        @SuppressWarnings("deprecation")
        public static EnchantmentData convert(final String string) throws InstructionParseException {
            final String[] parts = string.split(":");
            if (parts.length != 2) {
                throw new InstructionParseException("Could not parse enchantment: " + string);
            }
            final Enchantment enchantment = Enchantment.getByName(parts[0].toUpperCase(Locale.ROOT));
            if (enchantment == null) {
                throw new InstructionParseException("Enchantment type '" + parts[0] + "' does not exist");
            }
            final int level;
            try {
                level = Integer.parseInt(parts[1]);
            } catch (NumberFormatException e) {
                throw new InstructionParseException("Could not parse enchantment level: " + string, e);
            }
            return new EnchantmentData(enchantment, level);
        }

        public Enchantment getEnchantment() {
            return enchantment;
        }

        public int getLevel() {
            return level;
        }
    }

}
