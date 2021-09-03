package cc.minetale.flame.menu.grant;

import cc.minetale.commonlib.modules.grant.Grant;
import cc.minetale.commonlib.util.Duration;
import cc.minetale.commonlib.util.MC;
import cc.minetale.flame.procedure.GrantProcedure;
import cc.minetale.mlib.fabric.ClickableItem;
import cc.minetale.mlib.fabric.FabricInventory;
import cc.minetale.mlib.fabric.content.FabricContents;
import cc.minetale.mlib.fabric.content.FabricProvider;
import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;
import net.minestom.server.event.inventory.InventoryCloseEvent;
import net.minestom.server.inventory.InventoryType;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

public class GrantDurationMenu implements FabricProvider {

    private final GrantProcedure procedure;

    public GrantDurationMenu(Player player, GrantProcedure procedure) {
        this.procedure = procedure;
        FabricInventory inventory = FabricInventory.builder()
                .provider(this)
                .type(InventoryType.CHEST_3_ROW)
                .title(MC.Style.component("Select a Grant Duration"))
                .build();
        inventory.open(player);
    }

    @Override
    public void init(Player player, FabricContents contents) {
        Grant grant = this.procedure.getGrant();

        contents.fill(ClickableItem.empty(ItemStack.of(Material.GRAY_STAINED_GLASS_PANE).withDisplayName(Component.empty())));

        contents.setSlot(10, ClickableItem.of(ItemStack.of(Material.LIME_CONCRETE)
                        .withDisplayName(MC.Style.component("7 Days", MC.CC.GREEN)),
                event -> {
                    Duration duration = Duration.fromString("7d");

                    grant.setDuration(duration.getValue());
                    this.procedure.setGrant(grant);

//                    this.procedure.setStage(GrantProcedure.Stage.REQUIRE_REASON);

                    new GrantReasonMenu(player, this.procedure);
                }));

        contents.setSlot(12, ClickableItem.of(ItemStack.of(Material.YELLOW_CONCRETE)
                        .withDisplayName(MC.Style.component("30 Days", MC.CC.YELLOW)),
                event -> {
                    Duration duration = Duration.fromString("30d");

                    grant.setDuration(duration.getValue());
                    this.procedure.setGrant(grant);

//                    this.procedure.setStage(GrantProcedure.Stage.REQUIRE_REASON);

                    new GrantReasonMenu(player, this.procedure);
                }));

        contents.setSlot(14, ClickableItem.of(ItemStack.of(Material.ORANGE_CONCRETE)
                        .withDisplayName(MC.Style.component("Permanent", MC.CC.GOLD)),
                event -> {
                    Duration duration = Duration.fromString("perm");

                    grant.setDuration(duration.getValue());
                    this.procedure.setGrant(grant);

//                    this.procedure.setStage(GrantProcedure.Stage.REQUIRE_REASON);

                    new GrantReasonMenu(player, this.procedure);
                }));

        contents.setSlot(16, ClickableItem.of(ItemStack.of(Material.RED_CONCRETE)
                        .withDisplayName(MC.Style.component("Custom", MC.CC.RED)),
                event -> {
//                    this.procedure.setStage(GrantProcedure.Stage.PROVIDE_CUSTOM_TIME);

                    player.closeInventory();
                    player.sendMessage(MC.Style.component("Type the amount of time you would like to grant this rank for...", MC.CC.GREEN));
                }));
    }

    @Override
    public void close(InventoryCloseEvent event) {
        Player player = event.getPlayer();
        GrantProcedure procedure = GrantProcedure.getByPlayer(player.getUuid());

//        if (procedure != null && procedure.getStage() == GrantProcedure.Stage.REQUIRE_TIME) {
//            procedure.cancel();
//            player.sendMessage(MC.Style.component("Cancelled the grant procedure.", MC.CC.RED));
//        }
    }

}
