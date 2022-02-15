package cc.minetale.flame.commands.staff;

import cc.minetale.commonlib.grant.Rank;
import cc.minetale.commonlib.lang.Language;
import cc.minetale.commonlib.util.Message;
import cc.minetale.flame.util.CommandUtil;
import cc.minetale.flame.util.FlamePlayer;
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
        sender.sendMessage(Message.notification("Command",
                Component.text("Usage: /grant <player>", NamedTextColor.GRAY))
        );
    }

    private void onGrantCommand(CommandSender sender, CommandContext context) {
        if (sender instanceof Player player) {
            FlamePlayer.getProfile((String) context.get("player"))
                    .thenAccept(profile -> {
                        if (profile != null) {
//                            new GrantRankMenu(player, profile);
                        } else {
                            player.sendMessage(Message.parse(Language.Error.UNKNOWN_PLAYER));
                        }
                    });
        }
    }
}
