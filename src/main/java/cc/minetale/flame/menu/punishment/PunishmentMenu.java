package cc.minetale.flame.menu.punishment;

import cc.minetale.commonlib.modules.profile.Profile;
import cc.minetale.commonlib.util.MC;
import cc.minetale.flame.util.MenuUtils;
import cc.minetale.mlib.fabric.ClickableItem;
import cc.minetale.mlib.fabric.FabricInventory;
import cc.minetale.mlib.fabric.content.FabricContents;
import cc.minetale.mlib.fabric.content.FabricProvider;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.minestom.server.entity.Player;
import net.minestom.server.inventory.InventoryType;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

public class PunishmentMenu implements FabricProvider {

    private final FabricInventory inventory;
    private final Profile offender;

    public PunishmentMenu(Player player, Profile offender) {
        this.inventory = FabricInventory.builder()
                .provider(this)
                .type(InventoryType.CHEST_3_ROW)
                .title(MC.Style.component("Punish " + offender.getName()))
                .build();
        this.offender = offender;
        this.inventory.open(player);
    }

    @Override
    public void init(Player player, FabricContents contents) {
        contents.fill(MenuUtils.FILLER);

        contents.setSlot(13, ClickableItem.of(ItemStack.of(Material.ANVIL)
                .withDisplayName(MC.Style.component("Punish " + offender.getName(), MC.CC.GRAY)),
                event -> {
                    player.playSound(Sound.sound(Key.key("ui.button.click"), Sound.Source.MASTER, 1F, 2.0F));
            new PunishmentTypeMenu(event.getPlayer(), this.offender);
                }));
    }
}
