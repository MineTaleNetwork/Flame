//package cc.minetale.flame.menu.punishment;
//
//import cc.minetale.commonlib.profile.Profile;
//import cc.minetale.mlib.fabric.FabricInventory;
//import cc.minetale.mlib.fabric.content.FabricContents;
//import cc.minetale.mlib.fabric.content.FabricProvider;
//import net.minestom.server.entity.Player;
//import net.minestom.server.inventory.InventoryType;
//
//public class PunishmentMenu implements FabricProvider {
//
//    private final FabricInventory inventory;
//    private final Profile offender;
//
//    public PunishmentMenu(Player player, Profile offender) {
//        this.inventory = FabricInventory.builder()
//                .provider(this)
//                .type(InventoryType.CHEST_3_ROW)
////                .title(MC.component("Punish " + offender.getName()))
//                .build();
//        this.offender = offender;
//        this.inventory.open(player);
//    }
//
//    @Override
//    public void init(Player player, FabricContents contents) {
////        contents.fill(MenuUtil.FILLER);
////
////        contents.setSlot(13, ClickableItem.of(ItemStack.of(Material.ANVIL)
////                .withDisplayName(MC.component("Punish " + offender.getName(), NamedTextColor.GRAY)),
////                event -> {
////                    FlameUtil.playClickSound(player);
////            new PunishmentTypeMenu(event.getPlayer(), this.offender);
////                }));
//    }
//}
