package cc.minetale.flame.commands.essentials;

import cc.minetale.flame.menu.punishment.PunishmentTypeMenu;
import cc.minetale.mlib.canvas.template.Menu;
import cc.minetale.sodium.lang.Language;
import cc.minetale.sodium.profile.grant.Rank;
import cc.minetale.flame.util.CommandUtil;
import cc.minetale.flame.util.FlamePlayer;
import cc.minetale.sodium.util.Message;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.entity.Player;

public class PunishCommand extends Command {

    public PunishCommand() {
        super("punish");

        setCondition(CommandUtil.getRankCondition(Rank.OWNER));

        setDefaultExecutor(this::defaultExecutor);

        var profile = ArgumentType.Word("player");

        addSyntax(this::onPunishCommand, profile);
    }

    private void defaultExecutor(CommandSender sender, CommandContext context) {
        sender.sendMessage(CommandUtil.getUsage("punish", "player"));
    }

    private void onPunishCommand(CommandSender sender, CommandContext context) {
        if(sender instanceof Player executor) {
            var targetName = (String) context.get("player");
            var profile = FlamePlayer.getProfile(targetName);

            if(profile == null) {
                sender.sendMessage(Message.parse(Language.Error.UNKNOWN_PLAYER));
                return;
            }

            Menu.openMenu(new PunishmentTypeMenu(executor, profile));
        }
    }

}
