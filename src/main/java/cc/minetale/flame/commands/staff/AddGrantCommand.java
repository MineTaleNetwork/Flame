package cc.minetale.flame.commands.staff;

import cc.minetale.commonlib.grant.Grant;
import cc.minetale.commonlib.grant.Rank;
import cc.minetale.commonlib.util.Duration;
import cc.minetale.commonlib.util.Message;
import cc.minetale.commonlib.util.TimeUtil;
import cc.minetale.flame.Lang;
import cc.minetale.flame.util.CommandUtil;
import cc.minetale.flame.util.FlamePlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.entity.Player;

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
        sender.sendMessage(Message.message("Command",
                Component.text("Usage: /addgrant <player> <rank> <duration> <reason>", NamedTextColor.GRAY))
        );
    }

    private void addGrantExecutor(CommandSender sender, CommandContext context) {
        FlamePlayer.getProfile((String) context.get("profile"))
                .thenAccept(profile -> {
                    if (profile != null) {
                        Rank rank = context.get("rank");
                        long duration = Duration.fromString(context.get("duration")).value();
                        String[] reason = context.get("reason");

                        if (duration == -1) {
                            sender.sendMessage(Message.message("Command",
                                    Component.text("You have provided an invalid duration", NamedTextColor.GRAY)));
                            return;
                        }

                        profile.issueGrant(new Grant(
                                null,
                                profile.getUuid(),
                                (sender instanceof Player player ? player.getUuid() : null),
                                System.currentTimeMillis(),
                                String.join(" ", reason),
                                duration,
                                rank
                        ));

                        sender.sendMessage(Message.message("Grant",
                                Component.text("Granted " + profile.getName() + " " + rank.getName() + " rank " + (duration == Integer.MAX_VALUE ? "permanently" : "for " + TimeUtil.millisToRoundedTime(duration)), NamedTextColor.GRAY)
                        ));
                    } else {
                        sender.sendMessage(Lang.COULD_NOT_LOAD_PROFILE);
                    }
                });
    }

}
