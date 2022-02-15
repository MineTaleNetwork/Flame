//package cc.minetale.flame.menu.grant;
//
//import cc.minetale.flame.procedure.GrantProcedure;
//import cc.minetale.mlib.canvas.*;
//import cc.minetale.mlib.util.MenuUtil;
//import cc.minetale.mlib.util.SoundsUtil;
//import net.kyori.adventure.text.Component;
//import net.kyori.adventure.text.format.NamedTextColor;
//import net.kyori.adventure.text.format.Style;
//import net.kyori.adventure.text.format.TextDecoration;
//import net.minestom.server.entity.Player;
//import net.minestom.server.item.ItemStack;
//import net.minestom.server.item.Material;
//
//import java.util.Arrays;
//
//public class GrantReasonMenu extends Menu {
//
//    private final GrantProcedure procedure;
//    private boolean shouldCancel = true;
//
//    public GrantReasonMenu(Player player, GrantProcedure procedure) {
//        super(player, Component.text("Grant Reason Selection"), CanvasType.FOUR_ROW);
//
//        this.procedure = procedure;
//
//        setFiller(FillingType.BORDER);
//
//        setFragment(30, MenuUtil.PREVIOUS_PAGE(this));
//        setFragment(32, MenuUtil.NEXT_PAGE(this));
//
//        var reasons = Arrays.asList(
//                "Donation Issue",
//                "Promoted",
//                "Demoted",
//                "Admin Discretion",
//                "Event Winner",
//                "Custom"
//        );
//
//        var pagination = new Pagination(10, 14, true);
//        var fragments = new Fragment[reasons.size()];
//
//        int i = 0;
//
//        for (var reason : reasons) {
//            fragments[i] = Fragment.of(ItemStack.of(Material.PAPER)
//                    .withDisplayName(Component.text(reason, Style.style(NamedTextColor.GRAY, TextDecoration.ITALIC.as(false)))), event -> {
//                SoundsUtil.playClickSound(player);
//
//                if(reason.equals("Custom")) {
//                    this.procedure.setStage(GrantProcedure.Stage.PROVIDE_CONFIRMATION);
//                    this.shouldCancel = false;
//
//                    this.handleClose(player);
//                    player.closeInventory();
//
//                    player.sendMessage(Component.text("Type the reason for adding this grant in chat...", NamedTextColor.GREEN));
//                } else {
//                    this.procedure.setReason(reason);
//                    new GrantConfirmMenu(player, this.procedure);
//                }
//            });
//
//            i++;
//        }
//
//        pagination.setFragments(fragments);
//        setPagination(pagination);
//
//        openMenu();
//    }
//
//    @Override
//    public void close() {
//        if(this.shouldCancel)
//            this.procedure.cancel();
//    }
//
//}
