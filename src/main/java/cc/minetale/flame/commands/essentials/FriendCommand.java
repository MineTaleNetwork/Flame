package cc.minetale.flame.commands.essentials;

import cc.minetale.commonlib.cache.FriendCache;
import cc.minetale.commonlib.friend.FriendRequest;
import cc.minetale.commonlib.lang.Language;
import cc.minetale.commonlib.pigeon.payloads.friend.FriendRequestAcceptPayload;
import cc.minetale.commonlib.pigeon.payloads.friend.FriendRequestCreatePayload;
import cc.minetale.commonlib.util.Message;
import cc.minetale.commonlib.util.PigeonUtil;
import cc.minetale.flame.Lang;
import cc.minetale.flame.commands.essentials.friend.FriendListCommand;
import cc.minetale.flame.util.CommandUtil;
import cc.minetale.flame.util.FlamePlayer;
import cc.minetale.flame.util.SubCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.MinecraftServer;
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

public class FriendCommand extends Command {

    public FriendCommand() {
        super("friend", "f");

        setDefaultExecutor(this::defaultExecutor);

        addSubcommand(new AddCommand());
        addSubcommand(new AcceptCommand());
//        addSubcommand(new CancelCommand());
//        addSubcommand(new DenyCommand());
        addSubcommand(new RequestsCommand());
//        addSubcommand(new RemoveCommand());
        addSubcommand(new FriendListCommand());
    }

    private void defaultExecutor(CommandSender sender, CommandContext context) {
        sender.sendMessage(CommandUtil.getJoinedUsage("friend", "add", "accept", "cancel", "deny", "remove", "requests", "list"));
    }

    public static class AddCommand extends SubCommand {

        public AddCommand() {
            super("add");

            setDefaultExecutor(this::defaultExecutor);

            addSyntax(this::onFriendAddCommand, ArgumentType.Word("player"));
        }

        private void defaultExecutor(CommandSender sender, CommandContext context) {
            sender.sendMessage(CommandUtil.getUsage("friend add", "player"));
        }

        private void onFriendAddCommand(CommandSender sender, CommandContext context) {
            if (sender instanceof Player player) {
                FlamePlayer.getProfile((String) context.get("player"))
                        .thenAccept(target -> {
                            if (target != null) {
                                FriendRequest.addRequest(FlamePlayer.fromPlayer(player).getProfile(), target)
                                        .thenAccept(response -> {
                                            switch (response) {
                                                case SUCCESS -> {
                                                    var targetPlayer = MinecraftServer.getConnectionManager().getPlayer(target.getUuid());

                                                    if (targetPlayer != null) {
                                                        targetPlayer.sendMessage(Message.format(Language.Friend.Add.SUCCESS_TARGET, player.getUsername()));
                                                    } else {
                                                        PigeonUtil.broadcast(new FriendRequestCreatePayload(player.getUuid(), target.getUuid())); // TODO -> Handle Payload
                                                    }

                                                    sender.sendMessage(Message.format(Language.Friend.Add.SUCCESS_PLAYER, target.getUsername()));
                                                }
                                                case MAXIMUM_REQUESTS -> sender.sendMessage(Language.Friend.General.MAXIMUM_REQUESTS);
                                                case ERROR -> sender.sendMessage(Language.Error.COMMAND_ERROR);
                                                case REQUEST_EXIST -> sender.sendMessage(Language.Friend.Add.REQUEST_EXIST);
                                                case PENDING_REQUEST -> sender.sendMessage(Language.Friend.Add.PENDING_REQUEST);
                                                case TARGET_IGNORED -> sender.sendMessage(Language.Friend.General.TARGET_IGNORED);
                                                case REQUESTS_TOGGLED, PLAYER_IGNORED -> sender.sendMessage(Language.Friend.General.TARGET_TOGGLED);
                                            }
                                        });
                            } else {
                                sender.sendMessage(Lang.COULD_NOT_LOAD_PROFILE);
                            }
                        });
            }
        }
    }

    public static class AcceptCommand extends SubCommand {

        public AcceptCommand() {
            super("accept");

            setDefaultExecutor(this::defaultExecutor);

            addSyntax(this::onFriendAcceptCommand, ArgumentType.Word("player"));
        }

        private void defaultExecutor(CommandSender sender, CommandContext context) {
            sender.sendMessage(CommandUtil.getUsage("friend add", "player"));
        }

        private void onFriendAcceptCommand(CommandSender sender, CommandContext context) {
            if (sender instanceof Player player) {
                var profile = FlamePlayer.fromPlayer(player).getProfile();

                FlamePlayer.getProfile((String) context.get("player"))
                        .thenAccept(target -> {
                            if (target != null) {
                                FriendRequest.acceptRequest(profile, target)
                                        .thenAccept(response -> {
                                            switch (response) {
                                                case ERROR -> sender.sendMessage(Language.Error.COMMAND_ERROR);
                                                case SUCCESS -> {
                                                    var targetPlayer = MinecraftServer.getConnectionManager().getPlayer(target.getUuid());

                                                    if (targetPlayer != null) {
                                                        targetPlayer.sendMessage(Message.format(Language.Friend.Accept.SUCCESS, player.getUsername()));
                                                    } else {
                                                        PigeonUtil.broadcast(new FriendRequestAcceptPayload(player.getUuid(), target.getUuid()));  // TODO -> Handle Payload
                                                    }

                                                    sender.sendMessage(Message.format(Language.Friend.Accept.SUCCESS, target.getUsername()));
                                                }
                                                case PLAYER_MAXIMUM_FRIENDS -> sender.sendMessage(Language.Friend.General.PLAYER_MAXIMUM_FRIENDS);
                                                case TARGET_MAXIMUM_FRIENDS -> sender.sendMessage(Language.Friend.General.TARGET_MAXIMUM_FRIENDS);
                                                case NO_REQUEST -> sender.sendMessage(Language.Friend.Accept.NO_REQUEST);
                                                case TARGET_IGNORED -> sender.sendMessage(Language.Friend.General.TARGET_IGNORED);
                                                case PLAYER_IGNORED -> sender.sendMessage(Language.Friend.General.TARGET_TOGGLED);
                                            }
                                        });
                            } else {
                                sender.sendMessage(Lang.COULD_NOT_LOAD_PROFILE);
                            }
                        });
            }
        }
    }

    public static class RequestsCommand extends SubCommand {

        public RequestsCommand() {
            super("requests");

            var typeArgument = ArgumentType.Enum("type", RequestType.class).setFormat(ArgumentEnum.Format.LOWER_CASED);

            addSyntax(this::onRequests, typeArgument);
            addSyntax(this::onRequests, typeArgument, ArgumentType.Integer("page"));

            setDefaultExecutor(this::defaultExecutor);
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

}
