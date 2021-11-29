package cc.minetale.flame;

import cc.minetale.commonlib.CommonLib;
import cc.minetale.commonlib.api.Rank;
import cc.minetale.commonlib.util.MC;
import cc.minetale.flame.commands.essentials.*;
import cc.minetale.flame.commands.staff.AddGrantCommand;
import cc.minetale.flame.commands.staff.ClearChatCommand;
import cc.minetale.flame.commands.staff.GrantCommand;
import cc.minetale.flame.commands.staff.RankCommand;
import cc.minetale.flame.listeners.CoreListener;
import cc.minetale.flame.listeners.PlayerListener;
import cc.minetale.flame.util.FlamePlayer;
import lombok.Getter;
import net.minestom.server.MinecraftServer;
import net.minestom.server.event.EventFilter;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.trait.EntityEvent;
import net.minestom.server.extensions.Extension;

import java.util.*;

@Getter
public class Flame extends Extension {

    @Getter private static Flame flame;

    @Override
    public void initialize() {
        Flame.flame = this;

        MinecraftServer.getConnectionManager()
                .setPlayerProvider(FlamePlayer::new);

        MinecraftServer.getCommandManager()
                .setUnknownCommandCallback((sender, command) -> sender.sendMessage(Lang.UNKNOWN_COMMAND));

        Arrays.asList(
                new ClearChatCommand(),
                new PingCommand(),
                new StopCommand(),
                new RankCommand(),
                new GrantCommand(),
                new PunishCommand(),
                new AddGrantCommand()
        ).forEach(command -> MinecraftServer.getCommandManager().register(command));

        var commonLib = CommonLib.getCommonLib();

        commonLib.getApiListeners()
                .add(new CoreListener());

        MinecraftServer.getGlobalEventHandler().addChild(events());
    }

    @Override
    public void terminate() {}

    public static EventNode<EntityEvent> events() {
        EventNode<EntityEvent> node = EventNode.type("flame-events", EventFilter.ENTITY);

        node.addChild(playerEvents());

        return node;
    }

    public static EventNode<EntityEvent> playerEvents() {
        return PlayerListener.events();
    }

}
