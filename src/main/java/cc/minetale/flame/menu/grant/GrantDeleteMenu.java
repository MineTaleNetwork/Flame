package cc.minetale.flame.menu.grant;

import cc.minetale.commonlib.util.MC;
import cc.minetale.flame.procedure.GrantProcedure;
import cc.minetale.mlib.fabric.FabricInventory;
import cc.minetale.mlib.fabric.content.FabricContents;
import cc.minetale.mlib.fabric.content.FabricProvider;
import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;
import net.minestom.server.event.inventory.InventoryCloseEvent;
import net.minestom.server.inventory.InventoryType;

public class GrantDeleteMenu implements FabricProvider {

    private final FabricInventory inventory;
    private final GrantProcedure procedure;

    public GrantDeleteMenu(Player player, GrantProcedure procedure) {
        this.procedure = procedure;
        this.inventory = FabricInventory.builder()
                .provider(this)
                .type(InventoryType.CHEST_1_ROW)
                .title(MC.fixItalics(Component.text("Please confirm the Grant")))
                .build();
        inventory.open(player);
    }

    @Override
    public void init(Player player, FabricContents contents) {
//        Profile profile = this.procedure.getRecipient();
//        Grant grant = this.procedure.getBuilder().build();
//
//        contents.fill(MenuUtil.FILLER);
//
//        for (int i = 1; i <= 3; i++)
//            contents.setSlot(i, ClickableItem.of(ItemStack.of(Material.LIME_CONCRETE)
//                            .withDisplayName(MC.component("Confirm Grant Procedure", NamedTextColor.GREEN)),
//                    event -> {
//                        profile.api().addGrant(grant);
//
//                        this.procedure.finish();
//                        this.inventory.close(player);
//
//                        player.sendMessage(MC.component("You have added a " +  grant.api().getRank().getName() + " grant to " + profile.getName(), NamedTextColor.GREEN));
//                    }));
//
//        for (int i = 5; i <= 7; i++)
//            contents.setSlot(i, ClickableItem.of(ItemStack.of(Material.RED_CONCRETE)
//                            .withDisplayName(MC.component("Cancel Grant Procedure", NamedTextColor.RED)),
//                    event -> {
//
//                        this.procedure.cancel();
//                        this.inventory.close(player);
//
//                        player.sendMessage(MC.component("Cancelled the grant procedure.", NamedTextColor.RED));
//                    }));
    }

    @Override
    public void close(InventoryCloseEvent event) {
        this.procedure.cancel();
    }

}
