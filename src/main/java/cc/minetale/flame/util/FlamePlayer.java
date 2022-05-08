package cc.minetale.flame.util;

import cc.minetale.mlib.nametag.NameplateHandler;
import cc.minetale.mlib.nametag.NameplateProvider;
import cc.minetale.mlib.nametag.ProviderType;
import cc.minetale.mlib.util.TeamUtil;
import cc.minetale.sodium.profile.Profile;
import cc.minetale.sodium.profile.ProfileUtil;
import cc.minetale.sodium.util.Cooldown;
import lombok.Getter;
import lombok.Setter;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.network.player.PlayerConnection;
import net.minestom.server.utils.time.Tick;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.UUID;
import java.util.regex.Pattern;

@Getter @Setter
public class FlamePlayer extends Player {

    private Profile profile;
    private Cooldown cooldown;

    public FlamePlayer(@NotNull UUID uuid, @NotNull String username, @NotNull PlayerConnection playerConnection) {
        super(uuid, username, playerConnection);

        cooldown = new Cooldown(Duration.of(1L, Tick.SERVER_TICKS));
    }

    public static FlamePlayer fromPlayer(Player player) {
        return (FlamePlayer) player;
    }

    private static final Pattern USERNAME_PATTERN = Pattern.compile("^\\w+$");

    public static boolean isValidName(String name) {
        return name.length() >= 3 && name.length() <= 16 && USERNAME_PATTERN.matcher(name).matches();
    }

    public static Profile getProfile(String name) {
        var player = MinecraftServer.getConnectionManager().getPlayer(name);

        if(!isValidName(name)) {
            return null;
        }

        if(player != null) {
            return fromPlayer(player).getProfile();
        }

        return ProfileUtil.getProfile(name);
    }

    public void refreshPlayer() {
        profile.activateNextGrant();

        NameplateHandler.clearProviders(this);
        NameplateHandler.addProvider(this, new NameplateProvider(TeamUtil.RANK_MAP.get(profile.getGrant().getRank()), ProviderType.RANK));

        refreshCommands();
    }

    public static Profile getProfile(UUID uuid) {
        var player = MinecraftServer.getConnectionManager().getPlayer(uuid);

        if(player == null) {
            return ProfileUtil.getProfile(uuid);
        }

        return FlamePlayer.fromPlayer(player).getProfile();
    }

}
