package cc.minetale.flame.chat;

import cc.minetale.commonlib.api.Punishment;
import cc.minetale.commonlib.api.Rank;
import cc.minetale.commonlib.profile.Profile;
import cc.minetale.commonlib.util.Duration;
import cc.minetale.commonlib.util.MC;
import cc.minetale.flame.FlameAPI;
import cc.minetale.flame.menu.grant.GrantConfirmMenu;
import cc.minetale.flame.menu.grant.GrantReasonMenu;
import cc.minetale.flame.procedure.GrantProcedure;
import cc.minetale.flame.procedure.Procedure;
import cc.minetale.flame.procedure.PunishmentProcedure;
import cc.minetale.flame.util.FlamePlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.minestom.server.entity.Player;

public class Chat {

    public static void handleChat(FlamePlayer player, String message) {
        var profile = player.getProfile();

            Procedure procedure = Procedure.getProcedure(player);

            if (procedure != null) {
                if(procedure instanceof GrantProcedure grantProcedure) {
                    handleGrantProcedure(grantProcedure, player, message);
                    return;
                }

                if(procedure instanceof PunishmentProcedure punishmentProcedure) {
                    handlePunishmentProcedure(punishmentProcedure, player, message);
                    return;
                }

                return;
            }

            if(!Rank.hasMinimumRank(profile, Rank.HELPER)) {
                var punishment = profile.getActivePunishmentByType(Punishment.Type.MUTE);

                if (FlameAPI.isChatMuted()) {
                    player.sendMessage(MC.SEPARATOR_80);
                    player.sendMessage(Component.text("The chat is currently muted! Please try again later.", NamedTextColor.RED));
                    player.sendMessage(MC.SEPARATOR_80);
                    return;
                }

                if (punishment != null) {
                    punishment.getPunishmentMessage().forEach(player::sendMessage);
                    return;
                }
            }

            var instance = player.getInstance();

            if(instance != null)
                instance.sendMessage(player, formatChat(profile, message));
    }

    public static TextComponent formatChat(Profile profile, String message) {
        var rank = profile.getGrant().getRank();

        return Component.text().append(
                profile.getChatFormat(),
                Component.text(" » ", NamedTextColor.DARK_GRAY, TextDecoration.BOLD),
                Component.text(message, MC.toTextColor(MC.bleach(MC.fromNamedTextColor(rank.getColor()), 0.80)))
        ).build();
    }

    private static void handlePunishmentProcedure(PunishmentProcedure procedure, Player player, String message) {
        if (message.equalsIgnoreCase("cancel")) {
            procedure.cancel();
            return;
        }

        switch (procedure.getStage()) {
            case PROVIDE_TIME -> {
                long duration = Duration.fromString(message).getValue();

                if (duration == -1) {
                    player.sendMessage(Component.text("That duration is not valid. Example: [perm/1y1m1w1d]", NamedTextColor.RED));
                    procedure.cancel();
                } else {
                    procedure.setDuration(duration);
                    procedure.setStage(PunishmentProcedure.Stage.PROVIDE_REASON);

//                    new PunishmentReasonMenu(player, procedure);
                }
            }
            case PROVIDE_REASON -> {
                procedure.setReason(message);
                procedure.setStage(PunishmentProcedure.Stage.PROVIDE_CONFIRMATION);

//                new PunishmentConfirmMenu(player, procedure);
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
                long duration = Duration.fromString(message).getValue();

                if (duration == -1) {
                    player.sendMessage(Component.text("That duration is not valid. Example: [perm/1y1m1w1d]", NamedTextColor.RED));
                    procedure.cancel();
                } else {
                    procedure.setDuration(duration);
                    procedure.setStage(GrantProcedure.Stage.PROVIDE_REASON);

                    new GrantReasonMenu(player, procedure);
                }
            }
            case PROVIDE_REASON -> {
                procedure.setReason(message);
                procedure.setStage(GrantProcedure.Stage.PROVIDE_CONFIRMATION);

                switch (procedure.getType()) {
                    case ADD, REMOVE -> new GrantConfirmMenu(player, procedure);
                }
            }
        }
    }

    public static void handleAuthentication(Player player, Profile profile, Component message) {
        var component = (TextComponent) message;
        var content = component.content();

        try {
            int code = Integer.parseInt(content);

            if (correctCode(profile, code)) {
                player.sendMessage(MC.SEPARATOR_80);
                player.sendMessage(Component.text("Access Granted! Thank you for Authenticating!")
                        .color(NamedTextColor.GREEN));
                player.sendMessage(MC.SEPARATOR_80);
            } else {
                player.kick(Component.text("Incorrect or Expired Two Factor Code", NamedTextColor.RED));
            }
        } catch (NumberFormatException e) {
            player.kick(Component.text("Incorrect or Expired Two Factor Code!", NamedTextColor.RED));
        }
    }

    private static boolean correctCode(Profile profile, int code) {
//        String secretKey = profile.getStaffProfile().getTwoFactorKey();
//        GoogleAuthenticator auth = Flame.getFlame().getAuth();
//        boolean codeIsValid = auth.authorize(secretKey, code);
//        if (codeIsValid) {
//            if (profile.getStaffProfile().isLocked()) {
//                profile.getStaffProfile().setLocked(false);
//                return true;
//            }
//        }
        return false;
    }

}
