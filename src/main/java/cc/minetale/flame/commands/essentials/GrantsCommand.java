package cc.minetale.flame.commands.essentials;

import cc.minetale.commonlib.grant.Rank;
import cc.minetale.commonlib.lang.Language;
import cc.minetale.commonlib.util.Message;
import cc.minetale.flame.util.CommandUtil;
import cc.minetale.flame.util.FlamePlayer;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.entity.Player;

public class GrantsCommand extends Command {

    public GrantsCommand() {
        super("grants");

        setCondition(CommandUtil.getRankCondition(Rank.ADMIN));

        setDefaultExecutor(this::defaultExecutor);

        var profile = ArgumentType.Word("player");

        addSyntax(this::onGrantsCommand, profile);
    }

    private void defaultExecutor(CommandSender sender, CommandContext context) {
        sender.sendMessage(CommandUtil.getUsage("grants", "player"));
    }

    private void onGrantsCommand(CommandSender sender, CommandContext context) {
        if (sender instanceof Player player) {
            FlamePlayer.getProfile((String) context.get("player"))
                    .thenAccept(profile -> {
                        if (profile != null) {
//                            new GrantsMenu(player, profile);
                        } else {
                            player.sendMessage(Message.parse(Language.Error.UNKNOWN_PLAYER));
                        }
                    });
        }
    }

}
