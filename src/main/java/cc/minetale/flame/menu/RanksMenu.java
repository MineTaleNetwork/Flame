package cc.minetale.flame.menu;

import cc.minetale.mlib.canvas.CanvasType;
import cc.minetale.mlib.canvas.Fragment;
import cc.minetale.mlib.canvas.template.PaginatedMenu;
import cc.minetale.mlib.util.ColorUtil;
import cc.minetale.sodium.profile.grant.Rank;
import cc.minetale.sodium.util.Message;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;

import java.util.Arrays;
import java.util.List;

public class RanksMenu extends PaginatedMenu {

    public RanksMenu(Player player) {
        super(player, Component.text("Ranks"), CanvasType.FOUR_ROW);
    }

    @Override
    public Fragment[] getPaginatedFragments(Player player) {
        return Arrays.stream(Rank.values()).map(rank -> {
            var color = rank.getColor();

            return Fragment.empty(ItemStack.of(ColorUtil.toConcrete(color))
                    .withDisplayName(Component.text(rank.getName(), Message.style(color)))
                    .withLore(
                            List.of(
                                    Message.scoreboardSeparator(),
                                    Component.text().append(
                                            Component.text("Weight: ", Message.style(NamedTextColor.GRAY)),
                                            Component.text(rank.ordinal(), Message.style(color))
                                    ).build(),
                                    Component.text().append(
                                            Component.text("Prefix: ", Message.style(NamedTextColor.GRAY)),
                                            rank.getPrefix()
                                    ).build(),
                                    Component.text().append(
                                            Component.text("Color: ", Message.style(NamedTextColor.GRAY)),
                                            Component.text(color.toString().toUpperCase(), Message.style(color))
                                    ).build(),
                                    Message.scoreboardSeparator()
                            )
                    ));
        }).toArray(Fragment[]::new);
    }

    @Override
    public void close() {}

}
