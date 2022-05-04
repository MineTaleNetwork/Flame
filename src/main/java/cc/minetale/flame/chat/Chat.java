package cc.minetale.flame.chat;

import cc.minetale.flame.FlameAPI;
import cc.minetale.flame.menu.grant.ConfirmNewGrantMenu;
import cc.minetale.flame.menu.grant.GrantReasonMenu;
import cc.minetale.flame.menu.punishment.ConfirmPunishment;
import cc.minetale.flame.menu.punishment.PunishmentReasonMenu;
import cc.minetale.flame.procedure.GrantProcedure;
import cc.minetale.flame.procedure.Procedure;
import cc.minetale.flame.procedure.PunishmentProcedure;
import cc.minetale.flame.util.FlamePlayer;
import cc.minetale.mlib.canvas.template.Menu;
import cc.minetale.sodium.cache.ProfileCache;
import cc.minetale.sodium.lang.Language;
import cc.minetale.sodium.profile.Profile;
import cc.minetale.sodium.profile.ProfileUtil;
import cc.minetale.sodium.profile.grant.Rank;
import cc.minetale.sodium.profile.punishment.PunishmentType;
import cc.minetale.sodium.util.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.entity.Player;

public class Chat {

    public static void handleChat(FlamePlayer player, String message) {
        var profile = player.getProfile();
        var procedure = Procedure.getProcedure(player.getUuid());

        if(profile.getStaffProfile().isLocked()) {
            handleAuthentication(player, profile, message);
            return;
        }

        if (procedure != null) {
            if (procedure instanceof GrantProcedure grantProcedure) {
                handleGrantProcedure(grantProcedure, player, message);
                return;
            }

            if (procedure instanceof PunishmentProcedure punishmentProcedure) {
                handlePunishmentProcedure(punishmentProcedure, player, message);
                return;
            }

            return;
        }

        if (!Rank.hasMinimumRank(profile, Rank.HELPER)) {
            if (FlameAPI.isChatMuted()) {
                player.sendMessage(Message.chatSeparator());
                player.sendMessage(Component.text("The chat is currently muted! Please try again later.", NamedTextColor.RED));
                player.sendMessage(Message.chatSeparator());
                return;
            }

            var punishment = profile.getActivePunishmentByType(PunishmentType.MUTE);

            if (punishment != null) {
                punishment.getPunishmentMessage().forEach(player::sendMessage);
                return;
            }
        }

        var instance = player.getInstance();

        if (instance != null)
            instance.sendMessage(player, formatChat(player, message));
    }

    public static Component formatChat(FlamePlayer player, String message) {
        var profile = player.getProfile();

        return Message.parse(
                Language.General.CHAT_FORMAT,
                profile.getChatFormat(),
                Component.text(message, Colors.bleach(profile.getGrant().getRank().getColor(), 0.80))
        );
    }

    private static void handlePunishmentProcedure(PunishmentProcedure procedure, Player player, String message) {
        if (message.equalsIgnoreCase("cancel")) {
            procedure.cancel();
            return;
        }

        switch (procedure.getStage()) {
            case PROVIDE_TIME -> {
                long duration = Duration.fromString(message).value();

                if (duration == -1) {
                    player.sendMessage(Component.text("That duration is not valid. Example: [perm/1m1d1w1d]", NamedTextColor.RED));
                    procedure.cancel();
                } else {
                    procedure.setDuration(duration);
                    procedure.setStage(PunishmentProcedure.Stage.PROVIDE_REASON);

                    Menu.openMenu(new PunishmentReasonMenu(player, procedure));
                }
            }
            case PROVIDE_REASON -> {
                procedure.setReason(message);
                procedure.setStage(PunishmentProcedure.Stage.PROVIDE_CONFIRMATION);

                Menu.openMenu(new ConfirmPunishment(player, procedure));
            }
        }
    }

    private static void handleGrantProcedure(GrantProcedure procedure, Player player, String message) {
        if (message.equalsIgnoreCase("cancel")) {
            procedure.cancel();
            return;
        }

        switch (procedure.getStage()) {
            case PROVIDE_TIME -> {
                long duration = Duration.fromString(message).value();

                if (duration == -1) {
                    player.sendMessage(Component.text("That duration is not valid. Example: [perm/1y1m1w1M1y]", NamedTextColor.RED));
                    procedure.cancel();
                } else {
                    procedure.setDuration(duration);
                    procedure.setStage(GrantProcedure.Stage.PROVIDE_REASON);

                    Menu.openMenu(new GrantReasonMenu(player, procedure));
                }
            }
            case PROVIDE_REASON -> {
                procedure.setReason(message);
                procedure.setStage(GrantProcedure.Stage.PROVIDE_CONFIRMATION);

                switch (procedure.getType()) {
                    case ADD -> {
                        Menu.openMenu(new ConfirmNewGrantMenu(player, procedure));
                    }
                }
            }
        }
    }

    public static void handleAuthentication(Player player, Profile profile, String message) {
        try {
            int code = Integer.parseInt(message);
            var staffProfile = profile.getStaffProfile();

            if (Auth.isValid(staffProfile.getTwoFactorKey(), code)) {
                staffProfile.setLocked(false);

                // TODO -> ???

                MongoUtil.saveDocument(Profile.getCollection(), profile.getUuid(), profile);

                var redisProfile = ProfileUtil.fromCache(player.getUuid());
                redisProfile.getProfile().getStaffProfile().setLocked(false);
                ProfileCache.pushCache(redisProfile);

                player.sendMessage(Message.chatSeparator());
                player.sendMessage(Component.text("Access Granted! Thank you for Authenticating!", NamedTextColor.GREEN));
                player.sendMessage(Message.chatSeparator());

                return;
            }
        } catch (NumberFormatException ignored) {}

        player.kick(Component.text("Incorrect or Expired Two Factor Code!", NamedTextColor.RED));
    }

}
