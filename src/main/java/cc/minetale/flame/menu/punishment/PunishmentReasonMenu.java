package cc.minetale.flame.menu.punishment;

import cc.minetale.commonlib.util.MC;
import cc.minetale.flame.procedure.PunishmentProcedure;
import cc.minetale.flame.util.FlameUtil;
import cc.minetale.flame.util.MenuUtils;
import cc.minetale.mlib.fabric.ClickableItem;
import cc.minetale.mlib.fabric.FabricInventory;
import cc.minetale.mlib.fabric.content.FabricContents;
import cc.minetale.mlib.fabric.content.FabricProvider;
import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;
import net.minestom.server.event.inventory.InventoryCloseEvent;
import net.minestom.server.inventory.InventoryType;
import net.minestom.server.item.ItemHideFlag;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

public class PunishmentReasonMenu implements FabricProvider {

    private final FabricInventory inventory;
    private final PunishmentProcedure procedure;
    private boolean shouldCancel = true;

    public PunishmentReasonMenu(Player player, PunishmentProcedure procedure) {
        this.inventory = FabricInventory.builder()
                .provider(this)
                .type(InventoryType.CHEST_4_ROW)
                .title(MC.Style.component("Select a Punishment Reason"))
                .build();
        this.procedure = procedure;
        this.inventory.open(player);
    }

    @Override
    public void init(Player player, FabricContents contents) {
        contents.fill(MenuUtils.FILLER);

        contents.setSlot(10, ClickableItem.of(ItemStack.of(Material.ENDER_PEARL)
                        .withDisplayName(MC.Style.component("Staff Discretion", MC.CC.GRAY)),
                event -> this.selectReason(player, "Staff Discretion")));

        contents.setSlot(11, ClickableItem.of(ItemStack.of(Material.TNT)
                        .withDisplayName(MC.Style.component("Exploiting", MC.CC.GRAY)),
                event -> this.selectReason(player, "Exploiting")));

        contents.setSlot(12, ClickableItem.of(ItemStack.of(Material.EXPERIENCE_BOTTLE)
                        .withDisplayName(MC.Style.component("Boosting Stats", MC.CC.GRAY)),
                event -> this.selectReason(player, "Boosting Stats")));

        contents.setSlot(13, ClickableItem.of(ItemStack.of(Material.COMPASS)
                        .withDisplayName(MC.Style.component("Doxing/DDosing", MC.CC.GRAY)),
                event -> this.selectReason(player, "Doxing/DDosing a player or staff member")));

        contents.setSlot(14, ClickableItem.of(ItemStack.of(Material.STICK)
                        .withDisplayName(MC.Style.component("Inappropriate Conduct", MC.CC.GRAY)),
                event -> this.selectReason(player, "Inappropriate Conduct")));

        contents.setSlot(15, ClickableItem.of(ItemStack.of(Material.BONE)
                        .withDisplayName(MC.Style.component("Player Threats", MC.CC.GRAY)),
                event -> this.selectReason(player, "Player Threats")));

        contents.setSlot(16, ClickableItem.of(ItemStack.of(Material.DIAMOND_SWORD)
                        .withDisplayName(MC.Style.component("Cheating", MC.CC.GRAY))
                        .withMeta(meta -> meta.hideFlag(ItemHideFlag.HIDE_ATTRIBUTES)),
                event -> this.selectReason(player, "Cheating")));

        contents.setSlot(20, ClickableItem.of(ItemStack.of(Material.GOLDEN_SWORD)
                        .withDisplayName(MC.Style.component("Cross Teaming/Team Griefing", MC.CC.GRAY))
                        .withMeta(meta -> meta.hideFlag(ItemHideFlag.HIDE_ATTRIBUTES)),
                event -> this.selectReason(player, "Team Griefing/Cross Teaming")));

        contents.setSlot(21, ClickableItem.of(ItemStack.of(Material.ANVIL)
                        .withDisplayName(MC.Style.component("Encouraging Cheating", MC.CC.GRAY)),
                event -> this.selectReason(player, "Encouraging Cheating")));

        contents.setSlot(22, ClickableItem.of(ItemStack.of(Material.NAME_TAG)
                        .withDisplayName(MC.Style.component("Inappropriate Username", MC.CC.GRAY)),
                event -> this.selectReason(player, "Inappropriate Username")));

        contents.setSlot(23, ClickableItem.of(ItemStack.of(Material.BOOK)
                        .withDisplayName(MC.Style.component("Impersonation", MC.CC.GRAY)),
                event -> this.selectReason(player, "Impersonation of a player or staff member")));

        contents.setSlot(24, ClickableItem.of(ItemStack.of(Material.COMMAND_BLOCK)
                        .withDisplayName(MC.Style.component("Custom", MC.CC.GRAY)),
                event -> this.selectReason(player, null)));
    }

    @Override
    public void close(InventoryCloseEvent event) {
        if(this.shouldCancel) {
            this.procedure.cancel();
        }
    }

    private void selectReason(Player player, String reason) {
        FlameUtil.playClickSound(player);

        if(reason == null) {
            this.procedure.setStage(PunishmentProcedure.Stage.PROVIDE_REASON);

            this.shouldCancel = false;

            this.inventory.close(player);

            player.sendMessage(Component.text("Type a reason for adding this punishment in chat...", MC.CC.GREEN.getTextColor()));
        } else {
            PunishmentProcedure.Builder builder = this.procedure.getBuilder();

            builder.reason(reason);

            new PunishmentConfirmMenu(player, this.procedure);
        }
    }



}
