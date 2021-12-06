package cc.minetale.flame.listeners;

import cc.minetale.commonlib.api.APIListener;
import cc.minetale.commonlib.api.Punishment;
import cc.minetale.flame.util.FlamePlayer;
import cc.minetale.mlib.util.SoundsUtil;
import net.minestom.server.MinecraftServer;

public class CoreListener implements APIListener {

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
//                        MC.SEPARATOR_80,
//                        Lang.ANNOUNCE_PUNISHMENT_CONTEXT(targetProfile, initiatorProfile, punishment),
//                        MC.SEPARATOR_80
//                );
//        });
//    }

}
