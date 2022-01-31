package cc.minetale.flame.commands.essentials.friend;

import cc.minetale.commonlib.friend.Friend;
import cc.minetale.commonlib.lang.Language;
import cc.minetale.commonlib.pigeon.payloads.friend.FriendRequestDenyPayload;
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

            FlamePlayer.getProfile((String) context.get("player"))
                    .thenAccept(target -> {
                        if(target != null) {
                            Friend.denyRequest(profile, target)
                                    .thenAccept(response -> {
                                        switch (response) {
                                            case ERROR -> sender.sendMessage(Message.parse(Language.Command.COMMAND_EXCEPTION_ERROR));
                                            case SUCCESS -> {
                                                var targetPlayer = MinecraftServer.getConnectionManager().getPlayer(target.getUuid());

                                                if (targetPlayer != null) {
                                                    targetPlayer.sendMessage(Message.parse(Language.Friend.Deny.SUCCESS_TARGET, profile.getChatFormat()));
                                                } else {
                                                    PigeonUtil.broadcast(new FriendRequestDenyPayload(player.getUuid(), target.getUuid()));
                                                }

                                                sender.sendMessage(Message.parse(Language.Friend.Deny.SUCCESS_PLAYER, target.getChatFormat()));
                                            }
                                            case NO_REQUEST -> sender.sendMessage(Message.parse(Language.Friend.Cancel.NO_REQUEST, target.getChatFormat()));
                                        }
                                    });
                        } else {
                            sender.sendMessage(Message.parse(Language.Error.UNKNOWN_PLAYER_ERROR));
                        }
                    });
        }
    }

}
