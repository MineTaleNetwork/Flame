package cc.minetale.flame.procedure;

import cc.minetale.commonlib.profile.Profile;
import cc.minetale.flame.Lang;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Getter @Setter @Builder @AllArgsConstructor
public class GrantProcedure {

    @Getter private static final Map<UUID, GrantProcedure> procedures = new ConcurrentHashMap<>();

    private final UUID issuer;
    private final Profile recipient;
    private final Type type;
    private Stage stage;

    private long duration;
    private String reason;

    public GrantProcedure(UUID issuer, Profile recipient, Type type, Stage stage) {
        this.issuer = issuer;
        this.recipient = recipient;
        this.type = type;
        this.stage = stage;

        procedures.put(issuer, this);
    }

    public static GrantProcedure getByPlayer(UUID uuid) {
        for (GrantProcedure procedure : procedures.values()) {
            if (procedure.issuer.equals(uuid)) {
                return procedure;
            }
        }

        return null;
    }

    public void finish() {
        this.recipient.update();

        procedures.remove(this.issuer);
    }

    public void cancel() {
        Player player = MinecraftServer.getConnectionManager().getPlayer(this.issuer);

        if(player != null)
            player.sendMessage(Lang.CANCELLED_GRANT);

        procedures.remove(this.issuer);
    }

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