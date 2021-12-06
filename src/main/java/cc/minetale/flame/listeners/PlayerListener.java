package cc.minetale.flame.listeners;

import cc.minetale.commonlib.profile.Profile;
import cc.minetale.flame.Lang;
import cc.minetale.flame.chat.Chat;
import cc.minetale.flame.procedure.GrantProcedure;
import cc.minetale.flame.util.FlamePlayer;
import net.minestom.server.entity.Player;
import net.minestom.server.event.EventFilter;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.player.PlayerChatEvent;
import net.minestom.server.event.player.PlayerDisconnectEvent;
import net.minestom.server.event.player.PlayerLoginEvent;
import net.minestom.server.event.trait.EntityEvent;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class PlayerListener {

    public static EventNode<EntityEvent> events() {
        return EventNode.type("player-events", EventFilter.ENTITY)
                .addListener(PlayerChatEvent.class, event -> {
                    event.setCancelled(true);

                    Chat.handleChat(FlamePlayer.fromPlayer(event.getPlayer()), event.getMessage());
                })
                .addListener(PlayerDisconnectEvent.class, event -> {
                    Player player = event.getPlayer();

                    GrantProcedure grantProcedure = GrantProcedure.getByPlayer(player.getUuid());

                    if (grantProcedure != null)
                        grantProcedure.cancel();
                })
                .addListener(PlayerLoginEvent.class, event -> {
                    var player = (FlamePlayer) event.getPlayer();
                    var uuid = player.getUuid();

                    try {
                        Profile profile = Profile.getProfile(uuid).get(5, TimeUnit.SECONDS);

                        player.setProfile(profile);
//                        player.refreshCommands(); // TODO Will it work when the profile gets loaded?
                    } catch (InterruptedException | ExecutionException | TimeoutException e) {
                        player.kick(Lang.PROFILE_FAILED);
                        e.printStackTrace();
                    }
                });
    }

}
