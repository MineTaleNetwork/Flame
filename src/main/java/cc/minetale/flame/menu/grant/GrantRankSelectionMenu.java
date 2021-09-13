package cc.minetale.flame.menu.grant;

import cc.minetale.commonlib.modules.grant.Grant;
import cc.minetale.commonlib.modules.profile.Profile;
import cc.minetale.commonlib.modules.rank.Rank;
import cc.minetale.commonlib.util.MC;
import cc.minetale.flame.FlameAPI;
import cc.minetale.flame.procedure.GrantProcedure;
import cc.minetale.flame.util.FlameUtil;
import cc.minetale.mlib.fabric.ClickableItem;
import cc.minetale.mlib.fabric.FabricInventory;
import cc.minetale.mlib.fabric.content.FabricContents;
import cc.minetale.mlib.fabric.content.FabricProvider;
import cc.minetale.mlib.fabric.content.Pagination;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.minestom.server.entity.Player;
import net.minestom.server.event.inventory.InventoryOpenEvent;
import net.minestom.server.inventory.Inventory;
import net.minestom.server.inventory.InventoryType;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class GrantRankSelectionMenu implements FabricProvider {

    private final Profile profile;
    private final FabricInventory inventory;

    public GrantRankSelectionMenu(Player player, Profile profile) {
        this.profile = profile;
        this.inventory = FabricInventory.builder()
                .provider(this)
                .type(InventoryType.CHEST_5_ROW)
                .build();
        this.inventory.open(player);
    }

    @Override
    public void init(Player player, FabricContents contents) {
        ClickableItem FILLER = ClickableItem.empty(ItemStack.of(Material.GRAY_STAINED_GLASS_PANE).withDisplayName(Component.empty()));

        contents.fill(FILLER, 0, 9);
        contents.fill(FILLER, 36, 43);

        ClickableItem[] items = new ClickableItem[Rank.getRanks().size()];

        int i = 0;

        List<Rank> sortedRanks = new ArrayList<>(Rank.getRanks().values());
        sortedRanks.sort(Rank.COMPARATOR);

        for (Rank rank : sortedRanks) {
            MC.CC color = rank.api().getRankColor();

            items[i] = ClickableItem.of(ItemStack.of(FlameUtil.toConcrete(color))
                            .withDisplayName(MC.component(rank.getName(), color))
                            .withLore(
                                    Arrays.asList(
                                            MC.Style.SEPARATOR_50,
                                            Component.text()
                                                    .append(
                                                            MC.component("Click to grant ", MC.CC.GRAY),
                                                            MC.component(rank.getName(), color),
                                                            MC.component(" to ", MC.CC.GRAY),
                                                            MC.Style.fixItalics(this.profile.api().getColoredName())
                                                    ).build(),
                                            MC.Style.SEPARATOR_50
                                    )
                            ),
                    event -> {
                        UUID uuid = player.getUuid();

                        if (FlameAPI.canStartProcedure(uuid)) {
                            GrantProcedure procedure = new GrantProcedure(uuid, this.profile, GrantProcedure.Type.ADD, GrantProcedure.Stage.PROVIDE_TIME);
                            // TODO
//                            procedure.setGrant(new Grant(this.profile.getId(), rank.getUuid(), player.getUuid(), 0, "", 0));

                            new GrantDurationMenu(player, procedure);
                        }
                    });
            i++;
        }

        Pagination pagination = contents.getPagination();

        pagination.setItems(items);
        pagination.setItemsPerPage(27);
        pagination.addToIterator(contents.iterator(9));

        contents.setSlot(39, ClickableItem.of(ItemStack.of(Material.GRAY_CARPET)
                        .withDisplayName(Component.text()
                                .append(
                                        MC.component("< ", MC.CC.DARK_GRAY, TextDecoration.BOLD),
                                        MC.component("Previous Page", MC.CC.GRAY)
                                ).build()),
                event -> {
                    this.inventory.open(player, pagination.previous().getPage());
                    this.setTitle(this.inventory.getInventory(), pagination);
                }));

        contents.setSlot(41, ClickableItem.of(ItemStack.of(Material.GRAY_CARPET)
                        .withDisplayName(Component.text()
                                .append(
                                        MC.component("Next Page", MC.CC.GRAY),
                                        MC.component(" >", MC.CC.DARK_GRAY, TextDecoration.BOLD)
                                ).build()),
                event -> {
                    this.inventory.open(player, pagination.next().getPage());
                    this.setTitle(this.inventory.getInventory(), pagination);
                }));
    }

    @Override
    public void open(InventoryOpenEvent event, FabricContents contents) {
        Inventory inventory = event.getInventory();

        if (inventory != null)
            this.setTitle(inventory, contents.getPagination());
    }

    public void setTitle(Inventory inventory, Pagination pagination) {
        inventory.setTitle(MC.component("Ranks (" + (pagination.getPage() + 1) + "/" + pagination.getPageCount() + ")"));
    }

}
