package cc.minetale.flame.menu.grant;

import cc.minetale.flame.menu.DurationMenu;
import cc.minetale.flame.procedure.GrantProcedure;
import cc.minetale.flame.procedure.Procedure;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GrantRankMenu extends PaginatedMenu {

    private final Profile profile;

    public GrantRankMenu(Player player, Profile profile) {
        super(player, Component.text("Rank Selection"), CanvasType.FOUR_ROW);

        this.profile = profile;
    }

    @Override
    public Fragment[] getPaginatedFragments(Player player) {
        var ranks = new ArrayList<>(List.of(Rank.values()));
        ranks.remove(Rank.MEMBER);

        return ranks.stream().map(rank -> {
            var color = rank.getColor();

            return Fragment.of(ItemStack.of(ColorUtil.toConcrete(color))
                    .withDisplayName(Component.text(rank.getName(), Message.style(color)))
                    .withLore(
                            List.of(
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
                if (Procedure.canStartProcedure(player.getUuid())) {
                    var procedure = new GrantProcedure(player.getUuid(), profile, GrantProcedure.Type.ADD, GrantProcedure.Stage.PROVIDE_TIME);

                    SoundsUtil.playClickSound(player);
                    procedure.setRank(rank);

                    Menu.openMenu(new DurationMenu(player, procedure, new GrantReasonMenu(player, procedure)));
                } else {
                    SoundsUtil.playErrorSound(player);
                }
            });
        }).toArray(Fragment[]::new);
    }

}
