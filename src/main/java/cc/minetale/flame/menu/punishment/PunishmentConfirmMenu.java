package cc.minetale.flame.menu.punishment;

import cc.minetale.commonlib.modules.profile.Profile;
import cc.minetale.commonlib.util.MC;
import cc.minetale.commonlib.util.TimeUtil;
import cc.minetale.flame.procedure.PunishmentProcedure;
import cc.minetale.flame.util.MenuUtils;
import cc.minetale.mlib.fabric.ClickableItem;
import cc.minetale.mlib.fabric.FabricInventory;
import cc.minetale.mlib.fabric.content.FabricContents;
import cc.minetale.mlib.fabric.content.FabricProvider;
import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;
import net.minestom.server.event.inventory.InventoryCloseEvent;
import net.minestom.server.inventory.InventoryType;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

import java.util.Arrays;

public class PunishmentConfirmMenu implements FabricProvider {

    private final FabricInventory inventory;
    private final PunishmentProcedure procedure;

    public PunishmentConfirmMenu(Player player, PunishmentProcedure procedure) {
        this.inventory = FabricInventory.builder()
                .provider(this)
                .type(InventoryType.HOPPER)
                .title(MC.Style.component("Please confirm the Punishment"))
                .build();
        this.procedure = procedure;
        this.inventory.open(player);
    }

    @Override
    public void init(Player player, FabricContents contents) {
        contents.fill(MenuUtils.FILLER);

        long duration = this.procedure.getBuilder().getDuration();
        String durationString = duration == Integer.MAX_VALUE ? "Permanent" : TimeUtil.millisToRoundedTime(duration);

        contents.setSlot(2, ClickableItem.of(ItemStack.of(Material.EMERALD)
                .withDisplayName(MC.Style.component("Confirm this Punishment", MC.CC.GREEN))
                .withLore(Arrays.asList(
                        Component.text().append(
                                MC.Style.component("Type: ", MC.CC.WHITE),
                                MC.Style.component(this.procedure.getBuilder().getType().toString(), MC.CC.GRAY)
                        ).build(),
                        Component.text().append(
                                MC.Style.component("Time: ", MC.CC.WHITE),
                                MC.Style.component(durationString, MC.CC.GRAY)
                        ).build(),
                        Component.text().append(
                                MC.Style.component("Reason: ", MC.CC.WHITE),
                                MC.Style.component(this.procedure.getBuilder().getReason(), MC.CC.GRAY)
                        ).build())),
                event -> {
            Profile profile = this.procedure.getRecipient();

            profile.api().addPunishment(this.procedure.getBuilder()
                    .addedBy(player.getUuid())
                    .player(profile.getId())
                    .build());

            this.procedure.finish();
            this.inventory.close(player);

            player.sendMessage(Component.text("You have successfully punished " + profile.getName(), MC.CC.GREEN.getTextColor()));
        }));
    }

    @Override
    public void close(InventoryCloseEvent event) {
        this.procedure.cancel();
    }

}
