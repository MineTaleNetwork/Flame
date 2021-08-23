package cc.minetale.flame.commands.essentials;

import cc.minetale.flame.commands.RankUtil;
import cc.minetale.commonlib.modules.rank.Rank;
import cc.minetale.flame.menu.ViewRanksMenu;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.condition.Conditions;
import net.minestom.server.entity.Player;

public class TestMenuCommand extends Command {

    public TestMenuCommand() {
        super("devmenu");
        setCondition(Conditions::playerOnly);

        setDefaultExecutor(this::defaultExecutor);
    }

    private void defaultExecutor(CommandSender sender, CommandContext context) {
        Player player = sender.asPlayer();

        RankUtil.canUseCommand(player, Rank.getRank("Owner"), commandCallback -> {
            if (!commandCallback.isMinimum())
                return;

            System.out.println("Opening...");

            new ViewRanksMenu(player);
        });
    }

}
