package cc.minetale.flame.commands.staff;

import cc.minetale.flame.menu.grant.GrantsMenu;
import cc.minetale.flame.util.CommandUtil;
import cc.minetale.flame.util.FlamePlayer;
import cc.minetale.mlib.canvas.template.Menu;
import cc.minetale.sodium.lang.Language;
import cc.minetale.sodium.profile.grant.Rank;
import cc.minetale.sodium.util.Message;
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

        setCondition(CommandUtil.getRankCondition(Rank.OWNER));

        setDefaultExecutor(this::defaultExecutor);

        var profile = ArgumentType.Word("player");

        addSyntax(this::grantsExecutor, profile);
    }

    private void defaultExecutor(CommandSender sender, CommandContext context) {
        sender.sendMessage(Message.notification("Command",
                Component.text("Usage: /grants <player>", NamedTextColor.GRAY))
        );
    }

    private void grantsExecutor(CommandSender sender, CommandContext context) {
        if (sender instanceof Player player) {
            var profile = FlamePlayer.getProfile((String) context.get("player"));

            if(profile == null) {
                player.sendMessage(Message.parse(Language.Error.UNKNOWN_PLAYER));
                return;
            }

            Menu.openMenu(new GrantsMenu(player, profile));
        }
    }

}
