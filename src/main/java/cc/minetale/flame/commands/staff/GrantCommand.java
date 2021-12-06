package cc.minetale.flame.commands.staff;

import cc.minetale.commonlib.api.Rank;
import cc.minetale.commonlib.util.MC;
import cc.minetale.flame.util.CommandUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.entity.Player;

public class GrantCommand extends Command {

    public GrantCommand() {
        super("grant");

        setCondition(CommandUtil.getRankCondition(Rank.OWNER));

        setDefaultExecutor(this::defaultExecutor);

        var targets = ArgumentType.Entity("targets")
                .onlyPlayers(true);

        addSyntax(this::onGrantCommand, targets);
    }

    private void defaultExecutor(CommandSender sender, CommandContext context) {
        sender.sendMessage(MC.notificationMessage("Grant", Component.text("Usage: /grant <player>", NamedTextColor.GRAY)));
    }

    private void onGrantCommand(CommandSender sender, CommandContext context) {
        String playerName = context.get("player");

        if(sender instanceof Player player) {
//            ProfileUtil.getProfileByName(playerName).thenAccept(profile -> {
//                new GrantRankSelectionMenu(player, profile);
//            });
        }
    }

}
