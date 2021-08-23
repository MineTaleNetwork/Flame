package cc.minetale.flame.team;

import cc.minetale.commonlib.modules.profile.Profile;
import cc.minetale.commonlib.modules.rank.Rank;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.Instance;
import net.minestom.server.network.packet.server.play.TeamsPacket;
import net.minestom.server.utils.PacketUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FlameTeam {

    @Getter private static Map<UUID, FlameTeam> teams = new HashMap<>();

    public Profile profile;
    public Player player;
    public Instance instance;

    public String teamName;
    public Component teamPrefix;
    public NamedTextColor teamColor;

    public FlameTeam(Profile profile, Player player) {
        this.profile = profile;
        this.player = player;
        this.instance = player.getInstance();

        this.teamName = getTeamName();

        Rank rank = profile.api().getActiveGrant().api().getRank();

        this.teamPrefix = profile.api().getColoredPrefix();
        this.teamColor = NamedTextColor.NAMES.value(rank.getColor().toLowerCase());

        teams.put(player.getUuid(), this);

        this.createTeam();
    }

    public void createTeam() {
        TeamsPacket packet = new TeamsPacket();

        packet.action = TeamsPacket.Action.CREATE_TEAM;

        packet.teamName = this.teamName;
        packet.teamPrefix = this.teamPrefix;
        packet.teamColor = this.teamColor;

        packet.friendlyFlags = 0;
        packet.teamDisplayName = Component.empty();
        packet.teamSuffix = Component.empty();
        packet.nameTagVisibility = TeamsPacket.NameTagVisibility.ALWAYS;
        packet.entities = new String[]{this.player.getUsername()};
        packet.collisionRule = TeamsPacket.CollisionRule.ALWAYS;

        PacketUtils.sendPacket(this.instance, packet);
    }

    public void createTeam(Player player) {
        TeamsPacket packet = new TeamsPacket();

        packet.action = TeamsPacket.Action.CREATE_TEAM;

        packet.teamName = this.teamName;
        packet.teamPrefix = this.teamPrefix;
        packet.teamColor = this.teamColor;

        packet.friendlyFlags = 0;
        packet.teamDisplayName = Component.empty();
        packet.teamSuffix = Component.empty();
        packet.nameTagVisibility = TeamsPacket.NameTagVisibility.ALWAYS;
        packet.entities = new String[]{this.player.getUsername()};
        packet.collisionRule = TeamsPacket.CollisionRule.ALWAYS;

        PacketUtils.sendPacket(player, packet);
    }

    public void deleteTeam() {
        TeamsPacket packet = new TeamsPacket();

        packet.action = TeamsPacket.Action.REMOVE_TEAM;

        packet.teamName = this.teamName;

        teams.remove(this.player.getUuid());

        PacketUtils.sendPacket(this.instance, packet);
    }

    public String getTeamName() {
        Rank rank = this.profile.api().getActiveGrant().api().getRank();

        String trimmedName = StringUtils.left(this.profile.getName(), 10);

        return String.format("%03d", rank.getWeight()) + trimmedName + RandomStringUtils.randomAlphanumeric(3);
    }

}
