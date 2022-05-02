package cc.minetale.flame.menu;

import cc.minetale.flame.procedure.GrantProcedure;
import cc.minetale.flame.procedure.Procedure;
import cc.minetale.mlib.canvas.CanvasType;
import cc.minetale.mlib.canvas.Filler;
import cc.minetale.mlib.canvas.Fragment;
import cc.minetale.mlib.canvas.template.Menu;
import cc.minetale.mlib.util.SoundsUtil;
import cc.minetale.sodium.util.Duration;
import cc.minetale.sodium.util.Message;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

public class DurationMenu extends Menu {

    private final Procedure procedure;
    private final Menu nextMenu;
    private boolean shouldCancel = true;

    public DurationMenu(Player player, Procedure procedure, Menu nextMenu) {
        super(player, Component.text("Duration Selection"), CanvasType.FOUR_ROW);

        this.procedure = procedure;
        this.nextMenu = nextMenu;

        setFiller(Filler.BORDER);

        setButton(10, Fragment.of(ItemStack.of(Material.RED_DYE)
                        .withDisplayName(Component.text("30 minutes", Message.style(NamedTextColor.GRAY))),
                event -> selectDuration(player, Duration.fromString("30m"))));

        setButton(11, Fragment.of(ItemStack.of(Material.ORANGE_DYE)
                        .withDisplayName(Component.text("1 hour", Message.style(NamedTextColor.GRAY))),
                event -> selectDuration(player, Duration.fromString("1h"))));

        setButton(12, Fragment.of(ItemStack.of(Material.YELLOW_DYE)
                        .withDisplayName(Component.text("3 hours", Message.style(NamedTextColor.GRAY))),
                event -> selectDuration(player, Duration.fromString("3h"))));

        setButton(13, Fragment.of(ItemStack.of(Material.LIME_DYE)
                        .withDisplayName(Component.text("6 hours", Message.style(NamedTextColor.GRAY))),
                event -> selectDuration(player, Duration.fromString("6h"))));

        setButton(14, Fragment.of(ItemStack.of(Material.GREEN_DYE)
                        .withDisplayName(Component.text("12 hours", Message.style(NamedTextColor.GRAY))),
                event -> selectDuration(player, Duration.fromString("12h"))));

        setButton(15, Fragment.of(ItemStack.of(Material.CYAN_DYE)
                        .withDisplayName(Component.text("1 day", Message.style(NamedTextColor.GRAY))),
                event -> selectDuration(player, Duration.fromString("1d"))));

        setButton(16, Fragment.of(ItemStack.of(Material.LIGHT_BLUE_DYE)
                        .withDisplayName(Component.text("1 week", Message.style(NamedTextColor.GRAY))),
                event -> selectDuration(player, Duration.fromString("1w"))));

        setButton(19, Fragment.of(ItemStack.of(Material.BLUE_DYE)
                        .withDisplayName(Component.text("1 month", Message.style(NamedTextColor.GRAY))),
                event -> selectDuration(player, Duration.fromString("1M"))));

        setButton(20, Fragment.of(ItemStack.of(Material.PURPLE_DYE)
                        .withDisplayName(Component.text("3 months", Message.style(NamedTextColor.GRAY))),
                event -> selectDuration(player, Duration.fromString("3M"))));

        setButton(21, Fragment.of(ItemStack.of(Material.MAGENTA_DYE)
                        .withDisplayName(Component.text("6 months", Message.style(NamedTextColor.GRAY))),
                event -> selectDuration(player, Duration.fromString("6M"))));

        setButton(22, Fragment.of(ItemStack.of(Material.BLACK_DYE)
                        .withDisplayName(Component.text("9 months", Message.style(NamedTextColor.GRAY))),
                event -> selectDuration(player, Duration.fromString("9M"))));

        setButton(23, Fragment.of(ItemStack.of(Material.GRAY_DYE)
                        .withDisplayName(Component.text("1 year", Message.style(NamedTextColor.GRAY))),
                event -> selectDuration(player, Duration.fromString("1y"))));

        setButton(24, Fragment.of(ItemStack.of(Material.LIGHT_GRAY_DYE)
                        .withDisplayName(Component.text("Permanent", Message.style(NamedTextColor.GRAY))),
                event -> selectDuration(player, Duration.fromString("perm"))));

        setButton(25, Fragment.of(ItemStack.of(Material.WHITE_DYE)
                        .withDisplayName(Component.text("Custom", Message.style(NamedTextColor.GRAY))),
                event -> selectDuration(player, null)));
    }

    @Override
    public void close() {
        if(shouldCancel)
            procedure.cancel();
    }

    private void selectDuration(Player player, Duration duration) {
        SoundsUtil.playClickSound(player);

        if(duration == null) {
            procedure.setStage(GrantProcedure.Stage.PROVIDE_TIME);
            shouldCancel = false;

            handleClose(player);

            player.sendMessage(Component.text("Please enter a valid duration in chat...", NamedTextColor.GREEN));
        } else {
            procedure.setDuration(duration.value());
            Menu.openMenu(nextMenu);
        }
    }

}
