package cc.minetale.flame;

import cc.minetale.flame.chat.ChatFilter;
import cc.minetale.flame.commands.essentials.*;
import cc.minetale.flame.commands.staff.ClearChatCommand;
import cc.minetale.flame.listeners.PlayerListener;
import lombok.Getter;
import net.minestom.server.MinecraftServer;
import net.minestom.server.event.EventFilter;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.trait.EntityEvent;
import net.minestom.server.extensions.Extension;

@Getter
public class Flame extends Extension {

    @Override
    public void initialize() {
        ChatFilter.init();
        MinecraftServer.getCommandManager().register(new ClearChatCommand());
        MinecraftServer.getCommandManager().register(new GameModeCommand());
        MinecraftServer.getCommandManager().register(new PingCommand());
        MinecraftServer.getCommandManager().register(new TestMenuCommand());
        MinecraftServer.getCommandManager().register(new StopCommand());
        MinecraftServer.getCommandManager().register(new ExtensionsCommand());
        MinecraftServer.getGlobalEventHandler().addChild(events());
    }

    @Override
    public void terminate() {

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
