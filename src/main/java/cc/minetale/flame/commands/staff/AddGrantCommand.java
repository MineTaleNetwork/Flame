package cc.minetale.flame.commands.staff;

import cc.minetale.flame.util.CommandUtil;
import cc.minetale.flame.util.FlamePlayer;
import cc.minetale.postman.StringUtil;
import cc.minetale.sodium.lang.Language;
import cc.minetale.sodium.profile.grant.Grant;
import cc.minetale.sodium.profile.grant.Rank;
import cc.minetale.sodium.util.Duration;
import cc.minetale.sodium.util.Message;
import cc.minetale.sodium.util.TimeUtil;
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

        var profile = ArgumentType.Word("player");
        var rank = ArgumentType.Enum("rank", Rank.class);
        var duration = ArgumentType.Word("duration");
        var reason = ArgumentType.StringArray("reason");

        setArgumentCallback(CommandUtil::callbackError, rank);

        addSyntax(this::addGrantExecutor, profile, rank, duration, reason);
    }

    private void defaultExecutor(CommandSender sender, CommandContext context) {
        sender.sendMessage(CommandUtil.getUsage("grants", "player", "rank", "duration", "reason"));
    }

    private void addGrantExecutor(CommandSender sender, CommandContext context) {
        var targetName = (String) context.get("player");
        var profile = FlamePlayer.getProfile(targetName);

        if(profile == null) {
            sender.sendMessage(Message.parse(Language.Error.UNKNOWN_PLAYER));
            return;
        }

        Rank rank = context.get("rank");
        long duration = Duration.fromString(context.get("duration")).value();
        String[] reason = context.get("reason");

        if (duration == -1) {
            sender.sendMessage(Message.notification("Command",
                    Component.text("You have provided an invalid duration", NamedTextColor.GRAY)));
            return;
        }

        var grant = new Grant(
                StringUtil.generateId(),
                profile.getUuid(),
                (sender instanceof Player player ? player.getUuid() : null),
                System.currentTimeMillis(),
                String.join(" ", reason),
                duration,
                rank
        );

        profile.issueGrant(grant);

        sender.sendMessage(Message.notification("Grant",
                Component.text("Granted " + profile.getUsername() + " " + rank.getName() + " rank " + (duration == Integer.MAX_VALUE ? "permanently" : "for " + TimeUtil.millisToRoundedTime(duration)), NamedTextColor.GRAY)
        ));

//        var player = MinecraftServer.getConnectionManager().getPlayer(profile.getUuid());
//
//        if(player != null) {
//            var flamePlayer = FlamePlayer.fromPlayer(player);
//
//            flamePlayer.refreshPlayer();
//            PostmanListener.addGrant(player, grant);
//        }

    }

}
