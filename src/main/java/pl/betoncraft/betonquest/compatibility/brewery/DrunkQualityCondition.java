package pl.betoncraft.betonquest.compatibility.brewery;

import com.dre.brewery.BPlayer;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.api.Condition;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.exceptions.QuestRuntimeException;
import pl.betoncraft.betonquest.utils.PlayerConverter;

public class DrunkQualityCondition extends Condition {

    private final Integer quality;

    public DrunkQualityCondition(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);

        quality = instruction.getInt();

        if (quality < 1 || quality > 10) {
            throw new InstructionParseException("Drunk quality can only be between 1 and 10!");
        }
    }

    @Override
    protected Boolean execute(final String playerID) throws QuestRuntimeException {
        final BPlayer bPlayer = BPlayer.get(PlayerConverter.getPlayer(playerID));
        return bPlayer.getQuality() >= quality;
    }
}
