package cc.minetale.flame.procedure;

import lombok.Getter;
import lombok.Setter;
import net.minestom.server.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter @Setter
public abstract class Procedure {

    private static final Map<UUID, Procedure> activeProcedures = new HashMap<>();

    private final UUID issuer;
    private final UUID recipient;
    private final Type type;
    private Stage stage;
    private long duration;
    private String reason;

    public Procedure(UUID issuer, UUID recipient, Type type, Stage stage) {
        this.issuer = issuer;
        this.recipient = recipient;
        this.type = type;
        this.stage = stage;

        Procedure.addProcedure(issuer, this);
    }

    public static boolean canStartProcedure(UUID player) {
        return Procedure.getProcedure(player) == null;
    }

    public static Procedure getProcedure(UUID player) {
        return activeProcedures.getOrDefault(player, null);
    }

    private static void addProcedure(UUID player, Procedure procedure) {
        activeProcedures.put(player, procedure);
    }

    public static void removeProcedure(UUID player) {
        activeProcedures.remove(player);
    }

    public abstract void finish();
    public abstract void cancel();

    public enum Type {
        ADD,
        REMOVE
    }

    public enum Stage {
        PROVIDE_TIME,
        PROVIDE_REASON,
        PROVIDE_CONFIRMATION
    }

}
