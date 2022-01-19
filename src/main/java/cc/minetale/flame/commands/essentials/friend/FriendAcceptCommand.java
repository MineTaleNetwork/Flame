package cc.minetale.flame.commands.essentials.friend;

import cc.minetale.commonlib.friend.FriendRequest;
import cc.minetale.commonlib.lang.Language;
import cc.minetale.commonlib.pigeon.payloads.friend.FriendRequestAcceptPayload;
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
public class FriendAcceptCommand extends Command {

    public FriendAcceptCommand() {
        super("accept");

        setDefaultExecutor(this::defaultExecutor);

        addSyntax(this::onFriendAcceptCommand, ArgumentType.Word("player"));
    }

    private void defaultExecutor(CommandSender sender, CommandContext context) {
        sender.sendMessage(CommandUtil.getUsage("friend add", "player"));
    }

    private void onFriendAcceptCommand(CommandSender sender, CommandContext context) {
        if (sender instanceof Player player) {
            var profile = FlamePlayer.fromPlayer(player).getProfile();

            FlamePlayer.getProfile((String) context.get("player"))
                    .thenAccept(target -> {
                        if (target != null) {
                            FriendRequest.acceptRequest(profile, target)
                                    .thenAccept(response -> {
                                        switch (response) {
                                            case ERROR -> sender.sendMessage(Language.Command.COMMAND_EXCEPTION_ERROR);
                                            case SUCCESS -> {
                                                var targetPlayer = MinecraftServer.getConnectionManager().getPlayer(target.getUuid());

                                                if (targetPlayer != null) {
                                                    targetPlayer.sendMessage(Message.format(Language.Friend.Accept.SUCCESS, player.getUsername()));
                                                } else {
                                                    PigeonUtil.broadcast(new FriendRequestAcceptPayload(player.getUuid(), target.getUuid()));  // TODO -> Handle Payload
                                                }

                                                sender.sendMessage(Message.format(Language.Friend.Accept.SUCCESS, target.getUsername()));
                                            }
                                            case PLAYER_MAXIMUM_FRIENDS -> sender.sendMessage(Language.Friend.General.PLAYER_MAXIMUM_FRIENDS);
                                            case TARGET_MAXIMUM_FRIENDS -> sender.sendMessage(Language.Friend.General.TARGET_MAXIMUM_FRIENDS);
                                            case NO_REQUEST -> sender.sendMessage(Language.Friend.Accept.NO_REQUEST);
                                            case TARGET_IGNORED -> sender.sendMessage(Language.Friend.General.TARGET_IGNORED);
                                            case PLAYER_IGNORED -> sender.sendMessage(Language.Friend.General.TARGET_TOGGLED);
                                        }
                                    });
                        } else {
                            sender.sendMessage(Language.Error.UNKNOWN_PLAYER_ERROR);
                        }
                    });
        }
    }
}