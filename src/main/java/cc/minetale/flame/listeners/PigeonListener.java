package cc.minetale.flame.listeners;

import cc.minetale.commonlib.grant.Grant;
import cc.minetale.commonlib.lang.Language;
import cc.minetale.commonlib.pigeon.payloads.friend.*;
import cc.minetale.commonlib.pigeon.payloads.grant.GrantAddPayload;
import cc.minetale.commonlib.pigeon.payloads.grant.GrantExpirePayload;
import cc.minetale.commonlib.pigeon.payloads.grant.GrantRemovePayload;
import cc.minetale.commonlib.pigeon.payloads.party.PartyChatPayload;
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
     * Party Related Payloads
     */

    // TODO -> Don't send payloads if the party players are on the same server

    @PayloadHandler
    public void onPartyChat(PartyChatPayload payload) {
        var profile = payload.getProfile();
        var party = payload.getParty();
        var members = party.getAllMembers();

        for(var player : MinecraftServer.getConnectionManager().getOnlinePlayers()) {
            if(members.contains(player.getUuid())) {
                player.sendMessage(profile.getChatFormat());
            }
        }
    }

//    @PayloadHandler
//    public void onPartyDisband(PartyDisbandPayload payload) {
//        UUID initiatorUUID = payload.getInitiator();
//        Player initiator = Player.getPlayerByUuid(initiatorUUID);
//
//        if (initiator == null) {
//            Player.sendMessage(initiatorUUID,
//                    Component.text("An error has occurred, please try rejoining the network.", MC.CC.RED.getTextColor()));
//            return;
//        }
//
//        Party party = Party.getPartyByMember(initiatorUUID);
//
//        if(party == null) {
//            Player.sendNotification(initiatorUUID, "Party",
//                    Component.text("You are not in a party. Consider creating one.", MC.CC.RED.getTextColor()));
//            return;
//        } else {
//            if(!party.getLeader().equals(initiator.getUuid())) {
//                Player.sendNotification(initiatorUUID, "Party",
//                        Component.text("You are not the leader of the party.", MC.CC.RED.getTextColor()));
//                return;
//            }
//        }
//
//        party.disbandParty("The party has been disbanded by the leader.");
//    }
//
//    @PayloadHandler
//    public void onPartyInvite(PartyInvitePayload payload) {
//        UUID initiatorUUID = payload.getInitiator();
//        UUID targetUUID = payload.getTarget();
//        Player initiator = Player.getPlayerByUuid(initiatorUUID);
//        Player target = Player.getPlayerByUuid(targetUUID);
//
//        if (initiator == null) {
//            Player.sendMessage(initiatorUUID,
//                    Component.text("An error has occurred, please try rejoining the network.", MC.CC.RED.getTextColor()));
//            return;
//        }
//
//        if (target == null) {
//            Player.sendNotification(initiatorUUID, "Party",
//                    Component.text("That player is currently not online.", MC.CC.RED.getTextColor()));
//            return;
//        }
//
//        Party party = Party.getPartyByMember(initiatorUUID);
//
//        if(party == null) {
//            Player.sendNotification(initiatorUUID, "Party",
//                    Component.text("You've created a new party!", MC.CC.GRAY.getTextColor()));
//            party = new Party(initiatorUUID);
//        }
//
//        if (!party.getLeader().equals(initiator.getUuid())) {
//            Player.sendNotification(initiatorUUID, "Party",
//                    Component.text("You must be the leader to invite players.", MC.CC.RED.getTextColor()));
//            return;
//        }
//
//        if(party.getMembers().contains(targetUUID)) {
//            Player.sendNotification(initiatorUUID, "Party",
//                    Component.text("That player is already in the party.", MC.CC.RED.getTextColor()));
//            return;
//        }
//
//        if(target.getPartyInvite(party.getUuid()) != null) {
//            Player.sendNotification(initiatorUUID, "Party",
//                    Component.text("That player already has a pending invite.", MC.CC.RED.getTextColor()));
//            return;
//        }
//
//        target.addPartyInvite(party.getUuid(), initiatorUUID);
//    }
//
//    @PayloadHandler
//    public void onPartyJoin(PartyJoinPayload payload) {
//        UUID initiatorUUID = payload.getInitiator();
//        UUID targetUUID = payload.getTarget();
//        Player initiator = Player.getPlayerByUuid(initiatorUUID);
//
//        if (initiator == null) {
//            Player.sendMessage(initiatorUUID,
//                    Component.text("An error has occurred, please try rejoining the network.", MC.CC.RED.getTextColor()));
//            return;
//        }
//
//        if(initiator.isInParty()) {
//            Player.sendNotification(initiatorUUID, "Party",
//                    Component.text("You are already in a party. Try leaving it first.", MC.CC.RED.getTextColor()));
//            return;
//        }
//
//        PartyInvite invite = initiator.getPlayerPartyInvite(targetUUID);
//
//        if(invite != null) {
//            Party party = Party.getPartyByUUID(invite.getPartyUUID());
//
//            if(party != null) {
//                party.addMember(initiatorUUID);
//            } else {
//                Player.sendNotification(initiatorUUID, "Party",
//                        Component.text("The party you attempted to join has already been disbanded.", MC.CC.RED.getTextColor()));
//            }
//
//            invite.getTimer().stop();
//
//            initiator.getPartyInvites().remove(invite.getInviterUUID());
//        } else {
//            Player.sendNotification(initiatorUUID, "Party",
//                    Component.text("That player hasn't invited you to join their party.", MC.CC.RED.getTextColor()));
//        }
//    }
//
//    @PayloadHandler
//    public void onPartyKick(PartyKickPayload payload) {
//        UUID initiatorUUID = payload.getInitiator();
//        UUID targetUUID = payload.getTarget();
//        Player initiator = Player.getPlayerByUuid(initiatorUUID);
//
//        if (initiator == null) {
//            Player.sendMessage(initiatorUUID,
//                    Component.text("An error has occurred, please try rejoining the network.", MC.CC.RED.getTextColor()));
//            return;
//        }
//
//        Party party = Party.getPartyByMember(initiatorUUID);
//
//        if (party != null) {
//            if(!party.getLeader().equals(initiatorUUID)) {
//                Player.sendMessage(initiatorUUID,
//                        Component.text("Only the leader can kick party members.", MC.CC.RED.getTextColor()));
//                return;
//            }
//
//            if(targetUUID.equals(initiatorUUID)) {
//                Player.sendMessage(initiatorUUID,
//                        Component.text("You cannot kick yourself from the party.", MC.CC.RED.getTextColor()));
//                return;
//            }
//
//            party.removeMember(targetUUID);
//
//            Atom.getAtom().getProfilesManager()
//                    .getProfile(targetUUID)
//                    .thenAccept(profile -> {
//                        if(profile == null) { return; }
//                        party.sendPartyMessage(Component.text()
//                                .append(profile.api().getChatFormat())
//                                .append(Component.text(" has been kicked from the party.", MC.CC.RED.getTextColor()))
//                                .build());
//                    });
//
//            Player target = Player.getPlayerByUuid(targetUUID);
//
//            if(target != null) {
//                Player.sendNotification(targetUUID, "Party",
//                        Component.text("You were kicked from the party.", MC.CC.RED.getTextColor()));
//            }
//        } else {
//            Player.sendNotification(initiatorUUID, "Party",
//                    Component.text("You are not in a party. Consider creating one.", MC.CC.RED.getTextColor()));
//        }
//    }
//
//    @PayloadHandler
//    public void onPartyLeave(PartyLeavePayload payload) {
//        UUID initiatorUUID = payload.getInitiator();
//        Player initiator = Player.getPlayerByUuid(initiatorUUID);
//
//        if (initiator == null) {
//            Player.sendMessage(initiatorUUID,
//                    Component.text("An error has occurred, please try rejoining the network.", MC.CC.RED.getTextColor()));
//            return;
//        }
//
//        Party party = Party.getPartyByMember(initiatorUUID);
//
//        if(party != null) {
//            if(party.getLeader().equals(initiatorUUID)) {
//                Player.sendNotification(initiatorUUID, "Party",
//                        Component.text("You cannot leave your own party. Try disbanding it or promoting someone.", MC.CC.RED.getTextColor()));
//                return;
//            }
//
//            party.removeMember(initiatorUUID);
//
//            Atom.getAtom().getProfilesManager()
//                    .getProfile(initiatorUUID)
//                    .thenAccept(profile -> {
//                        if(profile == null) { return; }
//                        party.sendPartyMessage(Component.text()
//                                .append(profile.api().getChatFormat())
//                                .append(Component.text(" has left the party.", MC.CC.RED.getTextColor()))
//                                .build());
//                    });
//
//
//            Player.sendNotification(initiatorUUID, "Party",
//                    Component.text("You left the party.", MC.CC.RED.getTextColor()));
//        } else {
//            Player.sendNotification(initiatorUUID, "Party",
//                    Component.text("You are not in a party. Consider creating one.", MC.CC.RED.getTextColor()));
//        }
//
//    }
//
////    TODO: WIP
////    @PayloadHandler
////    public void onPartySummon(PartySummonPayload payload) { }
//
//    @PayloadHandler
//    public void onPartyTransfer(PartyTransferPayload payload) {
//        UUID initiatorUUID = payload.getInitiator();
//        UUID targetUUID = payload.getTarget();
//        Player initiator = Player.getPlayerByUuid(initiatorUUID);
//
//        if (initiator == null) {
//            Player.sendMessage(initiatorUUID,
//                    Component.text("An error has occurred, please try rejoining the network.", MC.CC.RED.getTextColor()));
//            return;
//        }
//
//        Party party = Party.getPartyByMember(initiatorUUID);
//
//        if(party != null) {
//
//            if(party.getLeader().equals(initiatorUUID)) {
//                Atom.getAtom().getProfilesManager()
//                        .getProfile(targetUUID)
//                        .thenAccept(profile -> {
//                            if(profile == null) { return; }
//                            party.sendPartyMessage(Component.text()
//                                    .append(Component.text("The party has been transferred to ", MC.CC.RED.getTextColor()))
//                                    .append(profile.api().getChatFormat())
//                                    .build());
//                        });
//
//                party.setLeader(targetUUID);
//            } else {
//                Player.sendNotification(initiatorUUID, "Party",
//                        Component.text("You are not the leader of the party.", MC.CC.RED.getTextColor()));
//            }
//        } else {
//            Player.sendNotification(initiatorUUID, "Party",
//                    Component.text("You are not in a party. Consider creating one.", MC.CC.RED.getTextColor()));
//        }
//    }


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
