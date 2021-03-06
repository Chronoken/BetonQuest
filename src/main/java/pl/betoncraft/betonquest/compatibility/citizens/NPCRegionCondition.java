package pl.betoncraft.betonquest.compatibility.citizens;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.internal.platform.WorldGuardPlatform;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.api.Condition;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.exceptions.QuestRuntimeException;
import pl.betoncraft.betonquest.utils.PlayerConverter;

/**
 * Checks if a npc is inside a region
 * <p>
 * Created on 01.10.2018.
 */
public class NPCRegionCondition extends Condition {

    private final int npcId;
    private final String region;

    public NPCRegionCondition(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);
        super.persistent = true;
        super.staticness = true;
        npcId = instruction.getInt();
        if (npcId < 0) {
            throw new InstructionParseException("NPC ID cannot be less than 0");
        }
        region = instruction.next();
    }

    @Override
    protected Boolean execute(final String playerID) throws QuestRuntimeException {
        final NPC npc = CitizensAPI.getNPCRegistry().getById(npcId);
        if (npc == null) {
            throw new QuestRuntimeException("NPC with ID " + npcId + " does not exist");
        }
        final Entity npcEntity = npc.getEntity();
        if (npcEntity == null) {
            return false;
        }

        final Player player = PlayerConverter.getPlayer(playerID);
        final WorldGuardPlatform worldguardPlatform = WorldGuard.getInstance().getPlatform();
        final RegionManager manager = worldguardPlatform.getRegionContainer().get(BukkitAdapter.adapt(player.getWorld()));
        if (manager == null) {
            return false;
        }
        final ApplicableRegionSet set = manager.getApplicableRegions(BlockVector3.at(player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ()));

        for (final ProtectedRegion compare : set) {
            if (compare.equals(region)) {
                return true;
            }
        }
        return false;
    }
}
