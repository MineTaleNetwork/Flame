package cc.minetale.flame.commands.staff;

import cc.minetale.commonlib.modules.profile.Profile;
import cc.minetale.commonlib.util.MC;
import cc.minetale.flame.util.CommandUtil;
import net.kyori.adventure.text.Component;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.entity.Player;

public class ClearChatCommand extends Command {

    public ClearChatCommand() {
        super("clearchat", "cc");

//        setCondition(CommandUtil.getRankCondition("Mod"));

        setDefaultExecutor(this::defaultExecutor);
    }

    private void defaultExecutor(CommandSender sender, CommandContext context) {
        MinecraftServer.LOGGER.debug("Command Executed");

        Player player = sender.asPlayer();

            var builder = Component.text();

            for (int i = 0; i < 150; i++) {
                builder.append(Component.newline());
            }

            var component = Component.text()
                    .append(MC.Style.SEPARATOR_80)
                    .append(Component.newline())
//                    .append(MC.Chat.notificationMessage("Chat",
//                            Component.text("Chat has been cleared by ", MC.CC.GRAY.getTextColor())
//                                    .append(commandCallback.getProfile().api().getChatFormat())))
                    .append(Component.newline())
                    .append(MC.Style.SEPARATOR_80)
                    .build();

//            for (Player instancePlayer : player.getInstance().getPlayers()) {
//                RankUtil.hasMinimumRank(instancePlayer, "Mod", rankCallback -> {
//                    if (!rankCallback.isEligible()) {
//                        instancePlayer.sendMessage(builder.build());
//                    }
//
//                    instancePlayer.sendMessage(component);
//                });
//            }
    }
}
