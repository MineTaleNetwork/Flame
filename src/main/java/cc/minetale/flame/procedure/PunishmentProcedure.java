package cc.minetale.flame.procedure;

import cc.minetale.commonlib.modules.profile.Profile;
import cc.minetale.commonlib.modules.punishment.Punishment;
import cc.minetale.commonlib.util.MC;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Getter @Setter
public class PunishmentProcedure {

    @Getter private static final Map<UUID, PunishmentProcedure> procedures = new ConcurrentHashMap<>();

    private final UUID issuer;
    private final Profile recipient;
    private final Type type;
    private Stage stage;
    private final Builder builder;

    public PunishmentProcedure(UUID issuer, Profile recipient, Type type, Stage stage) {
        this.issuer = issuer;
        this.recipient = recipient;
        this.type = type;
        this.stage = stage;
        this.builder = new Builder();

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

    @Getter
    public static final class Builder {
        private UUID player;
        private Punishment.Type type;
        private UUID addedBy;
        private String reason;
        private long duration;

        public PunishmentProcedure.Builder player(UUID player) {
            this.player = player;
            return this;
        }

        public PunishmentProcedure.Builder type(Punishment.Type type) {
            this.type = type;
            return this;
        }

        public PunishmentProcedure.Builder addedBy(UUID addedBy) {
            this.addedBy = addedBy;
            return this;
        }

        public PunishmentProcedure.Builder reason(String reason) {
            this.reason = reason;
            return this;
        }

        public PunishmentProcedure.Builder duration(long duration) {
            this.duration = duration;
            return this;
        }

        public Punishment build() {
            if(this.player == null || this.type == null || this.reason == null)
                throw new IllegalStateException("The builder must contain all values.");

            return new Punishment(this.player, this.type, this.addedBy, System.currentTimeMillis(), this.reason, this.duration);
        }
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
