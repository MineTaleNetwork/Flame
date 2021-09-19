package cc.minetale.flame.commands.staff;

import cc.minetale.commonlib.modules.profile.Profile;
import cc.minetale.commonlib.util.MC;
import cc.minetale.flame.arguments.ArgumentProfile;
import cc.minetale.flame.arguments.ArgumentRank;
import net.kyori.adventure.text.Component;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.exception.ArgumentSyntaxException;

import java.util.concurrent.CompletableFuture;

public class AddGrantCommand extends Command {

    public AddGrantCommand() {
        super("addgrant");

        setDefaultExecutor(this::defaultExecutor);

        var profile = new ArgumentProfile("profile");

        var rank = new ArgumentRank("rank");

        setArgumentCallback(this::onRankError, rank);

        addSyntax(this::addGrantExecutor, profile, rank);
    }

    private void defaultExecutor(CommandSender sender, CommandContext context) {
        sender.sendMessage(MC.Chat.notificationMessage("Add Grant", Component.text("Usage: /addgrant <player> <rank> <duration> <reason>", MC.CC.GRAY.getTextColor())));
    }

    public void onRankError(CommandSender sender, ArgumentSyntaxException exception) {
        sender.sendMessage(MC.Chat.notificationMessage("Add Grant", Component.text("A rank with that name could not be found.", MC.CC.GRAY.getTextColor())));
    }

    private void addGrantExecutor(CommandSender sender, CommandContext context) {
        CompletableFuture<Profile> profileFuture = context.get("profile");

        profileFuture.thenAccept(profile -> {
            System.out.println(profile.getName() + " has been loaded!");
        });
    }

}
