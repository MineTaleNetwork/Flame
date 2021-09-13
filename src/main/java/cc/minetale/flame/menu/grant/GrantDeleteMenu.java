package cc.minetale.flame.menu.grant;


import cc.minetale.commonlib.modules.grant.Grant;
import cc.minetale.commonlib.modules.profile.Profile;
import cc.minetale.commonlib.modules.rank.Rank;
import cc.minetale.commonlib.util.MC;
import cc.minetale.flame.procedure.GrantProcedure;
import cc.minetale.flame.util.MenuUtils;
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

public class GrantDeleteMenu implements FabricProvider {

    private final FabricInventory inventory;
    private final GrantProcedure procedure;

    public GrantDeleteMenu(Player player, GrantProcedure procedure) {
        this.procedure = procedure;
        this.inventory = FabricInventory.builder()
                .provider(this)
                .type(InventoryType.CHEST_1_ROW)
                .title(MC.component("Please confirm the Grant"))
                .build();
        inventory.open(player);
    }

    @Override
    public void init(Player player, FabricContents contents) {
        Profile profile = this.procedure.getRecipient();
        Grant grant = this.procedure.getBuilder().build();

        contents.fill(MenuUtils.FILLER);

        for (int i = 1; i <= 3; i++)
            contents.setSlot(i, ClickableItem.of(ItemStack.of(Material.LIME_CONCRETE)
                            .withDisplayName(MC.component("Confirm Grant Procedure", MC.CC.GREEN)),
                    event -> {
                        profile.api().addGrant(grant);

                        this.procedure.finish();
                        this.inventory.close(player);

                        player.sendMessage(MC.component("You have added a " +  grant.api().getRank().getName() + " grant to " + profile.getName(), MC.CC.GREEN));
                    }));

        for (int i = 5; i <= 7; i++)
            contents.setSlot(i, ClickableItem.of(ItemStack.of(Material.RED_CONCRETE)
                            .withDisplayName(MC.component("Cancel Grant Procedure", MC.CC.RED)),
                    event -> {

                        this.procedure.cancel();
                        this.inventory.close(player);

                        player.sendMessage(MC.component("Cancelled the grant procedure.", MC.CC.RED));
                    }));
    }

    @Override
    public void close(InventoryCloseEvent event) {
        this.procedure.cancel();
    }

}
