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

    private final Player issuer;
    private final UUID recipient;
    private final Type type;
    private Stage stage;
    private long duration;
    private String reason;

    public Procedure(Player issuer, UUID recipient, Type type, Stage stage) {
        this.issuer = issuer;
        this.recipient = recipient;
        this.type = type;
        this.stage = stage;

        Procedure.addProcedure(issuer, this);
    }

    public static Procedure getProcedure(Player player) {
        return activeProcedures.getOrDefault(player.getUuid(), null);
    }

    public static void addProcedure(Player player, Procedure procedure) {
        activeProcedures.put(player.getUuid(), procedure);
    }

    public static void removeProcedure(Player player) {
        activeProcedures.remove(player.getUuid());
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
