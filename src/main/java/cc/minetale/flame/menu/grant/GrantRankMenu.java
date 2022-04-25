package cc.minetale.flame.menu.grant;

import cc.minetale.flame.FlameAPI;
import cc.minetale.flame.procedure.GrantProcedure;
import cc.minetale.mlib.canvas.CanvasType;
import cc.minetale.mlib.canvas.Fragment;
import cc.minetale.mlib.canvas.template.Menu;
import cc.minetale.mlib.canvas.template.PaginatedMenu;
import cc.minetale.mlib.util.ColorUtil;
import cc.minetale.mlib.util.SoundsUtil;
import cc.minetale.sodium.profile.Profile;
import cc.minetale.sodium.profile.grant.Rank;
import cc.minetale.sodium.util.Message;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;

import java.util.Arrays;

public class GrantRankMenu extends PaginatedMenu {

    private final Profile profile;

    public GrantRankMenu(Player player, Profile profile) {
        super(player, Component.text("Grant Rank Selection"), CanvasType.FOUR_ROW);

        this.profile = profile;
    }

    @Override
    public Fragment[] getPaginatedFragments(Player player) {
        var fragments = new Fragment[Rank.values().length - 1];

        int i = 0;
        for (var rank : Rank.values()) {
            if(rank == Rank.MEMBER) continue;

            var color = rank.getColor();

            fragments[i] = Fragment.of(ItemStack.of(ColorUtil.toConcrete(color))
                    .withDisplayName(Component.text(rank.getName(), Message.style(color)))
                    .withLore(
                            Arrays.asList(
                                    Message.menuSeparator(),
                                    Component.text().append(
                                            Component.text("Click to grant ", Message.style(NamedTextColor.GRAY)),
                                            Component.text(rank.getName(), Message.style(color)),
                                            Component.text(" to ", Message.style(NamedTextColor.GRAY)),
                                            profile.getColoredName().decoration(TextDecoration.ITALIC, false)
                                    ).build(),
                                    Message.menuSeparator()
                            )
                    ), event -> {
                if (FlameAPI.canStartProcedure(player)) {
                    var procedure = new GrantProcedure(player, profile, GrantProcedure.Type.ADD, GrantProcedure.Stage.PROVIDE_TIME);

                    SoundsUtil.playClickSound(player);

                    procedure.setRank(rank);
                    handleClose(player);

                    Menu.openMenu(new GrantDurationMenu(player, procedure));
                } else {
                    SoundsUtil.playErrorSound(player);
                }
            });

            i++;
        }

        return fragments;
    }

}
