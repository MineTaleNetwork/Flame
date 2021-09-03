package cc.minetale.flame.menu.punishment;

import cc.minetale.commonlib.modules.profile.Profile;
import cc.minetale.commonlib.util.Duration;
import cc.minetale.commonlib.util.MC;
import cc.minetale.flame.procedure.PunishmentProcedure;
import cc.minetale.flame.util.FlameUtil;
import cc.minetale.flame.util.MenuUtils;
import cc.minetale.mlib.fabric.ClickableItem;
import cc.minetale.mlib.fabric.FabricInventory;
import cc.minetale.mlib.fabric.content.FabricContents;
import cc.minetale.mlib.fabric.content.FabricProvider;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.minestom.server.entity.Player;
import net.minestom.server.inventory.InventoryType;
import net.minestom.server.item.ItemHideFlag;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

public class PunishmentReasonMenu implements FabricProvider {

    private final FabricInventory inventory;
    private final PunishmentProcedure procedure;

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
                        .withDisplayName(MC.Style.component("Unspecified", MC.CC.GRAY)),
                event -> this.selectReason(player, "Staff discretion")));

        contents.setSlot(11, ClickableItem.of(ItemStack.of(Material.TNT)
                        .withDisplayName(MC.Style.component("Exploiting", MC.CC.GRAY)),
                event -> this.selectReason(player, "Use of an exploit to gain an advantage")));

        contents.setSlot(12, ClickableItem.of(ItemStack.of(Material.EXPERIENCE_BOTTLE)
                        .withDisplayName(MC.Style.component("Boosting", MC.CC.GRAY)),
                event -> this.selectReason(player, "Boosting stats")));

        contents.setSlot(13, ClickableItem.of(ItemStack.of(Material.COMPASS)
                        .withDisplayName(MC.Style.component("Doxing/Ddosing", MC.CC.GRAY)),
                event -> this.selectReason(player, "Doxing/Ddosing a player or staff member")));

        contents.setSlot(14, ClickableItem.of(ItemStack.of(Material.STICK)
                        .withDisplayName(MC.Style.component("Inappropriate Conduct", MC.CC.GRAY)),
                event -> this.selectReason(player, "Inappropriate conduct")));

        contents.setSlot(15, ClickableItem.of(ItemStack.of(Material.BONE)
                        .withDisplayName(MC.Style.component("Player Threats", MC.CC.GRAY)),
                event -> this.selectReason(player, "Serious threats to a player or staff member")));

        contents.setSlot(16, ClickableItem.of(ItemStack.of(Material.DIAMOND_SWORD)
                        .withDisplayName(MC.Style.component("Cheating", MC.CC.GRAY))
                        .withMeta(meta -> meta.hideFlag(ItemHideFlag.HIDE_ATTRIBUTES)),
                event -> this.selectReason(player, "Cheating through the use of unfair game advantages")));

        contents.setSlot(20, ClickableItem.of(ItemStack.of(Material.GOLDEN_SWORD)
                        .withDisplayName(MC.Style.component("Cross Teaming/Team Griefing", MC.CC.GRAY))
                        .withMeta(meta -> meta.hideFlag(ItemHideFlag.HIDE_ATTRIBUTES)),
                event -> this.selectReason(player, "Team Griefing or Cross Teaming in a non solo game")));

        contents.setSlot(21, ClickableItem.of(ItemStack.of(Material.ANVIL)
                        .withDisplayName(MC.Style.component("Encouraging Cheating", MC.CC.GRAY)),
                event -> this.selectReason(player, "Encouraging others to cheat")));

        contents.setSlot(22, ClickableItem.of(ItemStack.of(Material.NAME_TAG)
                        .withDisplayName(MC.Style.component("Inappropriate Name", MC.CC.GRAY)),
                event -> this.selectReason(player, "Your username is not allowed on the server and is breaking our rules")));

        contents.setSlot(23, ClickableItem.of(ItemStack.of(Material.BOOK)
                        .withDisplayName(MC.Style.component("Impersonation", MC.CC.GRAY)),
                event -> this.selectReason(player, "Impersonation of a player or staff member")));

        contents.setSlot(24, ClickableItem.of(ItemStack.of(Material.COMMAND_BLOCK)
                        .withDisplayName(MC.Style.component("Custom", MC.CC.GRAY)),
                event -> this.selectReason(player, null)));
    }

    private void selectReason(Player player, String reason) {
        FlameUtil.playClickSound(player);

        if(reason == null) {
            this.procedure.setStage(PunishmentProcedure.Stage.PROVIDE_REASON);
            this.inventory.close(player);
            // TODO: This will cancel the procedure
        } else {
            PunishmentProcedure.Builder builder = this.procedure.getBuilder();

            builder.reason(reason);

            new PunishmentConfirmMenu(player, this.procedure);
        }
    }

}
