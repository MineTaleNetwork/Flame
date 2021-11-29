package cc.minetale.flame.procedure;

import cc.minetale.commonlib.api.Punishment;
import cc.minetale.commonlib.profile.Profile;
import cc.minetale.commonlib.util.MC;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Getter @Setter @Builder
public class PunishmentProcedure {

    @Getter private static final Map<UUID, PunishmentProcedure> procedures = new ConcurrentHashMap<>();

    private final UUID issuer;
    private final Profile recipient;
    private final Type type;
    private Stage stage;

    public PunishmentProcedure(UUID issuer, Profile recipient, Type type, Stage stage) {
        this.issuer = issuer;
        this.recipient = recipient;
        this.type = type;
        this.stage = stage;

        procedures.put(issuer, this);
    }

    public static PunishmentProcedure getByPlayer(UUID uuid) {
        for (PunishmentProcedure procedure : procedures.values()) {
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
            player.sendMessage(Component.text("Cancelled the punishment procedure.", MC.CC.RED.getTextColor()));

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
