package cc.minetale.flame.menu.grant;

import cc.minetale.commonlib.util.Duration;
import cc.minetale.flame.procedure.GrantProcedure;
import cc.minetale.mlib.canvas.CanvasType;
import cc.minetale.mlib.canvas.FillingType;
import cc.minetale.mlib.canvas.Fragment;
import cc.minetale.mlib.canvas.Menu;
import cc.minetale.mlib.util.SoundsUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

public class GrantDurationMenu extends Menu {

    private final GrantProcedure procedure;
    private boolean shouldCancel = true;

    public GrantDurationMenu(Player player, GrantProcedure procedure) {
        super(player, Component.text("Grant Duration Selection"), CanvasType.FOUR_ROW);

        this.procedure = procedure;

        setFiller(FillingType.BORDER);

        setFragment(10, Fragment.of(ItemStack.of(Material.RED_DYE)
                        .withDisplayName(Component.text("5 minutes", Style.style(NamedTextColor.GRAY, TextDecoration.ITALIC.as(false)))),
                event -> selectDuration(player, Duration.fromString("5m"))));

        setFragment(11, Fragment.of(ItemStack.of(Material.ORANGE_DYE)
                        .withDisplayName(Component.text("15 minutes", Style.style(NamedTextColor.GRAY, TextDecoration.ITALIC.as(false)))),
                event -> selectDuration(player, Duration.fromString("15m"))));

        setFragment(12, Fragment.of(ItemStack.of(Material.YELLOW_DYE)
                        .withDisplayName(Component.text("30 minutes", Style.style(NamedTextColor.GRAY, TextDecoration.ITALIC.as(false)))),
                event -> selectDuration(player, Duration.fromString("30m"))));

        setFragment(13, Fragment.of(ItemStack.of(Material.LIME_DYE)
                        .withDisplayName(Component.text("1 hour", Style.style(NamedTextColor.GRAY, TextDecoration.ITALIC.as(false)))),
                event -> selectDuration(player, Duration.fromString("1h"))));

        setFragment(14, Fragment.of(ItemStack.of(Material.GREEN_DYE)
                        .withDisplayName(Component.text("12 hours", Style.style(NamedTextColor.GRAY, TextDecoration.ITALIC.as(false)))),
                event -> selectDuration(player, Duration.fromString("12h"))));

        setFragment(15, Fragment.of(ItemStack.of(Material.CYAN_DYE)
                        .withDisplayName(Component.text("1 day", Style.style(NamedTextColor.GRAY, TextDecoration.ITALIC.as(false)))),
                event -> selectDuration(player, Duration.fromString("1d"))));

        setFragment(16, Fragment.of(ItemStack.of(Material.LIGHT_BLUE_DYE)
                        .withDisplayName(Component.text("1 week", Style.style(NamedTextColor.GRAY, TextDecoration.ITALIC.as(false)))),
                event -> selectDuration(player, Duration.fromString("1w"))));

        setFragment(19, Fragment.of(ItemStack.of(Material.BLUE_DYE)
                        .withDisplayName(Component.text("1 month", Style.style(NamedTextColor.GRAY, TextDecoration.ITALIC.as(false)))),
                event -> selectDuration(player, Duration.fromString("1M"))));

        setFragment(20, Fragment.of(ItemStack.of(Material.PURPLE_DYE)
                        .withDisplayName(Component.text("3 months", Style.style(NamedTextColor.GRAY, TextDecoration.ITALIC.as(false)))),
                event -> selectDuration(player, Duration.fromString("3M"))));

        setFragment(21, Fragment.of(ItemStack.of(Material.MAGENTA_DYE)
                        .withDisplayName(Component.text("6 months", Style.style(NamedTextColor.GRAY, TextDecoration.ITALIC.as(false)))),
                event -> selectDuration(player, Duration.fromString("6M"))));

        setFragment(22, Fragment.of(ItemStack.of(Material.BLACK_DYE)
                        .withDisplayName(Component.text("9 months", Style.style(NamedTextColor.GRAY, TextDecoration.ITALIC.as(false)))),
                event -> selectDuration(player, Duration.fromString("9M"))));

        setFragment(23, Fragment.of(ItemStack.of(Material.GRAY_DYE)
                        .withDisplayName(Component.text("1 year", Style.style(NamedTextColor.GRAY, TextDecoration.ITALIC.as(false)))),
                event -> selectDuration(player, Duration.fromString("1y"))));

        setFragment(24, Fragment.of(ItemStack.of(Material.LIGHT_GRAY_DYE)
                        .withDisplayName(Component.text("Permanent", Style.style(NamedTextColor.GRAY, TextDecoration.ITALIC.as(false)))),
                event -> selectDuration(player, Duration.fromString("perm"))));

        setFragment(25, Fragment.of(ItemStack.of(Material.WHITE_DYE)
                        .withDisplayName(Component.text("Custom", Style.style(NamedTextColor.GRAY, TextDecoration.ITALIC.as(false)))),
                event -> selectDuration(player, null)));

        openMenu();
    }

    @Override
    public void close() {
        if(this.shouldCancel)
            this.procedure.cancel();
    }

    private void selectDuration(Player player, Duration duration) {
        SoundsUtil.playClickSound(player);

        if(duration == null) {
            this.procedure.setStage(GrantProcedure.Stage.PROVIDE_TIME);
            this.shouldCancel = false;

            this.handleClose(player);
            player.closeInventory();

            player.sendMessage(Component.text("Type the amount of time you would like to add this grant for in chat...", NamedTextColor.GREEN));
        } else {
            this.procedure.setDuration(duration.value());
            new GrantReasonMenu(player, this.procedure);
        }
    }

}
