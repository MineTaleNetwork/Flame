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
public class FriendDenyCommand extends Command {

    public FriendDenyCommand() {
        super("deny");

        setDefaultExecutor(this::defaultExecutor);

        addSyntax(this::onFriendRemove, ArgumentType.Word("player"));
    }

    private void defaultExecutor(CommandSender sender, CommandContext context) {
        sender.sendMessage(CommandUtil.getUsage("friend deny", "player"));
    }

    private void onFriendRemove(CommandSender sender, CommandContext context) {
        if (sender instanceof Player player) {
            var profile = FlamePlayer.fromPlayer(player).getProfile();
            var target = FlamePlayer.getProfile((String) context.get("player"));

            if (target == null) {
                sender.sendMessage(Message.parse(Language.Error.UNKNOWN_PLAYER));
                return;
            }

            var response = Friend.removeRequest(profile, target);

            switch (response) {
                case SUCCESS -> {
                    var targetPlayer = MinecraftServer.getConnectionManager().getPlayer(target.getUuid());

                    if (targetPlayer != null) {
                        targetPlayer.sendMessage(Message.parse(Language.Friend.DENY_TARGET, profile.getChatFormat()));
                    } else {
                        Postman.getPostman().broadcast(new FriendPayload(profile, target.getUuid(), FriendPayload.Action.REQUEST_DENY));
                    }

                    sender.sendMessage(Message.parse(Language.Friend.DENY_INITIATOR, target.getChatFormat()));
                }
                case NO_REQUEST -> sender.sendMessage(Message.parse(Language.Friend.NO_REQUEST, target.getChatFormat()));
            }
        }
    }

}
