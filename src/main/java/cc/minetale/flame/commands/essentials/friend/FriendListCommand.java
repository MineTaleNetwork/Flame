package cc.minetale.flame.commands.essentials.friend;

import cc.minetale.commonlib.profile.CachedProfile;
import cc.minetale.commonlib.util.Colors;
import cc.minetale.commonlib.util.Message;
import cc.minetale.commonlib.util.ProfileUtil;
import cc.minetale.flame.util.CommandUtil;
import cc.minetale.flame.util.FlamePlayer;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.TextDecoration;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class FriendListCommand extends Command{

        public FriendListCommand() {
            super("list");

            var page = ArgumentType.Integer("page");

            addSyntax(this::onFriendsListCommand, page);
            setArgumentCallback(CommandUtil::callbackError, page);


            setDefaultExecutor(this::defaultExecutor);
        }

        private void defaultExecutor(CommandSender sender, CommandContext context) {
            if (sender instanceof Player player) {
                sendMessage(player, 0);
            }
        }

        public void onFriendsListCommand(CommandSender sender, CommandContext context) {
            if (sender instanceof Player player) {
                sendMessage(player, (Integer) context.get("page") - 1);
            }
        }

        public void sendMessage(Player player, int page) {
            CompletableFuture.runAsync(() -> {
                var profile = FlamePlayer.fromPlayer(player).getProfile();
                var friends = profile.getFriends();

                try {
                    var profiles = ProfileUtil.getProfiles(friends).get();

                    var sorter = (Comparator<CachedProfile>) (profile1, profile2) -> {
//                            var onlineCompare = Boolean.compare(profile2.isOnline(), profile1.isOnline());
//                            if(onlineCompare != 0) { return onlineCompare; }

                        var rank1 = profile1.getProfile().getGrant().getRank();
                        var rank2 = profile2.getProfile().getGrant().getRank();

                        return Integer.compare(rank1.ordinal(), rank2.ordinal());
                    };

                    profiles.sort(sorter);

                    var pagination = new Pagination(8);
                    pagination.setProfiles(profiles.toArray(new CachedProfile[0]));

                    if(page < 0) {
                        System.out.println("Less");
                        pagination.setCurrentPage(0);
                    } else if(page > pagination.getPageCount()) {
                        System.out.println("Greater");
                        pagination.setCurrentPage(pagination.getPageCount() - 1);
                    } else {
                        System.out.println("Normal");
                        pagination.setCurrentPage(page);
                    }

                    var messages = new ArrayList<Component>(Arrays.asList(
                            Message.chatSeparator(),
                            Component.text().append(
                                    Component.text(pagination.isFirst() ? "" : "<< ", Colors.DARK_YELLOW, TextDecoration.BOLD)
                                            .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/f list " + (pagination.getCurrentPage() - 1))),
                                    Component.text("Your Friends (Page " + (pagination.getCurrentPage() + 1) + " of " + pagination.getPageCount() + ")", Colors.YELLOW),
                                    Component.text(pagination.isLast() ? "" : " >>", Colors.DARK_YELLOW, TextDecoration.BOLD)
                                            .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/f list " + (pagination.getCurrentPage() + 1)))
                            ).build(),
                            Message.chatSeparator()
                    ));

                        for(var playerProfile : pagination.getPageItems()) {
                            if(playerProfile == null) continue;

                            var online = playerProfile.getServer() != null;
                            var status = online ? "is currently in " + playerProfile.getServer() : "is currently offline";
                            var color = online ? Colors.GREEN : Colors.RED;

                            messages.add(Component.text().append(
                                    Component.text("● ", color),
                                    playerProfile.getProfile().getChatFormat(),
                                    Component.text(" " + status, color)
                            ).build());
                        }

                    messages.add(Message.chatSeparator());

                    player.sendMessage(Component.join(JoinConfiguration.separator(Component.newline()), messages));
                } catch (InterruptedException | ExecutionException ignored) {}
            });
        }

        @Getter @Setter
        public static class Pagination {

        private CachedProfile[] profiles = new CachedProfile[0];
        private int currentPage = 0;
        private final int itemsPerPage;

        public Pagination(int itemsPerPage) {
            this.itemsPerPage = itemsPerPage;
        }

        public CachedProfile[] getPageItems() {
            return Arrays.copyOfRange(this.profiles,
                    this.currentPage * this.itemsPerPage,
                    (this.currentPage + 1) * this.itemsPerPage);
        }

        public int getPageCount() {
            return (int) Math.ceil((double) this.profiles.length / this.itemsPerPage);
        }

        public boolean isFirst() {
            return this.currentPage == 0;
        }

        public boolean isLast() {
            return this.currentPage == getPageCount() - 1;
        }

        public boolean nextPage() {
            if(isLast()) {
                return false;
            } else {
                this.currentPage++;
                return true;
            }
        }

        public boolean previousPage() {
            if(isFirst()) {
                return false;
            } else {
                this.currentPage--;
                return true;
            }
        }

    }

}
