package pl.betoncraft.betonquest.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.config.Config;
import pl.betoncraft.betonquest.utils.PlayerConverter;

/**
 * Gives the player his journal
 */
public class JournalCommand implements CommandExecutor {

    /**
     * Registers a new executor of the /journal command
     */
    public JournalCommand() {
        BetonQuest.getInstance().getCommand("journal").setExecutor(this);
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {
        if (cmd.getName().equalsIgnoreCase("journal")) {
            // command sender must be a player, console can't have journal
            if (sender instanceof Player) {
                // giving the player his journal
                BetonQuest.getInstance().getPlayerData(PlayerConverter.getID((Player) sender)).getJournal()
                        .addToInv(Integer.parseInt(Config.getString("config.default_journal_slot")));
            }
            return true;
        }
        return false;
    }

}
