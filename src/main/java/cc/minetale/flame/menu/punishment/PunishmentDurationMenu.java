package cc.minetale.flame.menu.punishment;

import cc.minetale.commonlib.modules.profile.Profile;
import cc.minetale.commonlib.util.Duration;
import cc.minetale.commonlib.util.MC;
import cc.minetale.flame.FlameAPI;
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
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

import java.util.UUID;

public class PunishmentDurationMenu implements FabricProvider {

    private final FabricInventory inventory;
    private final PunishmentProcedure procedure;

    public PunishmentDurationMenu(Player player, PunishmentProcedure procedure) {
        this.inventory = FabricInventory.builder()
                .provider(this)
                .type(InventoryType.CHEST_4_ROW)
                .title(MC.Style.component("Select a Punishment Duration"))
                .build();
        this.procedure = procedure;
        this.inventory.open(player);
    }

    @Override
    public void init(Player player, FabricContents contents) {
        contents.fill(MenuUtils.FILLER);

        contents.setSlot(10, ClickableItem.of(ItemStack.of(Material.RED_DYE)
                        .withDisplayName(MC.Style.component("5 minutes", MC.CC.GRAY)),
                event -> this.selectDuration(player, Duration.fromString("5m"))));

        contents.setSlot(11, ClickableItem.of(ItemStack.of(Material.ORANGE_DYE)
                        .withDisplayName(MC.Style.component("15 minutes", MC.CC.GRAY)),
                event -> this.selectDuration(player, Duration.fromString("15m"))));

        contents.setSlot(12, ClickableItem.of(ItemStack.of(Material.YELLOW_DYE)
                        .withDisplayName(MC.Style.component("30 minutes", MC.CC.GRAY)),
                event -> this.selectDuration(player, Duration.fromString("30m"))));

        contents.setSlot(13, ClickableItem.of(ItemStack.of(Material.LIME_DYE)
                        .withDisplayName(MC.Style.component("1 hour", MC.CC.GRAY)),
                event -> this.selectDuration(player, Duration.fromString("1h"))));

        contents.setSlot(14, ClickableItem.of(ItemStack.of(Material.GREEN_DYE)
                        .withDisplayName(MC.Style.component("12 hours", MC.CC.GRAY)),
                event -> this.selectDuration(player, Duration.fromString("12h"))));

        contents.setSlot(15, ClickableItem.of(ItemStack.of(Material.CYAN_DYE)
                        .withDisplayName(MC.Style.component("1 day", MC.CC.GRAY)),
                event -> this.selectDuration(player, Duration.fromString("1d"))));

        contents.setSlot(16, ClickableItem.of(ItemStack.of(Material.LIGHT_BLUE_DYE)
                        .withDisplayName(MC.Style.component("1 week", MC.CC.GRAY)),
                event -> this.selectDuration(player, Duration.fromString("1w"))));

        contents.setSlot(19, ClickableItem.of(ItemStack.of(Material.BLUE_DYE)
                        .withDisplayName(MC.Style.component("1 month", MC.CC.GRAY)),
                event -> this.selectDuration(player, Duration.fromString("1M"))));

        contents.setSlot(20, ClickableItem.of(ItemStack.of(Material.PURPLE_DYE)
                        .withDisplayName(MC.Style.component("3 months", MC.CC.GRAY)),
                event -> this.selectDuration(player, Duration.fromString("3M"))));

        contents.setSlot(21, ClickableItem.of(ItemStack.of(Material.MAGENTA_DYE)
                        .withDisplayName(MC.Style.component("6 months", MC.CC.GRAY)),
                event -> this.selectDuration(player, Duration.fromString("6M"))));

        contents.setSlot(22, ClickableItem.of(ItemStack.of(Material.BLACK_DYE)
                        .withDisplayName(MC.Style.component("9 months", MC.CC.GRAY)),
                event -> this.selectDuration(player, Duration.fromString("9M"))));

        contents.setSlot(23, ClickableItem.of(ItemStack.of(Material.GRAY_DYE)
                        .withDisplayName(MC.Style.component("1 year", MC.CC.GRAY)),
                event -> this.selectDuration(player, Duration.fromString("1y"))));

        contents.setSlot(24, ClickableItem.of(ItemStack.of(Material.LIGHT_GRAY_DYE)
                        .withDisplayName(MC.Style.component("Permanent", MC.CC.GRAY)),
                event -> this.selectDuration(player, Duration.fromString("perm"))));

        contents.setSlot(25, ClickableItem.of(ItemStack.of(Material.WHITE_DYE)
                        .withDisplayName(MC.Style.component("Custom", MC.CC.GRAY)),
                event -> this.selectDuration(player, null)));
    }

    private void selectDuration(Player player, Duration duration) {
        FlameUtil.playClickSound(player);

        if(duration == null) {
            this.procedure.setStage(PunishmentProcedure.Stage.PROVIDE_TIME);
            this.inventory.close(player);
            // TODO: This will cancel the procedure
        } else {
            PunishmentProcedure.Builder builder = this.procedure.getBuilder();

            builder.duration(duration.getValue());

            new PunishmentReasonMenu(player, procedure);
        }
    }

}
