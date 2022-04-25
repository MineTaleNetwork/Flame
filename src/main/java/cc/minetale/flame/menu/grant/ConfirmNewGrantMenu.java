package cc.minetale.flame.menu.grant;


import cc.minetale.flame.procedure.GrantProcedure;
import cc.minetale.flame.util.FlamePlayer;
import cc.minetale.mlib.canvas.CanvasType;
import cc.minetale.mlib.canvas.Filler;
import cc.minetale.mlib.canvas.Fragment;
import cc.minetale.mlib.canvas.template.Menu;
import cc.minetale.postman.StringUtil;
import cc.minetale.sodium.profile.Profile;
import cc.minetale.sodium.profile.grant.Grant;
import cc.minetale.sodium.util.Message;
import cc.minetale.sodium.util.TimeUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

import java.util.Arrays;

public class ConfirmNewGrantMenu extends Menu {

    private final GrantProcedure procedure;
    private boolean shouldCancel = true;

    public ConfirmNewGrantMenu(Player player, GrantProcedure procedure) {
        super(player, Component.text("Grant Confirmation"), CanvasType.ONE_ROW);

        this.procedure = procedure;

        var profile = procedure.getProfile();
        var type = procedure.getType();
        var rank = procedure.getRank();
        var duration = procedure.getDuration();
        var reason = procedure.getReason();

        var color = rank.getColor();

        setFiller(Filler.EMPTY_SLOTS);

        setButton(2, Fragment.of(ItemStack.of(Material.LIME_CONCRETE)
                .withDisplayName(Component.text("Confirm Grant", Message.style(NamedTextColor.GREEN))), event -> {
            profile.issueGrant(new Grant(
                    StringUtil.generateId(),
                    profile.getUuid(),
                    player.getUuid(),
                    System.currentTimeMillis(),
                    reason,
                    duration,
                    rank
            ));

            player.sendMessage(Message.notification("Grant",
                    Component.text("Granted " + profile.getUsername() + " " + rank.getName() + " rank " + (duration == Integer.MAX_VALUE ? "permanently" : "for " + TimeUtil.millisToRoundedTime(duration)), NamedTextColor.GRAY)
            ));

            shouldCancel = false;

            procedure.finish();
            handleClose(player);
        }));

        setButton(4, Fragment.empty(ItemStack.of(Material.BOOK)
                .withDisplayName(Component.text("Grant Information", Style.style(color, TextDecoration.ITALIC.as(false))))
                .withLore(Arrays.asList(
                        Message.scoreboardSeparator(),
                        Component.text().append(
                                Component.text("Player: ", NamedTextColor.GRAY),
                                Component.text(profile.getUsername(), color)
                        ).decoration(TextDecoration.ITALIC, false).build(),
                        Component.text().append(
                                Component.text("Rank: ", NamedTextColor.GRAY),
                                Component.text(rank.getName(), color)
                        ).decoration(TextDecoration.ITALIC, false).build(),
                        Component.text().append(
                                Component.text("Reason: ", NamedTextColor.GRAY),
                                Component.text(reason, color)
                        ).decoration(TextDecoration.ITALIC, false).build(),
                        Component.text().append(
                                Component.text("Duration: ", NamedTextColor.GRAY),
                                Component.text((duration == Integer.MAX_VALUE ? "Permanent" : TimeUtil.millisToRoundedTime(duration)), color)
                        ).decoration(TextDecoration.ITALIC, false).build(),
                        Message.scoreboardSeparator()
                ))));

        setItems();


        setButton(6, Fragment.of(ItemStack.of(Material.RED_CONCRETE)
                .withDisplayName(Component.text("Cancel Grant" + (type == GrantProcedure.Type.REMOVE ? " Removal" : ""), Style.style(NamedTextColor.RED, TextDecoration.ITALIC.as(false)))), event -> {
            handleClose(player);
        }));
    }

    @Override
    public void close() {
        if (shouldCancel)
            procedure.cancel();
    }

}
