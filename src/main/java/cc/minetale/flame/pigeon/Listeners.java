package cc.minetale.flame.pigeon;

import cc.minetale.commonlib.pigeon.payloads.conversation.ConversationFromPayload;
import cc.minetale.commonlib.pigeon.payloads.conversation.ConversationToPayload;
import cc.minetale.commonlib.pigeon.payloads.minecraft.MessagePlayerPayload;
import cc.minetale.commonlib.pigeon.payloads.network.ServerOfflinePayload;
import cc.minetale.commonlib.pigeon.payloads.network.ServerOnlinePayload;
import cc.minetale.commonlib.pigeon.payloads.rank.RankReloadPayload;
import cc.minetale.commonlib.pigeon.payloads.rank.RankRemovePayload;
import cc.minetale.commonlib.profile.Profile;
import cc.minetale.commonlib.rank.Rank;
import cc.minetale.commonlib.util.MC;
import cc.minetale.flame.FlameAPI;
import cc.minetale.flame.Lang;
import cc.minetale.flame.util.FlameUtil;
import cc.minetale.mlib.util.ProfileUtil;
import cc.minetale.pigeon.annotations.PayloadHandler;
import cc.minetale.pigeon.annotations.PayloadListener;
import cc.minetale.pigeon.listeners.Listener;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;

import java.util.UUID;

@PayloadListener
public class Listeners implements Listener {

    @PayloadHandler
    public void onServerOnline(ServerOnlinePayload payload) {
        MinecraftServer.getConnectionManager().getOnlinePlayers().forEach(
                player -> {
                    player.sendMessage(MC.Chat.notificationMessage("Proxy",
                            MC.component(
                                    MC.component(payload.getName(), MC.CC.GOLD),
                                    MC.component( " has came ", MC.CC.GRAY),
                                    MC.component("Online", MC.CC.GREEN)
                            )
                    ));
                }
        );
    }

    @PayloadHandler
    public void onServerOffline(ServerOfflinePayload payload) {
        MinecraftServer.getConnectionManager().getOnlinePlayers().forEach(
                player -> {
                    player.sendMessage(MC.Chat.notificationMessage("Proxy",
                            MC.component(
                                    MC.component(payload.getName(), MC.CC.GOLD),
                                    MC.component(" has went ", MC.CC.GRAY),
                                    MC.component("Offline", MC.CC.RED)
                            )
                    ));
                }
        );
    }

    @PayloadHandler
    public void onReloadRank(RankReloadPayload payload) {
        Rank rank = Rank.getRank(payload.getRank(), false);

        Rank.getRanks().remove(payload.getRank());

        if (rank != null) {
            Rank.getRanks().put(rank.getUuid(), rank);

            FlameAPI.refreshPlayers(rank.getUuid());
        }
    }

    @PayloadHandler
    public void onRemoveRank(RankRemovePayload payload) {
        UUID rank = payload.getRank();

        Rank.getRanks().remove(rank);

        FlameAPI.refreshPlayers(rank);
    }

    @PayloadHandler
    public void onConversationTo(ConversationToPayload payload) {
        var initiator = MinecraftServer.getConnectionManager().getPlayer(payload.getInitiator());
        if(initiator == null || !initiator.isOnline()) { return; }

        Profile.getProfile(payload.getTarget()).thenAccept(targetProfile -> {
            if(targetProfile == null) { return; }
            initiator.sendMessage(Lang.TO_MSG(targetProfile, payload.getMessage()));
        });
    }

    @PayloadHandler
    public void onConversationFrom(ConversationFromPayload payload) {
        var target = MinecraftServer.getConnectionManager().getPlayer(payload.getTarget());
        if(target == null || !target.isOnline()) { return; }

        Profile.getProfile(payload.getInitiator())
                .thenAccept(initiatorProfile -> {
                    if(initiatorProfile == null) { return; }
                    target.sendMessage(Lang.FROM_MSG(initiatorProfile, payload.getMessage()));

                    ProfileUtil.getAssociatedProfile(target).thenAccept(targetProfile -> {
                        if(targetProfile.getOptionsProfile().isReceivingMessageSounds())
                            FlameUtil.playMessageSound(target);
                    });
                });
    }

    @PayloadHandler
    public void onMessagePlayer(MessagePlayerPayload payload) {
        Player player = MinecraftServer.getConnectionManager().getPlayer(payload.getPlayer());

        if(player != null)
            player.sendMessage(payload.getMessage());
    }

}
