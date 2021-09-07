package cc.minetale.flame;

import cc.minetale.commonlib.modules.rank.Rank;
import cc.minetale.commonlib.util.MC;
import cc.minetale.flame.chat.ChatFilter;
import cc.minetale.flame.commands.essentials.*;
import cc.minetale.flame.commands.staff.ClearChatCommand;
import cc.minetale.flame.commands.staff.GrantCommand;
import cc.minetale.flame.commands.staff.RankCommand;
import cc.minetale.flame.listeners.PlayerListener;
import cc.minetale.flame.team.TeamUtils;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.MinecraftServer;
import net.minestom.server.event.EventFilter;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.trait.EntityEvent;
import net.minestom.server.extensions.Extension;
import net.minestom.server.scoreboard.Team;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class Flame extends Extension {

    @Getter private static Flame flame;
    private Map<UUID, Team> rankTeams;

    @Override
    public void initialize() {
        flame = this;

        this.rankTeams = new ConcurrentHashMap<>();

        ChatFilter.init();

        Arrays.asList(
                new ClearChatCommand(),
                new GameModeCommand(),
                new PingCommand(),
                new StopCommand(),
                new ExtensionsCommand(),
                new ListCommand(),
                new RankCommand(),
                new GrantCommand(),
                new PunishCommand()
        ).forEach(command -> MinecraftServer.getCommandManager().register(command));

        MinecraftServer.getGlobalEventHandler().addChild(events());

        this.updateTeams();
    }

    @Override
    public void terminate() {

    }

    public void updateTeams() {
        this.rankTeams.clear();

        for(Rank rank : Rank.getRanks().values()) {
            Team team = MinecraftServer.getTeamManager().createTeam(
                    TeamUtils.getTeamName(rank),
                    Component.empty(),
                    MC.Style.fromLegacy(rank.getPrefix()),
                    NamedTextColor.NAMES.value(rank.getColor().toLowerCase()),
                    Component.empty()
            );

            this.rankTeams.put(rank.getUuid(), team);
        }
    }

    public static EventNode<EntityEvent> events() {
        EventNode<EntityEvent> node = EventNode.type("flame-events", EventFilter.ENTITY);

        node.addChild(playerEvents());

        return node;
    }

    public static EventNode<EntityEvent> playerEvents() {
        return PlayerListener.events();
    }
}
