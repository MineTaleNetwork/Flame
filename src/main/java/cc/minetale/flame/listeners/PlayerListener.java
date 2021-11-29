package cc.minetale.flame.listeners;

import cc.minetale.commonlib.profile.Profile;
import cc.minetale.commonlib.api.Rank;
import cc.minetale.flame.Flame;
import cc.minetale.flame.Lang;
import cc.minetale.flame.chat.Chat;
import cc.minetale.flame.procedure.GrantProcedure;
import cc.minetale.flame.util.FlamePlayer;
import cc.minetale.mlib.util.ProfileUtil;
import net.minestom.server.entity.Player;
import net.minestom.server.event.EventFilter;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.player.*;
import net.minestom.server.event.trait.EntityEvent;
import net.minestom.server.scoreboard.Team;

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
                    Team team = player.getTeam();

                    if(team != null)
                        team.removeMember(player.getUsername());

                    GrantProcedure grantProcedure = GrantProcedure.getByPlayer(player.getUuid());

                    if (grantProcedure != null)
                        grantProcedure.cancel();

                    ProfileUtil.dissociateProfile(player);
                })
                .addListener(PlayerPluginMessageEvent.class, event -> {
                    // TODO
                })
                .addListener(PlayerLoginEvent.class, event -> {
                    var player = (FlamePlayer) event.getPlayer();
                    var uuid = player.getUuid();

                    try {
                        Profile profile = ProfileUtil.getProfileById(uuid).get(5, TimeUnit.SECONDS);

                        player.refreshCommands();

                        player.setProfile(profile);
                    } catch (InterruptedException | ExecutionException | TimeoutException e) {
                        player.kick(Lang.PROFILE_FAILED);
                        e.printStackTrace();
                    }
                });
    }

}
