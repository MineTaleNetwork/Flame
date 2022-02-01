package cc.minetale.flame.commands.essentials.party;

import cc.minetale.flame.util.CommandUtil;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;

public class PartyCommand extends Command {

    public PartyCommand() {
        super("party", "p");

        setDefaultExecutor(this::defaultExecutor);

//        addSubcommand(new FriendAddCommand());
//        addSubcommand(new FriendAcceptCommand());
//        addSubcommand(new FriendRequestsCommand());
//        addSubcommand(new FriendListCommand());
//        addSubcommand(new FriendRemoveCommand());
//        addSubcommand(new FriendCancelCommand());
//        addSubcommand(new FriendDenyCommand());
    }

    private void defaultExecutor(CommandSender sender, CommandContext context) {
        sender.sendMessage(CommandUtil.getJoinedUsage("party", "invite", "accept", "cancel", "kick", "requests", "list", "settings", "warp", "promote", "demote", "disband"));
    }

}
