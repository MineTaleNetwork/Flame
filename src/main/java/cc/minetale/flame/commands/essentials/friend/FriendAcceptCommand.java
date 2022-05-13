package cc.minetale.flame.commands.essentials.friend;

import cc.minetale.flame.util.CommandUtil;
import cc.minetale.flame.util.FlamePlayer;
import cc.minetale.flame.util.SubCommand;
import cc.minetale.postman.Postman;
import cc.minetale.sodium.lang.Language;
import cc.minetale.sodium.payloads.FriendPayload;
import cc.minetale.sodium.profile.friend.Friend;
import cc.minetale.sodium.util.Message;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.entity.Player;

@SubCommand
public class FriendAcceptCommand extends Command {

    public FriendAcceptCommand() {
        super("accept");

        setDefaultExecutor((sender, context) -> sender.sendMessage(CommandUtil.getUsage("friend " + getName(), "player")));

        addSyntax((sender, context) -> {
            if (sender instanceof Player player) {
                var profile = FlamePlayer.fromPlayer(player).getProfile();
                var target = FlamePlayer.getProfile((String) context.get("player"));

                if (target == null) {
                    sender.sendMessage(Message.parse(Language.Error.UNKNOWN_PLAYER));
                    return;
                }

                var response = Friend.acceptRequest(profile, target);

                switch (response) {
                    case SUCCESS -> {
                        var targetPlayer = MinecraftServer.getConnectionManager().getPlayer(target.getUuid());

                        if (targetPlayer != null) {
                            targetPlayer.sendMessage(Message.parse(Language.Friend.ACCEPT_REQUEST, profile.getChatFormat()));
                        } else {
                            Postman.getPostman().broadcast(new FriendPayload(profile, target.getUuid(), FriendPayload.Action.REQUEST_ACCEPT));
                        }

                        sender.sendMessage(Message.parse(Language.Friend.ACCEPT_REQUEST, target.getChatFormat()));
                    }
                    case PLAYER_MAX_FRIENDS -> sender.sendMessage(Message.parse(Language.Friend.MAX_FRIENDS_INITIATOR));
                    case TARGET_MAX_FRIENDS -> sender.sendMessage(Message.parse(Language.Friend.MAX_FRIENDS_TARGET));
                    case NO_REQUEST -> sender.sendMessage(Message.parse(Language.Friend.NO_REQUEST, target.getChatFormat()));
                    case TARGET_IGNORED -> sender.sendMessage(Message.parse(Language.Friend.TARGET_IGNORED));
                    case PLAYER_IGNORED -> sender.sendMessage(Message.parse(Language.Friend.TARGET_TOGGLED));
                }
            }
        }, ArgumentType.Word("player"));
    }

}