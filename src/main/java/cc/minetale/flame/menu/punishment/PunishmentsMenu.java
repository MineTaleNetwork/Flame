package cc.minetale.flame.menu.punishment;

import cc.minetale.mlib.canvas.CanvasType;
import cc.minetale.mlib.canvas.Fragment;
import cc.minetale.mlib.canvas.template.PaginatedMenu;
import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;

public class PunishmentsMenu extends PaginatedMenu {

    public PunishmentsMenu(Player player, Component title, CanvasType type) {
        super(player, title, type);
    }

    @Override
    public Fragment[] getPaginatedFragments(Player player) {
        return new Fragment[0];
    }

}
