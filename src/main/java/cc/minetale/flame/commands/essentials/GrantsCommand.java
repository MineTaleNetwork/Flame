package cc.minetale.flame.commands.essentials;

import cc.minetale.commonlib.api.Rank;
import cc.minetale.commonlib.util.MC;
import cc.minetale.flame.Lang;
import cc.minetale.flame.menu.grant.GrantsMenu;
import cc.minetale.flame.util.CommandUtil;
import cc.minetale.flame.util.FlamePlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.entity.Player;

public class GrantsCommand extends Command {

    public GrantsCommand() {
        super("grants");

        setCondition(CommandUtil.getRankCondition(Rank.ADMIN));
        setDefaultExecutor(this::defaultExecutor);

        var profile = ArgumentType.Word("profile");

        addSyntax(this::onGrantsCommand, profile);
    }

    private void defaultExecutor(CommandSender sender, CommandContext context) {
        sender.sendMessage(MC.notificationMessage("Command",
                Component.text("Usage: /grants <player>", NamedTextColor.GRAY)));
    }

    private void onGrantsCommand(CommandSender sender, CommandContext context) {
        if (sender instanceof Player player) {
            FlamePlayer.getProfile((String) context.get("profile"))
                    .thenAccept(profile -> {
                        if (profile != null) {
                            new GrantsMenu(player, profile);
                        } else {
                            player.sendMessage(Lang.COULD_NOT_LOAD_PROFILE);
                        }
                    });
        }
    }

}
