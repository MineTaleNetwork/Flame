package cc.minetale.flame.chat;

import cc.minetale.mlib.util.ProfileUtil;
import cc.minetale.commonlib.modules.profile.Profile;
import cc.minetale.commonlib.modules.rank.Rank;
import cc.minetale.commonlib.util.MC;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.minestom.server.entity.Player;

import java.awt.*;

public class Chat {

    public static void handleChat(Player player, String message) {
        var instance = player.getInstance();

        if(instance == null) {
            player.sendMessage("A fatal error has occurred. Please trying rejoining the network.");
            return;
        }

        ProfileUtil.getAssociatedProfile(player).thenAccept(profile -> {
//            if (profile.getStaffProfile().isLocked()) {
//                ChatProcedure.handleAuthentication(sender, profile, message);
//                return;
//            }
//
//            PunishmentProcedure punishmentProcedure = PunishmentProcedure.getByPlayer(sender);
//
//            if (punishmentProcedure != null) {
//                ChatProcedure.handlePunishmentProcedure(punishmentProcedure, sender, message);
//                return;
//            }
//
//            GrantProcedure grantProcedure = GrantProcedure.getByPlayer(sender);
//
//            if (grantProcedure != null) {
//                ChatProcedure.handleGrantProcedure(grantProcedure, sender, message);
//                return;
//            }
//
//            if (!player.hasPermission("flame.staff")) {
//                Punishment punishment = profile.api().getActivePunishmentByType(Punishment.Type.MUTE);
//
//                if (FlameAPI.chatMuted) {
//                    player.sendMessage(MC.Style.SEPARATOR);
//                    player.sendMessage(Component.text("The chat is currently muted! Please try again later.")
//                            .color(MC.CC.RED.getTextColor()));
//                    player.sendMessage(MC.Style.SEPARATOR);
//                    return;
//                }
//
//                if (punishment != null) {
//                    Util.getPunishmentMessage(punishment).forEach(sender::sendMessage);
//                    return;
//                }
//
//                if (!ChatFilter.isSafe(message)) {
//                    var markedMessage = Component.text("[")
//                            .color(NamedTextColor.DARK_GRAY)
//                            .append(Component.text("➤")
//                                    .color(NamedTextColor.RED))
//                            .append(Component.text("] ")
//                                    .color(NamedTextColor.DARK_GRAY))
//                            .append(formatChat(profile, message));
//
//                    Bukkit.getServer().getOnlinePlayers().stream()
//                            .filter(staff -> staff.hasPermission("flame.staff"))
//                            .forEach(staff -> {
//                                staff.sendMessage(markedMessage);
//                            });
//
//                    sender.sendMessage(formatChat(profile, message));
//                    return;
//                }
//            }

            instance.sendMessage(player, formatChat(profile, message));
        });
    }

    // TODO: TEMP UNTIL IT STOPS BEING A BITCH AND WORKING WITH HOVER EVENT

    public static TextComponent formatChat(Profile profile, String message) {
        Rank rank = profile.api().getActiveGrant().api().getRank();
        Color color = rank.api().getRankColor().getColor();

        return Component.text()
                .append(profile.api().getChatFormat())
                .append(Component.text(" » ")
                        .color(NamedTextColor.DARK_GRAY))
                .append(Component.text(message, TextColor.color(MC.Style.bleach(color, 0.80))))
                .build();
    }

    public static void handleAuthentication(Player player, Profile profile, Component message) {
        TextComponent component = (TextComponent) message;
        String content = component.content();

        try {
            int code = Integer.parseInt(content);

            if (correctCode(profile, code)) {
                player.sendMessage(MC.Style.SEPARATOR);
                player.sendMessage(Component.text("Access Granted! Thank you for Authenticating!")
                        .color(MC.CC.GREEN.getTextColor()));
                player.sendMessage(MC.Style.SEPARATOR);
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

//    public static void handleGrantProcedure(GrantProcedure procedure, Player player, Component message) {
//        TextComponent component = (TextComponent) message;
//        String content = component.content();
//
//        if (procedure.getStage() == GrantProcedure.Stage.REQUIRE_REASON && procedure.getType() == GrantProcedure.Type.REMOVE) {
//            if (content.equalsIgnoreCase("cancel")) {
//                GrantProcedure.procedures.remove(procedure);
//                player.sendMessage(Component.text("You have cancelled the grant removal procedure.")
//                        .color(MC.CC.RED.getTextColor()));
//                return;
//            }
//
//            Bukkit.getServer().getScheduler().runTask(Flame.getFlame(), () -> new DeleteGrantMenu(player, procedure, content));
//        }
//
//        if (procedure.getStage() == GrantProcedure.Stage.PROVIDE_CUSTOM_TIME && procedure.getType() == GrantProcedure.Type.ADD) {
//            if (content.equalsIgnoreCase("cancel")) {
//                GrantProcedure.procedures.remove(procedure);
//                player.sendMessage(Component.text("You have cancelled the grant procedure.")
//                        .color(MC.CC.RED.getTextColor()));
//                return;
//            }
//
//            Duration duration = Duration.fromString(content);
//
//            if (duration.getValue() == -1) {
//                procedure.cancel();
//                player.sendMessage(Component.text("That duration is not valid. Canceling grant procedure.")
//                        .color(MC.CC.RED.getTextColor()));
//                player.sendMessage(Component.text("Example: [perm/1y1m1w1d]")
//                        .color(MC.CC.RED.getTextColor()));
//            } else {
//                Grant grant = procedure.getGrant();
//                grant.setDuration(duration.getValue());
//                procedure.setGrant(grant);
//                procedure.setStage(GrantProcedure.Stage.REQUIRE_REASON);
//                Bukkit.getServer().getScheduler().runTask(Flame.getFlame(), () -> new GrantReasonMenu(player, procedure));
//            }
//            return;
//        }
//
//        if (procedure.getStage() == GrantProcedure.Stage.PROVIDE_CUSTOM_REASON && procedure.getType() == GrantProcedure.Type.ADD) {
//            if (content.equalsIgnoreCase("cancel")) {
//                GrantProcedure.procedures.remove(procedure);
//                player.sendMessage(Component.text("You have cancelled the grant procedure.")
//                        .color(MC.CC.RED.getTextColor()));
//                return;
//            }
//
//            Grant grant = procedure.getGrant();
//            grant.setAddedReason(content);
//            procedure.setGrant(grant);
//            procedure.setStage(GrantProcedure.Stage.REQUIRE_CONFIRMATION);
//            Bukkit.getServer().getScheduler().runTask(Flame.getFlame(), () -> new ConfirmGrantMenu(player, procedure));
//        }
//    }
//
//    public static void handlePunishmentProcedure(PunishmentProcedure procedure, Player player, Component message) {
//        TextComponent component = (TextComponent) message;
//        String content = component.content();
//
//        if (procedure.getStage() == PunishmentProcedure.Stage.REQUIRE_TEXT) {
//
//            if (content.equalsIgnoreCase("cancel")) {
//                PunishmentProcedure.getProcedures().remove(procedure);
//                player.sendMessage(Component.text("You have cancelled the punishment procedure.")
//                        .color(MC.CC.RED.getTextColor()));
//                return;
//            }
//
//            if (procedure.getType() == PunishmentProcedure.Type.REMOVE) {
//                Bukkit.getServer().getScheduler().runTask(Flame.getFlame(), () -> new DeletePunishmentMenu(player, procedure, content));
//            }
//        }
//    }

}
