package cc.minetale.flame.util;

import cc.minetale.flame.menu.DurationType;
import cc.minetale.mlib.fabric.ClickableItem;
import cc.minetale.mlib.fabric.FabricInventory;
import cc.minetale.mlib.fabric.content.FabricContents;
import cc.minetale.mlib.fabric.content.Pagination;
import cc.minetale.mlib.util.SoundsUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.minestom.server.entity.Player;
import net.minestom.server.inventory.Inventory;
import net.minestom.server.inventory.click.ClickType;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

public class MenuUtil {

    public static final ClickableItem FILLER = ClickableItem.empty(ItemStack.of(Material.GRAY_STAINED_GLASS_PANE).withDisplayName(Component.empty()));
    public static final ClickableItem AIR = ClickableItem.empty(ItemStack.AIR);

    public static void addNextPage(int slot, FabricInventory inventory, FabricContents contents, String prefix) {
        Pagination pagination = contents.getPagination();

        contents.setSlot(slot, ClickableItem.of(ItemStack.of(Material.GRAY_CARPET)
                        .withDisplayName(Component.text().append(
                                        Component.text("Next Page", NamedTextColor.GRAY),
                                        Component.text(" >", NamedTextColor.DARK_GRAY, TextDecoration.BOLD)
                                ).build()),
                event -> {
                    Player player = event.getPlayer();

                    if (event.getClickType() == ClickType.LEFT_CLICK) {
                        if (pagination.getNextPage() != pagination.getPage() + 1) {
                            inventory.open(player, pagination.next().getPage());
                            
                            setPaginatedTitle(inventory.getInventory(), pagination, prefix);
                            
                            SoundsUtil.playClickSound(player);
                        } else {
                            SoundsUtil.playErrorSound(player);
                        }
                    }
                }));
    }

    public static void addPreviousPage(int slot, FabricInventory inventory, FabricContents contents, String prefix) {
        Pagination pagination = contents.getPagination();

        contents.setSlot(slot, ClickableItem.of(ItemStack.of(Material.GRAY_CARPET)
                        .withDisplayName(Component.text().append(
                                Component.text("< ", NamedTextColor.DARK_GRAY, TextDecoration.BOLD),
                                Component.text("Previous Page", NamedTextColor.GRAY)
                        ).build()),
                event -> {
                    if (event.getClickType() == ClickType.LEFT_CLICK) {
                        Player player = event.getPlayer();

                        if (pagination.getPreviousPage() != pagination.getPage() + 1) {
                            inventory.open(player, pagination.previous().getPage());
                            setPaginatedTitle(inventory.getInventory(), pagination, prefix);

                            SoundsUtil.playClickSound(player);
                        } else {
                            SoundsUtil.playErrorSound(player);
                        }
                    }
                }));
    }

    public static void setPaginatedTitle(Inventory inventory, Pagination pagination, String prefix) {
        inventory.setTitle(Component.text(prefix + " (" + (pagination.getPage() + 1) + "/" + pagination.getPageCount() + ")"));
    }

    public static void addDuration(Player player, FabricContents contents, DurationType durationType) {
//        contents.fill(MenuUtil.FILLER);
//
//        contents.setSlot(10, ClickableItem.of(ItemStack.of(Material.RED_DYE)
//                        .withDisplayName(MC.component("5 minutes", NamedTextColor.GRAY)),
//                event -> durationType.selectDuration(player, Duration.fromString("5m"))));
//
//        contents.setSlot(11, ClickableItem.of(ItemStack.of(Material.ORANGE_DYE)
//                        .withDisplayName(MC.component("15 minutes", NamedTextColor.GRAY)),
//                event -> durationType.selectDuration(player, Duration.fromString("15m"))));
//
//        contents.setSlot(12, ClickableItem.of(ItemStack.of(Material.YELLOW_DYE)
//                        .withDisplayName(MC.component("30 minutes", NamedTextColor.GRAY)),
//                event -> durationType.selectDuration(player, Duration.fromString("30m"))));
//
//        contents.setSlot(13, ClickableItem.of(ItemStack.of(Material.LIME_DYE)
//                        .withDisplayName(MC.component("1 hour", NamedTextColor.GRAY)),
//                event -> durationType.selectDuration(player, Duration.fromString("1h"))));
//
//        contents.setSlot(14, ClickableItem.of(ItemStack.of(Material.GREEN_DYE)
//                        .withDisplayName(MC.component("12 hours", NamedTextColor.GRAY)),
//                event -> durationType.selectDuration(player, Duration.fromString("12h"))));
//
//        contents.setSlot(15, ClickableItem.of(ItemStack.of(Material.CYAN_DYE)
//                        .withDisplayName(MC.component("1 day", NamedTextColor.GRAY)),
//                event -> durationType.selectDuration(player, Duration.fromString("1d"))));
//
//        contents.setSlot(16, ClickableItem.of(ItemStack.of(Material.LIGHT_BLUE_DYE)
//                        .withDisplayName(MC.component("1 week", NamedTextColor.GRAY)),
//                event -> durationType.selectDuration(player, Duration.fromString("1w"))));
//
//        contents.setSlot(19, ClickableItem.of(ItemStack.of(Material.BLUE_DYE)
//                        .withDisplayName(MC.component("1 month", NamedTextColor.GRAY)),
//                event -> durationType.selectDuration(player, Duration.fromString("1M"))));
//
//        contents.setSlot(20, ClickableItem.of(ItemStack.of(Material.PURPLE_DYE)
//                        .withDisplayName(MC.component("3 months", NamedTextColor.GRAY)),
//                event -> durationType.selectDuration(player, Duration.fromString("3M"))));
//
//        contents.setSlot(21, ClickableItem.of(ItemStack.of(Material.MAGENTA_DYE)
//                        .withDisplayName(MC.component("6 months", NamedTextColor.GRAY)),
//                event -> durationType.selectDuration(player, Duration.fromString("6M"))));
//
//        contents.setSlot(22, ClickableItem.of(ItemStack.of(Material.BLACK_DYE)
//                        .withDisplayName(MC.component("9 months", NamedTextColor.GRAY)),
//                event -> durationType.selectDuration(player, Duration.fromString("9M"))));
//
//        contents.setSlot(23, ClickableItem.of(ItemStack.of(Material.GRAY_DYE)
//                        .withDisplayName(MC.component("1 year", NamedTextColor.GRAY)),
//                event -> durationType.selectDuration(player, Duration.fromString("1y"))));
//
//        contents.setSlot(24, ClickableItem.of(ItemStack.of(Material.LIGHT_GRAY_DYE)
//                        .withDisplayName(MC.component("Permanent", NamedTextColor.GRAY)),
//                event -> durationType.selectDuration(player, Duration.fromString("perm"))));
//
//        contents.setSlot(25, ClickableItem.of(ItemStack.of(Material.WHITE_DYE)
//                        .withDisplayName(MC.component("Custom", NamedTextColor.GRAY)),
//                event -> durationType.selectDuration(player, null)));
    }

}
