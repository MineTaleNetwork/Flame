package cc.minetale.flame.chat;

import cc.minetale.commonlib.profile.Profile;
import cc.minetale.commonlib.punishment.Punishment;
import cc.minetale.commonlib.rank.Rank;
import cc.minetale.commonlib.util.Duration;
import cc.minetale.commonlib.util.MC;
import cc.minetale.flame.FlameAPI;
import cc.minetale.flame.menu.grant.GrantConfirmMenu;
import cc.minetale.flame.menu.grant.GrantDeleteMenu;
import cc.minetale.flame.menu.grant.GrantReasonMenu;
import cc.minetale.flame.menu.punishment.PunishmentConfirmMenu;
import cc.minetale.flame.menu.punishment.PunishmentReasonMenu;
import cc.minetale.flame.procedure.GrantProcedure;
import cc.minetale.flame.procedure.PunishmentProcedure;
import cc.minetale.flame.util.FlamePlayer;
import cc.minetale.flame.util.FlameUtil;
import cc.minetale.flame.util.RankUtil;
import cc.minetale.mlib.util.ProfileUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.minestom.server.entity.Player;

import java.awt.*;
import java.util.UUID;

public class Chat {

    public static void handleChat(FlamePlayer player, String message) {
        ProfileUtil.getAssociatedProfile(player).thenAccept(profile -> {
            GrantProcedure grantProcedure = GrantProcedure.getByPlayer(profile.getId());

            if (grantProcedure != null) {
                handleGrantProcedure(grantProcedure, player, message);
                return;
            }

            PunishmentProcedure punishmentProcedure = PunishmentProcedure.getByPlayer(profile.getId());

            if (punishmentProcedure != null) {
                handlePunishmentProcedure(punishmentProcedure, player, message);
                return;
            }

            if(!RankUtil.hasMinimumRank(player, "Helper")) {
                Punishment punishment = profile.api().getActivePunishmentByType(Punishment.Type.MUTE);

                if (FlameAPI.isChatMuted()) {
                    player.sendMessage(MC.Style.SEPARATOR_80);
                    player.sendMessage(Component.text("The chat is currently muted! Please try again later.")
                            .color(MC.CC.RED.getTextColor()));
                    player.sendMessage(MC.Style.SEPARATOR_80);
                    return;
                }

                if (punishment != null) {
                    FlameUtil.getPunishmentMessage(punishment, false).forEach(player::sendMessage);
                    return;
                }
            }

            var instance = player.getInstance();

            if(instance != null)
                instance.sendMessage(player, formatChat(profile, message));
        });
    }

    public static TextComponent formatChat(Profile profile, String message) {
        Rank rank = profile.getGrant().api().getRank();
        Color color = rank.api().getRankColor().getColor();

        return Component.text()
                .append(profile.api().getChatFormat())
                .append(Component.text(" » ")
                        .color(NamedTextColor.DARK_GRAY))
                .append(Component.text(message, TextColor.color(MC.Style.bleach(color, 0.80))))
                .build();
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
                    player.sendMessage(Component.text("That duration is not valid. Example: [perm/1y1m1w1d]", MC.CC.RED.getTextColor()));
                    procedure.cancel();
                } else {
                    procedure.getBuilder().duration(duration);
                    procedure.setStage(PunishmentProcedure.Stage.PROVIDE_REASON);

                    new PunishmentReasonMenu(player, procedure);
                }
            }
            case PROVIDE_REASON -> {
                procedure.getBuilder().reason(message);
                procedure.setStage(PunishmentProcedure.Stage.PROVIDE_CONFIRMATION);
                new PunishmentConfirmMenu(player, procedure);
            }
        }
    }

    private static void handleGrantProcedure(GrantProcedure procedure, Player player, String message) {
        UUID uuid = player.getUuid();

        if (message.equalsIgnoreCase("cancel")) {
            GrantProcedure.getProcedures().remove(uuid);
            player.sendMessage(Component.text("You have cancelled the grant removal procedure.")
                    .color(MC.CC.RED.getTextColor()));
            return;
        }

        switch (procedure.getStage()) {
            case PROVIDE_TIME -> {
                long duration = Duration.fromString(message).getValue();

                if (duration == -1) {
                    player.sendMessage(Component.text("That duration is not valid. Example: [perm/1y1m1w1d]", MC.CC.RED.getTextColor()));
                    procedure.cancel();
                } else {
                    procedure.getBuilder().duration(duration);
                    procedure.setStage(GrantProcedure.Stage.PROVIDE_REASON);

                    new GrantReasonMenu(player, procedure);
                }
            }
            case PROVIDE_REASON -> {
                procedure.getBuilder().reason(message);
                procedure.setStage(GrantProcedure.Stage.PROVIDE_CONFIRMATION);

                switch (procedure.getType()) {
                    case ADD -> new GrantConfirmMenu(player, procedure);
                    case REMOVE -> new GrantDeleteMenu(player, procedure);
                }
            }
        }
    }

    public static void handleAuthentication(Player player, Profile profile, Component message) {
        TextComponent component = (TextComponent) message;
        String content = component.content();

        try {
            int code = Integer.parseInt(content);

            if (correctCode(profile, code)) {
                player.sendMessage(MC.Style.SEPARATOR_80);
                player.sendMessage(Component.text("Access Granted! Thank you for Authenticating!")
                        .color(MC.CC.GREEN.getTextColor()));
                player.sendMessage(MC.Style.SEPARATOR_80);
            } else {
                player.kick(Component.text("Incorrect or Expired Two Factor Code", NamedTextColor.RED));
            }
        } catch (NumberFormatException e) {
            player.kick(Component.text("Incorrect or Expired Two Factor Code!", MC.CC.RED.getTextColor()));
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
