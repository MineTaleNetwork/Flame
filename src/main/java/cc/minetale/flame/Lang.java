package cc.minetale.flame;

import cc.minetale.commonlib.modules.profile.Profile;
import cc.minetale.commonlib.modules.punishment.Punishment;
import cc.minetale.commonlib.util.MC;
import net.kyori.adventure.text.Component;

public class Lang {

    public static final Component UNKNOWN_COMMAND = MC.Chat.notificationMessage("Command", MC.component("You have entered an unknown command.", MC.CC.RED));
    public static final Component CANCELLED_GRANT = MC.component("Cancelled the grant procedure.", MC.CC.RED);
    public static final Component PROFILE_FAILED = MC.component("Failed to load your profile. Try again later.", MC.CC.RED);

    public static Component ANNOUNCE_PUNISHMENT_CONTEXT(Profile target, Profile initiator, Punishment punishment) {
        return MC.Chat.notificationMessage("Punishment",
                MC.component(
                        target.api().getColoredName(),
                        MC.component(" has been " + punishment.api().getContext() + " by ", MC.CC.GRAY),
                        (punishment.getAddedByUUID() != null ? initiator.api().getColoredName() : MC.Style.CONSOLE)
                )
        );
    }

    public static Component PUNISHMENT_SUCCESS(Profile profile) {
        return MC.component("You have successfully punished " + profile.getName(), MC.CC.GREEN);
    }

    public static Component TO_MSG(Profile target, String message) {
        return MC.component(
                MC.component("(To ", MC.CC.GRAY),
                target.api().getChatFormat(),
                MC.component(") " + message, MC.CC.GRAY)
        );
    }

    public static Component FROM_MSG(Profile initiator, String message) {
        return MC.component(
                MC.component("(From ", MC.CC.GRAY),
                initiator.api().getChatFormat(),
                MC.component(") " + message, MC.CC.GRAY)
        );
    }

    public static Component COMMAND_PERMISSION(String rank) {
        return MC.Chat.notificationMessage("Permission",
                MC.component(
                        MC.component("You need ", MC.CC.GRAY),
                        MC.component(rank, MC.CC.GOLD),
                        MC.component(" rank to use this command.", MC.CC.GRAY)
                )
        );
    }

    public static Component CHAT_CLEARED(Profile profile) {
        return MC.component(
                MC.Style.SEPARATOR_80,
                Component.newline(),
                MC.Chat.notificationMessage("Chat",
                        MC.component(
                                MC.component("Chat has been cleared by ", MC.CC.GRAY),
                                profile.api().getChatFormat()
                        )
                ),
                Component.newline(),
                MC.Style.SEPARATOR_80
        );
    }

}
