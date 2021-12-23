package cc.minetale.flame.commands.essentials;

import cc.minetale.commonlib.api.Rank;
import cc.minetale.commonlib.util.MC;
import cc.minetale.flame.Lang;
import cc.minetale.flame.util.CommandUtil;
import cc.minetale.flame.util.ProfileUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.entity.Player;

import java.util.concurrent.TimeUnit;

public class PunishCommand extends Command {

    public PunishCommand() {
        super("punish");

        setCondition(CommandUtil.getRankCondition(Rank.OWNER));
        setDefaultExecutor(this::defaultExecutor);

        var profile = ArgumentType.Word("profile");

        addSyntax(this::onPunishCommand, profile);
    }

    private void defaultExecutor(CommandSender sender, CommandContext context) {
        sender.sendMessage(MC.notificationMessage("Command",
                Component.text("Usage: /punish <player>", NamedTextColor.GRAY)));
    }

    private void onPunishCommand(CommandSender sender, CommandContext context) {
        if (sender instanceof Player player) {
            ProfileUtil.getProfile((String) context.get("profile"))
                    .orTimeout(5, TimeUnit.SECONDS)
                    .whenComplete((profile, throwable) -> {
                        if (profile != null) {
                            player.sendMessage(Component.text("Work in progress", NamedTextColor.RED));
//                            new PunishmentMenu(player, profile);
                        } else {
                            player.sendMessage(Lang.COULD_NOT_LOAD_PROFILE);
                        }
                    });
        }
    }

}
