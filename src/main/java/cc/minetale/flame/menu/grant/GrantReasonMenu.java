package cc.minetale.flame.menu.grant;

import cc.minetale.flame.procedure.GrantProcedure;
import cc.minetale.mlib.fabric.FabricInventory;
import cc.minetale.mlib.fabric.content.FabricContents;
import cc.minetale.mlib.fabric.content.FabricProvider;
import net.minestom.server.entity.Player;
import net.minestom.server.event.inventory.InventoryCloseEvent;
import net.minestom.server.inventory.InventoryType;

public class GrantReasonMenu implements FabricProvider {

    private final GrantProcedure procedure;
    private final FabricInventory inventory;

    public GrantReasonMenu(Player player, GrantProcedure procedure) {
        this.procedure = procedure;
        this.inventory = FabricInventory.builder()
                .provider(this)
                .type(InventoryType.CHEST_3_ROW)
//                .title(MC.component("Select a Grant Reason"))
                .build();
        inventory.open(player);
    }

    @Override
    public void init(Player player, FabricContents contents) {
        // TODO

//        Grant grant = this.procedure.getGrant();
//
//        contents.fill(ClickableItem.empty(ItemStack.of(Material.GRAY_STAINED_GLASS_PANE).withDisplayName(Component.empty())));
//
//        contents.setSlot(10, ClickableItem.of(ItemStack.of(Material.LIME_CONCRETE)
//                        .withDisplayName(MC.component("Promoted", NamedTextColor.GREEN)),
//                event -> {
//                    grant.setAddedReason("Promoted");
//                    procedure.setGrant(grant);
//
//                    procedure.setStage(GrantProcedure.Stage.PROVIDE_CONFIRMATION);
//
//                    new GrantConfirmMenu(player, procedure);
//                }));
//
//        contents.setSlot(12, ClickableItem.of(ItemStack.of(Material.YELLOW_CONCRETE)
//                        .withDisplayName(MC.component("Demoted", NamedTextColor.YELLOW)),
//                event -> {
//                    grant.setAddedReason("Demoted");
//                    procedure.setGrant(grant);
//
//                    procedure.setStage(GrantProcedure.Stage.PROVIDE_CONFIRMATION);
//
//                    new GrantConfirmMenu(player, procedure);
//                }));
//
//        contents.setSlot(14, ClickableItem.of(ItemStack.of(Material.ORANGE_CONCRETE)
//                        .withDisplayName(MC.component("Granted", NamedTextColor.GOLD)),
//                event -> {
//
//                    grant.setAddedReason("Granted");
//                    procedure.setGrant(grant);
//
//                    procedure.setStage(GrantProcedure.Stage.PROVIDE_CONFIRMATION);
//
//                    new GrantConfirmMenu(player, procedure);
//                }));
//
//        contents.setSlot(16, ClickableItem.of(ItemStack.of(Material.RED_CONCRETE)
//                        .withDisplayName(MC.component("Custom", NamedTextColor.RED)),
//                event -> {
//                    procedure.setGrant(grant);
//
//                    procedure.setStage(GrantProcedure.Stage.PROVIDE_CONFIRMATION);
//
//                    player.closeInventory();
//                    player.sendMessage(MC.component("Type a reason for adding this grant in chat...", NamedTextColor.GREEN));
//                }));

    }

    @Override
    public void close(InventoryCloseEvent event) {
        Player player = event.getPlayer();
        GrantProcedure procedure = GrantProcedure.getByPlayer(player.getUuid());

        if (procedure != null && procedure.getStage() == GrantProcedure.Stage.PROVIDE_REASON) {
            procedure.cancel();
//            player.sendMessage(MC.component("Cancelled the grant procedure.", NamedTextColor.RED));
        }
    }

}
