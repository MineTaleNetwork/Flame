package cc.minetale.flame.commands.essentials.friend;

import cc.minetale.commonlib.cache.FriendCache;
import cc.minetale.commonlib.friend.FriendRequest;
import cc.minetale.commonlib.util.Message;
import cc.minetale.flame.util.CommandUtil;
import cc.minetale.flame.util.SubCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.arguments.ArgumentEnum;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@SubCommand
public class FriendRequestsCommand extends Command {

    public FriendRequestsCommand() {
        super("requests");

        setDefaultExecutor(this::defaultExecutor);

        var typeArgument = ArgumentType.Enum("type", RequestType.class).setFormat(ArgumentEnum.Format.LOWER_CASED);

        addSyntax(this::onRequests, typeArgument);
        addSyntax(this::onRequests, typeArgument, ArgumentType.Integer("page"));
    }

    private void defaultExecutor(CommandSender sender, CommandContext context) {
        sender.sendMessage(CommandUtil.getJoinedUsage("friend requests", "outgoing", "incoming"));
    }

    private void onRequests(CommandSender sender, CommandContext context) {
        if (sender instanceof Player player) {
            CompletableFuture.runAsync(() -> {
                try {
                    var friendRequests = new HashSet<FriendRequest>();

                    var messages = new ArrayList<>(Arrays.asList(
                            Message.chatSeparator()
                    ));

                    var type = (RequestType) context.get("type");

                    switch (type) {
                        case INCOMING -> {
                            messages.add(Component.text(""));
                            friendRequests.addAll(FriendCache.getIncomingRequests(player.getUuid()).get());
                        }
                        case OUTGOING -> friendRequests.addAll(FriendCache.getOutgoingRequests(player.getUuid()).get());
                    }

                    player.sendMessage(Component.text("Incoming Requests:", NamedTextColor.GOLD));

//                        for (var uuid : incoming) {
//                            player.sendMessage(uuid.toString());
//                        }

                    player.sendMessage(Component.text("Outgoing Requests:", NamedTextColor.GOLD));

//                        for (var uuid : outgoing) {
//                            player.sendMessage(uuid.toString());
//                        }

                } catch (InterruptedException | ExecutionException exception) {
                    exception.printStackTrace();
                }
            });
        }
    }

    public enum RequestType {
        INCOMING,
        OUTGOING
    }

}
