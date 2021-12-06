package cc.minetale.flame.commands.essentials;

import cc.minetale.commonlib.api.Rank;
import cc.minetale.commonlib.util.MC;
import cc.minetale.flame.util.CommandUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.command.builder.suggestion.SuggestionEntry;
import net.minestom.server.entity.Player;

public class PunishCommand extends Command {

    public PunishCommand() {
        super("punish");

        setCondition(CommandUtil.getRankCondition(Rank.OWNER));

        setDefaultExecutor(this::defaultExecutor);

        // TODO -> Replace this with the proper argument

        var player = ArgumentType.Word("player").setSuggestionCallback((commandSender, context, suggestion) -> {
            for(var onlinePlayer : MinecraftServer.getConnectionManager().getOnlinePlayers()) {
                suggestion.addEntry(new SuggestionEntry(onlinePlayer.getUsername()));
            }
        });

        addSyntax(this::onPunishCommand, player);
    }

    private void defaultExecutor(CommandSender sender, CommandContext context) {
        sender.sendMessage(MC.notificationMessage("Punish", Component.text("Usage: /punish <player>", NamedTextColor.GRAY)));
    }

    private void onPunishCommand(CommandSender sender, CommandContext context) {
        if(sender instanceof Player player) {
            String playerName = context.get("player");
        }



//        ProfileUtil.getProfileByName(playerName).thenAccept(profile -> new PunishmentMenu(player, profile));
    }

}
