package cc.minetale.flame;

import cc.minetale.commonlib.CommonLib;
import cc.minetale.commonlib.network.server.Server;
import cc.minetale.commonlib.network.server.ServerAction;
import cc.minetale.commonlib.network.server.ServerData;
import cc.minetale.commonlib.pigeon.payloads.atom.AtomPlayerCountRequestPayload;
import cc.minetale.commonlib.pigeon.payloads.network.ServerUpdatePayload;
import cc.minetale.commonlib.rank.Rank;
import cc.minetale.commonlib.util.MC;
import cc.minetale.commonlib.util.PigeonUtil;
import cc.minetale.flame.chat.ChatFilter;
import cc.minetale.flame.commands.essentials.*;
import cc.minetale.flame.commands.staff.AddGrantCommand;
import cc.minetale.flame.commands.staff.ClearChatCommand;
import cc.minetale.flame.commands.staff.GrantCommand;
import cc.minetale.flame.commands.staff.RankCommand;
import cc.minetale.flame.listeners.CoreListener;
import cc.minetale.flame.listeners.PlayerListener;
import cc.minetale.flame.pigeon.Listeners;
import cc.minetale.flame.team.TeamUtils;
import cc.minetale.flame.util.FlamePlayer;
import cc.minetale.mlib.config.mLibConfig;
import cc.minetale.mlib.mLib;
import cc.minetale.mlib.util.PlayerUtil;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.MinecraftServer;
import net.minestom.server.event.EventFilter;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.trait.EntityEvent;
import net.minestom.server.extensions.Extension;
import net.minestom.server.monitoring.TickMonitor;
import net.minestom.server.scoreboard.Team;
import net.minestom.server.timer.SchedulerManager;

import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

@Getter
public class Flame extends Extension {

    @Getter private static Flame flame;
    private final AtomicReference<TickMonitor> LAST_TICK = new AtomicReference<>();
    private Map<UUID, Team> rankTeams;

    private long start;
    private int globalPlayers = 0;

    @Override
    public void initialize() {
        flame = this;

        this.start = System.currentTimeMillis();

        MinecraftServer.getConnectionManager().setPlayerProvider(FlamePlayer::new);
        MinecraftServer.getUpdateManager().addTickMonitor(LAST_TICK::set);

        this.rankTeams = new ConcurrentHashMap<>();

        ChatFilter.init();

        MinecraftServer.getCommandManager().setUnknownCommandCallback((sender, command) -> sender.sendMessage(Lang.UNKNOWN_COMMAND));

        Arrays.asList(
                new ClearChatCommand(),
                new GameModeCommand(),
                new PingCommand(),
                new StopCommand(),
                new ExtensionsCommand(),
                new ListCommand(),
                new RankCommand(),
                new GrantCommand(),
                new PunishCommand(),
                new AddGrantCommand()
        ).forEach(command -> MinecraftServer.getCommandManager().register(command));

        CommonLib commonLib = CommonLib.getCommonLib();

        commonLib.getApiListeners()
                .add(new CoreListener());

        commonLib.getPigeon().getListenersRegistry()
                .registerListener(new Listeners());

        MinecraftServer.getGlobalEventHandler().addChild(events());

        this.updateTeams();

        SchedulerManager scheduler = MinecraftServer.getSchedulerManager();

        /* Server Update Task */
        scheduler.buildTask(() -> PigeonUtil.broadcast(new ServerUpdatePayload(this.getServer(), ServerAction.STATE_CHANGE)))
                .repeat(3, ChronoUnit.SECONDS)
                .schedule();

        /* Retrieve Online Players */
        scheduler.buildTask(() -> PigeonUtil.broadcast(new AtomPlayerCountRequestPayload(callback -> this.globalPlayers = callback.getPlayers())))
                .repeat(5, ChronoUnit.SECONDS)
                .schedule();
    }

    @Override
    public void terminate() {}

    public Server getServer() {
        mLibConfig config = mLib.getMLib().getConfig();

        // TODO: On player join send payload with size of players orrr not?

        List<UUID> players = PlayerUtil.getUUIDs(MinecraftServer.getConnectionManager().getOnlinePlayers());

        return new Server(
                config.getName(),
                this.start,
                new ServerData(
                        config.getMaxPlayers(),
                        0.0,
                        Collections.emptyMap(),
                        players
                )
        );
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
