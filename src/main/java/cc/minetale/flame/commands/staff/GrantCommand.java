package cc.minetale.flame.commands.staff;

import cc.minetale.commonlib.api.Rank;
import cc.minetale.commonlib.util.MC;
import cc.minetale.flame.Lang;
import cc.minetale.flame.menu.grant.GrantRankMenu;
import cc.minetale.flame.util.CommandUtil;
import cc.minetale.flame.util.ProfileUtil;
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

        var profile = ArgumentType.Word("profile");

        addSyntax(this::onGrantCommand, profile);
    }

    private void defaultExecutor(CommandSender sender, CommandContext context) {
        sender.sendMessage(MC.notificationMessage("Command",
                Component.text("Usage: /grant <player>", NamedTextColor.GRAY)));
    }

    private void onGrantCommand(CommandSender sender, CommandContext context) {
        if (sender instanceof Player player) {
            ProfileUtil.getProfile((String) context.get("profile"))
                    .thenAccept(profile -> {
                        if (profile != null) {
                            new GrantRankMenu(player, profile);
                        } else {
                            player.sendMessage(Lang.COULD_NOT_LOAD_PROFILE);
                        }
                    });
        }
    }
}
