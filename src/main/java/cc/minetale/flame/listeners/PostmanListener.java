package cc.minetale.flame.listeners;

import cc.minetale.flame.util.FlamePlayer;
import cc.minetale.postman.payload.PayloadHandler;
import cc.minetale.postman.payload.PayloadListener;
import cc.minetale.sodium.lang.Language;
import cc.minetale.sodium.payloads.ConversationPayload;
import cc.minetale.sodium.payloads.ProfileUpdatePayloads;
import cc.minetale.sodium.profile.ProfileUtil;
import cc.minetale.sodium.profile.grant.Grant;
import cc.minetale.sodium.profile.punishment.Punishment;
import cc.minetale.sodium.util.Message;
import cc.minetale.sodium.util.TimeUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;

public class PostmanListener implements PayloadListener {

    /*
     * Profile Related Payloads
     */

    @PayloadHandler
    public void onPunishmentUpdate(ProfileUpdatePayloads.PunishmentPayload payload) {
        var player = MinecraftServer.getConnectionManager().getPlayer(payload.getPlayerUuid());

        if(player != null) {
            var profile = FlamePlayer.fromPlayer(player).getProfile();
            var punishment = payload.getPunishment();

            profile.getPunishments().remove(punishment);
            profile.getPunishments().add(punishment);

            profile.expirePunishments();

            if(payload.getAction() == ProfileUpdatePayloads.Action.ADD) {
                addPunishment(player, punishment);
            }
        }
    }

    @PayloadHandler
    public void onGrantUpdate(ProfileUpdatePayloads.GrantPayload payload) {
        var player = MinecraftServer.getConnectionManager().getPlayer(payload.getPlayerUuid());

        if(player != null) {
            var flamePlayer = FlamePlayer.fromPlayer(player);
            var profile = flamePlayer.getProfile();
            var grant = payload.getGrant();

            profile.getGrants().remove(grant);
            profile.getGrants().add(grant);

            flamePlayer.refreshPlayer();

            switch (payload.getAction()) {
                case ADD -> addGrant(player, grant);
                case REMOVE -> removeGrant(player, grant);
                case EXPIRE -> expireGrant(player, grant);
            }
        }
    }

    @PayloadHandler
    public void onConversation(ConversationPayload payload) {
        var target = MinecraftServer.getConnectionManager().getPlayer(payload.getTarget());

        if(target == null) { return; }

        var profile = ProfileUtil.getProfile(payload.getPlayer());

        if(profile == null) { return; }

        target.sendMessage(Message.parse(Language.Conversation.FROM_MSG, profile.getChatFormat(), payload.getMessage()));
    }

    public static void addGrant(Player player, Grant grant) {
        var rank = grant.getRank();

        player.sendMessage(Message.chatSeparator());
        player.sendMessage(Message.notification("Grant",
                Component.text().append(
                        Component.text("A ", NamedTextColor.GRAY),
                        Component.text(rank.getName(), rank.getColor()),
                        Component.text(" grant has been applied to you " +
                                (grant.getDuration() == Integer.MAX_VALUE ? "permanently" :
                                        "for " + TimeUtil.millisToRoundedTime(grant.getDuration())) + ".", NamedTextColor.GRAY)
                ).build()));
        player.sendMessage(Message.chatSeparator());
    }

    public static void removeGrant(Player player, Grant grant) {
        var rank = grant.getRank();

        player.sendMessage(Message.chatSeparator());
        player.sendMessage(Message.notification("Grant",
                Component.text().append(
                        Component.text("Your ", NamedTextColor.GRAY),
                        Component.text(rank.getName(), rank.getColor()),
                        Component.text(" grant has been removed.", NamedTextColor.GRAY)
                ).build()));
        player.sendMessage(Message.chatSeparator());
    }

    public static void expireGrant(Player player, Grant grant) {
        var rank = grant.getRank();

        player.sendMessage(Message.chatSeparator());
        player.sendMessage(Message.notification("Grant",
                Component.text().append(
                        Component.text("Your ", NamedTextColor.GRAY),
                        Component.text(rank.getName(), rank.getColor()),
                        Component.text(" grant has expired.", NamedTextColor.GRAY)
                ).build()));
        player.sendMessage(Message.chatSeparator());
    }

    public static void addPunishment(Player player, Punishment punishment) {
        switch (punishment.getType()) {
            case BAN -> player.kick(Component.join(JoinConfiguration.newlines(), punishment.getPunishmentMessage()));
            case MUTE -> {
                for(var component : punishment.getPunishmentMessage()) {
                    player.sendMessage(component);
                }
            }
        }
    }

}
