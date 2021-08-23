package cc.minetale.flame.util;

import cc.minetale.commonlib.modules.profile.Profile;
import cc.minetale.commonlib.modules.punishment.Punishment;
import cc.minetale.commonlib.util.MC;
import cc.minetale.commonlib.util.TimeUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.permission.Permission;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class FlameUtil {

    public static TextColor getPunishmentColor(Punishment punishment) {
        switch (punishment.getType()) {
            case BLACKLIST: {
                return MC.CC.RED.getTextColor();
            }
            case BAN: {
                return MC.CC.GOLD.getTextColor();
            }
            case MUTE: {
                return MC.CC.GREEN.getTextColor();
            }
            case WARN: {
                return MC.CC.BLUE.getTextColor();
            }
            default:
                return MC.CC.WHITE.getTextColor();
        }
    }

    public static List<Component> getPunishmentMessage(Punishment punishment) {
        Date date = new Date(punishment.getAddedAt());

        return Arrays.asList(
                MC.Style.SEPARATOR,
                Component.text("You are " + punishment.api().getContext() + (!punishment.api().isPermanent() ? " for " + punishment.api().getTimeRemaining() : "") + ".")
                        .color(NamedTextColor.RED),
                Component.empty(),
                Component.text("Reason: ")
                        .color(NamedTextColor.GRAY)
                        .append(
                                Component.text(punishment.getAddedReason())
                                        .color(NamedTextColor.WHITE)
                        ),
                Component.text("Added On: ")
                        .color(NamedTextColor.GRAY)
                        .append(
                                Component.text(TimeUtil.dateToString(date, true))
                                        .color(NamedTextColor.WHITE)
                        ),
                Component.text("Punishment ID: ")
                        .color(NamedTextColor.GRAY)
                        .append(
                                Component.text(punishment.getId())
                                        .color(NamedTextColor.WHITE)
                        ),
                Component.empty(),
                Component.text("Appeal At: ")
                        .color(NamedTextColor.GRAY)
                        .append(
                                Component.text("https://customwrld.com/discord")
                                        .color(NamedTextColor.AQUA)
                                        .decoration(TextDecoration.UNDERLINED, true)
                        ),
                MC.Style.SEPARATOR
        );
    }

//    public static boolean isElevatedStaff(UUID player) {
//        return Flame.getFlame().getElevatedStaff().contains(player);
//    }

    public static void setupPlayer(Profile profile) {
        Player player = MinecraftServer.getConnectionManager().getPlayer(profile.getId());

        if (player != null) {
            for (String permission : profile.api().getAllPermissions()) {
                player.addPermission(new Permission(permission));
            }

//            boolean isElevated = isElevatedStaff(player.getUniqueId());
//            if (isElevated)
//                player.setOp(true);
        }
    }

}
