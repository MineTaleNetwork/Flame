package cc.minetale.flame.menu;

import cc.minetale.commonlib.grant.Rank;
import cc.minetale.commonlib.util.MC;
import cc.minetale.mlib.canvas.*;
import cc.minetale.mlib.util.ColorUtil;
import cc.minetale.mlib.util.MenuUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;

import java.util.Arrays;

public class RanksMenu extends Menu {

    public RanksMenu(Player player) {
        super(player, Component.text("Ranks"), CanvasType.FOUR_ROW);

        setFiller(FillingType.BORDER);

        setFragment(30, MenuUtil.PREVIOUS_PAGE(this));
        setFragment(32, MenuUtil.NEXT_PAGE(this));

        var pagination = new Pagination(10, 14, true);
        var fragments = new Fragment[Rank.values().length];

        int i = 0;

        for (var rank : Rank.values()) {
            var color = rank.getColor();

            fragments[i] = Fragment.empty(ItemStack.of(ColorUtil.toConcrete(color))
                    .withDisplayName(Component.text(rank.getName(), Style.style(color, TextDecoration.ITALIC.as(false))))
                    .withLore(
                            Arrays.asList(
                                    MC.SEPARATOR_32,
                                    Component.text().append(
                                            Component.text("Weight: ", NamedTextColor.GRAY),
                                            Component.text(rank.getWeight(), color)
                                    ).decoration(TextDecoration.ITALIC, false).build(),
                                    Component.text().append(
                                            Component.text("Prefix: ", NamedTextColor.GRAY),
                                            rank.getPrefix()
                                    ).decoration(TextDecoration.ITALIC, false).build(),
                                    Component.text().append(
                                            Component.text("Color: ", NamedTextColor.GRAY),
                                            Component.text(color.toString().toUpperCase(), color)
                                    ).decoration(TextDecoration.ITALIC, false).build(),
                                    MC.SEPARATOR_32
                            )
                    ));
            i++;
        }

        pagination.setFragments(fragments);
        setPagination(pagination);

        openMenu();
    }

    @Override
    public void close() {}

}
