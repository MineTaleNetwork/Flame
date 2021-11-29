package cc.minetale.flame.commands.staff;

import cc.minetale.commonlib.pigeon.payloads.rank.RankReloadPayload;
import cc.minetale.commonlib.pigeon.payloads.rank.RankRemovePayload;
import cc.minetale.commonlib.api.Rank;
import cc.minetale.commonlib.util.MC;
import cc.minetale.commonlib.util.PigeonUtil;
import cc.minetale.flame.menu.RanksMenu;
import cc.minetale.flame.util.CommandUtil;
import net.kyori.adventure.text.Component;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.arguments.ArgumentEnum;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.command.builder.exception.ArgumentSyntaxException;

public class RankCommand extends Command {

    public RankCommand() {
        super("rank");

        setCondition(CommandUtil.getRankCondition("Owner"));

        setDefaultExecutor(this::defaultExecutor);

        addSubcommand(new Create());
        addSubcommand(new Delete());
        addSubcommand(new List());
        addSubcommand(new SetColor());
        addSubcommand(new SetWeight());
        addSubcommand(new SetPrefix());
    }

    public static void onRankError(CommandSender sender, ArgumentSyntaxException exception) {
        sender.sendMessage(MC.Chat.notificationMessage("Rank", Component.text("A rank with that name could not be found.", MC.CC.GRAY.getTextColor())));
    }

    private void defaultExecutor(CommandSender sender, CommandContext context) {
        sender.sendMessage(MC.Chat.notificationMessage("Rank", Component.text("Usage: /rank <create/delete/list/setcolor/setweight/setprefix>", MC.CC.GRAY.getTextColor())));
    }

    private static final class Create extends Command {

        public Create() {
            super("create");

            setDefaultExecutor(this::defaultExecutor);

            addSyntax(this::onRankCreateCommand, ArgumentType.Word("rank"));
        }

        private void defaultExecutor(CommandSender sender, CommandContext context) {
            sender.sendMessage(MC.Chat.notificationMessage("Rank", Component.text("Usage: /rank create <rank>", MC.CC.GRAY.getTextColor())));
        }

        private void onRankCreateCommand(CommandSender sender, CommandContext context) {
            String rankName = context.get("rank");

            if (Rank.getRank(rankName) != null) {
                sender.sendMessage(MC.Chat.notificationMessage("Rank", Component.text("A rank with that name already exists.", MC.CC.GRAY.getTextColor())));
                return;
            }

            Rank rank = new Rank(rankName, 999, "", MC.CC.WHITE.toString());
            rank.save();

            sender.sendMessage(MC.Chat.notificationMessage("Rank", Component.text()
                    .append(
                            Component.text("You have created the ", MC.CC.GRAY.getTextColor()),
                            Component.text(rank.getName(), MC.CC.GOLD.getTextColor()),
                            Component.text(" rank.", MC.CC.GRAY.getTextColor())
                    ).build()));

            PigeonUtil.broadcast(new RankReloadPayload(rank));
        }
    }

    private static final class Delete extends Command {

        public Delete() {
            super("delete");

            setDefaultExecutor(this::defaultExecutor);

            var rank = new ArgumentRank("rank");
            setArgumentCallback(RankCommand::onRankError, rank);

            addSyntax(this::onRankDeleteCommand, rank);
        }

        private void defaultExecutor(CommandSender sender, CommandContext context) {
            sender.sendMessage(MC.Chat.notificationMessage("Rank", Component.text("Usage: /rank delete <rank>", MC.CC.GRAY.getTextColor())));
        }

        private void onRankDeleteCommand(CommandSender sender, CommandContext context) {
            Rank rank = context.get("rank");

            rank.delete();

            sender.sendMessage(MC.Chat.notificationMessage("Rank",
                    MC.component(
                            MC.component("You've deleted the ", MC.CC.GRAY),
                            MC.component(rank.getName(), MC.CC.GOLD),
                            MC.component(" rank.", MC.CC.GRAY)
                    )
            ));

            PigeonUtil.broadcast(new RankRemovePayload(rank.getUuid()));
        }
    }

    private static final class List extends Command {

        public List() {
            super("list");

            setDefaultExecutor(this::defaultExecutor);
        }

        private void defaultExecutor(CommandSender sender, CommandContext context) {
            new RanksMenu(sender.asPlayer());
        }
    }

    private static final class SetColor extends Command {

        public SetColor() {
            super("setcolor");

            setDefaultExecutor(this::defaultExecutor);

            var rank = new ArgumentRank("rank");
            setArgumentCallback(RankCommand::onRankError, rank);

            var color = ArgumentType.Enum("color", MC.CC.class).setFormat(ArgumentEnum.Format.LOWER_CASED);
            setArgumentCallback(this::onColorError, color);

            addSyntax(this::onRankSetColorCommand, rank, color);
        }

