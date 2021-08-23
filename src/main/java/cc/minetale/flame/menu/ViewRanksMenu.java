package cc.minetale.flame.menu;

import cc.minetale.mlib.guilib.GUI;
import cc.minetale.mlib.guilib.buttons.ActionButton;
import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;
import net.minestom.server.inventory.InventoryType;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

public class ViewRanksMenu {

    public ViewRanksMenu(Player player) {
        try {
            GUI gui = new GUI(InventoryType.CHEST_2_ROW, Component.text("Example GUI"), "test.inventory");

            gui.addButton(0, new ActionButton(ItemStack.of(Material.STONE), event -> {
                if(event.isLeftClick()) {
                    Player viewer = event.getPlayer();

                    viewer.sendMessage(Component.text("Test!"));
                }
            }));

            gui.setReadOnly(true);

            gui.open(player);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
