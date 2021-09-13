package cc.minetale.flame.menu.punishment;

import cc.minetale.commonlib.modules.profile.Profile;
import cc.minetale.commonlib.modules.punishment.Punishment;
import cc.minetale.commonlib.modules.rank.Rank;
import cc.minetale.commonlib.util.MC;
import cc.minetale.flame.FlameAPI;
import cc.minetale.flame.commands.RankUtil;
import cc.minetale.flame.procedure.PunishmentProcedure;
import cc.minetale.flame.util.FlameUtil;
import cc.minetale.flame.util.MenuUtils;
import cc.minetale.mlib.fabric.ClickableItem;
import cc.minetale.mlib.fabric.FabricInventory;
import cc.minetale.mlib.fabric.content.FabricContents;
import cc.minetale.mlib.fabric.content.FabricProvider;
import net.minestom.server.entity.Player;
import net.minestom.server.inventory.InventoryType;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

import java.util.UUID;

public class PunishmentTypeMenu implements FabricProvider {

    private final FabricInventory inventory;
    private final Profile offender;

    public PunishmentTypeMenu(Player player, Profile offender) {
        this.inventory = FabricInventory.builder()
                .provider(this)
                .type(InventoryType.CHEST_3_ROW)
                .title(MC.component("Select a Punishment Type"))
                .build();
        this.offender = offender;
        this.inventory.open(player);
    }

    @Override
    public void init(Player player, FabricContents contents) {
        contents.fill(MenuUtils.FILLER);

        contents.setSlot(10, ClickableItem.empty(ItemStack.of(Material.RED_CONCRETE)
                .withDisplayName(MC.component("Blacklist", MC.CC.GRAY))));

        RankUtil.hasMinimumRank(player, "Owner", rankCallback -> {
            if (rankCallback.isEligible()) {
                contents.setSlot(10, ClickableItem.of(ItemStack.of(Material.RED_CONCRETE)
                                .withDisplayName(MC.component("Blacklist", MC.CC.GRAY)),
                        event -> this.selectType(event.getPlayer(), Punishment.Type.BLACKLIST)));
            }
        });

        contents.setSlot(12, ClickableItem.of(ItemStack.of(Material.YELLOW_CONCRETE)
                        .withDisplayName(MC.component("Ban", MC.CC.GRAY)),
                event -> this.selectType(event.getPlayer(), Punishment.Type.BAN)));

        contents.setSlot(14, ClickableItem.of(ItemStack.of(Material.LIME_CONCRETE)
                        .withDisplayName(MC.component("Mute", MC.CC.GRAY)),
                event -> this.selectType(event.getPlayer(), Punishment.Type.MUTE)));

        contents.setSlot(16, ClickableItem.of(ItemStack.of(Material.LIGHT_BLUE_CONCRETE)
                        .withDisplayName(MC.component("Warn", MC.CC.GRAY)),
                event -> this.selectType(event.getPlayer(), Punishment.Type.WARN)));
    }

    private void selectType(Player player, Punishment.Type type) {
        UUID uuid = player.getUuid();

        if (FlameAPI.canStartProcedure(uuid)) {
            PunishmentProcedure procedure = new PunishmentProcedure(uuid, this.offender, PunishmentProcedure.Type.ADD, PunishmentProcedure.Stage.PROVIDE_TIME);

            PunishmentProcedure.Builder builder = procedure.getBuilder();
            builder.type(type);

            FlameUtil.playClickSound(player);

            new PunishmentDurationMenu(player, procedure);
        } else {
            FlameUtil.playErrorSound(player);
        }
    }

}
