package cc.minetale.flame.commands.essentials;

import cc.minetale.flame.commands.RankUtil;
import cc.minetale.commonlib.modules.rank.Rank;
import cc.minetale.commonlib.util.MC;
import cc.minetale.flame.menu.punishment.PunishmentMenu;
import cc.minetale.mlib.util.ProfileUtil;
import net.kyori.adventure.text.Component;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.arguments.ArgumentEnum;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.command.builder.condition.Conditions;
import net.minestom.server.command.builder.exception.ArgumentSyntaxException;
import net.minestom.server.command.builder.suggestion.SuggestionEntry;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.utils.entity.EntityFinder;

import java.util.List;

public class PunishCommand extends Command {

    public PunishCommand() {
        super("punish");
        setCondition(Conditions::playerOnly);
        setDefaultExecutor(this::defaultExecutor);

        var player = ArgumentType.Word("player").setSuggestionCallback((commandSender, context, suggestion) -> {
            for(var onlinePlayer : MinecraftServer.getConnectionManager().getOnlinePlayers()) {
                suggestion.addEntry(new SuggestionEntry(onlinePlayer.getUsername()));
            }
        });

        addSyntax(this::onPunishCommand, player);
    }

    private void defaultExecutor(CommandSender sender, CommandContext context) {
        sender.sendMessage(MC.Chat.notificationMessage("Grant", Component.text("Usage: /punish <player>", MC.CC.GRAY.getTextColor())));
    }

    private void onPunishCommand(CommandSender sender, CommandContext context) {
        Player player = sender.asPlayer();

        RankUtil.canUseCommand(player, Rank.getRank("Owner"), commandCallback -> {
            if (!commandCallback.isMinimum())
                return;

            String playerName = context.get("player");

            ProfileUtil.getProfileByName(playerName).thenAccept(profile -> {
                new PunishmentMenu(player, profile);
            });
        });
    }

}
