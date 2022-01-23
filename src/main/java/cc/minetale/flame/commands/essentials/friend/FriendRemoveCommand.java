package cc.minetale.flame.commands.essentials.friend;

import cc.minetale.commonlib.friend.FriendRequest;
import cc.minetale.commonlib.lang.Language;
import cc.minetale.commonlib.pigeon.payloads.friend.FriendRemovePayload;
import cc.minetale.commonlib.pigeon.payloads.friend.FriendRequestCreatePayload;
import cc.minetale.commonlib.util.Message;
import cc.minetale.commonlib.util.PigeonUtil;
import cc.minetale.flame.util.CommandUtil;
import cc.minetale.flame.util.FlamePlayer;
import cc.minetale.flame.util.SubCommand;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.entity.Player;

@SubCommand
public class FriendRemoveCommand extends Command {

    public FriendRemoveCommand() {
        super("remove");

        setDefaultExecutor(this::defaultExecutor);

        addSyntax(this::onFriendRemove, ArgumentType.Word("player"));
    }

    private void defaultExecutor(CommandSender sender, CommandContext context) {
        sender.sendMessage(CommandUtil.getUsage("friend remove", "player"));
    }

    private void onFriendRemove(CommandSender sender, CommandContext context) {
        if (sender instanceof Player player) {
            var profile = FlamePlayer.fromPlayer(player).getProfile();

            FlamePlayer.getProfile((String) context.get("player"))
                    .thenAccept(target -> FriendRequest.removeFriend(profile, target)
                            .thenAccept(response -> {
                                switch (response) {
                                    case SUCCESS -> {
                                        var targetPlayer = MinecraftServer.getConnectionManager().getPlayer(target.getUuid());

                                        if(targetPlayer != null) {
                                            targetPlayer.sendMessage(Message.parse(Language.Friend.Remove.SUCCESS_TARGET, profile.getChatFormat()));
                                        } else {
                                            PigeonUtil.broadcast(new FriendRemovePayload(player.getUuid(), target.getUuid()));
                                        }

                                        sender.sendMessage(Message.parse(Language.Friend.Remove.SUCCESS_PLAYER, target.getChatFormat()));
                                    }
                                    case NOT_ADDED -> sender.sendMessage(Message.parse(Language.Friend.Remove.NOT_ADDED, target.getChatFormat()));
                                }
                            }));
        }
    }

}
