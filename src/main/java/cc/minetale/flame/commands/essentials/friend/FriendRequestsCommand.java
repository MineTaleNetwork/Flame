package cc.minetale.flame.commands.essentials.friend;

import cc.minetale.commonlib.lang.Language;
import cc.minetale.commonlib.profile.CachedProfile;
import cc.minetale.commonlib.profile.Profile;
import cc.minetale.commonlib.util.*;
import cc.minetale.flame.util.CommandUtil;
import cc.minetale.flame.util.Pagination;
import cc.minetale.flame.util.SubCommand;
import cc.minetale.mlib.util.MathUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.arguments.ArgumentEnum;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.entity.Player;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@SubCommand
public class FriendRequestsCommand extends Command {

    public FriendRequestsCommand() {
        super("requests");

        setDefaultExecutor(this::defaultExecutor);

        var typeArgument = ArgumentType.Enum("type", RequestType.class).setFormat(ArgumentEnum.Format.LOWER_CASED);
        typeArgument.setCallback(CommandUtil::callbackError);

        addSyntax(this::onRequests, typeArgument);
        addSyntax(this::onPageRequests, typeArgument, ArgumentType.Integer("page"));
    }

    private void defaultExecutor(CommandSender sender, CommandContext context) {
        sender.sendMessage(CommandUtil.getJoinedUsage("friend requests", "outgoing", "incoming"));
    }

    private void onRequests(CommandSender sender, CommandContext context) {
        if (sender instanceof Player player) {
            sendMessage(player, context.get("type"), 0);
        }
    }

    private void onPageRequests(CommandSender sender, CommandContext context) {
        if (sender instanceof Player player) {
            sendMessage(player, context.get("type"), context.get("page"));
        }
    }

    public void sendMessage(Player player, RequestType type, int page) {
        CompletableFuture.runAsync(() -> {
            var requests = new HashMap<UUID, Long>();
            var profiles = new ArrayList<Profile>();
            var cache = Cache.getFriendRequestCache();

            try {
                switch (type) {
                    case INCOMING -> {
                        var incoming = cache.getIncoming(player.getUuid()).get();

                        for (var request : incoming) {
                            requests.put(request.initiator(), request.ttl());
                        }

                        profiles.addAll(getProfiles(incoming, type));

                        if (profiles.size() == 0) {
                            player.sendMessage(Message.parse(Language.Friend.General.NO_INCOMING));
                            return;
                        }
                    }
                    case OUTGOING -> {
                        var outgoing = cache.getOutgoing(player.getUuid()).get();

                        for (var request : outgoing) {
                            requests.put(request.target(), request.ttl());
                        }

                        profiles.addAll(getProfiles(outgoing, type));

                        if (profiles.size() == 0) {
                            player.sendMessage(Message.parse(Language.Friend.General.NO_OUTGOING));
                            return;
                        }
                    }
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }

            var sorter = (Comparator<Profile>) (profile1, profile2) -> {
                var rank1 = profile1.getGrant().getRank();
                var rank2 = profile2.getGrant().getRank();

                return Integer.compare(rank1.ordinal(), rank2.ordinal());
            };

            profiles.sort(sorter);

            var pagination = new Pagination<>(8, profiles.toArray(new Profile[0]));
            pagination.setCurrentPage(MathUtil.clamp(page, 0, pagination.getPageCount() - 1));

            var messages = new ArrayList<>(Arrays.asList(
                    Message.chatSeparator(),
                    Component.text().append(
                            Component.text(pagination.isFirst() ? "" : "<< ", Colors.DARK_YELLOW, TextDecoration.BOLD)
                                    .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/f requests " + type.getCommand() + " " + (pagination.getCurrentPage()))),
                            Component.text(type.getReadable() + " Friend Requests (Page " + (pagination.getCurrentPage() + 1) + " of " + pagination.getPageCount() + ")", Colors.YELLOW),
                            Component.text(pagination.isLast() ? "" : " >>", Colors.DARK_YELLOW, TextDecoration.BOLD)
                                    .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/f requests " + type.getCommand() + " " + (pagination.getCurrentPage() + 2)))
                    ).build(),
                    Message.chatSeparator()
            ));

            for (var playerProfile : pagination.getPageItems()) {
                if (playerProfile == null) continue;

                System.out.println(playerProfile.getUuid());

                messages.add(Component.text().append(
                        Component.text("➤ ", NamedTextColor.YELLOW),
                        playerProfile.getChatFormat(),
                        Component.text(" (" + TimeUtil.millisToRoundedTime(requests.get(playerProfile.getUuid())) + ")", NamedTextColor.YELLOW)
                ).build());
            }
            messages.add(Message.chatSeparator());

            player.sendMessage(Component.join(JoinConfiguration.separator(Component.newline()), messages));
        });
    }

    public List<Profile> getProfiles(Set<Request> requests, RequestType type) {
        var profiles = new ArrayList<Profile>();

        if (requests != null && requests.size() > 0) {
            List<CachedProfile> cachedProfiles = null;
            try {
                cachedProfiles = ProfileUtil.getProfiles(requests.stream()
                                .map(request -> type == RequestType.INCOMING ? request.initiator() : request.target())
                                .collect(Collectors.toList()))
                        .get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }

            if (cachedProfiles != null) {
                profiles.addAll(cachedProfiles.stream()
                        .map(CachedProfile::getProfile)
                        .toList());
            }
        }

        return profiles;
    }

    @Getter
    @AllArgsConstructor
    public enum RequestType {
        INCOMING("Incoming", "incoming"),
        OUTGOING("Outgoing", "outgoing");

        private final String readable;
        private final String command;
    }

}
