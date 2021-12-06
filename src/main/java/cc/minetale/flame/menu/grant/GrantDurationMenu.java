package cc.minetale.flame.menu.grant;

import cc.minetale.commonlib.util.Duration;
import cc.minetale.flame.menu.DurationType;
import cc.minetale.flame.procedure.GrantProcedure;
import cc.minetale.flame.util.MenuUtil;
import cc.minetale.mlib.fabric.FabricInventory;
import cc.minetale.mlib.fabric.content.FabricContents;
import cc.minetale.mlib.fabric.content.FabricProvider;
import net.minestom.server.entity.Player;
import net.minestom.server.event.inventory.InventoryCloseEvent;
import net.minestom.server.inventory.InventoryType;

public class GrantDurationMenu implements FabricProvider, DurationType {

    private final FabricInventory inventory;
    private final GrantProcedure procedure;
    private boolean shouldCancel = true;

    public GrantDurationMenu(Player player, GrantProcedure procedure) {
        this.inventory = FabricInventory.builder()
                .provider(this)
                .type(InventoryType.CHEST_4_ROW)
//                .title(MC.component("Select a Punishment Duration"))
                .build();
        this.procedure = procedure;
        this.inventory.open(player);
    }

    @Override
    public void init(Player player, FabricContents contents) {
        MenuUtil.addDuration(player, contents, this);
    }

    @Override
    public void close(InventoryCloseEvent event) {
        if(this.shouldCancel) {
            this.procedure.cancel();
        }
    }

    @Override
    public void selectDuration(Player player, Duration duration) {
//        FlameUtil.playClickSound(player);
//
//        if(duration == null) {
//            this.procedure.setStage(GrantProcedure.Stage.PROVIDE_TIME);
//
//            this.shouldCancel = false;
//
//            this.inventory.close(player);
//
//            player.sendMessage(Component.text("Type the amount of time you would like to add this grant for in chat...", NamedTextColor.GREEN));
//        } else {
//            GrantProcedure.Builder builder = this.procedure.getBuilder();
//
//            builder.duration(duration.getValue());
//
//            new GrantReasonMenu(player, procedure);
//        }
    }

}
