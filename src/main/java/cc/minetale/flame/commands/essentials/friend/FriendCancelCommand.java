package cc.minetale.flame.commands.essentials.friend;

import cc.minetale.flame.util.CommandUtil;
import cc.minetale.flame.util.FlamePlayer;
import cc.minetale.flame.util.SubCommand;
import cc.minetale.sodium.lang.Language;
import cc.minetale.sodium.profile.friend.Friend;
import cc.minetale.sodium.util.Message;
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
            var target = FlamePlayer.getProfile((String) context.get("player"));

            if (target == null) {
                sender.sendMessage(Message.parse(Language.Error.UNKNOWN_PLAYER));
                return;
            }

            var response = Friend.removeRequest(profile, target);

            switch (response) {
                case SUCCESS -> sender.sendMessage(Message.parse(Language.Friend.CANCEL_INITIATOR, target.getChatFormat()));
                case NO_REQUEST -> sender.sendMessage(Message.parse(Language.Friend.CANCEL_NO_REQUEST, target.getChatFormat()));
            }
        }
    }

}
