package cc.minetale.flame.commands.essentials.friend;

import cc.minetale.flame.menu.friend.FriendListMenu;
import cc.minetale.flame.util.CommandUtil;
import cc.minetale.flame.util.FlamePlayer;
import cc.minetale.flame.util.SubCommand;
import cc.minetale.mlib.canvas.template.Menu;
import cc.minetale.sodium.cache.RequestCache;
import cc.minetale.sodium.lang.Language;
import cc.minetale.sodium.profile.ProfileUtil;
import cc.minetale.sodium.util.Message;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.arguments.ArgumentEnum;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@SubCommand
public class FriendRequestsCommand extends Command {

    public FriendRequestsCommand() {
        super("list");

        setDefaultExecutor(this::defaultExecutor);

        var typeArgument = ArgumentType.Enum("type", RequestType.class).setFormat(ArgumentEnum.Format.LOWER_CASED);
        typeArgument.setCallback(CommandUtil::callbackError);

        addSyntax(this::onRequests, typeArgument);
    }

    private void defaultExecutor(CommandSender sender, CommandContext context) {
        sender.sendMessage(CommandUtil.getJoinedUsage("friend requests", "incoming", "outgoing"));
    }

    private void onRequests(CommandSender sender, CommandContext context) {
        if (sender instanceof Player player) {
            var profile = FlamePlayer.fromPlayer(player).getProfile();
            var type = (RequestType) context.get("type");

            Set<RequestCache.Request> requests = new HashSet<>();

            switch (type) {
                case INCOMING -> {
                    requests = RequestCache.getFriendRequest().getIncoming(profile.getUuid());

                    if (requests.size() == 0) {
                        player.sendMessage(Message.parse(Language.Friend.INVITE_NO_OUTGOING));
                        return;
                    }
                }
                case OUTGOING -> {
                    requests = RequestCache.getFriendRequest().getOutgoing(profile.getUuid());

                    if (requests.size() == 0) {
                        player.sendMessage(Message.parse(Language.Friend.INVITE_NO_OUTGOING));
                        return;
                    }
                }
            }

            ProfileUtil.getProfiles(requests.stream().map(RequestCache.Request::target).collect(Collectors.toList()))
                    .thenAccept(friends -> {
                        Menu.openMenu(new FriendListMenu(player, friends));
                    });
        }
    }

    @Getter
    @AllArgsConstructor
    public enum RequestType {
        INCOMING("Incoming"),
        OUTGOING("Outgoing");

        private final String readable;
    }

}
