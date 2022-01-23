package cc.minetale.flame.listeners;

import cc.minetale.commonlib.cache.ProfileCache;
import cc.minetale.commonlib.grant.Grant;
import cc.minetale.commonlib.lang.Language;
import cc.minetale.commonlib.pigeon.payloads.friend.*;
import cc.minetale.commonlib.pigeon.payloads.grant.GrantAddPayload;
import cc.minetale.commonlib.pigeon.payloads.grant.GrantExpirePayload;
import cc.minetale.commonlib.pigeon.payloads.grant.GrantRemovePayload;
import cc.minetale.commonlib.pigeon.payloads.profile.ProfileUpdatePayload;
import cc.minetale.commonlib.pigeon.payloads.punishment.PunishmentAddPayload;
import cc.minetale.commonlib.pigeon.payloads.punishment.PunishmentExpirePayload;
import cc.minetale.commonlib.pigeon.payloads.punishment.PunishmentRemovePayload;
import cc.minetale.commonlib.util.Message;
import cc.minetale.commonlib.util.ProfileUtil;
import cc.minetale.flame.util.FlamePlayer;
import cc.minetale.flame.util.FlameProvider;
import cc.minetale.pigeon.annotations.PayloadHandler;
import cc.minetale.pigeon.annotations.PayloadListener;
import cc.minetale.pigeon.listeners.Listener;
import net.minestom.server.MinecraftServer;

@PayloadListener
public class PigeonListener implements Listener {

    /*
     * Profile Related Payloads
     */

    @PayloadHandler
    public void onProfileUpdate(ProfileUpdatePayload payload) {
        var playerUuid = payload.getPlayer();
        var player = MinecraftServer.getConnectionManager().getPlayer(playerUuid);

        if (player != null) {
            ProfileUtil.getProfile(playerUuid)
                    .thenAccept(profile -> {
                        if (profile != null) {
                            var flamePlayer = FlamePlayer.fromPlayer(player);

                            flamePlayer.setProfile(profile);
                        }
                    });
        }
    }

    /*
     * Friend Related Payloads
     */

    @PayloadHandler
    public void onFriendJoined(FriendJoinedPayload payload) {
        System.out.println("Received Payload");
        ProfileUtil.getCachedProfile(payload.getPlayer())
                .thenAccept(cachedProfile -> {
            var profile = cachedProfile.getProfile();

            for(var player : MinecraftServer.getConnectionManager().getOnlinePlayers()) {
                var playerProfile = FlamePlayer.fromPlayer(player).getProfile();

                if(playerProfile.getFriends().contains(profile.getUuid())) {
                    player.sendMessage(Message.parse(Language.Friend.General.FRIEND_JOINED, profile.getChatFormat()));
                }
            }
        });
    }

    @PayloadHandler
    public void onFriendLeft(FriendLeftPayload payload) {
        ProfileUtil.getCachedProfile(payload.getPlayer())
                .thenAccept(cachedProfile -> {
            var profile = cachedProfile.getProfile();

            for(var player : MinecraftServer.getConnectionManager().getOnlinePlayers()) {
                var playerProfile = FlamePlayer.fromPlayer(player).getProfile();

                if(playerProfile.getFriends().contains(profile.getUuid())) {
                    player.sendMessage(Message.parse(Language.Friend.General.FRIEND_LEFT, profile.getChatFormat()));
                }
            }
        });
    }

    @PayloadHandler
    public void onFriendRequestDeny(FriendRequestDenyPayload payload) {
        var player = MinecraftServer.getConnectionManager().getPlayer(payload.getTarget());

        if (player != null) {
            ProfileUtil.getProfile(payload.getInitiator())
                    .thenAccept(profile -> {
                        if (profile != null) {
                            player.sendMessage(Message.parse(Language.Friend.Deny.SUCCESS_TARGET, profile.getChatFormat()));
                        }
                    });

        }
    }

    @PayloadHandler
    public void onFriendRemove(FriendRemovePayload payload) {
        var player = MinecraftServer.getConnectionManager().getPlayer(payload.getTarget());

        if (player != null) {
            ProfileUtil.getProfile(payload.getInitiator())
                    .thenAccept(profile -> {
                        if (profile != null) {
                            player.sendMessage(Message.parse(Language.Friend.Remove.SUCCESS_TARGET, profile.getChatFormat()));
                        }
                    });

        }
    }

    @PayloadHandler
    public void onFriendRequestAccept(FriendRequestAcceptPayload payload) {
        var player = MinecraftServer.getConnectionManager().getPlayer(payload.getTarget());

        if (player != null) {
            ProfileUtil.getProfile(payload.getInitiator())
                    .thenAccept(profile -> {
                        if (profile != null) {
                            player.sendMessage(Message.parse(Language.Friend.Accept.SUCCESS, profile.getChatFormat()));
                        }
                    });

        }
    }

    @PayloadHandler
    public void onFriendRequestCreate(FriendRequestCreatePayload payload) {
        var player = MinecraftServer.getConnectionManager().getPlayer(payload.getTarget());

        if (player != null) {
            ProfileUtil.getProfile(payload.getInitiator())
                    .thenAccept(profile -> {
                        if (profile != null) {
                            player.sendMessage(Message.parse(Language.Friend.Add.SUCCESS_TARGET, profile.getChatFormat()));
                        }
                    });

        }
    }

    /*
     * Grant Related Payloads
     */

    @PayloadHandler
    public void onGrantAdd(GrantAddPayload payload) {
        var player = MinecraftServer.getConnectionManager().getPlayer(payload.getPlayer());

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
        var player = MinecraftServer.getConnectionManager().getPlayer(payload.getPlayer());

        if (player != null) {
            Grant.getGrant(payload.getGrant())
                    .thenAccept(grant -> {
                        if (grant != null) {
                            FlameProvider.removeGrant(player, grant);
                        }
                    });
        }
    }

    @PayloadHandler
    public void onGrantExpire(GrantExpirePayload payload) {
        var player = MinecraftServer.getConnectionManager().getPlayer(payload.getPlayer());

        if (player != null) {
            Grant.getGrant(payload.getGrant())
                    .thenAccept(grant -> {
                        if (grant != null) {
                            FlameProvider.expireGrant(player, grant);
                        }
                    });
        }
    }

    /*
     * Punishment Related Payloads
     */

    @PayloadHandler
    public void onPunishmentAdd(PunishmentAddPayload payload) {

    }

    @PayloadHandler
    public void onPunishmentRemove(PunishmentRemovePayload payload) {

    }

    @PayloadHandler
    public void onPunishmentExpire(PunishmentExpirePayload payload) {

    }

}
