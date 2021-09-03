package cc.minetale.flame.procedure;

import cc.minetale.commonlib.modules.grant.Grant;
import cc.minetale.commonlib.modules.profile.Profile;
import lombok.Getter;
import lombok.Setter;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Getter @Setter
public class GrantProcedure {

    @Getter private static final Map<UUID, GrantProcedure> procedures = new ConcurrentHashMap<>();

    private final UUID issuer;
    private final Profile recipient;
    private final Type type;
    private Stage stage;
    private Grant grant;

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