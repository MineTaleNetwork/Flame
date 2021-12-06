package cc.minetale.flame.menu.punishment;

import cc.minetale.flame.procedure.PunishmentProcedure;
import cc.minetale.mlib.fabric.FabricInventory;
import cc.minetale.mlib.fabric.content.FabricContents;
import cc.minetale.mlib.fabric.content.FabricProvider;
import net.minestom.server.entity.Player;
import net.minestom.server.event.inventory.InventoryCloseEvent;
import net.minestom.server.inventory.InventoryType;

public class PunishmentReasonMenu implements FabricProvider {

    private final FabricInventory inventory;
    private final PunishmentProcedure procedure;
    private boolean shouldCancel = true;

    public PunishmentReasonMenu(Player player, PunishmentProcedure procedure) {
        this.inventory = FabricInventory.builder()
                .provider(this)
                .type(InventoryType.CHEST_4_ROW)
//                .title(MC.component("Select a Punishment Reason"))
                .build();
        this.procedure = procedure;
        this.inventory.open(player);
    }

    @Override
    public void init(Player player, FabricContents contents) {
//        contents.fill(MenuUtil.FILLER);
//
//        contents.setSlot(10, ClickableItem.of(ItemStack.of(Material.ENDER_PEARL)
//                        .withDisplayName(MC.component("Staff Discretion", NamedTextColor.GRAY)),
//                event -> this.selectReason(player, "Staff Discretion")));
//
//        contents.setSlot(11, ClickableItem.of(ItemStack.of(Material.TNT)
//                        .withDisplayName(MC.component("Exploiting", NamedTextColor.GRAY)),
//                event -> this.selectReason(player, "Exploiting")));
//
//        contents.setSlot(12, ClickableItem.of(ItemStack.of(Material.EXPERIENCE_BOTTLE)
//                        .withDisplayName(MC.component("Boosting Stats", NamedTextColor.GRAY)),
//                event -> this.selectReason(player, "Boosting Stats")));
//
//        contents.setSlot(13, ClickableItem.of(ItemStack.of(Material.COMPASS)
//                        .withDisplayName(MC.component("Doxing/DDosing", NamedTextColor.GRAY)),
//                event -> this.selectReason(player, "Doxing/DDosing a player or staff member")));
//
//        contents.setSlot(14, ClickableItem.of(ItemStack.of(Material.STICK)
//                        .withDisplayName(MC.component("Inappropriate Conduct", NamedTextColor.GRAY)),
//                event -> this.selectReason(player, "Inappropriate Conduct")));
//
//        contents.setSlot(15, ClickableItem.of(ItemStack.of(Material.BONE)
//                        .withDisplayName(MC.component("Player Threats", NamedTextColor.GRAY)),
//                event -> this.selectReason(player, "Player Threats")));
//
//        contents.setSlot(16, ClickableItem.of(ItemStack.of(Material.DIAMOND_SWORD)
//                        .withDisplayName(MC.component("Cheating", NamedTextColor.GRAY))
//                        .withMeta(meta -> meta.hideFlag(ItemHideFlag.HIDE_ATTRIBUTES)),
//                event -> this.selectReason(player, "Cheating")));
//
//        contents.setSlot(20, ClickableItem.of(ItemStack.of(Material.GOLDEN_SWORD)
//                        .withDisplayName(MC.component("Cross Teaming/Team Griefing", NamedTextColor.GRAY))
//                        .withMeta(meta -> meta.hideFlag(ItemHideFlag.HIDE_ATTRIBUTES)),
//                event -> this.selectReason(player, "Team Griefing/Cross Teaming")));
//
//        contents.setSlot(21, ClickableItem.of(ItemStack.of(Material.ANVIL)
//                        .withDisplayName(MC.component("Encouraging Cheating", NamedTextColor.GRAY)),
//                event -> this.selectReason(player, "Encouraging Cheating")));
//
//        contents.setSlot(22, ClickableItem.of(ItemStack.of(Material.NAME_TAG)
//                        .withDisplayName(MC.component("Inappropriate Username", NamedTextColor.GRAY)),
//                event -> this.selectReason(player, "Inappropriate Username")));
//
//        contents.setSlot(23, ClickableItem.of(ItemStack.of(Material.BOOK)
//                        .withDisplayName(MC.component("Impersonation", NamedTextColor.GRAY)),
//                event -> this.selectReason(player, "Impersonation of a player or staff member")));
//
//        contents.setSlot(24, ClickableItem.of(ItemStack.of(Material.COMMAND_BLOCK)
//                        .withDisplayName(MC.component("Custom", NamedTextColor.GRAY)),
//                event -> this.selectReason(player, null)));
    }

    @Override
    public void close(InventoryCloseEvent event) {
        if(this.shouldCancel) {
            this.procedure.cancel();
        }
    }

    private void selectReason(Player player, String reason) {
//        FlameUtil.playClickSound(player);
//
//        if(reason == null) {
//            this.procedure.setStage(PunishmentProcedure.Stage.PROVIDE_REASON);
//
//            this.shouldCancel = false;
//
//            this.inventory.close(player);
//
//            player.sendMessage(Component.text("Type a reason for adding this punishment in chat...", NamedTextColor.GREEN));
//        } else {
//            PunishmentProcedure.Builder builder = this.procedure.getBuilder();
//
//            builder.reason(reason);
//
//            new PunishmentConfirmMenu(player, this.procedure);
//        }
    }



}
