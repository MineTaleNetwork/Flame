package cc.minetale.flame.commands.staff;

import cc.minetale.flame.menu.RanksMenu;
import cc.minetale.mlib.canvas.template.Menu;
import cc.minetale.sodium.profile.grant.Rank;
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
            Menu.openMenu(new RanksMenu(player));
        }
    }

}
