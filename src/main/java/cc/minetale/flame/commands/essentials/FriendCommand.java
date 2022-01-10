package cc.minetale.flame.commands.essentials;

import cc.minetale.commonlib.cache.FriendCache;
import cc.minetale.commonlib.cache.ProfileCache;
import cc.minetale.commonlib.friend.FriendRequest;
import cc.minetale.commonlib.grant.Rank;
import cc.minetale.commonlib.pigeon.payloads.friend.FriendRequestAcceptPayload;
import cc.minetale.commonlib.pigeon.payloads.friend.FriendRequestCreatePayload;
import cc.minetale.commonlib.profile.Profile;
import cc.minetale.commonlib.util.CustomColor;
import cc.minetale.commonlib.util.MC;
import cc.minetale.commonlib.util.PigeonUtil;
import cc.minetale.flame.Lang;
import cc.minetale.flame.util.FlamePlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.entity.Player;
import org.checkerframework.checker.units.qual.C;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class FriendCommand extends Command {

    public FriendCommand() {
        super("friend", "f");

        setDefaultExecutor(this::defaultExecutor);

        addSubcommand(new MockupCommand());

        addSubcommand(new AddCommand());
        addSubcommand(new AcceptCommand());
//        addSubcommand(new CancelCommand());
//        addSubcommand(new DenyCommand());
        addSubcommand(new RequestsCommand());
//        addSubcommand(new RemoveCommand());
        addSubcommand(new ListCommand());
    }

    private void defaultExecutor(CommandSender sender, CommandContext context) {
        sender.sendMessage(MC.notificationMessage("Friend", Component.text("Usage: /friend <add/accept/deny/cancel/remove/list>", NamedTextColor.GRAY)));
    }

    public static class AddCommand extends Command {

        public AddCommand() {
            super("add");

            setDefaultExecutor(this::defaultExecutor);

            addSyntax(this::onFriendAddCommand, ArgumentType.Word("profile"));
        }

        private void defaultExecutor(CommandSender sender, CommandContext context) {
            sender.sendMessage(MC.notificationMessage("Friend", Component.text("Usage: /friend add <player>", NamedTextColor.GRAY)));
        }

        private void onFriendAddCommand(CommandSender sender, CommandContext context) {
            if (sender instanceof Player player) {
                FlamePlayer.getProfile((String) context.get("profile"))
                        .thenAccept(target -> {
                            if (target != null) {
                                var profile = FlamePlayer.fromPlayer(player).getProfile();

                                FriendRequest.addRequest(profile, target).thenAccept(response -> {
                                    switch (response) {
                                        case ERROR -> {
                                            player.sendMessage(Component.text("An error occurred when trying to execute that command.", NamedTextColor.RED));
                                        }
                                        case SUCCESS -> {
                                            var targetPlayer = MinecraftServer.getConnectionManager().getPlayer(target.getUuid());

                                            if(targetPlayer != null) {
                                                targetPlayer.sendMessage(Component.text(player.getUsername() + " has sent you a friend request!", NamedTextColor.GREEN));
                                            } else {
                                                PigeonUtil.broadcast(new FriendRequestCreatePayload(player.getUuid(), target.getUuid())); // TODO -> Handle Payload
                                            }

                                            player.sendMessage(Component.text("You sent a friend request to " + target.getName(), NamedTextColor.GREEN));
                                        }
                                        case REQUEST_EXIST -> {
                                            player.sendMessage(Component.text("You already sent a request to that player.", NamedTextColor.RED));
                                        }
                                        case TARGET_IS_IGNORED -> {
                                            player.sendMessage(Component.text("You are currently ignoring that player.", NamedTextColor.RED));
                                        }
                                        case PENDING_REQUEST -> {
                                            player.sendMessage(Component.text("You already have a request from that player.", NamedTextColor.RED));
                                        }
                                        case REQUESTS_TOGGLED, PLAYER_IS_IGNORED -> player.sendMessage(Component.text("That players is not receiving new friends at this moment.", NamedTextColor.RED));
                                    }
                                });
                            } else {
                                player.sendMessage(Lang.COULD_NOT_LOAD_PROFILE);
                            }
                        });
            }
        }
    }

    public static class AcceptCommand extends Command {

        public AcceptCommand() {
            super("accept");

            setDefaultExecutor(this::defaultExecutor);

            addSyntax(this::onFriendAcceptCommand, ArgumentType.Word("profile"));
        }

        private void defaultExecutor(CommandSender sender, CommandContext context) {
            sender.sendMessage(MC.notificationMessage("Friend", Component.text("Usage: /friend accept <player>", NamedTextColor.GRAY)));
        }

        private void onFriendAcceptCommand(CommandSender sender, CommandContext context) {
            if (sender instanceof Player player) {
                FlamePlayer.getProfile((String) context.get("profile"))
                        .thenAccept(target -> {
                            if (target != null) {
                                var profile = FlamePlayer.fromPlayer(player).getProfile();

                                FriendRequest.acceptRequest(profile, target).thenAccept(response -> {
                                   switch (response) {
                                       case ERROR -> {
                                           player.sendMessage(Component.text("An error occurred when trying to execute that command.", NamedTextColor.RED));
                                       }
                                       case SUCCESS -> {
                                           var targetPlayer = MinecraftServer.getConnectionManager().getPlayer(target.getUuid());

                                           if(targetPlayer != null) {
                                               targetPlayer.sendMessage(Component.text("You are now friends with " + player.getUsername(), NamedTextColor.GREEN));
                                           } else {
                                               PigeonUtil.broadcast(new FriendRequestAcceptPayload(player.getUuid(), target.getUuid()));  // TODO -> Handle Payload
                                           }

                                           player.sendMessage(Component.text("You are now friends with " + target.getName(), NamedTextColor.GREEN));
                                       }
                                       case NO_REQUEST -> {
                                           player.sendMessage(Component.text("You do not have a friend request from that player.", NamedTextColor.RED));
                                       }
                                       case TARGET_IS_IGNORED -> {
                                           player.sendMessage(Component.text("You are currently ignoring that player.", NamedTextColor.RED));
                                       }
                                       case PLAYER_IS_IGNORED -> {
                                           player.sendMessage(Component.text("That players is not receiving new friends at this moment.", NamedTextColor.RED));
                                       }
                                   }
                                });
                            } else {
                                player.sendMessage(Lang.COULD_NOT_LOAD_PROFILE);
                            }
                        });
            }
        }
    }

    public static class ListCommand extends Command {

        public ListCommand() {
            super("list");

            setDefaultExecutor(this::defaultExecutor);
        }

        private void defaultExecutor(CommandSender sender, CommandContext context) {
            if (sender instanceof Player player) {
                CompletableFuture.runAsync(() -> {
                    var profile = FlamePlayer.fromPlayer(player).getProfile();
                    var friends = profile.getFriends();

                    try {
                        // TODO -> Use cached profile instead and if it loads from the database they will be offline
                        // TODO -> Map each profile to a Map<UUID, CachedProfile> and if they don't get mapped or are null then default to Unknown player

                        var profiles = ProfileCache.getProfiles(friends).get();
                    } catch (InterruptedException | ExecutionException ignored) {}
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

                        for(var uuid : incoming) {
                            player.sendMessage(uuid.toString());
                        }

                        player.sendMessage(Component.text("Outgoing Requests:", NamedTextColor.GOLD));

                        for(var uuid : outgoing) {
                            player.sendMessage(uuid.toString());
                        }

                    } catch (InterruptedException | ExecutionException exception) {
                        exception.printStackTrace();
                    }
                });
            }
        }

    }

    public static class MockupCommand extends Command {

        public MockupCommand() {
            super("mockup");

            setDefaultExecutor(this::defaultExecutor);
        }

        private void defaultExecutor(CommandSender sender, CommandContext context) {
            if (sender instanceof Player player) {
                sender.sendMessage(MC.SEPARATOR_80);
                sender.sendMessage(Component.text().append(
                        Component.text("<< ", CustomColor.DARK_YELLOW, TextDecoration.BOLD),
                        Component.text("Your Friends (Page 2 of 3)", CustomColor.YELLOW),
                        Component.text(" >>", CustomColor.DARK_YELLOW, TextDecoration.BOLD)
                ));
                sender.sendMessage(MC.SEPARATOR_80);
                sender.sendMessage(Component.text().append(
                        Component.text("● ", CustomColor.GREEN),
                        Rank.OWNER.getPrefix(),
                        Component.text(" oHate ", NamedTextColor.DARK_RED),
                        Component.text("is in a Bed Wars Game", CustomColor.GREEN)
                ));
                sender.sendMessage(Component.text().append(
                        Component.text("● ", CustomColor.GREEN),
                        Rank.LEGEND.getPrefix(),
                        Component.text(" ByteCrack ", NamedTextColor.GOLD),
                        Component.text("is in Housing", CustomColor.GREEN)
                ));
                sender.sendMessage(Component.text().append(
                        Component.text("● ", CustomColor.RED),
                        Rank.HELPER.getPrefix(),
                        Component.text(" Dezept ", NamedTextColor.BLUE),
                        Component.text("is currently offline", CustomColor.DARK_GRAY)
                ));
                sender.sendMessage(Component.text().append(
                        Component.text("● ", CustomColor.RED),
                        Rank.HIGHROLLER.getPrefix(),
                        Component.text(" Snowy ", NamedTextColor.DARK_PURPLE),
                        Component.text("is currently offline", CustomColor.DARK_GRAY)
                ));
                sender.sendMessage(MC.SEPARATOR_80);
            }
        }

    }


}
