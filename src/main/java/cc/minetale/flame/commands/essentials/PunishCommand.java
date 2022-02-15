package cc.minetale.flame.commands.essentials;

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

public class PunishCommand extends Command {

    public PunishCommand() {
        super("punish");

        setCondition(CommandUtil.getRankCondition(Rank.OWNER));

        setDefaultExecutor(this::defaultExecutor);

        var profile = ArgumentType.Word("player");

        addSyntax(this::onPunishCommand, profile);
    }

    private void defaultExecutor(CommandSender sender, CommandContext context) {
        sender.sendMessage(CommandUtil.getUsage("punish", "player"));
    }

    private void onPunishCommand(CommandSender sender, CommandContext context) {
        if (sender instanceof Player player) {
            FlamePlayer.getProfile((String) context.get("player"))
                    .thenAccept(target -> {
                        if (target != null) {
                            player.sendMessage(Component.text("Work in progress", NamedTextColor.RED));
//                            new PunishmentMenu(player, profile);
                        } else {
                            player.sendMessage(Message.parse(Language.Error.UNKNOWN_PLAYER));
                        }
                    });
        }
    }

}
