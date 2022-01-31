package cc.minetale.flame.commands.essentials.friend;

import cc.minetale.commonlib.friend.Friend;
import cc.minetale.commonlib.lang.Language;
import cc.minetale.commonlib.util.Message;
import cc.minetale.flame.util.CommandUtil;
import cc.minetale.flame.util.FlamePlayer;
import cc.minetale.flame.util.SubCommand;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.entity.Player;

@SubCommand
public class FriendCancelCommand extends Command {

    public FriendCancelCommand() {
        super("cancel");

        setDefaultExecutor(this::defaultExecutor);

        addSyntax(this::onFriendRemove, ArgumentType.Word("player"));
    }

    private void defaultExecutor(CommandSender sender, CommandContext context) {
        sender.sendMessage(CommandUtil.getUsage("friend cancel", "player"));
    }

    private void onFriendRemove(CommandSender sender, CommandContext context) {
        if (sender instanceof Player player) {
            var profile = FlamePlayer.fromPlayer(player).getProfile();

            FlamePlayer.getProfile((String) context.get("player"))
                    .thenAccept(target -> {
                        if (target != null) {
                            Friend.cancelRequest(profile, target)
                                    .thenAccept(response -> {
                                        switch (response) {
                                            case SUCCESS -> sender.sendMessage(Message.parse(Language.Friend.Cancel.SUCCESS, target.getChatFormat()));
                                            case NO_REQUEST -> sender.sendMessage(Message.parse(Language.Friend.Cancel.NO_REQUEST, target.getChatFormat()));
                                            case ERROR -> sender.sendMessage(Message.parse(Language.Command.COMMAND_EXCEPTION_ERROR));
                                        }
                                    });
                        } else {
                            sender.sendMessage(Message.parse(Language.Error.UNKNOWN_PLAYER_ERROR));
                        }
                    });
        }
    }

}
