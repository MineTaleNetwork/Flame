package cc.minetale.flame.listeners;

import cc.minetale.flame.chat.Chat;
import cc.minetale.flame.util.FlameUtil;
import cc.minetale.mlib.util.ProfileTagSerializer;
import cc.minetale.mlib.util.ProfileUtil;
import cc.minetale.commonlib.modules.profile.Profile;
import cc.minetale.commonlib.modules.punishment.Punishment;
import cc.minetale.commonlib.util.MC;
import cc.minetale.flame.team.FlameTeam;
import com.google.common.hash.Hashing;
import net.kyori.adventure.text.Component;
import net.minestom.server.event.EventFilter;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.player.*;
import net.minestom.server.event.trait.EntityEvent;
import net.minestom.server.tag.Tag;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class PlayerListener {

    public static EventNode<EntityEvent> events() {
        return EventNode.type("player-events", EventFilter.ENTITY)
                .addListener(PlayerChatEvent.class, event -> {
                    event.setCancelled(true);

                    Chat.handleChat(event.getPlayer(), event.getMessage());
                })
                .addListener(PlayerDisconnectEvent.class, event -> FlameTeam.getTeams().get(event.getPlayer().getUuid()).deleteTeam())
                .addListener(PlayerSpawnEvent.class, event -> {
                    var player = event.getPlayer();

                    ProfileUtil.getAssociatedProfile(player).thenAccept(profile -> {
                        new FlameTeam(profile, player);

                        for(FlameTeam team : FlameTeam.getTeams().values()) {
                            team.createTeam(player);
                        }

                        FlameUtil.setupPlayer(profile);

//                        if (Util.isElevatedStaff(player.getUniqueId()))
//                            player.setOp(true);

//                        Tab.getTeamMap().values().forEach(scoreboardTeam -> {
//                            try {
//                                NMSUtil.setValue(scoreboardTeam, "i", 0);
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//
//                            NMSUtil.sendPacket(player, scoreboardTeam);
//                        });
//
//                        Tab.createTeam(player);
                    });
                })
                .addListener(PlayerLoginEvent.class, event -> {
                    var player = event.getPlayer();
                    var name = player.getUsername();
                    var uuid = player.getUuid();

                    try {
                        Profile profile = ProfileUtil.getProfileByBoth(name, uuid).get(10, TimeUnit.SECONDS);

                        profile.setName(name);
                        profile.setSearchableName(name);

                        if(profile.getFirstSeen() == null)
                            profile.setFirstSeen(System.currentTimeMillis());

                        Punishment punishment = profile.api().getActiveBan();

                        if(punishment != null) {
                            player.kick(Component.join(Component.newline(), FlameUtil.getPunishmentMessage(punishment)));
                            return;
                        }

                        profile.setLastSeen(System.currentTimeMillis());

                        String socketAddress = player.getPlayerConnection().getRemoteAddress().toString();

                        String hashedIP = Hashing.sha256().hashString(socketAddress.substring(1, socketAddress.indexOf(':')), StandardCharsets.UTF_8).toString();

                        if(profile.getCurrentAddress() == null)
                            profile.setCurrentAddress(hashedIP);

                        Profile.Staff staff = profile.getStaffProfile();

                        if(!profile.getCurrentAddress().equals(hashedIP)) {
                            if(staff.isTwoFactor() && !staff.isLocked())
                                staff.setLocked(true);

                            profile.setCurrentAddress(hashedIP);
                        }

                        profile.update();

                        player.setTag(Tag.Structure("profile", new ProfileTagSerializer()), profile);
//                        if(server.hasWhitelist()) {
//                            if(server.getWhitelistedPlayers().stream().map(OfflinePlayer::getUniqueId).collect(Collectors.toList()).contains(uuid) || isElevated) {
//                                event.allow();
//                            } else {
//                                event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_WHITELIST,
//                                        Component.text("The server is currently whitelisted.", MC.CC.RED.getTextColor()));
//                                return;
//                            }
//                        }

//                        if(Bukkit.getOnlinePlayers().size() >= Bukkit.getMaxPlayers()) {
//                            if(profile.api().getAllPermissions().contains("flame.staff") || isElevated) {
//                                event.allow();
//                            } else {
//                                event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_FULL,
//                                        Component.text("The server is currently full.", MC.CC.RED.getTextColor()));
//                            }
//                        }
                    } catch (InterruptedException | ExecutionException | TimeoutException e) {
                        Thread.currentThread().interrupt();
                        player.kick(Component.text("Failed to load your profile. Try again later.", MC.CC.RED.getTextColor()));
                        e.printStackTrace();
                    }
                });
    }

}
