package cc.minetale.flame.commands.staff;

import cc.minetale.commonlib.util.MC;
import cc.minetale.flame.Lang;
import cc.minetale.flame.util.RankUtil;
import cc.minetale.flame.util.CommandUtil;
import cc.minetale.mlib.util.ProfileUtil;
import net.kyori.adventure.text.Component;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.Instance;

public class ClearChatCommand extends Command {

    private final String rankName;

    public ClearChatCommand() {
        super("clearchat", "cc");

        this.rankName = "Helper";

        setCondition(CommandUtil.getRankCondition(this.rankName));

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

                    Instance instance = player.getInstance();

                    if (instance != null) {
                        for (Player instancePlayer : instance.getPlayers()) {
                            if (!RankUtil.hasMinimumRank(instancePlayer, this.rankName)) {
                                instancePlayer.sendMessage(builder.build());
                            }

                            instancePlayer.sendMessage(component);
                        }
                    }
                });
    }
}
