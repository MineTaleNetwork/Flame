package cc.minetale.flame.commands.staff;

import cc.minetale.commonlib.grant.Rank;
import cc.minetale.commonlib.lang.Language;
import cc.minetale.commonlib.util.Message;
import cc.minetale.flame.util.CommandUtil;
import cc.minetale.flame.util.FlamePlayer;
import net.kyori.adventure.text.Component;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.entity.Player;

public class ClearChatCommand extends Command {

    private static final Component CLEAR_CHAT;

    static {
        var builder = Component.text();

        for (int i = 0; i < 150; i++) {
            builder.append(Component.newline());
        }

        CLEAR_CHAT = builder.build();
    }

    public ClearChatCommand() {
        super("clearchat", "cc");

        setCondition(CommandUtil.getRankCondition(Rank.HELPER));

        setDefaultExecutor(this::defaultExecutor);
    }

    private void defaultExecutor(CommandSender sender, CommandContext context) {
        if(sender instanceof Player player) {
            var profile = FlamePlayer.fromPlayer(player).getProfile();
            var instance = player.getInstance();

            if (instance != null) {
                for (Player instancePlayer : instance.getPlayers()) {
                    if (!Rank.hasMinimumRank(FlamePlayer.fromPlayer(instancePlayer).getProfile(), Rank.HELPER)) {
                        instancePlayer.sendMessage(CLEAR_CHAT);
                    }

                    instancePlayer.sendMessage(Message.chatSeparator());
                    instancePlayer.sendMessage(Component.empty());
                    instancePlayer.sendMessage(Message.format(Language.General.CHAT_CLEARED, profile.getChatFormat()));
                    instancePlayer.sendMessage(Component.empty());
                    instancePlayer.sendMessage(Message.chatSeparator());
                }
            }
        }
    }
}
