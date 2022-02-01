package cc.minetale.flame.listeners;

import cc.minetale.commonlib.grant.Grant;
import cc.minetale.commonlib.lang.Language;
import cc.minetale.commonlib.party.PartyMember;
import cc.minetale.commonlib.pigeon.payloads.friend.*;
import cc.minetale.commonlib.pigeon.payloads.grant.GrantAddPayload;
import cc.minetale.commonlib.pigeon.payloads.grant.GrantExpirePayload;
import cc.minetale.commonlib.pigeon.payloads.grant.GrantRemovePayload;
import cc.minetale.commonlib.pigeon.payloads.network.ProxyPlayerConnectPayload;
import cc.minetale.commonlib.pigeon.payloads.network.ProxyPlayerDisconnectPayload;
import cc.minetale.commonlib.pigeon.payloads.network.ProxyPlayerSwitchPayload;
import cc.minetale.commonlib.pigeon.payloads.party.*;
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
import net.kyori.adventure.text.Component;
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
     * Party Related Payloads
     */

    @PayloadHandler
    public void onPartyChat(PartyChatPayload payload) {
        var profile = payload.getProfile();
        var party = payload.getParty();
        var message = Message.parse(Language.Party.PARTY_CHAT_FORMAT, profile.getColoredPrefix(), profile.getColoredName(), "", payload.getMessage());

        for (var member : party.getMembers()) {
            var player = MinecraftServer.getConnectionManager().getPlayer(member.player());

            if(player != null) {
                player.sendMessage(message);
            }
        }
    }

    @PayloadHandler
    public void onPartyDisband(PartyDisbandPayload payload) {
        var party = payload.getParty();
        var message = Message.parse(Language.Party.PARTY_DISBANDED);

        for (var member : party.getMembers()) {
            var player = MinecraftServer.getConnectionManager().getPlayer(member.player());

            if(player != null) {
                player.sendMessage(message);
            }
        }
    }

    @PayloadHandler
    public void onPartyInvite(PartyRequestCreatePayload payload) {
        var initiatorProfile = payload.getInitiator();
        var targetProfile = payload.getTarget();

        var playerMessage = Message.parse(Language.Party.Invite.SUCCESS_PLAYER, targetProfile.getChatFormat());
        var targetMessage = Message.parse(Language.Party.Invite.SUCCESS_TARGET, initiatorProfile.getChatFormat());

        for(var member : payload.getParty().getMembers()) {
            var player = MinecraftServer.getConnectionManager().getPlayer(member.player());

            if(player == null) { continue; }

            player.sendMessage(playerMessage);
        }

        var target = MinecraftServer.getConnectionManager().getPlayer(targetProfile.getUuid());

        if(target == null) { return; }

        target.sendMessage(targetMessage);
    }

    @PayloadHandler
    public void onPartyJoin(PartyJoinPayload payload) {
        var party = payload.getParty();
        var profile = payload.getPlayer();
        var message = Message.parse(Language.Party.PARTY_JOIN, profile.getChatFormat());

        for (var member : party.getMembers()) {
            var player = MinecraftServer.getConnectionManager().getPlayer(member.player());

            if(player != null) {
                player.sendMessage(message);
            }
        }
    }

    @PayloadHandler
    public void onPartyKick(PartyKickPayload payload) {
        var party = payload.getParty();
        var profile = payload.getPlayer();
        var message = Message.parse(Language.Party.PARTY_KICK, profile.getChatFormat());

        for (var member : party.getMembers()) {
            var player = MinecraftServer.getConnectionManager().getPlayer(member.player());

            if(player != null) {
                player.sendMessage(message);
            }
        }
    }

    @PayloadHandler
    public void onPartyLeave(PartyLeavePayload payload) {
        var party = payload.getParty();
        var profile = payload.getPlayer();
        var message = Message.parse(Language.Party.PARTY_LEAVE, profile.getChatFormat());

        for (var member : party.getMembers()) {
            var player = MinecraftServer.getConnectionManager().getPlayer(member.player());

            if(player != null) {
                player.sendMessage(message);
            }
        }
    }

    @PayloadHandler
    public void onPartySummon(PartySummonPayload payload) {
//        var party = payload.getParty();
//        var profile = payload.getPlayer();
//        var members = party.getAllMembers();
//
//        for (var player : MinecraftServer.getConnectionManager().getOnlinePlayers()) {
//            if (members.contains(player.getUuid())) {
//                player.sendMessage(""); // TODO -> Party summon message
//            }
//        }
    }

    @PayloadHandler
    public void onPartyRoleChange(PartyRoleChangePayload payload) {
        var party = payload.getParty();
        var profile = payload.getPlayer();
        var newRole = payload.getNewRole();
        var oldRole = payload.getOldRole();

        Component message = Component.empty();

        if(oldRole != PartyMember.Role.LEADER && oldRole.ordinal() < newRole.ordinal()) {
            message = Message.parse(Language.Party.PARTY_DEMOTE, profile.getChatFormat(), newRole.getReadable());
        } else if(oldRole.ordinal() > newRole.ordinal()) {
            message = Message.parse(Language.Party.PARTY_PROMOTE, profile.getChatFormat(), newRole.getReadable());
        }

        for (var member : party.getMembers()) {
            var player = MinecraftServer.getConnectionManager().getPlayer(member.player());

            if(player != null) {
                player.sendMessage(message);
            }
        }
    }

    /*
     * Friend Related Payloads
     */

    @PayloadHandler
    public void onFriendRequestDeny(FriendRequestDenyPayload payload) {
        var profile = payload.getInitiator();
        var player = MinecraftServer.getConnectionManager().getPlayer(payload.getTarget());

        if (player != null) {
            player.sendMessage(Message.parse(Language.Friend.Deny.SUCCESS_TARGET, profile.getChatFormat()));
        }
    }

    @PayloadHandler
    public void onFriendRemove(FriendRemovePayload payload) {
        var profile = payload.getInitiator();
        var player = MinecraftServer.getConnectionManager().getPlayer(payload.getTarget());

        if (player != null) {
            player.sendMessage(Message.parse(Language.Friend.Remove.SUCCESS_TARGET, profile.getChatFormat()));
        }
    }

    @PayloadHandler
    public void onFriendRequestAccept(FriendRequestAcceptPayload payload) {
        var profile = payload.getInitiator();
        var player = MinecraftServer.getConnectionManager().getPlayer(payload.getTarget());

        if (player != null) {
            player.sendMessage(Message.parse(Language.Friend.Accept.SUCCESS, profile.getChatFormat()));
        }
    }

    @PayloadHandler
    public void onFriendRequestCreate(FriendRequestCreatePayload payload) {
        var profile = payload.getInitiator();
        var player = MinecraftServer.getConnectionManager().getPlayer(payload.getTarget());

        if (player != null) {
            player.sendMessage(Message.parse(Language.Friend.Add.SUCCESS_TARGET, profile.getChatFormat()));
        }
    }

    /*
     * Proxy Related Payloads
     */

    @PayloadHandler
    public void onProxyPlayerConnect(ProxyPlayerConnectPayload payload) {
        var profile = payload.getPlayer();
        var friendJoined = Message.parse(Language.Friend.General.FRIEND_JOINED, profile.getChatFormat());

        for (var player : MinecraftServer.getConnectionManager().getOnlinePlayers()) {
            var playerProfile = FlamePlayer.fromPlayer(player).getProfile();

            if(profile.getRank().isStaff() && playerProfile.getGrant().getRank().isStaff() && playerProfile.getStaffProfile().isReceivingStaffMessages()) {
                // TODO -> Send staff connect message
            } else if (playerProfile.getFriends().contains(profile.getUuid())) {
                player.sendMessage(friendJoined);
            }

        }
    }

    @PayloadHandler
    public void onProxyPlayerDisconnect(ProxyPlayerDisconnectPayload payload) {
        var profile = payload.getPlayer();
        var friendLeft = Message.parse(Language.Friend.General.FRIEND_LEFT, profile.getChatFormat());

        for (var player : MinecraftServer.getConnectionManager().getOnlinePlayers()) {
            var playerProfile = FlamePlayer.fromPlayer(player).getProfile();

            if(profile.getRank().isStaff() && playerProfile.getGrant().getRank().isStaff() && playerProfile.getStaffProfile().isReceivingStaffMessages()) {
                // TODO -> Send staff left message
            } else if (playerProfile.getFriends().contains(profile.getUuid())) {
                player.sendMessage(friendLeft);
            }
        }
    }

    @PayloadHandler
    public void onProxyPlayerSwitch(ProxyPlayerSwitchPayload payload) {
        var profile = payload.getPlayer();

        if(profile.getRank().isStaff()) {
            for (var player : MinecraftServer.getConnectionManager().getOnlinePlayers()) {
                var playerProfile = FlamePlayer.fromPlayer(player).getProfile();

                if(playerProfile.getGrant().getRank().isStaff() && playerProfile.getStaffProfile().isReceivingStaffMessages()) {
                    // TODO -> Send staff switch message
                }
            }
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
