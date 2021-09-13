package cc.minetale.flame.menu.punishment;

import cc.minetale.commonlib.util.Duration;
import cc.minetale.commonlib.util.MC;
import cc.minetale.flame.menu.impl.DurationType;
import cc.minetale.flame.procedure.PunishmentProcedure;
import cc.minetale.flame.util.FlameUtil;
import cc.minetale.flame.util.MenuUtils;
import cc.minetale.mlib.fabric.FabricInventory;
import cc.minetale.mlib.fabric.content.FabricContents;
import cc.minetale.mlib.fabric.content.FabricProvider;
import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;
import net.minestom.server.event.inventory.InventoryCloseEvent;
import net.minestom.server.inventory.InventoryType;

public class PunishmentDurationMenu implements FabricProvider, DurationType {

    private final FabricInventory inventory;
    private final PunishmentProcedure procedure;
    private boolean shouldCancel = true;

    public PunishmentDurationMenu(Player player, PunishmentProcedure procedure) {
        this.inventory = FabricInventory.builder()
                .provider(this)
                .type(InventoryType.CHEST_4_ROW)
                .title(MC.component("Select a Punishment Duration"))
                .build();
        this.procedure = procedure;
        this.inventory.open(player);
    }

    @Override
    public void init(Player player, FabricContents contents) {
        MenuUtils.addDuration(player, contents, this);
    }

    @Override
    public void close(InventoryCloseEvent event) {
        if(this.shouldCancel) {
            this.procedure.cancel();
        }
    }

    @Override
    public void selectDuration(Player player, Duration duration) {
        FlameUtil.playClickSound(player);

        if(duration == null) {
            this.procedure.setStage(PunishmentProcedure.Stage.PROVIDE_TIME);

            this.shouldCancel = false;

            this.inventory.close(player);

            player.sendMessage(Component.text("Type the amount of time you would like to add this punishment for in chat...", MC.CC.GREEN.getTextColor()));
        } else {
            PunishmentProcedure.Builder builder = this.procedure.getBuilder();

            builder.duration(duration.getValue());

            new PunishmentReasonMenu(player, procedure);
        }
    }

}
