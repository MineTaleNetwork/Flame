package cc.minetale.flame.menu.grant;


import cc.minetale.commonlib.api.Grant;
import cc.minetale.commonlib.util.MC;
import cc.minetale.commonlib.util.TimeUtil;
import cc.minetale.flame.Lang;
import cc.minetale.flame.procedure.GrantProcedure;
import cc.minetale.flame.util.ProfileUtil;
import cc.minetale.mlib.canvas.CanvasType;
import cc.minetale.mlib.canvas.FillingType;
import cc.minetale.mlib.canvas.Fragment;
import cc.minetale.mlib.canvas.Menu;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class GrantConfirmMenu extends Menu {

    private final GrantProcedure procedure;
    private boolean shouldCancel = true;

    public GrantConfirmMenu(Player player, GrantProcedure procedure) {
        super(player, Component.text("Grant Confirmation"), CanvasType.ONE_ROW);

        this.procedure = procedure;

        var recipient = procedure.getRecipient();
        var rank = this.procedure.getRank();
        var duration = procedure.getDuration();
        var reason = procedure.getReason();

        var color = rank.getColor();

        setFiller(FillingType.EMPTY_SLOTS);

        setFragment(2, Fragment.of(ItemStack.of(Material.LIME_CONCRETE)
                .withDisplayName(Component.text("Confirm Grant", Style.style(NamedTextColor.GREEN, TextDecoration.ITALIC.as(false)))), event -> {
            ProfileUtil.getProfile(recipient.getId()) // Retrieve up-to-date profile
                    .orTimeout(5, TimeUnit.SECONDS)
                    .whenComplete((profile, throwable) -> {
                        if(profile != null) {
                            profile.addGrant(new Grant(
                                    profile.getId(),
                                    rank,
                                    player.getUuid(),
                                    System.currentTimeMillis(),
                                    reason,
                                    duration
                            ));

                            player.sendMessage(MC.notificationMessage("Grant",
                                    Component.text("Granted " + profile.getName() + " " + rank.getName() + " rank " + (duration == Integer.MAX_VALUE ? "permanently" : "for " + TimeUtil.millisToRoundedTime(duration)), NamedTextColor.GRAY)
                            ));
                        } else {
                            player.sendMessage(Lang.COULD_NOT_LOAD_PROFILE);
                        }
                    });

            this.shouldCancel = false;

            this.procedure.finish();
            this.handleClose(player);
        }));

        setFragment(4, Fragment.of(ItemStack.of(Material.BOOK)
                .withDisplayName(Component.text("Grant Information", Style.style(color, TextDecoration.ITALIC.as(false))))
                .withLore(Arrays.asList(
                        MC.SEPARATOR_32,
                        Component.text().append(
                                Component.text("Player: ", NamedTextColor.GRAY),
                                Component.text(recipient.getName(), color)
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
                        MC.SEPARATOR_32
                )), event -> {

        }));

        setFragment(6, Fragment.of(ItemStack.of(Material.RED_CONCRETE)
                .withDisplayName(Component.text("Cancel Grant", Style.style(NamedTextColor.RED, TextDecoration.ITALIC.as(false)))), event -> {
            this.handleClose(player);
        }));

        openMenu();
    }

    @Override
    public void close() {
        this.getPlayer().closeInventory();

        if(this.shouldCancel)
            this.procedure.cancel();
    }

}
