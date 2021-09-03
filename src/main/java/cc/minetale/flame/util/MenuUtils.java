package cc.minetale.flame.util;

import cc.minetale.commonlib.util.MC;
import cc.minetale.mlib.fabric.ClickableItem;
import cc.minetale.mlib.fabric.FabricInventory;
import cc.minetale.mlib.fabric.content.FabricContents;
import cc.minetale.mlib.fabric.content.Pagination;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.PlayerSkin;
import net.minestom.server.inventory.Inventory;
import net.minestom.server.inventory.click.ClickType;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.item.metadata.PlayerHeadMeta;

public class MenuUtils {

    public static final ClickableItem FILLER = ClickableItem.empty(ItemStack.of(Material.GRAY_STAINED_GLASS_PANE).withDisplayName(Component.empty()));
    public static final ClickableItem AIR = ClickableItem.empty(ItemStack.AIR);

    public static void addNextPage(int slot, FabricInventory inventory, FabricContents contents, String prefix) {
        Pagination pagination = contents.getPagination();

        contents.setSlot(slot, ClickableItem.of(ItemStack.of(Material.PLAYER_HEAD)
                        .withMeta(PlayerHeadMeta.class, meta -> meta.playerSkin(new PlayerSkin(
                                "eyJ0aW1lc3RhbXAiOjE1MTY4MjU0ODMxODMsInByb2ZpbGVJZCI6ImFkMWM2Yjk1YTA5ODRmNTE4MWJhOTgyMzY0OTllM2JkIiwicHJvZmlsZU5hbWUiOiJGdXJrYW5iejAwIiwic2lnbmF0dXJlUmVxdWlyZWQiOnRydWUsInRleHR1cmVzIjp7IlNLSU4iOnsidXJsIjoiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9mMzJjYTY2MDU2YjcyODYzZTk4ZjdmMzJiZDdkOTRjN2EwZDc5NmFmNjkxYzlhYzNhOTEzNjMzMTM1MjI4OGY5In19fQ==",
                                null
                        )).build())
                        .withDisplayName(Component.text()
                                .append(MC.Style.component("Next Page", MC.CC.GRAY))
                                .append(MC.Style.component(" >", MC.CC.DARK_GRAY, TextDecoration.BOLD))
                                .build()),
                event -> {
                    Player player = event.getPlayer();

                    if (event.getClickType() == ClickType.LEFT_CLICK) {
                        if (pagination.getNextPage() != pagination.getPage() + 1) {
                            inventory.open(player, pagination.next().getPage());
                            setPaginatedTitle(inventory.getInventory(), pagination, prefix);
                            player.playSound(Sound.sound(Key.key("ui.button.click"), Sound.Source.MASTER, 1F, 2.0F));
                        } else {
                            player.playSound(Sound.sound(Key.key("block.note_block.bass"), Sound.Source.MASTER, 1F, 0.5F));
                        }
                    }
                }));
    }

    public static void addPreviousPage(int slot, FabricInventory inventory, FabricContents contents, String prefix) {
        Pagination pagination = contents.getPagination();

        contents.setSlot(slot, ClickableItem.of(ItemStack.of(Material.PLAYER_HEAD)
                        .withMeta(PlayerHeadMeta.class, meta -> meta.playerSkin(
                                new PlayerSkin(
                                        "ewogICJ0aW1lc3RhbXAiIDogMTYzMDU0NTExMTIzMCwKICAicHJvZmlsZUlkIiA6ICIyM2YxYTU5ZjQ2OWI0M2RkYmRiNTM3YmZlYzEwNDcxZiIsCiAgInByb2ZpbGVOYW1lIiA6ICIyODA3IiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2RkMmI5ZWQxNmQxMmNjMmJkMmQwYmMyYWExZDBlNmQxMDg4NjAwY2ZhMGE5NGM1MTUxMTFhNGMzODM0NzdlNDYiCiAgICB9CiAgfQp9",
                                        null
                                )).build())
                        .withDisplayName(Component.text()
                                .append(MC.Style.component("< ", MC.CC.DARK_GRAY, TextDecoration.BOLD))
                                .append(MC.Style.component("Previous Page", MC.CC.GRAY))
                                .build()),
                event -> {
                    if (event.getClickType() == ClickType.LEFT_CLICK) {
                        Player player = event.getPlayer();

                        if (pagination.getPreviousPage() != pagination.getPage() + 1) {
                            inventory.open(player, pagination.previous().getPage());
                            setPaginatedTitle(inventory.getInventory(), pagination, prefix);
                            player.playSound(Sound.sound(Key.key("ui.button.click"), Sound.Source.MASTER, 1F, 2.0F));
                        } else {
                            player.playSound(Sound.sound(Key.key("block.note_block.bass"), Sound.Source.MASTER, 1F, 0.5F));
                        }
                    }
                }));
    }

    public static void setPaginatedTitle(Inventory inventory, Pagination pagination, String prefix) {
        inventory.setTitle(Component.text(prefix + " (" + (pagination.getPage() + 1) + "/" + pagination.getPageCount() + ")"));
    }

}
