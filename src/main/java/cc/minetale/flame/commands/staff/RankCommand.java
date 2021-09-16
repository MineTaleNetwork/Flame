package cc.minetale.flame.commands.staff;

import cc.minetale.commonlib.modules.pigeon.payloads.rank.RankReloadPayload;
import cc.minetale.commonlib.modules.pigeon.payloads.rank.RankRemovePayload;
import cc.minetale.commonlib.modules.rank.Rank;
import cc.minetale.commonlib.util.MC;
import cc.minetale.commonlib.util.PigeonUtil;
import cc.minetale.flame.menu.RanksMenu;
import cc.minetale.flame.util.CommandUtil;
import net.kyori.adventure.text.Component;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.command.builder.exception.ArgumentSyntaxException;
import net.minestom.server.command.builder.suggestion.SuggestionEntry;

import java.util.Arrays;

public class RankCommand extends Command {

    public RankCommand() {
        super("rank");

        setCondition(CommandUtil.getRankCondition("Owner"));

        setDefaultExecutor(this::defaultExecutor);

        addSubcommand(new RankCreateCommand());
        addSubcommand(new RankDeleteCommand());
        addSubcommand(new RankListCommand());
        addSubcommand(new RankSetColorCommand());
        addSubcommand(new RankSetWeightCommand());
        addSubcommand(new RankSetPrefixCommand());
    }

    private void defaultExecutor(CommandSender sender, CommandContext context) {
        sender.sendMessage(MC.Chat.notificationMessage("Rank", Component.text("Usage: /rank <create/delete/list/setcolor/setweight/setprefix>", MC.CC.GRAY.getTextColor())));
    }

    private static final class RankCreateCommand extends Command {

