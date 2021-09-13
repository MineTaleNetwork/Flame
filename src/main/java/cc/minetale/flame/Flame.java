package cc.minetale.flame;

import cc.minetale.commonlib.CommonLib;
import cc.minetale.commonlib.modules.network.Gamemode;
import cc.minetale.commonlib.modules.pigeon.payloads.atom.AtomOnlinePlayerCountPayload;
import cc.minetale.commonlib.modules.pigeon.payloads.atom.AtomPlayerRequestPayload;
import cc.minetale.commonlib.modules.pigeon.payloads.network.ServerUpdatePayload;
import cc.minetale.commonlib.modules.rank.Rank;
import cc.minetale.commonlib.util.MC;
import cc.minetale.commonlib.util.PigeonUtil;
import cc.minetale.flame.chat.ChatFilter;
import cc.minetale.flame.commands.essentials.*;
import cc.minetale.flame.commands.staff.ClearChatCommand;
import cc.minetale.flame.commands.staff.GrantCommand;
import cc.minetale.flame.commands.staff.RankCommand;
import cc.minetale.flame.listeners.CoreListener;
import cc.minetale.flame.listeners.PlayerListener;
import cc.minetale.flame.pigeon.Listeners;
import cc.minetale.flame.team.TeamUtils;
import cc.minetale.mlib.config.mLibConfig;
import cc.minetale.mlib.mLib;
import cc.minetale.mlib.util.PlayerUtil;
import cc.minetale.pigeon.Converter;
import cc.minetale.pigeon.Pigeon;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.event.EventFilter;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.trait.EntityEvent;
import net.minestom.server.extensions.Extension;
import net.minestom.server.monitoring.TickMonitor;
import net.minestom.server.scoreboard.Team;
import net.minestom.server.timer.SchedulerManager;

import java.lang.management.ManagementFactory;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

@Getter
public class Flame extends Extension {

    @Getter private static Flame flame;

    private final AtomicReference<TickMonitor> LAST_TICK = new AtomicReference<>();

    private Map<UUID, Team> rankTeams;

    private int globalPlayers = 0;

    @Override
    public void initialize() {
        flame = this;

        MinecraftServer.getUpdateManager().addTickMonitor(LAST_TICK::set);

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

        CommonLib commonLib = CommonLib.getCommonLib();

        commonLib.getApiListeners()
                .add(new CoreListener());

        commonLib.getPigeon().getListenersRegistry()
                .registerListener(new Listeners());

        MinecraftServer.getGlobalEventHandler().addChild(events());

        SchedulerManager scheduler = MinecraftServer.getSchedulerManager();

        /* Server Update Task */
        scheduler.buildTask(() -> {
            mLib lib = mLib.getMLib();
            mLibConfig config = lib.getConfig();

            List<String> players = PlayerUtil.getNames(MinecraftServer.getConnectionManager().getOnlinePlayers());

            PigeonUtil.broadcast(new ServerUpdatePayload(
                    config.getName(),
                    Gamemode.getByName(config.getType()),
                    ManagementFactory.getRuntimeMXBean().getUptime(),
                    this.LAST_TICK.get().getTickTime(),
                    players,
                    mLib.getMLib().getConfig().getMaxPlayers()
            ));
        }).repeat(3, ChronoUnit.SECONDS).schedule();

        /* Retrieve Online Players */
        scheduler.buildTask(() -> {
            PigeonUtil.broadcast(new AtomOnlinePlayerCountPayload(callback -> {
                this.globalPlayers = callback.getPlayers();
            }));
        }).repeat(5, ChronoUnit.SECONDS).schedule();

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
