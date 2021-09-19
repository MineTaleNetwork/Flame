package cc.minetale.flame.commands.essentials;

import cc.minetale.commonlib.util.MC;
import cc.minetale.flame.menu.punishment.PunishmentMenu;
import cc.minetale.flame.util.CommandUtil;
import cc.minetale.mlib.util.ProfileUtil;
import net.kyori.adventure.text.Component;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.command.builder.condition.Conditions;
import net.minestom.server.command.builder.suggestion.SuggestionEntry;
import net.minestom.server.entity.Player;

public class PunishCommand extends Command {

    public PunishCommand() {
        super("punish");

        setCondition(CommandUtil.getRankCondition("Default"));

        setDefaultExecutor(this::defaultExecutor);

        var player = ArgumentType.Word("player").setSuggestionCallback((commandSender, context, suggestion) -> {
            for(var onlinePlayer : MinecraftServer.getConnectionManager().getOnlinePlayers()) {
                suggestion.addEntry(new SuggestionEntry(onlinePlayer.getUsername()));
            }
        });

        addSyntax(this::onPunishCommand, player);
    }

    private void defaultExecutor(CommandSender sender, CommandContext context) {
        sender.sendMessage(MC.Chat.notificationMessage("Punish", Component.text("Usage: /punish <player>", MC.CC.GRAY.getTextColor())));
    }

    private void onPunishCommand(CommandSender sender, CommandContext context) {
        Player player = sender.asPlayer();

        String playerName = context.get("player");

        ProfileUtil.getProfileByName(playerName).thenAccept(profile -> new PunishmentMenu(player, profile));
    }

}
