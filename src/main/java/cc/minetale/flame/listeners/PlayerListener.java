package cc.minetale.flame.listeners;

import cc.minetale.commonlib.profile.Profile;
import cc.minetale.commonlib.punishment.Punishment;
import cc.minetale.commonlib.rank.Rank;
import cc.minetale.flame.Flame;
import cc.minetale.flame.Lang;
import cc.minetale.flame.chat.Chat;
import cc.minetale.flame.procedure.GrantProcedure;
import cc.minetale.flame.util.FlamePlayer;
import cc.minetale.flame.util.FlameUtil;
import cc.minetale.mlib.util.ProfileUtil;
import com.google.common.hash.Hashing;
import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;
import net.minestom.server.event.EventFilter;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.player.*;
import net.minestom.server.event.trait.EntityEvent;
import net.minestom.server.scoreboard.Team;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class PlayerListener {

    public static EventNode<EntityEvent> events() {
        return EventNode.type("player-events", EventFilter.ENTITY)
                .addListener(PlayerChatEvent.class, event -> {
                    event.setCancelled(true);

                    Chat.handleChat((FlamePlayer) event.getPlayer(), event.getMessage());
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
                .addListener(PlayerSpawnEvent.class, event -> {
                    var player = event.getPlayer();

                    if (event.isFirstSpawn())
                        ProfileUtil.getAssociatedProfile(player).thenAccept(profile -> {
                            Rank rank = profile.getGrant().api().getRank();

                            Team team = Flame.getFlame().getRankTeams().get(rank.getUuid());

                            team.addMember(player.getUsername());
                            player.setTeam(team);
                        });
                })
                .addListener(PlayerLoginEvent.class, event -> {
                    var player = (FlamePlayer) event.getPlayer();
                    var name = player.getUsername();
                    var uuid = player.getUuid();

                    try {
                        Profile profile = ProfileUtil.getProfileByBoth(name, uuid).get(10, TimeUnit.SECONDS);

                        player.refreshCommands();

                        profile.setName(name);

                        profile.setSearchableName(name);

                        if (profile.getFirstSeen() == 0L)
                            profile.setFirstSeen(System.currentTimeMillis());

                        Punishment punishment = profile.api().getActiveBan();

                        if (punishment != null) {
                            player.kick(Component.join(Component.newline(), FlameUtil.getPunishmentMessage(punishment)));
                            return;
                        }

                        profile.setLastSeen(System.currentTimeMillis());

                        String socketAddress = player.getPlayerConnection().getRemoteAddress().toString();

                        String hashedIP = Hashing.sha256().hashString(socketAddress.substring(1, socketAddress.indexOf(':')), StandardCharsets.UTF_8).toString();

                        if (profile.getCurrentAddress() == null)
                            profile.setCurrentAddress(hashedIP);

                        Profile.Staff staff = profile.getStaffProfile();

                        if (!profile.getCurrentAddress().equals(hashedIP)) {
                            if (staff.isTwoFactor() && !staff.isLocked())
                                staff.setLocked(true);

                            profile.setCurrentAddress(hashedIP);
                        }

                        profile.update();

                        player.setProfile(profile);
                        player.updatePermission();
                    } catch (InterruptedException | ExecutionException | TimeoutException e) {
                        player.kick(Lang.PROFILE_FAILED);
                        e.printStackTrace();
                    }
                });
    }

}
