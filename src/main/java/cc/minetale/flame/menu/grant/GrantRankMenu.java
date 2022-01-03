package cc.minetale.flame.menu.grant;

import cc.minetale.commonlib.grant.Rank;
import cc.minetale.commonlib.profile.Profile;
import cc.minetale.commonlib.util.MC;
import cc.minetale.flame.FlameAPI;
import cc.minetale.flame.procedure.GrantProcedure;
import cc.minetale.mlib.canvas.*;
import cc.minetale.mlib.util.ColorUtil;
import cc.minetale.mlib.util.MenuUtil;
import cc.minetale.mlib.util.SoundsUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;

import java.util.Arrays;

public class GrantRankMenu extends Menu {

    private final Profile profile;

    public GrantRankMenu(Player player, Profile profile) {
        super(player, Component.text("Grant Rank Selection"), CanvasType.FOUR_ROW);

        this.profile = profile;

        setFiller(FillingType.BORDER);

        setFragment(30, MenuUtil.PREVIOUS_PAGE(this));
        setFragment(32, MenuUtil.NEXT_PAGE(this));

        var pagination = new Pagination(10, 14);
        var fragments = new Fragment[Rank.values().length];

        int i = 0;

        for (var rank : Rank.values()) {
            if(rank == Rank.MEMBER) continue;

            var color = rank.getColor();

            fragments[i] = Fragment.of(ItemStack.of(ColorUtil.toConcrete(color))
                    .withDisplayName(Component.text(rank.getName(), Style.style(color, TextDecoration.ITALIC.as(false))))
                    .withLore(
                            Arrays.asList(
                                    MC.SEPARATOR_50,
                                    Component.text().append(
                                            Component.text("Click to grant ", Style.style(NamedTextColor.GRAY, TextDecoration.ITALIC.as(false))),
                                            Component.text(rank.getName(), Style.style(color, TextDecoration.ITALIC.as(false))),
                                            Component.text(" to ", Style.style(NamedTextColor.GRAY, TextDecoration.ITALIC.as(false))),
                                            this.profile.getColoredName().decoration(TextDecoration.ITALIC, false)
                                    ).build(),
                                    MC.SEPARATOR_50
                            )
                    ), event -> {
                if (FlameAPI.canStartProcedure(player)) {
                    var procedure = new GrantProcedure(player, this.profile.getUuid(), GrantProcedure.Type.ADD, GrantProcedure.Stage.PROVIDE_TIME);

                    SoundsUtil.playClickSound(player);

                    procedure.setRank(rank);
                    handleClose(player);

                    new GrantDurationMenu(player, procedure);
                } else {
                    SoundsUtil.playErrorSound(player);
                }
            });

            i++;
        }

        pagination.setFragments(fragments);
        setPagination(pagination);

        openMenu();
    }

    @Override
    public void close() {}

}
