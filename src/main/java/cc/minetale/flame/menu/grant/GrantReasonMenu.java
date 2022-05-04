package cc.minetale.flame.menu.grant;

import cc.minetale.flame.procedure.GrantProcedure;
import cc.minetale.flame.procedure.Procedure;
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

import java.util.stream.Stream;

public class GrantReasonMenu extends PaginatedMenu {

    private final GrantProcedure procedure;
    private boolean shouldCancel = true;

    public GrantReasonMenu(Player player, GrantProcedure procedure) {
        super(player, Component.text("Reason Selection"), CanvasType.FOUR_ROW);

        this.procedure = procedure;
    }

    @Override
    public void close() {
        if(shouldCancel)
            procedure.cancel();
    }

    @Override
    public Fragment[] getPaginatedFragments(Player player) {
        return Stream.of(
                "Donation Issue",
                "Promoted",
                "Demoted",
                "Admin Discretion",
                "Event Winner",
                "Custom"
        ).map(reason -> Fragment.of(ItemStack.of(Material.PAPER)
                .withDisplayName(Component.text(reason, Message.style(NamedTextColor.GRAY))), event -> {
            SoundsUtil.playClickSound(player);

            if (reason.equals("Custom")) {
                procedure.setStage(Procedure.Stage.PROVIDE_CONFIRMATION);
                shouldCancel = false;

                handleClose(player);

                player.sendMessage(Component.text("Type the reason for adding this grant in chat...", NamedTextColor.GREEN));
            } else {
                procedure.setReason(reason);
                Menu.openMenu(new ConfirmNewGrantMenu(player, procedure));
            }
        })).toArray(Fragment[]::new);
    }

}