        private void onColorError(CommandSender sender, ArgumentSyntaxException exception) {
            sender.sendMessage(MC.Chat.notificationMessage("Rank", Component.text("A color with that name could not be found.", MC.CC.GRAY.getTextColor())));
        }

        private void defaultExecutor(CommandSender sender, CommandContext context) {
            sender.sendMessage(MC.Chat.notificationMessage("Rank", Component.text("Usage: /rank setcolor <rank> <color>", MC.CC.GRAY.getTextColor())));
        }

        private void onRankSetColorCommand(CommandSender sender, CommandContext context) {
            Rank rank = context.get("rank");
            MC.CC color = context.get("color");

            rank.setColor(color.toString());
            rank.save();

            sender.sendMessage(MC.Chat.notificationMessage("Rank", Component.text()
                    .append(
                            Component.text("You have set the ", MC.CC.GRAY.getTextColor()),
                            Component.text(rank.getName(), MC.CC.GOLD.getTextColor()),
                            Component.text(" rank's color to ", MC.CC.GRAY.getTextColor()),
                            Component.text(color.toString(), color.getTextColor())
                    ).build()));

            PigeonUtil.broadcast(new RankReloadPayload(rank));
        }
    }

    private static final class SetWeight extends Command {

        public SetWeight() {
            super("setweight");

            setDefaultExecutor(this::defaultExecutor);

            var rank = new ArgumentRank("rank");
            setArgumentCallback(RankCommand::onRankError, rank);

            var weight = ArgumentType.Integer("weight").between(0, 999);
            setArgumentCallback(this::weightArgumentCallback, weight);

            addSyntax(this::onRankSetWeightCommand, rank, weight);
        }

        private void defaultExecutor(CommandSender sender, CommandContext context) {
            sender.sendMessage(MC.Chat.notificationMessage("Rank", Component.text("Usage: /rank setweight <rank> <weight>", MC.CC.GRAY.getTextColor())));
        }

        private void weightArgumentCallback(CommandSender sender, ArgumentSyntaxException e) {
            sender.sendMessage(MC.Chat.notificationMessage("Rank", Component.text("The weight must be between 0 and 999.", MC.CC.GRAY.getTextColor())));
        }

        private void onRankSetWeightCommand(CommandSender sender, CommandContext context) {
            Rank rank = context.get("rank");
            Integer rankWeight = context.get("weight");

            rank.setWeight(rankWeight);
            rank.save();

            sender.sendMessage(MC.Chat.notificationMessage("Rank", Component.text()
                    .append(
                            Component.text("You have set the ", MC.CC.GRAY.getTextColor()),
                            Component.text(rank.getName(), MC.CC.GOLD.getTextColor()),
                            Component.text(" rank's weight to ", MC.CC.GRAY.getTextColor()),
                            Component.text(rankWeight, MC.CC.GOLD.getTextColor())
                    ).build()));

            PigeonUtil.broadcast(new RankReloadPayload(rank));
        }
    }

    private static final class SetPrefix extends Command {

        public SetPrefix() {
            super("setprefix");

            setDefaultExecutor(this::defaultExecutor);

            var rank = new ArgumentRank("rank");
            setArgumentCallback(RankCommand::onRankError, rank);

            var prefix = ArgumentType.StringArray("prefix");

            addSyntax(this::onRankSetPrefixCommand, rank, prefix);
        }

        private void defaultExecutor(CommandSender sender, CommandContext context) {
            sender.sendMessage(MC.Chat.notificationMessage("Rank", Component.text("Usage: /rank setprefix <rank> <prefix>", MC.CC.GRAY.getTextColor())));
        }

        private void onRankSetPrefixCommand(CommandSender sender, CommandContext context) {
            Rank rank = context.get("rank");
            String prefix = String.join(" ", (String[]) context.get("prefix"));

            rank.setPrefix(prefix);
            rank.save();

            sender.sendMessage(MC.Chat.notificationMessage("Rank", Component.text()
                    .append(
                            Component.text("You have set the ", MC.CC.GRAY.getTextColor()),
                            Component.text(rank.getName(), MC.CC.GOLD.getTextColor()),
                            Component.text(" rank's prefix to ", MC.CC.GRAY.getTextColor()),
                            MC.Style.fromLegacy(rank.getPrefix())
                    ).build()));

            PigeonUtil.broadcast(new RankReloadPayload(rank));
        }
    }

}
