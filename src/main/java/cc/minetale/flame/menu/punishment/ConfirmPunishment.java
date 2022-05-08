package cc.minetale.flame.menu.punishment;

import cc.minetale.flame.procedure.Procedure;
import cc.minetale.flame.procedure.PunishmentProcedure;
import cc.minetale.flame.util.FlamePlayer;
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

import java.util.ArrayList;
import java.util.List;

public class ConfirmPunishment extends Menu {

    private final PunishmentProcedure procedure;

    public ConfirmPunishment(Player player, PunishmentProcedure procedure) {
        super(player, Component.text("Confirm Punishment"), CanvasType.ONE_ROW);

        this.procedure = procedure;

        var profile = procedure.getProfile();

        var punishment = new Punishment(
                StringUtil.generateId(),
                profile.getUuid(),
                player.getUuid(),
                System.currentTimeMillis(),
                procedure.getReason(),
                procedure.getDuration(),
                procedure.getPunishmentType()
        );

        if(procedure.getType() == Procedure.Type.REMOVE) {
            punishment = Punishment.getPunishment(procedure.getPunishment());

            if(punishment == null) {
                handleClose(player);
                player.sendMessage(Component.text("An error has occurred while loading the punishment.", NamedTextColor.RED));
                return;
            }
        }

        var lore = new ArrayList<>(List.of(
                Message.scoreboardSeparator(),
                Component.text().append(
                        Component.text("Player: ", Message.style(NamedTextColor.GRAY)),
                        Component.text(profile.getUsername(), Message.style(NamedTextColor.RED))
                ).build(),
                Component.text().append(
                        Component.text("Punishment: ", Message.style(NamedTextColor.GRAY)),
                        Component.text(punishment.getType().getReadable(), Message.style(NamedTextColor.RED))
                ).build(),
                Component.text().append(
                        Component.text("Reason: ", Message.style(NamedTextColor.GRAY)),
                        Component.text(punishment.getAddedReason(), Message.style(NamedTextColor.RED))
                ).build(),
                Component.text().append(
                        Component.text("Duration: ", Message.style(NamedTextColor.GRAY)),
                        Component.text((punishment.getDuration() == Integer.MAX_VALUE ? "Permanent" : TimeUtil.millisToRoundedTime(punishment.getDuration())), Message.style(NamedTextColor.RED))
                ).build()
        ));

        if(procedure.getType() == Procedure.Type.REMOVE) {
            lore.add(Component.text().append(
                    Component.text("Remove Reason: ", Message.style(NamedTextColor.GRAY)),
                    Component.text(procedure.getReason(), Message.style(NamedTextColor.RED))
            ).build());
        }

        lore.add(Message.scoreboardSeparator());

        setFiller(Filler.EMPTY_SLOTS);

        setButton(4, Fragment.empty(ItemStack.of(Material.BOOK)
                .withDisplayName(Component.text("Punishment Information", Message.style(NamedTextColor.RED)))
                .withLore(lore)));

        final var finalPunishment = punishment;

        setButton(2, Fragment.of(ItemStack.of(Material.LIME_CONCRETE)
                .withDisplayName(Component.text("Confirm", Message.style(NamedTextColor.GREEN))), event -> {

            var playerProfile = FlamePlayer.fromPlayer(player).getProfile();

            switch (procedure.getType()) {
                case ADD -> {
                    profile.issuePunishment(finalPunishment);
                    player.sendMessage(Message.notification("Punishment",
                            Component.text("Successfully " + (finalPunishment.getDuration() == Integer.MAX_VALUE ? "permanently " : "temporarily " + TimeUtil.millisToRoundedTime(finalPunishment.getDuration()) + finalPunishment.getContext() + " " + profile.getChatFormat()), NamedTextColor.GRAY)
                    ));
                }
                case REMOVE -> {
                    profile.removePunishment(finalPunishment, playerProfile.getUuid(), System.currentTimeMillis(), procedure.getReason());
                    player.sendMessage(Message.notification("Punishment",
                            Component.text("Successfully removed a punishment from " + profile.getChatFormat(), NamedTextColor.GRAY)
                    ));
                }
            }

            procedure.setCanceled(false);

            procedure.finish();
            handleClose(player);
        }));

        setButton(6, Fragment.of(ItemStack.of(Material.RED_CONCRETE)
                .withDisplayName(Component.text("Cancel", Message.style(NamedTextColor.RED))), event -> handleClose(player)));
    }

    @Override
    public void close() {
        if (procedure.isCanceled())
            procedure.cancel();
    }

}
