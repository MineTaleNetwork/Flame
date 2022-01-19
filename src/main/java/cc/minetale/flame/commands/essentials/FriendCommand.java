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
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.entity.Player;

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

    public static class AddCommand extends Command {

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
                FriendCache.getOutgoingRequests(player.getUuid())
                        .thenAccept(friendRequests -> {
                            if (friendRequests.size() >= 100) {
                                sender.sendMessage(Language.Friend.General.MAXIMUM_REQUESTS);
                                return;
                            }

                            var profile = FlamePlayer.fromPlayer(player).getProfile();

                            if (profile.getFriends().size() >= 100) {
                                sender.sendMessage(Language.Friend.General.MAXIMUM_FRIENDS);
                                return;
                            }

                            FlamePlayer.getProfile((String) context.get("player"))
                                    .thenAccept(target -> {
                                        if (target != null) {
                                            FriendRequest.addRequest(profile, target)
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
                        });
            }
        }
    }

    public static class AcceptCommand extends Command {

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

                if (profile.getFriends().size() >= 100) {
                    sender.sendMessage(Language.Friend.General.MAXIMUM_FRIENDS);
                    return;
                }

                FlamePlayer.getProfile((String) context.get("player"))
                        .thenAccept(target -> {
                            if (target != null) {
                                FriendRequest.acceptRequest(profile, target).thenAccept(response -> {
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

    public static class RequestsCommand extends Command {

        public RequestsCommand() {
            super("requests");

            setDefaultExecutor(this::defaultExecutor);
        }

        private void defaultExecutor(CommandSender sender, CommandContext context) {
            if (sender instanceof Player player) {
                CompletableFuture.runAsync(() -> {
                    try {
                        var incoming = FriendCache.getIncomingRequests(player.getUuid()).get();
                        var outgoing = FriendCache.getOutgoingRequests(player.getUuid()).get();

                        player.sendMessage(Component.text("Incoming Requests:", NamedTextColor.GOLD));

                        for (var uuid : incoming) {
                            player.sendMessage(uuid.toString());
                        }

                        player.sendMessage(Component.text("Outgoing Requests:", NamedTextColor.GOLD));

                        for (var uuid : outgoing) {
                            player.sendMessage(uuid.toString());
                        }

                    } catch (InterruptedException | ExecutionException exception) {
                        exception.printStackTrace();
                    }
                });
            }
        }

    }

//    public static class MockupCommand extends Command {
//
//        public MockupCommand() {
//            super("mockup");
//
//            setDefaultExecutor(this::defaultExecutor);
//        }
//
//        private void defaultExecutor(CommandSender sender, CommandContext context) {
//            if (sender instanceof Player player) {
//                sender.sendMessage(MC.SEPARATOR_80);
//                sender.sendMessage(Component.text().append(
//                        Component.text("<< ", CustomColor.DARK_YELLOW, TextDecoration.BOLD),
//                        Component.text("Your Friends (Page 2 of 3)", CustomColor.YELLOW),
//                        Component.text(" >>", CustomColor.DARK_YELLOW, TextDecoration.BOLD)
//                ));
//                sender.sendMessage(MC.SEPARATOR_80);
//                sender.sendMessage(Component.text().append(
//                        Component.text("● ", CustomColor.GREEN),
//                        Rank.OWNER.getPrefix(),
//                        Component.text(" oHate ", NamedTextColor.DARK_RED),
//                        Component.text("is in a Bed Wars Game", CustomColor.GREEN)
//                ));
//                sender.sendMessage(Component.text().append(
//                        Component.text("● ", CustomColor.GREEN),
//                        Rank.LEGEND.getPrefix(),
//                        Component.text(" ByteCrack ", NamedTextColor.GOLD),
//                        Component.text("is in Housing", CustomColor.GREEN)
//                ));
//                sender.sendMessage(Component.text().append(
//                        Component.text("● ", CustomColor.RED),
//                        Rank.HELPER.getPrefix(),
//                        Component.text(" Dezept ", NamedTextColor.BLUE),
//                        Component.text("is currently offline", CustomColor.DARK_GRAY)
//                ));
//                sender.sendMessage(Component.text().append(
//                        Component.text("● ", CustomColor.RED),
//                        Rank.HIGHROLLER.getPrefix(),
//                        Component.text(" Snowy ", NamedTextColor.DARK_PURPLE),
//                        Component.text("is currently offline", CustomColor.DARK_GRAY)
//                ));
//                sender.sendMessage(MC.SEPARATOR_80);
//            }
//        }
//
//    }


}
