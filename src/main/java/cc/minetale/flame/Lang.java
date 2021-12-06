package cc.minetale.flame;

import cc.minetale.commonlib.api.Punishment;
import cc.minetale.commonlib.api.Rank;
import cc.minetale.commonlib.profile.Profile;
import cc.minetale.commonlib.util.MC;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class Lang {

    public static final Component UNKNOWN_COMMAND = MC.notificationMessage("Command", Component.text("You have entered an unknown command.", NamedTextColor.RED));
    public static final Component CANCELLED_GRANT = Component.text("Cancelled the grant procedure.", NamedTextColor.RED);
    public static final Component PROFILE_FAILED = Component.text("Failed to load your profile. Try again later.", NamedTextColor.RED);

    public static Component ANNOUNCE_PUNISHMENT_CONTEXT(Profile target, Profile initiator, Punishment punishment) {
        return MC.notificationMessage("Punishment",
                Component.text().append(
                        target.getColoredName(),
                        Component.text(" has been " + punishment.getContext() + " by ", NamedTextColor.GRAY),
                        (punishment.getAddedById() != null ? initiator.getColoredName() : MC.CONSOLE)
                ).build()
        );
    }

    public static Component PUNISHMENT_SUCCESS(Profile profile) {
        return Component.text("You have successfully punished " + profile.getName(), NamedTextColor.GREEN);
    }

    public static Component TO_MSG(Profile target, String message) {
        return Component.text().append(
                Component.text("(To ", NamedTextColor.GRAY),
                target.getChatFormat(),
                Component.text(") " + message, NamedTextColor.GRAY)
        ).build();
    }

    public static Component FROM_MSG(Profile initiator, String message) {
        return Component.text().append(
                Component.text("(From ", NamedTextColor.GRAY),
                initiator.getChatFormat(),
                Component.text(") " + message, NamedTextColor.GRAY)
        ).build();
    }

    public static Component COMMAND_PERMISSION(Rank rank) {
        return MC.notificationMessage("Permission",
                Component.text().append(
                        Component.text("You need ", NamedTextColor.GRAY),
                        Component.text(rank.getName(), NamedTextColor.GOLD),
                        Component.text(" rank to use this command.", NamedTextColor.GRAY)
                ).build()
        );
    }

    public static Component CHAT_CLEARED(Profile profile) {
        return Component.text().append(
                MC.SEPARATOR_80,
                Component.newline(),
                MC.notificationMessage("Chat",
                        Component.text().append(
                                Component.text("Chat has been cleared by ", NamedTextColor.GRAY),
                                profile.getChatFormat()
                        ).build()),
                Component.newline(),
                MC.SEPARATOR_80
        ).build();
    }

}
