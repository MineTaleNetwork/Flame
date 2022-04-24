package cc.minetale.flame.listeners;

import cc.minetale.flame.chat.Chat;
import cc.minetale.flame.procedure.GrantProcedure;
import cc.minetale.flame.util.FlamePlayer;
import cc.minetale.mlib.nametag.NameplateHandler;
import cc.minetale.mlib.nametag.NameplateProvider;
import cc.minetale.mlib.nametag.ProviderType;
import cc.minetale.mlib.util.TeamUtil;
import cc.minetale.sodium.lang.Language;
import cc.minetale.sodium.profile.ProfileUtil;
import cc.minetale.sodium.util.Message;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.event.EventFilter;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.player.*;
import net.minestom.server.event.trait.PlayerEvent;

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

                        var profile = ProfileUtil.getProfile(player.getUuid());

                        if (profile != null) {
                            profile.checkGrants();
                            player.setProfile(profile);
                            return;
                        }

                        player.kick(Message.parse(Language.Error.PROFILE_LOAD));
                })
                .addListener(PlayerSpawnEvent.class, event -> {
                    var player = event.getPlayer();
                    var profile = FlamePlayer.fromPlayer(player).getProfile();
                    if(profile == null) { return; }

                    if (event.isFirstSpawn()) {
                        NameplateHandler.addProvider(player, new NameplateProvider(TeamUtil.RANK_MAP.get(profile.getGrant().getRank()), ProviderType.RANK));
                    }
                });
    }

}