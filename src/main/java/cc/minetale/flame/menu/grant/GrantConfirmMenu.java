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

public class GrantConfirmMenu implements FabricProvider {

    private final FabricInventory inventory;
    private final GrantProcedure procedure;

    public GrantConfirmMenu(Player player, GrantProcedure procedure) {
        this.procedure = procedure;
        this.inventory = FabricInventory.builder()
                .provider(this)
                .type(InventoryType.CHEST_1_ROW)
                .title(MC.Style.component("Please confirm the Grant"))
                .build();
        inventory.open(player);
    }

    @Override
    public void init(Player player, FabricContents contents) {
        Profile profile = this.procedure.getRecipient();
        Grant grant = this.procedure.getGrant();
        Rank rank = this.procedure.getGrant().api().getRank();

        contents.fill(MenuUtils.FILLER);

        for (int i = 1; i <= 3; i++)
            contents.setSlot(i, ClickableItem.of(ItemStack.of(Material.LIME_CONCRETE)
                            .withDisplayName(MC.Style.component("Confirm Grant Procedure", MC.CC.GREEN)),
                    event -> {

                        grant.setAddedAt(System.currentTimeMillis());

                        profile.api().addGrant(grant);

                        this.procedure.finish();
                        this.inventory.close(player);

                        player.sendMessage(MC.Style.component("You have updated " + profile.getName() + "'s rank to " + rank.getName(), MC.CC.GREEN));
                    }));

        for (int i = 5; i <= 7; i++)
            contents.setSlot(i, ClickableItem.of(ItemStack.of(Material.RED_CONCRETE)
                            .withDisplayName(MC.Style.component("Cancel Grant Procedure", MC.CC.RED)),
                    event -> {

                        procedure.cancel();
                        inventory.close(player);

                        player.sendMessage(MC.Style.component("Cancelled the grant procedure.", MC.CC.RED));
                    }));
    }

    @Override
    public void close(InventoryCloseEvent event) {
        Player player = event.getPlayer();
        GrantProcedure procedure = GrantProcedure.getByPlayer(player.getUuid());

//        if (procedure != null && procedure.getStage() == GrantProcedure.Stage.REQUIRE_CONFIRMATION) {
//            procedure.cancel();
//            player.sendMessage(MC.Style.component("Cancelled the grant procedure.", MC.CC.RED));
//        }
    }

}
