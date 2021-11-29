package cc.minetale.flame.commands.staff;

import cc.minetale.commonlib.api.Rank;
import cc.minetale.flame.Lang;
import cc.minetale.flame.util.CommandUtil;
import cc.minetale.flame.util.FlamePlayer;
import cc.minetale.mlib.util.ProfileUtil;
import net.kyori.adventure.text.Component;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.Instance;

public class ClearChatCommand extends Command {

    public ClearChatCommand() {
        super("clearchat", "cc");

        setCondition(CommandUtil.getRankCondition(Rank.HELPER));

        setDefaultExecutor(this::defaultExecutor);
    }

    private void defaultExecutor(CommandSender sender, CommandContext context) {
        Player player = sender.asPlayer();

        ProfileUtil.getAssociatedProfile(player)
                .thenAccept(profile -> {
                    var builder = Component.text();

                    for (int i = 0; i < 150; i++) {
                        builder.append(Component.newline());
                    }

                    var component = Lang.CHAT_CLEARED(profile);
                    var instance = player.getInstance();

                    if (instance != null) {
                        for (Player instancePlayer : instance.getPlayers()) {
                            if (!Rank.hasMinimumRank(FlamePlayer.fromPlayer(instancePlayer).getProfile(), Rank.HELPER)) {
                                instancePlayer.sendMessage(builder.build());
                            }

                            instancePlayer.sendMessage(component);
                        }
                    }
                });
    }
}