        public RankCreateCommand() {
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

                Rank rank = new Rank(rankName, 0, "", MC.CC.WHITE.toString());
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

    private static final class RankDeleteCommand extends Command {

        public RankDeleteCommand() {
            super("delete");
            setDefaultExecutor(this::defaultExecutor);

            var rank = ArgumentType.Word("rank").setSuggestionCallback((commandSender, context, suggestion) -> {
                for(Rank suggestRank : Rank.getRanks().values()) {
                    suggestion.addEntry(new SuggestionEntry(suggestRank.getName()));
                }
            });

            addSyntax(this::onRankDeleteCommand, rank);
        }

        private void defaultExecutor(CommandSender sender, CommandContext context) {
            sender.sendMessage(MC.Chat.notificationMessage("Rank", Component.text("Usage: /rank delete <rank>", MC.CC.GRAY.getTextColor())));
        }

        private void onRankDeleteCommand(CommandSender sender, CommandContext context) {
                String rankName = context.get("rank");

                Rank rank = Rank.getRank(rankName);

                if (rank != null) {
                    rank.delete();

                    sender.sendMessage(MC.Chat.notificationMessage("Rank", Component.text()
                            .append(
                                    Component.text("You have deleted the ", MC.CC.GRAY.getTextColor()),
                                    Component.text(rank.getName(), MC.CC.GOLD.getTextColor()),
                                    Component.text(" rank.", MC.CC.GRAY.getTextColor())
                            ).build()));

                    PigeonUtil.broadcast(new RankRemovePayload(rank.getUuid()));
                } else {
                    sender.sendMessage(MC.Chat.notificationMessage("Rank", Component.text("A rank with that name could not be found.", MC.CC.GRAY.getTextColor())));
                }
        }
    }

    private static final class RankListCommand extends Command {

        public RankListCommand() {
            super("list");
            setDefaultExecutor(this::defaultExecutor);
        }

        private void defaultExecutor(CommandSender sender, CommandContext context) {
                new RanksMenu(sender.asPlayer());
        }
    }

    private static final class RankSetColorCommand extends Command {

        public RankSetColorCommand() {
            super("setcolor");
            setDefaultExecutor(this::defaultExecutor);

            var rank = ArgumentType.Word("rank").setSuggestionCallback((commandSender, context, suggestion) -> {
                for(Rank suggestRank : Rank.getRanks().values()) {
                    suggestion.addEntry(new SuggestionEntry(suggestRank.getName()));
                }
            });

            var color = ArgumentType.Word("color").from(Arrays.stream(MC.CC.values()).map(MC.CC::getName).toArray(String[]::new));

            addSyntax(this::onRankSetColorCommand, rank, color);
        }

        private void defaultExecutor(CommandSender sender, CommandContext context) {
            sender.sendMessage(MC.Chat.notificationMessage("Rank", Component.text("Usage: /rank setcolor <rank> <color>", MC.CC.GRAY.getTextColor())));
        }

        private void onRankSetColorCommand(CommandSender sender, CommandContext context) {
                String rankName = context.get("rank");
                String colorName = context.get("color");

                Rank rank = Rank.getRank(rankName);

                if (rank != null) {

                    try {
                        MC.CC color = MC.CC.valueOf(colorName.toUpperCase());

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
                    } catch (IllegalArgumentException e) {
                        sender.sendMessage(MC.Chat.notificationMessage("Rank", Component.text("A color with that name could not be found.", MC.CC.GRAY.getTextColor())));
                    }
                } else {
                    sender.sendMessage(MC.Chat.notificationMessage("Rank", Component.text("A rank with that name could not be found.", MC.CC.GRAY.getTextColor())));
                }
        }
    }

    private static final class RankSetWeightCommand extends Command {

        public RankSetWeightCommand() {
            super("setweight");
            setDefaultExecutor(this::defaultExecutor);

            var rank = ArgumentType.Word("rank").setSuggestionCallback((commandSender, context, suggestion) -> {
                for(Rank suggestRank : Rank.getRanks().values()) {
                    suggestion.addEntry(new SuggestionEntry(suggestRank.getName()));
                }
            });

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
                String rankName = context.get("rank");
                Integer rankWeight = context.get("weight");

                Rank rank = Rank.getRank(rankName);

                if (rank != null) {
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
                } else {
                    sender.sendMessage(MC.Chat.notificationMessage("Rank", Component.text("A rank with that name could not be found.", MC.CC.GRAY.getTextColor())));
                }
        }
    }

    private static final class RankSetPrefixCommand extends Command {

        public RankSetPrefixCommand() {
            super("setprefix");
            setDefaultExecutor(this::defaultExecutor);

            var rank = ArgumentType.Word("rank").setSuggestionCallback((commandSender, context, suggestion) -> {
                for(Rank suggestRank : Rank.getRanks().values()) {
                    suggestion.addEntry(new SuggestionEntry(suggestRank.getName()));
                }
            });

            var prefix = ArgumentType.String("prefix");

            addSyntax(this::onRankSetPrefixCommand, rank, prefix);
        }

        private void defaultExecutor(CommandSender sender, CommandContext context) {
            sender.sendMessage(MC.Chat.notificationMessage("Rank", Component.text("Usage: /rank setprefix <rank> <prefix>", MC.CC.GRAY.getTextColor())));
        }

        private void onRankSetPrefixCommand(CommandSender sender, CommandContext context) {
                String rankName = context.get("rank");
                String rankPrefix = context.get("prefix");

                Rank rank = Rank.getRank(rankName);

                if (rank != null) {
                    rank.setPrefix(rankPrefix);
                    rank.save();

                    sender.sendMessage(MC.Chat.notificationMessage("Rank", Component.text()
                            .append(
                                    Component.text("You have set the ", MC.CC.GRAY.getTextColor()),
                                    Component.text(rank.getName(), MC.CC.GOLD.getTextColor()),
                                    Component.text(" rank's prefix to ", MC.CC.GRAY.getTextColor()),
                                    MC.Style.fromLegacy(rank.getPrefix())
                            ).build()));

                    PigeonUtil.broadcast(new RankReloadPayload(rank));
                } else {
                    sender.sendMessage(MC.Chat.notificationMessage("Rank", Component.text("A rank with that name could not be found.", MC.CC.GRAY.getTextColor())));
                }
        }
    }

}
