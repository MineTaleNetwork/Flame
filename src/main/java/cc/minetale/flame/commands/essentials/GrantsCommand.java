//package cc.minetale.flame.commands.essentials;
//
//import cc.minetale.sodium.profile.grant.Rank;
//import cc.minetale.flame.util.CommandUtil;
//import cc.minetale.flame.util.FlamePlayer;
//import cc.minetale.sodium.util.Message;
//import net.minestom.server.command.CommandSender;
//import net.minestom.server.command.builder.Command;
//import net.minestom.server.command.builder.CommandContext;
//import net.minestom.server.command.builder.arguments.ArgumentType;
//import net.minestom.server.entity.Player;
//
//public class GrantsCommand extends Command {
//
//    public GrantsCommand() {
//        super("grants");
//
//        setCondition(CommandUtil.getRankCondition(Rank.ADMIN));
//
//        setDefaultExecutor(this::defaultExecutor);
//
//        var profile = ArgumentType.Word("player");
//
//        addSyntax(this::onGrantsCommand, profile);
//    }
//
//    private void defaultExecutor(CommandSender sender, CommandContext context) {
//        sender.sendMessage(CommandUtil.getUsage("grants", "player"));
//    }
//
//    private void onGrantsCommand(CommandSender sender, CommandContext context) {
//        if (sender instanceof Player player) {
//            var profile = FlamePlayer.getProfile((String) context.get("player"));
//            if (profile != null) {
////                            new GrantsMenu(player, profile);
//            } else {
//                player.sendMessage(Message.parse(Language.Error.UNKNOWN_PLAYER));
//            }
//        }
//    }
//
//}
