package cc.minetale.flame.commands.staff;

import cc.minetale.commonlib.api.Grant;
import cc.minetale.commonlib.api.Rank;
import cc.minetale.commonlib.util.Duration;
import cc.minetale.commonlib.util.MC;
import cc.minetale.commonlib.util.TimeUtil;
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

public class AddGrantCommand extends Command {

    public AddGrantCommand() {
        super("addgrant");

        setCondition(CommandUtil.getRankCondition(Rank.OWNER));
        setDefaultExecutor(this::defaultExecutor);

        var profile = ArgumentType.Word("profile");
        var rank = ArgumentType.Enum("rank", Rank.class);
        var duration = ArgumentType.Word("duration");
        var reason = ArgumentType.StringArray("reason");

        setArgumentCallback(CommandUtil::callbackError, rank);

        addSyntax(this::addGrantExecutor, profile, rank, duration, reason);
    }

    private void defaultExecutor(CommandSender sender, CommandContext context) {
        sender.sendMessage(MC.notificationMessage("Command",
                Component.text("Usage: /addgrant <player> <rank> <duration> <reason>", NamedTextColor.GRAY)));
    }

    private void addGrantExecutor(CommandSender sender, CommandContext context) {
        ProfileUtil.getProfile((String) context.get("profile"))
                .orTimeout(5, TimeUnit.SECONDS)
                .whenComplete((profile, throwable) -> {
                    if (profile != null) {
                        Rank rank = context.get("rank");
                        long duration = Duration.fromString(context.get("duration")).getValue();
                        String[] reason = context.get("reason");

                        if (duration == -1) {
                            sender.sendMessage(MC.notificationMessage("Command",
                                    Component.text("You have provided an invalid duration", NamedTextColor.GRAY)));
                            return;
                        }

                        profile.addGrant(new Grant(
                                profile.getId(),
                                rank,
                                (sender instanceof Player player ? player.getUuid() : null),
                                System.currentTimeMillis(),
                                String.join(" ", reason),
                                duration
                        ));

                        sender.sendMessage(MC.notificationMessage("Grant",
                                Component.text("Granted " + profile.getName() + " " + rank.getName() + " rank " + (duration == Integer.MAX_VALUE ? "permanently" : "for " + TimeUtil.millisToRoundedTime(duration)), NamedTextColor.GRAY)
                        ));
                    } else {
                        sender.sendMessage(Lang.COULD_NOT_LOAD_PROFILE);
                    }
                });
    }

}
