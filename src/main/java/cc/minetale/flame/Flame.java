package cc.minetale.flame;

import cc.minetale.flame.commands.essentials.GrantsCommand;
import cc.minetale.flame.commands.essentials.PingCommand;
import cc.minetale.flame.commands.essentials.PunishCommand;
import cc.minetale.flame.commands.essentials.StopCommand;
import cc.minetale.flame.commands.staff.AddGrantCommand;
import cc.minetale.flame.commands.staff.ClearChatCommand;
import cc.minetale.flame.commands.staff.GrantCommand;
import cc.minetale.flame.commands.staff.RanksCommand;
import cc.minetale.flame.listeners.PigeonListener;
import cc.minetale.flame.listeners.PlayerListener;
import cc.minetale.flame.util.FlamePlayer;
import cc.minetale.pigeon.Pigeon;
import lombok.Getter;
import net.minestom.server.MinecraftServer;
import net.minestom.server.extensions.Extension;

import java.util.Arrays;

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
                new RanksCommand(),
                new GrantCommand(),
                new PunishCommand(),
                new AddGrantCommand(),
                new GrantsCommand()
        ).forEach(command -> MinecraftServer.getCommandManager().register(command));

        Pigeon.getPigeon().getListenersRegistry().registerListener(new PigeonListener());

        MinecraftServer.getGlobalEventHandler().addChild(PlayerListener.events());
    }

    @Override
    public void terminate() {}

}
