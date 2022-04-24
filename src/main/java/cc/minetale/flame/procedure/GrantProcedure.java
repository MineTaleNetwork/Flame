package cc.minetale.flame.procedure;

import cc.minetale.sodium.profile.grant.Rank;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.entity.Player;

@Getter @Setter
public class GrantProcedure extends Procedure {

    private String grant;
    private Rank rank;
    private String target;

    public GrantProcedure(Player issuer, String recipient, Type type, Stage stage) {
        super(issuer, null, type, stage);

        this.target = recipient;
    }

    @Override
    public void finish() {
        Procedure.removeProcedure(getIssuer());
    }

    @Override
    public void cancel() {
        getIssuer().sendMessage(Component.text("You have cancelled the grant " + (this.getType() == Type.REMOVE ? "removal " : "") + "procedure.", NamedTextColor.RED));
        finish();
    }

}