package cc.minetale.flame.listeners;

import cc.minetale.flame.util.FlamePlayer;
import cc.minetale.flame.util.FlameProvider;
import cc.minetale.postman.payload.PayloadHandler;
import cc.minetale.postman.payload.PayloadListener;
import cc.minetale.sodium.payloads.GrantPayload;
import cc.minetale.sodium.payloads.PunishmentPayload;
import cc.minetale.sodium.payloads.UpdateProfilePayload;
import cc.minetale.sodium.profile.ProfileUtil;
import net.minestom.server.MinecraftServer;

public class PostmanListener implements PayloadListener {

    /*
     * Profile Related Payloads
     */

    @PayloadHandler
    public void onProfileUpdate(UpdateProfilePayload payload) {
        var playerUuid = payload.getPlayer();
        var player = MinecraftServer.getConnectionManager().getPlayer(playerUuid);

        if (player != null) {
            var profile = ProfileUtil.getProfile(playerUuid);

            if (profile != null) {
                var flamePlayer = FlamePlayer.fromPlayer(player);

                flamePlayer.setProfile(profile);
             }
        }
    }

    /*
     * Grant Related Payloads
     */

    @PayloadHandler
    public void onGrant(GrantPayload payload) {
        var player = MinecraftServer.getConnectionManager().getPlayer(payload.getPlayer());
        var grant = payload.getGrant();

        if(player == null) { return; }

        switch (payload.getAction()) {
            case ADD -> FlameProvider.addGrant(player, grant);
            case EXPIRE -> FlameProvider.expireGrant(player, grant);
            case REMOVE -> FlameProvider.removeGrant(player, grant);
        }
    }

    /*
     * Punishment Related Payloads
     */

    @PayloadHandler
    public void onPunishment(PunishmentPayload payload) {
        var player = MinecraftServer.getConnectionManager().getPlayer(payload.getPlayer());
        var punishment = payload.getPunishment();

        if(player == null) { return; }

        switch (payload.getAction()) {
            case ADD -> {
//                FlameProvider.addGrant(player, grant);
            }
            case EXPIRE -> {
//                FlameProvider.expireGrant(player, grant);
            }
            case REMOVE -> {
//                FlameProvider.removeGrant(player, grant);
            }
        }
    }

}
