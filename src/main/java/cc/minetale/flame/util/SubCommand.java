package cc.minetale.flame.util;

import net.minestom.server.command.builder.Command;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SubCommand extends Command {

    public SubCommand(@NotNull String name, @Nullable String... aliases) {
        super(name, aliases);
    }

    public SubCommand(@NotNull String name) {
        super(name);
    }

}
