package cc.minetale.flame.commands.staff;

import cc.minetale.commonlib.grant.Rank;
import cc.minetale.flame.util.CommandUtil;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.entity.Player;

public class RanksCommand extends Command {

    public RanksCommand() {
        super("ranks");

        setCondition(CommandUtil.getRankCondition(Rank.OWNER));

        setDefaultExecutor(this::defaultExecutor);
    }

    private void defaultExecutor(CommandSender sender, CommandContext context) {
        if(sender instanceof Player player) {
//            new RanksMenu(player);
        }
    }

}
