package cc.minetale.flame.commands.staff;

import cc.minetale.commonlib.api.Rank;
import cc.minetale.commonlib.util.MC;
import cc.minetale.flame.menu.grant.GrantRankSelectionMenu;
import cc.minetale.flame.util.CommandUtil;
import cc.minetale.mlib.util.ProfileUtil;
import net.kyori.adventure.text.Component;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.arguments.ArgumentType;

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
        sender.sendMessage(MC.Chat.notificationMessage("Grant", Component.text("Usage: /grant <player>", MC.CC.GRAY.getTextColor())));
    }

    private void onGrantCommand(CommandSender sender, CommandContext context) {
        String playerName = context.get("player");

        ProfileUtil.getProfileByName(playerName).thenAccept(profile -> {
            new GrantRankSelectionMenu(sender.asPlayer(), profile);
        });
    }

}
