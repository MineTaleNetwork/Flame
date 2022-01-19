package cc.minetale.flame;

import cc.minetale.commonlib.lang.Language;
import cc.minetale.flame.listeners.PigeonListener;
import cc.minetale.flame.listeners.PlayerListener;
import cc.minetale.flame.util.FlamePlayer;
import cc.minetale.flame.util.FlameProvider;
import cc.minetale.flame.util.SubCommand;
import cc.minetale.pigeon.Pigeon;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.builder.Command;
import net.minestom.server.extensions.Extension;
import net.minestom.server.timer.ExecutionType;
import net.minestom.server.utils.time.Tick;
import org.reflections.Reflections;

public class Flame extends Extension {

    @Override
    public void initialize() {
        MinecraftServer.getConnectionManager()
                .setPlayerProvider(FlamePlayer::new);

        MinecraftServer.getCommandManager()
                .setUnknownCommandCallback((sender, command) -> sender.sendMessage(Language.Command.UNKNOWN_COMMAND_ERROR));

        FlameProvider.init();

        var commandClasses = new Reflections("cc.minetale.flame.commands")
                .getSubTypesOf(Command.class)
                .stream()
                .filter(command -> !command.isAnnotationPresent(SubCommand.class))
                .toList();

        for (var commandClass : commandClasses) {
            try {
                var command = commandClass.getDeclaredConstructor().newInstance();

                MinecraftServer.getCommandManager().register(command);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }

        Pigeon.getPigeon()
                .getListenersRegistry()
                .registerListener(new PigeonListener());

        MinecraftServer.getSchedulerManager()
                .buildTask(() -> {
                    for(var player : MinecraftServer.getConnectionManager().getOnlinePlayers()) {
                        var profile = FlamePlayer.fromPlayer(player).getProfile();

                        profile.checkGrants();
                    }
                })
                .executionType(ExecutionType.ASYNC)
                .repeat(20, Tick.SERVER_TICKS)
                .schedule();

        MinecraftServer.getGlobalEventHandler().addChild(PlayerListener.events());
    }

    @Override
    public void terminate() {}

}
