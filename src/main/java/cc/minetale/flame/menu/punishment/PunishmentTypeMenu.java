package cc.minetale.flame.menu.punishment;

import cc.minetale.flame.menu.DurationMenu;
import cc.minetale.flame.procedure.Procedure;
import cc.minetale.flame.procedure.PunishmentProcedure;
import cc.minetale.mlib.canvas.CanvasType;
import cc.minetale.mlib.canvas.Filler;
import cc.minetale.mlib.canvas.Fragment;
import cc.minetale.mlib.canvas.template.Menu;
import cc.minetale.mlib.util.SoundsUtil;
import cc.minetale.sodium.profile.Profile;
import cc.minetale.sodium.profile.punishment.PunishmentType;
import cc.minetale.sodium.util.Message;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

public class PunishmentTypeMenu extends Menu {

    private final Profile offender;

    public PunishmentTypeMenu(Player player, Profile offender) {
        super(player, Component.text("Punish " + offender.getUsername()), CanvasType.THREE_ROW);

        this.offender = offender;

        setFiller(Filler.EMPTY_SLOTS);

        setButton(12, Fragment.of(ItemStack.of(Material.ANVIL)
                        .withDisplayName(Component.text("Ban " + offender.getUsername(), Message.style(NamedTextColor.GRAY))),
                event -> selectType(player, PunishmentType.BAN)));

        setButton(14, Fragment.of(ItemStack.of(Material.ANVIL)
                        .withDisplayName(Component.text("Mute " + offender.getUsername(), Message.style(NamedTextColor.GRAY))),
                event -> selectType(player, PunishmentType.MUTE)));
    }

    private void selectType(Player player, PunishmentType type) {
        var uuid = player.getUuid();

        if (Procedure.canStartProcedure(uuid)) {
            SoundsUtil.playClickSound(player);

            var procedure = new PunishmentProcedure(uuid, offender, PunishmentProcedure.Type.ADD, PunishmentProcedure.Stage.PROVIDE_TIME);
            procedure.setPunishmentType(type);

            Menu.openMenu(new DurationMenu(player, procedure, new PunishmentReasonMenu(player, procedure)));
        } else {
            SoundsUtil.playErrorSound(player);
        }
    }

}
