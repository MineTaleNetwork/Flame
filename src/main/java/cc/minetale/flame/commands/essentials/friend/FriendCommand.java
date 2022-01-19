package cc.minetale.flame.commands.essentials.friend;

import cc.minetale.flame.util.CommandUtil;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;

public class FriendCommand extends Command {

    public FriendCommand() {
        super("friend", "f");

        setDefaultExecutor(this::defaultExecutor);

        addSubcommand(new FriendAddCommand());
        addSubcommand(new FriendAcceptCommand());
        addSubcommand(new FriendRequestsCommand());
        addSubcommand(new FriendListCommand());
        //        addSubcommand(new RemoveCommand());
        //        addSubcommand(new CancelCommand());
        //        addSubcommand(new DenyCommand());
    }

    private void defaultExecutor(CommandSender sender, CommandContext context) {
        sender.sendMessage(CommandUtil.getJoinedUsage("friend", "add", "accept", "cancel", "deny", "remove", "requests", "list"));
    }

}
