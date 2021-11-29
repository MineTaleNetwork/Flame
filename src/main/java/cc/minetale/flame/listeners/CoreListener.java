package cc.minetale.flame.listeners;

import cc.minetale.commonlib.api.APIListener;
import cc.minetale.commonlib.api.Grant;
import cc.minetale.commonlib.api.Punishment;
import cc.minetale.commonlib.api.Rank;
import cc.minetale.commonlib.util.MC;
import cc.minetale.commonlib.util.TimeUtil;
import cc.minetale.flame.util.FlamePlayer;
import cc.minetale.mlib.util.SoundsUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.MinecraftServer;

public class CoreListener implements APIListener {

    @Override
    public void grantAdd(Grant grant) {
        var player = MinecraftServer.getConnectionManager().getPlayer(grant.getPlayerId());
        var rank = grant.getRank();

        if(player == null || rank == null) { return; }

        var profile = FlamePlayer.fromPlayer(player).getProfile();

        if(rank != Rank.DEFAULT) {
            player.sendMessage(MC.Style.SEPARATOR_80);
            player.sendMessage(MC.Chat.notificationMessage("Grant",
                            Component.text("A '" + rank.getName() + "' grant has been applied to you " +
                                    (grant.getDuration() == Integer.MAX_VALUE ? "permanently" : "for " + TimeUtil.millisToRoundedTime(grant.getDuration())) + "."))
                    .color(NamedTextColor.GRAY));
            player.sendMessage(MC.Style.SEPARATOR_80);
        }

        profile.reloadGrant();
    }

    @Override
    public void grantExpire(Grant grant) {
        var player = MinecraftServer.getConnectionManager().getPlayer(grant.getPlayerId());
        var rank = grant.getRank();

        if(player == null || rank == null) { return; }

        var profile = FlamePlayer.fromPlayer(player).getProfile();

        player.sendMessage(MC.Style.SEPARATOR_80);
        player.sendMessage(MC.Chat.notificationMessage("Grant",
                Component.text("Your '" + rank.getName() + "' grant has expired.")
                        .color(NamedTextColor.GRAY)));
        player.sendMessage(MC.Style.SEPARATOR_80);

        profile.reloadGrant();
    }

    @Override
    public void grantRemove(Grant grant) {
        var player = MinecraftServer.getConnectionManager().getPlayer(grant.getPlayerId());
        var rank = grant.getRank();

        if(player == null || rank == null) { return; }

        var profile = FlamePlayer.fromPlayer(player).getProfile();

        player.sendMessage(MC.Style.SEPARATOR_80);
        player.sendMessage(MC.Chat.notificationMessage("Grant",
                Component.text("Your '" + rank.getName() + "' grant has been removed.")
                        .color(NamedTextColor.GRAY)));
        player.sendMessage(MC.Style.SEPARATOR_80);

        profile.reloadGrant();
    }

    // TODO -> Moving the kicking over to Blitz
    @Override
    public void punishmentAdd(Punishment punishment) {
        var player = MinecraftServer.getConnectionManager().getPlayer(punishment.getPlayerId());

//        announcePunishment(punishment);

        if(player == null) { return; }

        var profile = FlamePlayer.fromPlayer(player).getProfile();

        if(!punishment.isRemoved()) {
            if (punishment.getType() == Punishment.Type.BAN || punishment.getType() == Punishment.Type.BLACKLIST) {
//                player.kick(Component.join(JoinConfiguration.separator(Component.newline()), FlameUtil.getPunishmentMessage(punishment, true)));
            } else if (punishment.getType() == Punishment.Type.MUTE) {
//                FlameUtil.getPunishmentMessage(punishment, true).forEach(player::sendMessage);
                SoundsUtil.playErrorSound(player);
            }
        }
    }

    @Override
    public void punishmentExpire(Punishment punishment) {
//        announcePunishment(punishment);
    }

    @Override
    public void punishmentRemove(Punishment punishment) {
//        announcePunishment(punishment);
    }

//    public void announcePunishment(Punishment punishment) {
//        List<UUID> profileIds = new ArrayList<>();
//
//        UUID target = punishment.getAddedById();
//
//        profileIds.add(target);
//
//        UUID initiator = punishment.getAddedByUUID();
//
//        if(initiator != null)
//            profileIds.add(initiator);
//
//        ProfileUtil.getProfilesByIds(profileIds).thenAccept(profiles -> {
//            Profile targetProfile = null;
//            Profile initiatorProfile = null;
//
//            for(Profile profile : profiles) {
//                if(profile.getId().equals(target)) {
//                    targetProfile = profile;
//                } else if(profile.getId().equals(initiator)) {
//                    initiatorProfile = profile;
//                }
//            }
//
//            if(targetProfile != null)
//                FlameUtil.broadcast("Helper",
//                        MC.Style.SEPARATOR_80,
//                        Lang.ANNOUNCE_PUNISHMENT_CONTEXT(targetProfile, initiatorProfile, punishment),
//                        MC.Style.SEPARATOR_80
//                );
//        });
//    }

}
