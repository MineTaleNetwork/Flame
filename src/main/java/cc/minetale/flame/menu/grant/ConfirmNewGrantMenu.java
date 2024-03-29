package cc.minetale.flame.menu.grant;

import cc.minetale.flame.procedure.GrantProcedure;
import cc.minetale.mlib.canvas.CanvasType;
import cc.minetale.mlib.canvas.Filler;
import cc.minetale.mlib.canvas.Fragment;
import cc.minetale.mlib.canvas.template.Menu;
import cc.minetale.postman.StringUtil;
import cc.minetale.sodium.profile.grant.Grant;
import cc.minetale.sodium.util.Message;
import cc.minetale.sodium.util.TimeUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

import java.util.List;

public class ConfirmNewGrantMenu extends Menu {

    private final GrantProcedure procedure;
    private boolean shouldCancel = true;

    public ConfirmNewGrantMenu(Player player, GrantProcedure procedure) {
        super(player, Component.text("Grant Confirmation"), CanvasType.ONE_ROW);

        this.procedure = procedure;

        var profile = procedure.getProfile();
        var rank = procedure.getRank();
        var duration = procedure.getDuration();
        var reason = procedure.getReason();

        var color = rank.getColor();

        setFiller(Filler.EMPTY_SLOTS);

        setButton(4, Fragment.empty(ItemStack.of(Material.BOOK)
                .withDisplayName(Component.text("Grant Information", Message.style(color)))
                .withLore(List.of(
                        Message.scoreboardSeparator(),
                        Component.text().append(
                                Component.text("Player: ", Message.style(NamedTextColor.GRAY)),
                                Component.text(profile.getUsername(), Message.style(color))
                        ).build(),
                        Component.text().append(
                                Component.text("Rank: ", Message.style(NamedTextColor.GRAY)),
                                Component.text(rank.getName(), Message.style(color))
                        ).build(),
                        Component.text().append(
                                Component.text("Reason: ", Message.style(NamedTextColor.GRAY)),
                                Component.text(reason, Message.style(color))
                        ).build(),
                        Component.text().append(
                                Component.text("Duration: ", Message.style(NamedTextColor.GRAY)),
                                Component.text((duration == Integer.MAX_VALUE ? "Permanent" : TimeUtil.millisToRoundedTime(duration)), Message.style(color))
                        ).build(),
                        Message.scoreboardSeparator()
                ))));

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

            player.sendMessage(Message.notification("Punishment",
                    Component.text("Successfully " + (duration == Integer.MAX_VALUE ? "permanently " : "temporarily " + TimeUtil.millisToRoundedTime(duration) + " granted " + profile.getChatFormat() + " " + rank.getName()), NamedTextColor.GRAY)
            ));

            shouldCancel = false;

            procedure.finish();
            handleClose(player);
        }));

        setButton(6, Fragment.of(ItemStack.of(Material.RED_CONCRETE)
                .withDisplayName(Component.text("Cancel Grant", Message.style(NamedTextColor.RED))), event -> handleClose(player)));
    }

    @Override
    public void close() {
        if (shouldCancel)
            procedure.cancel();
    }

}
