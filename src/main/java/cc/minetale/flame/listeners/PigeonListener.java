package cc.minetale.flame.listeners;

import cc.minetale.commonlib.cache.ProfileCache;
import cc.minetale.commonlib.grant.Grant;
import cc.minetale.commonlib.pigeon.payloads.grant.GrantAddPayload;
import cc.minetale.commonlib.pigeon.payloads.grant.GrantRemovePayload;
import cc.minetale.commonlib.pigeon.payloads.profile.ProfileUpdatePayload;
import cc.minetale.commonlib.pigeon.payloads.punishment.PunishmentAddPayload;
import cc.minetale.commonlib.pigeon.payloads.punishment.PunishmentRemovePayload;
import cc.minetale.flame.util.FlamePlayer;
import cc.minetale.flame.util.FlameProvider;
import cc.minetale.mlib.nametag.NameplateHandler;
import cc.minetale.mlib.nametag.NameplateProvider;
import cc.minetale.mlib.nametag.ProviderType;
import cc.minetale.mlib.util.TeamUtil;
import cc.minetale.pigeon.annotations.PayloadHandler;
import cc.minetale.pigeon.annotations.PayloadListener;
import cc.minetale.pigeon.listeners.Listener;
import net.minestom.server.MinecraftServer;

@PayloadListener
public class PigeonListener implements Listener {

    @PayloadHandler
    public void onProfileUpdate(ProfileUpdatePayload payload) {
        var playerUuid = payload.getPlayer();
        var player = MinecraftServer.getConnectionManager().getPlayer(playerUuid);

        ProfileCache.getProfile(payload.getPlayer())
                .thenAccept(profile -> {
                    if (profile != null && player != null) {
                        var flamePlayer = FlamePlayer.fromPlayer(player);

                        flamePlayer.setProfile(profile);

                        NameplateHandler.addProvider(player, new NameplateProvider(TeamUtil.RANK_MAP.get(profile.getGrant().getRank()), ProviderType.RANK));
                        player.refreshCommands();
                    }
                });
    }

    @PayloadHandler
    public void onGrantAdd(GrantAddPayload payload) {
        var player = MinecraftServer.getConnectionManager().getPlayer(payload.getPlayerUuid());

        if (player != null) {
            Grant.getGrant(payload.getGrant())
                    .thenAccept(grant -> {
                        if (grant != null) {
                            FlameProvider.addGrant(player, grant);
                        }
                    });
        }
    }

    @PayloadHandler
    public void onGrantRemove(GrantRemovePayload payload) {
        var player = MinecraftServer.getConnectionManager().getPlayer(payload.getPlayerUuid());

        if (player != null) {
            Grant.getGrant(payload.getGrant())
                    .thenAccept(grant -> {
                        if (grant != null) {
                            FlameProvider.removeGrant(player, grant);
                        }
                    });
        }
    }

    // TODO
//    @PayloadHandler
//    public void onGrantExpire(GrantExpirePayload payload) {
//        FlameProvider.expireGrant(Grant.getGrant(payload.getGrant()));
//    }

    @PayloadHandler
    public void onPunishmentAdd(PunishmentAddPayload payload) {

    }

    @PayloadHandler
    public void onPunishmentRemove(PunishmentRemovePayload payload) {

    }

//    @PayloadHandler
//    public void onPunishmentExpire(PunishmentExpirePayload payload) {
//
//    }

}
