package cc.minetale.flame.commands.essentials;

import cc.minetale.flame.commands.RankUtil;
import cc.minetale.commonlib.modules.rank.Rank;
import cc.minetale.commonlib.util.MC;
import net.kyori.adventure.text.Component;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.arguments.ArgumentEnum;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.command.builder.condition.Conditions;
import net.minestom.server.command.builder.exception.ArgumentSyntaxException;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.utils.entity.EntityFinder;

import java.util.List;

public class GameModeCommand extends Command {

    public GameModeCommand() {
        super("gamemode", "gm");
        setCondition(Conditions::playerOnly);
        setDefaultExecutor(this::defaultExecutor);

        var gamemode = ArgumentType.Enum("gamemode", GameMode.class).setFormat(ArgumentEnum.Format.LOWER_CASED);
        var targets = ArgumentType.Entity("targets").onlyPlayers(true);

        setArgumentCallback(this::onGamemodeError, gamemode);
        addSyntax(this::onGamemodeSelfCommand, gamemode);
        addSyntax(this::onGamemodeOthersCommand, gamemode, targets);
    }

    private void defaultExecutor(CommandSender sender, CommandContext context) {
        String commandName = context.getCommandName();

        sender.sendMessage(MC.Chat.notificationMessage("Gamemode", Component.text("Usage: /" + commandName + " <gamemode> [targets]", MC.CC.GRAY.getTextColor())));
    }

    private void onGamemodeError(CommandSender sender, ArgumentSyntaxException exception) {
        sender.sendMessage(MC.Chat.notificationMessage("Gamemode", Component.text("You've entered an unknown gamemode.", MC.CC.GRAY.getTextColor())));
    }

    private void onGamemodeSelfCommand(CommandSender sender, CommandContext context) {
        Player player = sender.asPlayer();

        RankUtil.canUseCommand(player, Rank.getRank("Admin"), commandCallback -> {
            if (!commandCallback.isMinimum())
                return;

            GameMode mode = context.get("gamemode");

            executeSelf(player, mode);
        });
    }

    private void onGamemodeOthersCommand(CommandSender sender, CommandContext context) {
        RankUtil.canUseCommand(sender.asPlayer(), Rank.getRank("Admin"), commandCallback -> {
            if (!commandCallback.isMinimum())
                return;

            GameMode mode = context.get("gamemode");
            EntityFinder finder = context.get("targets");

            executeOthers(sender, mode, finder.find(sender));
        });
    }

    private void executeOthers(CommandSender sender, GameMode gameMode, List<Entity> entities) {
        if (entities.size() == 0) {
            sender.sendMessage(MC.Chat.notificationMessage("Gamemode", Component.text("A player with that name doesn't exist.", MC.CC.GRAY.getTextColor())));
        } else for (Entity entity : entities) {
            if (entity instanceof Player) {
                Player player = (Player) entity;
                if (player == sender) {
                    executeSelf(sender.asPlayer(), gameMode);
                } else {
                    player.setGameMode(gameMode);
                    String playerName = player.getUsername();

                    sender.sendMessage(MC.Chat.notificationMessage("Gamemode", Component.text("You've updated " + playerName + "'s gamemode to " + gameMode.name(), MC.CC.GRAY.getTextColor())));
                    player.sendMessage(MC.Chat.notificationMessage("Gamemode", Component.text("Your gamemode has been updated to " + gameMode.name(), MC.CC.GRAY.getTextColor())));
                }
            }
        }
    }

    private void executeSelf(Player sender, GameMode gameMode) {
        sender.setGameMode(gameMode);

        sender.sendMessage(MC.Chat.notificationMessage("Gamemode", Component.text("You've updated your own gamemode to " + gameMode.name(), MC.CC.GRAY.getTextColor())));
    }

}
