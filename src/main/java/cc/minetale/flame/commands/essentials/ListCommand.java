package cc.minetale.flame.commands.essentials;

import cc.minetale.commonlib.modules.profile.Profile;
import cc.minetale.commonlib.modules.rank.Rank;
import cc.minetale.commonlib.util.MC;
import cc.minetale.flame.util.CommandUtil;
import cc.minetale.mlib.util.ProfileUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.Instance;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ListCommand extends Command {
    public ListCommand() {
        super("list");

        setCondition(CommandUtil.getRankCondition("Helper"));

        setDefaultExecutor(this::execute);
    }

    private void execute(@NotNull CommandSender sender, @NotNull CommandContext context) {
        if (sender.isPlayer()) {
            Instance instance = sender.asPlayer().getInstance();
            if (instance == null) {
                return;
            }

            List<Player> sortedPlayers = new ArrayList<>(instance.getPlayers());

            sortedPlayers.sort((player1, player2) -> {
                Profile profile1 = ProfileUtil.getAssociatedProfile(player1, false, false).getNow(null);
                int weight1 = profile1 != null ? profile1.getGrant().api().getRank().getWeight() : 100;

                Profile profile2 = ProfileUtil.getAssociatedProfile(player2, false, false).getNow(null);
                int weight2 = profile2 != null ? profile2.getGrant().api().getRank().getWeight() : 100;

                return weight1 - weight2;
            });

            List<Component> playerComponents = new ArrayList<>();


            for (Player player : sortedPlayers) {
                Profile profile = ProfileUtil.getAssociatedProfile(player, false, false).getNow(null);
                if (profile != null)
                    playerComponents.add(profile.api().getColoredName());
            }


            List<Rank> sortedRanks = new ArrayList<>(Rank.getRanks().values());
            sortedRanks.sort(Rank.COMPARATOR);

            List<Component> rankComponents = new ArrayList<>();

            for (Rank rank : sortedRanks) {
                rankComponents.add(Component.text(rank.getName()).color(TextColor.color(rank.api().getRankColor().getTextColor())));
            }

            sender.sendMessage(MC.Style.SEPARATOR_80);
            sender.sendMessage(Component.join(Component.text(", ")
                    .color(NamedTextColor.GRAY), rankComponents));
            sender.sendMessage(MC.Style.SEPARATOR_80);
            sender.sendMessage(Component.text("There are " + instance.getPlayers().size() + " players online:")
                    .color(NamedTextColor.GRAY));
            sender.sendMessage(Component.join(Component.text(", ")
                    .color(NamedTextColor.GRAY), playerComponents));
            sender.sendMessage(MC.Style.SEPARATOR_80);
        }
    }
}
