package cc.minetale.flame.commands.staff;

import cc.minetale.flame.menu.grant.GrantRankMenu;
import cc.minetale.flame.util.CommandUtil;
import cc.minetale.flame.util.FlamePlayer;
import cc.minetale.mlib.canvas.template.Menu;
import cc.minetale.sodium.lang.Language;
import cc.minetale.sodium.profile.grant.Rank;
import cc.minetale.sodium.util.Message;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.entity.Player;

public class GrantCommand extends Command {

    public GrantCommand() {
        super("grant");

        setCondition(CommandUtil.getRankCondition(Rank.OWNER));

        setDefaultExecutor(this::defaultExecutor);

        var profile = ArgumentType.Word("player");

        addSyntax(this::onGrantCommand, profile);
    }

    private void defaultExecutor(CommandSender sender, CommandContext context) {
        sender.sendMessage(CommandUtil.getUsage("grant", "player"));
    }

    private void onGrantCommand(CommandSender sender, CommandContext context) {
        if (sender instanceof Player player) {
            var profile = FlamePlayer.getProfile((String) context.get("player"));

            if(profile == null) {
                player.sendMessage(Message.parse(Language.Error.UNKNOWN_PLAYER));
                return;
            }

            Menu.openMenu(new GrantRankMenu(player, profile));
        }
    }
}
