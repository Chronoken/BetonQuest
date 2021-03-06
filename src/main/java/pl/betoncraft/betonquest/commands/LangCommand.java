package pl.betoncraft.betonquest.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.Journal;
import pl.betoncraft.betonquest.config.Config;
import pl.betoncraft.betonquest.database.PlayerData;
import pl.betoncraft.betonquest.exceptions.QuestRuntimeException;
import pl.betoncraft.betonquest.utils.LogUtils;
import pl.betoncraft.betonquest.utils.PlayerConverter;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * Changes the default language for the player
 */
public class LangCommand implements CommandExecutor, SimpleTabCompleter {

    public LangCommand() {
        BetonQuest.getInstance().getCommand("questlang").setExecutor(this);
        BetonQuest.getInstance().getCommand("questlang").setTabCompleter(this);
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {
        if (cmd.getName().equalsIgnoreCase("questlang")) {
            if (args.length < 1) {
                sender.sendMessage(Config.getMessage(Config.getLanguage(), "language_missing"));
                return true;
            }
            if (!Config.getLanguages().contains(args[0]) && !args[0].equalsIgnoreCase("default")) {
                final StringBuilder builder = new StringBuilder();
                builder.append("default (").append(Config.getLanguage()).append("), ");
                for (final String lang : Config.getLanguages()) {
                    builder.append(lang).append(", ");
                }
                if (builder.length() < 3) {
                    LogUtils.getLogger().log(Level.WARNING, "No translations loaded, somethings wrong!");
                    return false;
                }
                final String finalMessage = builder.substring(0, builder.length() - 2) + ".";
                sender.sendMessage(Config.getMessage(Config.getLanguage(), "language_not_exist") + finalMessage);
                return true;
            }
            if (sender instanceof Player) {
                final String lang = args[0];
                final String playerID = PlayerConverter.getID((Player) sender);
                final PlayerData playerData = BetonQuest.getInstance().getPlayerData(playerID);
                final Journal journal = playerData.getJournal();
                int slot = -1;
                if (Journal.hasJournal(playerID)) {
                    slot = journal.removeFromInv();
                }
                playerData.setLanguage(lang);
                if (slot > 0) {
                    journal.addToInv(slot);
                }
                try {
                    Config.sendNotify(null, playerID, "language_changed", new String[]{lang}, "language_changed,info");
                } catch (QuestRuntimeException exception) {
                    LogUtils.getLogger().log(Level.WARNING, "The notify system was unable to play a sound for the 'language_changed' category. Error was: '" + exception.getMessage() + "'");
                    LogUtils.logThrowableIgnore(exception);
                }

            } else {
                BetonQuest.getInstance().getConfig().set("language", args[0]);
                sender.sendMessage(Config.getMessage(args[0], "default_language_changed"));
            }
            return true;
        }
        return false;
    }

    @Override
    public List<String> simpleTabComplete(final CommandSender sender, final Command command, final String alias, final String[] args) {
        if (args.length == 1) {
            return Config.getLanguages();
        }
        return new ArrayList<>();
    }
}
