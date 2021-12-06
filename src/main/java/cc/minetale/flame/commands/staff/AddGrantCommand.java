package cc.minetale.flame.commands.staff;

import cc.minetale.commonlib.api.Grant;
import cc.minetale.commonlib.api.Rank;
import cc.minetale.commonlib.profile.Profile;
import cc.minetale.commonlib.util.Duration;
import cc.minetale.commonlib.util.MC;
import cc.minetale.flame.arguments.ArgumentDuration;
import cc.minetale.flame.arguments.ArgumentProfile;
import cc.minetale.flame.util.CommandUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.arguments.ArgumentStringArray;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.command.builder.exception.ArgumentSyntaxException;
import net.minestom.server.entity.Player;

import java.util.concurrent.CompletableFuture;

public class AddGrantCommand extends Command {

    public AddGrantCommand() {
        super("addgrant");

        setCondition(CommandUtil.getRankCondition(Rank.OWNER));

        setDefaultExecutor(this::defaultExecutor);

        var profile = new ArgumentProfile("profile");
        var rank = ArgumentType.Enum("rank", Rank.class);
        var duration = new ArgumentDuration("duration");
        var reason = new ArgumentStringArray("reason");

        setArgumentCallback(this::onRankError, rank);
        setArgumentCallback(this::onDurationError, duration);

        addSyntax(this::addGrantExecutor, profile, rank, duration, reason);
    }

    private void defaultExecutor(CommandSender sender, CommandContext context) {
        sender.sendMessage(MC.notificationMessage("Add Grant", Component.text("Usage: /addgrant <player> <rank> <duration> <reason>", NamedTextColor.GRAY)));
    }

    public void onRankError(CommandSender sender, ArgumentSyntaxException exception) {
        sender.sendMessage(MC.notificationMessage("Add Grant", Component.text("A rank with that name could not be found.", NamedTextColor.GRAY)));
    }

    public void onDurationError(CommandSender sender, ArgumentSyntaxException exception) {
        sender.sendMessage(MC.notificationMessage("Add Grant", Component.text("The provided duration is invalid.", NamedTextColor.GRAY)));
    }

    private void addGrantExecutor(CommandSender sender, CommandContext context) {
        CompletableFuture<Profile> profileFuture = context.get("profile");
        Rank rank = context.get("rank");
        Duration duration = context.get("duration");
        String[] reason = context.get("reason");

        profileFuture.thenAccept(profile -> {
            profile.addGrant(new Grant(
                    profile.getId(),
                    rank,
                    (sender instanceof Player player ? player.getUuid() : null),
                    System.currentTimeMillis(),
                    String.join(" ", reason),
                    duration.getValue()
            ));
        });
    }

}
