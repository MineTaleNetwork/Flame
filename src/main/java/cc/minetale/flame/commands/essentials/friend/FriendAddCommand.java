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
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.entity.Player;

@SubCommand
public class FriendAddCommand extends Command {

    public FriendAddCommand() {
        super("add");

        setDefaultExecutor(this::defaultExecutor);

        addSyntax(this::onFriendAdd, ArgumentType.Word("player"));
    }

    private void defaultExecutor(CommandSender sender, CommandContext context) {
        sender.sendMessage(CommandUtil.getUsage("friend add", "player"));
    }

    private void onFriendAdd(CommandSender sender, CommandContext context) {
        if (sender instanceof Player player) {
            var profile = FlamePlayer.fromPlayer(player).getProfile();
            var target = FlamePlayer.getProfile((String) context.get("player"));

            if (target == null) {
                sender.sendMessage(Message.parse(Language.Error.UNKNOWN_PLAYER));
                return;
            }

            var response = Friend.addRequest(FlamePlayer.fromPlayer(player).getProfile(), target);

            switch (response) {
                case SUCCESS -> {
                    var targetPlayer = MinecraftServer.getConnectionManager().getPlayer(target.getUuid());

                    if (targetPlayer != null) {
                        targetPlayer.sendMessage(Message.parse(Language.Friend.INVITE_TARGET, profile.getChatFormat()));
                    } else {
                        Postman.getPostman().broadcast(new FriendPayload(profile, target.getUuid(), FriendPayload.Action.REQUEST_CREATE));
                    }

                    sender.sendMessage(Message.parse(Language.Friend.INVITE_INITIATOR, target.getChatFormat()));
                }
                case ALREADY_FRIENDS -> sender.sendMessage(Message.parse(Language.Friend.INVITE_ALREADY_FRIENDS, target.getChatFormat()));
                case TARGET_IS_PLAYER -> sender.sendMessage(Message.parse(Language.Friend.INVITE_SELF_TARGET));
                case MAX_OUTGOING -> sender.sendMessage(Message.parse(Language.Friend.INVITE_MAX_OUTGOING));
                case REQUEST_EXIST -> sender.sendMessage(Message.parse(Language.Friend.INVITE_EXIST, target.getChatFormat()));
                case PENDING_REQUEST -> sender.sendMessage(Message.parse(Language.Friend.INVITE_PENDING, target.getChatFormat()));
                case TARGET_IGNORED -> sender.sendMessage(Message.parse(Language.Friend.TARGET_IGNORED));
                case REQUESTS_TOGGLED, PLAYER_IGNORED -> sender.sendMessage(Message.parse(Language.Friend.TARGET_TOGGLED));
            }
        }
    }

}
