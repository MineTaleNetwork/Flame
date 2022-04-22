//package cc.minetale.flame.commands.essentials.friend;
//
//import cc.minetale.flame.util.CommandUtil;
//import cc.minetale.flame.util.FlamePlayer;
//import cc.minetale.flame.util.SubCommand;
//import cc.minetale.sodium.util.Message;
//import net.minestom.server.MinecraftServer;
//import net.minestom.server.command.CommandSender;
//import net.minestom.server.command.builder.Command;
//import net.minestom.server.command.builder.CommandContext;
//import net.minestom.server.command.builder.arguments.ArgumentType;
//import net.minestom.server.entity.Player;
//
//@SubCommand
//public class FriendAcceptCommand extends Command {
//
//    public FriendAcceptCommand() {
//        super("accept");
//
//        setDefaultExecutor(this::defaultExecutor);
//
//        addSyntax(this::onFriendAccept, ArgumentType.Word("player"));
//    }
//
//    private void defaultExecutor(CommandSender sender, CommandContext context) {
//        sender.sendMessage(CommandUtil.getUsage("friend add", "player"));
//    }
//
//    private void onFriendAccept(CommandSender sender, CommandContext context) {
//        if (sender instanceof Player player) {
//            var profile = FlamePlayer.fromPlayer(player).getProfile();
//            var target = FlamePlayer.getProfile((String) context.get("player"));
//
//            if (target == null) {
//                sender.sendMessage(Message.parse(Language.Error.UNKNOWN_PLAYER));
//                return;
//            }
//
//            var response = Friend.acceptRequest(profile, target);
//
//            switch (response) {
//                case SUCCESS -> {
//                    var targetPlayer = MinecraftServer.getConnectionManager().getPlayer(target.getUuid());
//
//                    if (targetPlayer != null) {
//                        targetPlayer.sendMessage(Message.parse(Language.Friend.ACCEPT_REQUEST, profile.getChatFormat()));
//                    } else {
//                        PigeonUtil.broadcast(new FriendRequestAcceptPayload(profile, target.getUuid()));
//                    }
//
//                    sender.sendMessage(Message.parse(Language.Friend.ACCEPT_REQUEST, target.getChatFormat()));
//                }
//                case PLAYER_MAX_FRIENDS -> sender.sendMessage(Message.parse(Language.Friend.MAX_FRIENDS_INITIATOR));
//                case TARGET_MAX_FRIENDS -> sender.sendMessage(Message.parse(Language.Friend.MAX_FRIENDS_TARGET));
//                case NO_REQUEST -> sender.sendMessage(Message.parse(Language.Friend.NO_REQUEST, target.getChatFormat()));
//                case TARGET_IGNORED -> sender.sendMessage(Message.parse(Language.Friend.TARGET_IGNORED));
//                case PLAYER_IGNORED -> sender.sendMessage(Message.parse(Language.Friend.TARGET_TOGGLED));
//            }
//        }
//    }
//
//}