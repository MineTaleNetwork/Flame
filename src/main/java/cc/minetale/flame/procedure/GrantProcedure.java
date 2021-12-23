package cc.minetale.flame.procedure;

import cc.minetale.commonlib.api.Rank;
import cc.minetale.commonlib.profile.Profile;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.entity.Player;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter @Setter @Builder @AllArgsConstructor
public class GrantProcedure {

    @Getter private static final Map<Player, GrantProcedure> procedures = new ConcurrentHashMap<>();

    private final Player issuer;
    private final Profile recipient;
    private final Type type;
    private Stage stage;

    private String grantId;
    private Rank rank;
    private long duration;
    private String reason;

    public GrantProcedure(Player issuer, Profile recipient, Type type, Stage stage) {
        this.issuer = issuer;
        this.recipient = recipient;
        this.type = type;
        this.stage = stage;

        procedures.put(issuer, this);
    }

    public static GrantProcedure getByPlayer(Player player) {
        for (GrantProcedure procedure : procedures.values()) {
            if (procedure.issuer.equals(player)) {
                return procedure;
            }
        }

        return null;
    }

    public void finish() {
        procedures.remove(this.issuer);
    }

    public void cancel() {
        this.issuer.sendMessage(Component.text("You have cancelled the grant " + (this.type == Type.REMOVE ? "removal " : "") + "procedure.", NamedTextColor.RED));
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