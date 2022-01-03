package cc.minetale.flame.listeners;

import cc.minetale.commonlib.cache.ProfileCache;
import cc.minetale.flame.Lang;
import cc.minetale.flame.chat.Chat;
import cc.minetale.flame.procedure.GrantProcedure;
import cc.minetale.flame.util.FlamePlayer;
import cc.minetale.mlib.nametag.NameplateHandler;
import cc.minetale.mlib.nametag.NameplateProvider;
import cc.minetale.mlib.nametag.ProviderType;
import cc.minetale.mlib.util.TeamUtil;
import net.minestom.server.event.EventFilter;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.player.*;
import net.minestom.server.event.trait.PlayerEvent;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class PlayerListener {

    public static EventNode<PlayerEvent> events() {
        return EventNode.type("flame", EventFilter.PLAYER)
                .addListener(PlayerChatEvent.class, event -> {
                    event.setCancelled(true);

                    Chat.handleChat(FlamePlayer.fromPlayer(event.getPlayer()), event.getMessage());
                })
                .addListener(PlayerDisconnectEvent.class, event -> {
                    var procedure = GrantProcedure.getProcedure(event.getPlayer());

                    if (procedure != null)
                        procedure.finish();
                })
                .addListener(AsyncPlayerPreLoginEvent.class, event -> {
                    var player = FlamePlayer.fromPlayer(event.getPlayer());

                    try {
                        var profile = ProfileCache.getProfile(player.getUuid()).get(3, TimeUnit.SECONDS);

                        if (profile != null) {
                            profile.checkGrants();

                            player.setProfile(profile);
                            return;
                        }
                    } catch (InterruptedException | ExecutionException | TimeoutException e) {
                        e.printStackTrace();
                    }

                    player.kick(Lang.PROFILE_FAILED);
                })
                .addListener(PlayerSpawnEvent.class, event -> {
                    var player = event.getPlayer();
                    var profile = FlamePlayer.fromPlayer(player).getProfile();

                    if (event.isFirstSpawn()) {
                        NameplateHandler.addProvider(player, new NameplateProvider(TeamUtil.RANK_MAP.get(profile.getGrant().getRank()), ProviderType.RANK));
                    }
                });
    }

}
