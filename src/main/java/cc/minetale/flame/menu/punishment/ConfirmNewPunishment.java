package cc.minetale.flame.menu.punishment;

import cc.minetale.flame.procedure.PunishmentProcedure;
import cc.minetale.mlib.canvas.CanvasType;
import cc.minetale.mlib.canvas.Filler;
import cc.minetale.mlib.canvas.Fragment;
import cc.minetale.mlib.canvas.template.Menu;
import cc.minetale.postman.StringUtil;
import cc.minetale.sodium.profile.punishment.Punishment;
import cc.minetale.sodium.util.Message;
import cc.minetale.sodium.util.TimeUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

import java.util.List;

public class ConfirmNewPunishment extends Menu {

    private final PunishmentProcedure procedure;
    private boolean shouldCancel = true;

    public ConfirmNewPunishment(Player player, PunishmentProcedure procedure) {
        super(player, Component.text("Confirm Punishment"), CanvasType.ONE_ROW);

        this.procedure = procedure;

        var profile = procedure.getProfile();
        var punishment = procedure.getPunishmentType();
        var duration = procedure.getDuration();
        var reason = procedure.getReason();

        setFiller(Filler.EMPTY_SLOTS);

        setButton(4, Fragment.empty(ItemStack.of(Material.BOOK)
                .withDisplayName(Component.text("Punishment Information", Message.style(NamedTextColor.RED)))
                .withLore(List.of(
                        Message.scoreboardSeparator(),
                        Component.text().append(
                                Component.text("Player: ", Message.style(NamedTextColor.GRAY)),
                                Component.text(profile.getUsername(), Message.style(NamedTextColor.RED))
                        ).build(),
                        Component.text().append(
                                Component.text("Punishment: ", Message.style(NamedTextColor.GRAY)),
                                Component.text(punishment.getReadable(), Message.style(NamedTextColor.RED))
                        ).build(),
                        Component.text().append(
                                Component.text("Reason: ", Message.style(NamedTextColor.GRAY)),
                                Component.text(reason, Message.style(NamedTextColor.RED))
                        ).build(),
                        Component.text().append(
                                Component.text("Duration: ", NamedTextColor.GRAY),
                                Component.text((duration == Integer.MAX_VALUE ? "Permanent" : TimeUtil.millisToRoundedTime(duration)), Message.style(NamedTextColor.RED))
                        ).build(),
                        Message.scoreboardSeparator()
                ))));

        setButton(2, Fragment.of(ItemStack.of(Material.LIME_CONCRETE)
                .withDisplayName(Component.text("Confirm Punishment", Message.style(NamedTextColor.GREEN))), event -> {
            profile.issuePunishment(new Punishment(
                    StringUtil.generateId(),
                    profile.getUuid(),
                    player.getUuid(),
                    System.currentTimeMillis(),
                    reason,
                    duration,
                    punishment
            ));

            player.sendMessage(Message.notification("Punishment",
                    Component.text("Successfully " + (duration == Integer.MAX_VALUE ? "permanently " : "temporarily " + TimeUtil.millisToRoundedTime(duration) + punishment.getContext() + " " + profile.getChatFormat()), NamedTextColor.GRAY)
            ));

            shouldCancel = false;

            procedure.finish();
            handleClose(player);
        }));

        setButton(6, Fragment.of(ItemStack.of(Material.RED_CONCRETE)
                .withDisplayName(Component.text("Cancel Punishment", Message.style(NamedTextColor.RED))), event -> handleClose(player)));
    }

    @Override
    public void close() {
        if (shouldCancel)
            procedure.cancel();
    }

}
