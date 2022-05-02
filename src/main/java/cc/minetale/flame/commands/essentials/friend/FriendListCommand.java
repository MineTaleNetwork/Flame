//package cc.minetale.flame.commands.essentials.friend;
//
//import cc.minetale.commonlib.lang.Language;
//import cc.minetale.commonlib.profile.CachedProfile;
//import cc.minetale.commonlib.util.Colors;
//import cc.minetale.commonlib.util.Message;
//import cc.minetale.commonlib.util.ProfileUtil;
//import cc.minetale.flame.util.CommandUtil;
//import cc.minetale.flame.util.FlamePlayer;
//import cc.minetale.flame.util.SubCommand;
//import cc.minetale.mlib.util.MathUtil;
//import net.kyori.adventure.text.Component;
//import net.kyori.adventure.text.JoinConfiguration;
//import net.kyori.adventure.text.event.ClickEvent;
//import net.kyori.adventure.text.format.TextDecoration;
//import net.minestom.server.command.CommandSender;
//import net.minestom.server.command.builder.Command;
//import net.minestom.server.command.builder.CommandContext;
//import net.minestom.server.command.builder.arguments.ArgumentType;
//import net.minestom.server.entity.Player;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.Comparator;
//import java.util.concurrent.CompletableFuture;
//import java.util.concurrent.ExecutionException;
//
//@SubCommand
//public class FriendListCommand extends Command {
//
//    public FriendListCommand() {
//        super("list");
//
//        setDefaultExecutor(this::defaultExecutor);
//
//        var page = ArgumentType.Integer("page");
//
//        addSyntax(this::onFriendsListCommand, page);
//        setArgumentCallback(CommandUtil::callbackError, page);
//    }
//
//    private void defaultExecutor(CommandSender sender, CommandContext context) {
//        if (sender instanceof Player player) {
//            sendMessage(player, 0);
//        }
//    }
//
//    public void onFriendsListCommand(CommandSender sender, CommandContext context) {
//        if (sender instanceof Player player) {
//            sendMessage(player, (Integer) context.get("page") - 1);
//        }
//    }
//
//    public void sendMessage(Player player, int page) {
//        CompletableFuture.runAsync(() -> {
//            var profile = FlamePlayer.fromPlayer(player).getProfile();
//            var friends = profile.getFriends();
//
//            if(friends.size() == 0) {
//                player.sendMessage(Message.parse(Language.Friend.NO_FRIENDS));
//                return;
//            }
//
//            try {
//                var profiles = ProfileUtil.getProfiles(friends).get();
//
//                var sorter = (Comparator<CachedProfile>) (profile1, profile2) -> {
//                    var onlineCompare = Boolean.compare(profile2.getServer() != null, profile1.getServer() != null);
//                    if(onlineCompare != 0) { return onlineCompare; }
//
//                    var rank1 = profile1.getProfile().getGrant().getRank();
//                    var rank2 = profile2.getProfile().getGrant().getRank();
//
//                    return Integer.compare(rank1.ordinal(), rank2.ordinal());
//                };
//
//                profiles.sort(sorter);
//
//                var pagination = new Pagination<>(8, profiles.toArray(new CachedProfile[0]));
//                pagination.setCurrentPage(MathUtil.clamp(page, 0, pagination.getPageCount() - 1));
//
//                var messages = new ArrayList<>(List.of(
//                        Message.chatSeparator(),
//                        Component.text().append(
//                                Component.text(pagination.isFirst() ? "" : "<< ", Colors.DARK_YELLOW, TextDecoration.BOLD)
//                                        .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/f list " + (pagination.getCurrentPage()))),
//                                Component.text("Your Friends (Page " + (pagination.getCurrentPage() + 1) + " of " + pagination.getPageCount() + ")", Colors.YELLOW),
//                                Component.text(pagination.isLast() ? "" : " >>", Colors.DARK_YELLOW, TextDecoration.BOLD)
//                                        .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/f list " + (pagination.getCurrentPage() + 2)))
//                        ).build(),
//                        Message.chatSeparator()
//                ));
//
//                for (var playerProfile : pagination.getPageItems()) {
//                    if (playerProfile == null) continue;
//
//                    var online = playerProfile.getServer() != null;
//                    var status = online ? "is currently in " + playerProfile.getServer() : "is currently offline";
//                    var color = online ? Colors.GREEN : Colors.RED;
//
//                    messages.add(Component.text().append(
//                            Component.text("● ", color),
//                            playerProfile.getProfile().getChatFormat(),
//                            Component.text(" " + status, color)
//                    ).build());
//                }
//
//                messages.add(Message.chatSeparator());
//
//                player.sendMessage(Component.join(JoinConfiguration.separator(Component.newline()), messages));
//            } catch (InterruptedException | ExecutionException ignored) {}
//        });
//    }
//
//}
