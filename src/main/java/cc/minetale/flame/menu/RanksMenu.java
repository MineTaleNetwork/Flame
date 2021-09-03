package cc.minetale.flame.menu;

import cc.minetale.commonlib.modules.rank.Rank;
import cc.minetale.commonlib.util.MC;
import cc.minetale.flame.util.FlameUtil;
import cc.minetale.flame.util.MenuUtils;
import cc.minetale.mlib.fabric.ClickableItem;
import cc.minetale.mlib.fabric.FabricInventory;
import cc.minetale.mlib.fabric.content.FabricContents;
import cc.minetale.mlib.fabric.content.FabricProvider;
import cc.minetale.mlib.fabric.content.Pagination;
import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;
import net.minestom.server.event.inventory.InventoryOpenEvent;
import net.minestom.server.inventory.Inventory;
import net.minestom.server.inventory.InventoryType;
import net.minestom.server.item.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RanksMenu implements FabricProvider {

    private final FabricInventory inventory;

    public RanksMenu(Player player) {
        this.inventory = FabricInventory.builder()
                .provider(this)
                .type(InventoryType.CHEST_4_ROW)
                .title(Component.text("Ranks"))
                .build();

        this.inventory.open(player);
    }

    @Override
    public void init(Player player, FabricContents contents) {
        contents.fill(MenuUtils.FILLER);
        contents.fill(MenuUtils.AIR, 9, 26);

        ClickableItem[] items = new ClickableItem[Rank.getRanks().size()];

        int i = 0;

        List<Rank> sortedRanks = new ArrayList<>(Rank.getRanks().values());
        sortedRanks.sort(Rank.COMPARATOR);

        for (Rank rank : sortedRanks) {
            MC.CC color = rank.api().getRankColor();

            items[i] = ClickableItem.empty(ItemStack.of(FlameUtil.toConcrete(color))
                    .withDisplayName(MC.Style.component(rank.getName(), color))
                    .withLore(
                            Arrays.asList(
                                    MC.Style.SEPARATOR_32,
                                    Component.text().append(
                                            MC.Style.component("Weight: ", MC.CC.GRAY),
                                            MC.Style.component(String.valueOf(rank.getWeight()), color)
                                    ).build(),
                                    Component.text().append(
                                            MC.Style.component("Prefix: ", MC.CC.GRAY),
                                            MC.Style.fixItalics(MC.Style.fromLegacy(rank.getPrefix()))
                                    ).build(),
                                    Component.text().append(
                                            MC.Style.component("Color: ", MC.CC.GRAY),
                                            MC.Style.component(color.getName().toUpperCase(), color)
                                    ).build(),
                                    MC.Style.SEPARATOR_32
                            )
                    ));
            i++;
        }

        Pagination pagination = contents.getPagination();

        pagination.setItems(items);
        pagination.setItemsPerPage(27);
        pagination.addToIterator(contents.iterator(9));

        MenuUtils.addPreviousPage(30, this.inventory, contents, "Ranks");
        MenuUtils.addNextPage(32, this.inventory, contents, "Ranks");
    }

    @Override
    public void open(InventoryOpenEvent event, FabricContents contents) {
        Inventory inventory = event.getInventory();

        if(inventory != null)
            MenuUtils.setPaginatedTitle(inventory, contents.getPagination(), "Ranks");
    }

}
