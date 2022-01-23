package cc.minetale.flame.commands.essentials.friend;

import cc.minetale.commonlib.friend.FriendRequest;
import cc.minetale.commonlib.lang.Language;
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

            FlamePlayer.getProfile((String) context.get("player"))
                    .thenAccept(target -> {
                        if (target != null) {
                            FriendRequest.addRequest(FlamePlayer.fromPlayer(player).getProfile(), target)
                                    .thenAccept(response -> {
                                        switch (response) {
                                            case SUCCESS -> {
                                                var targetPlayer = MinecraftServer.getConnectionManager().getPlayer(target.getUuid());

                                                if (targetPlayer != null) {
                                                    targetPlayer.sendMessage(Message.parse(Language.Friend.Add.SUCCESS_TARGET, profile.getChatFormat()));
                                                } else {
                                                    PigeonUtil.broadcast(new FriendRequestCreatePayload(player.getUuid(), target.getUuid()));
                                                }

                                                sender.sendMessage(Message.parse(Language.Friend.Add.SUCCESS_PLAYER, target.getChatFormat()));
                                            }
                                            case ALREADY_FRIENDS -> sender.sendMessage(Message.parse(Language.Friend.Add.ALREADY_FRIENDS, target.getChatFormat()));
                                            case TARGET_IS_PLAYER -> sender.sendMessage(Message.parse(Language.Friend.Add.TARGET_IS_PLAYER));
                                            case MAXIMUM_REQUESTS -> sender.sendMessage(Message.parse(Language.Friend.General.MAXIMUM_REQUESTS));
                                            case ERROR -> sender.sendMessage(Message.parse(Language.Command.COMMAND_EXCEPTION_ERROR));
                                            case REQUEST_EXIST -> sender.sendMessage(Message.parse(Language.Friend.Add.REQUEST_EXIST, target.getChatFormat()));
                                            case PENDING_REQUEST -> sender.sendMessage(Message.parse(Language.Friend.Add.PENDING_REQUEST, target.getChatFormat()));
                                            case TARGET_IGNORED -> sender.sendMessage(Message.parse(Language.Friend.General.TARGET_IGNORED));
                                            case REQUESTS_TOGGLED, PLAYER_IGNORED -> sender.sendMessage(Message.parse(Language.Friend.General.TARGET_TOGGLED));
                                        }
                                    });
                        } else {
                            sender.sendMessage(Message.parse(Language.Error.UNKNOWN_PLAYER_ERROR));
                        }
                    });
        }
    }

}