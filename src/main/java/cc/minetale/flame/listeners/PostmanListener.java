package cc.minetale.flame.listeners;

import cc.minetale.flame.util.FlamePlayer;
import cc.minetale.postman.payload.PayloadHandler;
import cc.minetale.postman.payload.PayloadListener;
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

                flamePlayer.refreshPlayer(profile);
             }
        }
    }

    /*
     * Punishment Related Payloads
     */

}
