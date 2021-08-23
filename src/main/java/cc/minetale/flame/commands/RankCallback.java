package cc.minetale.flame.commands;

import cc.minetale.commonlib.modules.profile.Profile;
import lombok.Getter;

@Getter
public class RankCallback {

    private final Profile profile;
    private final boolean minimum;

    public RankCallback(Profile profile, boolean minimum) {
        this.profile = profile;
        this.minimum = minimum;
    }

}
