package cc.minetale.flame.menu;

import cc.minetale.commonlib.util.MC;
import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.PlayerSkin;
import net.minestom.server.inventory.Inventory;
import net.minestom.server.inventory.InventoryType;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.item.metadata.PlayerHeadMeta;

import java.util.Arrays;
import java.util.UUID;

public class ProfileMenu extends Inventory {

    public ProfileMenu(Player player) {
        super(InventoryType.CHEST_5_ROW, "Server Selector");

        player.openInventory(this);

        this.addInventoryCondition((viewer, slot, clickType, result) -> {
            result.setCancel(true);
        });

        for(int i = 0; i < this.getSize(); i++) {
            this.setItemStack(i, ItemStack.of(Material.GRAY_STAINED_GLASS_PANE).withDisplayName(Component.empty()));
        }

        UUID uuid = player.getUuid();

        this.setItemStack(0, ItemStack.of(Material.PLAYER_HEAD)
                .withDisplayName(MC.Style.component("My Profile ", MC.CC.GREEN))
                .withLore(Arrays.asList(
                        MC.Style.component("Right Click to open your profile!", MC.CC.GRAY) // Rank
                ))
                .withMeta(PlayerHeadMeta.class, meta -> meta.skullOwner(uuid).playerSkin(PlayerSkin.fromUuid(uuid.toString())))
        );
    }

}
