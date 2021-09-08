package cc.minetale.flame.procedure;

import cc.minetale.commonlib.modules.grant.Grant;
import cc.minetale.commonlib.modules.profile.Profile;
import cc.minetale.commonlib.util.MC;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Getter @Setter
public class GrantProcedure {

    @Getter private static final Map<UUID, GrantProcedure> procedures = new ConcurrentHashMap<>();

    private final UUID issuer;
    private final Profile recipient;
    private final Type type;
    private Stage stage;
    private Builder builder;

    public GrantProcedure(UUID issuer, Profile recipient, Type type, Stage stage) {
        this.issuer = issuer;
        this.recipient = recipient;
        this.type = type;
        this.stage = stage;
        this.builder = new Builder();

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
            player.sendMessage(Component.text("Cancelled the grant procedure.", MC.CC.RED.getTextColor()));

        procedures.remove(this.issuer);
    }

    public static final class Builder {
        private UUID player;
        private UUID rank;
        private UUID addedBy;
        private String reason;
        private long duration;

        public GrantProcedure.Builder player(UUID player) {
            this.player = player;
            return this;
        }

        public GrantProcedure.Builder rank(UUID rank) {
            this.rank = rank;
            return this;
        }

        public GrantProcedure.Builder addedBy(UUID addedBy) {
            this.addedBy = addedBy;
            return this;
        }

        public GrantProcedure.Builder reason(String reason) {
            this.reason = reason;
            return this;
        }

        public GrantProcedure.Builder duration(long duration) {
            this.duration = duration;
            return this;
        }

        public Grant build() {
            if(this.player == null || this.rank == null || this.reason == null)
                throw new IllegalStateException("The builder must contain all values.");

            return new Grant(this.player, this.rank, this.addedBy, System.currentTimeMillis(), this.reason, this.duration);
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