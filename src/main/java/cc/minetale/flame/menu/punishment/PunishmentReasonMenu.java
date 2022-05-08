package cc.minetale.flame.menu.punishment;

import cc.minetale.flame.procedure.Procedure;
import cc.minetale.flame.procedure.PunishmentProcedure;
import cc.minetale.mlib.canvas.CanvasType;
import cc.minetale.mlib.canvas.Fragment;
import cc.minetale.mlib.canvas.template.Menu;
import cc.minetale.mlib.canvas.template.PaginatedMenu;
import cc.minetale.mlib.util.SoundsUtil;
import cc.minetale.sodium.util.Message;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

import java.util.ArrayList;
import java.util.List;

public class PunishmentReasonMenu extends PaginatedMenu {

    private final PunishmentProcedure procedure;

    public PunishmentReasonMenu(Player player, PunishmentProcedure procedure) {
        super(player, Component.text("Reason Selection"), CanvasType.FOUR_ROW);

        this.procedure = procedure;
    }

    @Override
    public Fragment[] getPaginatedFragments(Player player) {
        var punishments = new ArrayList<String>();

        switch (procedure.getType()) {
            case ADD -> {
                switch (procedure.getPunishmentType()) {
                    case BAN -> punishments.addAll(List.of(
                            "Cheating",
                            "Admin Discretion",
                            "Custom"
                    ));
                    case MUTE -> punishments.addAll(List.of(
                            "Chat Flooding/Chat Spamming",
                            "Advertising",
                            "Discrimination/Racism",
                            "Admin Discretion",
                            "Custom"
                    ));
                }
            }
            case REMOVE -> punishments.addAll(List.of(
                    "Appealed",
                    "False",
                    "Admin Discretion",
                    "Custom"
            ));
        }


        return punishments.stream().map(reason -> Fragment.of(ItemStack.of(Material.PAPER)
                .withDisplayName(Component.text(reason, Message.style(NamedTextColor.GRAY))), event -> {
            SoundsUtil.playClickSound(player);

            if (reason.equals("Custom")) {
                procedure.setStage(Procedure.Stage.PROVIDE_CONFIRMATION);
                procedure.setCanceled(false);

                handleClose(player);

                player.sendMessage(Component.text("Type the reason for adding this punishment in chat...", NamedTextColor.GREEN));
            } else {
                procedure.setReason(reason);

                switch (procedure.getType()) {
                    case ADD -> {
                        Menu.openMenu(new ConfirmPunishment(player, procedure));
                    }

                    case REMOVE -> {
                        Menu.openMenu(new ConfirmPunishment(player, procedure));
                    }
                }
            }
        })).toArray(Fragment[]::new);
    }

    @Override
    public void close() {
        if (procedure.isCanceled())
            procedure.cancel();
    }

}
