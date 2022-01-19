package cc.minetale.flame.listeners;

import cc.minetale.commonlib.lang.Language;
import cc.minetale.commonlib.util.ProfileUtil;
import cc.minetale.flame.chat.Chat;
import cc.minetale.flame.procedure.GrantProcedure;
import cc.minetale.flame.util.FlamePlayer;
import cc.minetale.mlib.nametag.NameplateHandler;
import cc.minetale.mlib.nametag.NameplateProvider;
import cc.minetale.mlib.nametag.ProviderType;
import cc.minetale.mlib.util.TeamUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
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
                .addListener(PlayerCommandEvent.class, event -> {
                    var player = FlamePlayer.fromPlayer(event.getPlayer());
                    var cooldown = player.getCooldown();

                    if(cooldown.hasCooldown()) {
                        player.sendMessage(Component.text("You are on cooldown for another " + cooldown.getSecondsRemaining() + " seconds.", NamedTextColor.RED));
                        event.setCancelled(true);
                    } else {
                        cooldown.refresh();
                    }
                })
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
                        var profile = ProfileUtil.getProfile(player.getUuid()).get(3, TimeUnit.SECONDS);

                        if (profile != null) {
                            profile.checkGrants();
                            player.setProfile(profile);
                            return;
                        }
                    } catch (InterruptedException | ExecutionException | TimeoutException e) {
                        e.printStackTrace();
                    }

                    player.kick(Language.Error.PROFILE_LOAD_ERROR);
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
