package cc.minetale.flame.menu;

import cc.minetale.flame.util.MenuUtil;
import cc.minetale.mlib.fabric.FabricInventory;
import cc.minetale.mlib.fabric.content.FabricContents;
import cc.minetale.mlib.fabric.content.FabricProvider;
import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;
import net.minestom.server.event.inventory.InventoryOpenEvent;
import net.minestom.server.inventory.Inventory;
import net.minestom.server.inventory.InventoryType;

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
//        contents.fill(MenuUtil.FILLER);
//        contents.fill(MenuUtil.AIR, 9, 26);
//
//        ClickableItem[] items = new ClickableItem[Rank.getRanks().size()];
//
//        int i = 0;
//
//        List<Rank> sortedRanks = new ArrayList<>(Rank.getRanks().values());
//        sortedRanks.sort(Rank.COMPARATOR);
//
//        for (Rank rank : sortedRanks) {
//            NamedTextColor color = rank.api().getRankColor();
//
//            items[i] = ClickableItem.empty(ItemStack.of(FlameUtil.toConcrete(color))
//                    .withDisplayName(MC.component(rank.getName(), color))
//                    .withLore(
//                            Arrays.asList(
//                                    MC.SEPARATOR_32,
//                                    Component.text().append(
//                                            MC.component("Weight: ", NamedTextColor.GRAY),
//                                            MC.component(String.valueOf(rank.getWeight()), color)
//                                    ).build(),
//                                    Component.text().append(
//                                            MC.component("Prefix: ", NamedTextColor.GRAY),
//                                            MC.fixItalics(MC.fromLegacy(rank.getPrefix()))
//                                    ).build(),
//                                    Component.text().append(
//                                            MC.component("Color: ", NamedTextColor.GRAY),
//                                            MC.component(color.getName().toUpperCase(), color)
//                                    ).build(),
//                                    MC.SEPARATOR_32
//                            )
//                    ));
//            i++;
//        }
//
//        Pagination pagination = contents.getPagination();
//
//        pagination.setItems(items);
//        pagination.setItemsPerPage(27);
//        pagination.addToIterator(contents.iterator(9));
//
//        MenuUtil.addPreviousPage(30, this.inventory, contents, "Ranks");
//        MenuUtil.addNextPage(32, this.inventory, contents, "Ranks");
    }

    @Override
    public void open(InventoryOpenEvent event, FabricContents contents) {
        Inventory inventory = event.getInventory();

        if(inventory != null)
            MenuUtil.setPaginatedTitle(inventory, contents.getPagination(), "Ranks");
    }

}
