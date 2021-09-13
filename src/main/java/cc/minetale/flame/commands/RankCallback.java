package cc.minetale.flame.commands;

import cc.minetale.commonlib.modules.profile.Profile;
import cc.minetale.commonlib.modules.rank.Rank;
import lombok.Getter;

@Getter
public class RankCallback {

    private final Profile profile;
    private final Rank rank;
    private final boolean eligible;
    private final boolean console;

    public RankCallback(Profile profile, Rank rank, boolean eligible, boolean console) {
        this.profile = profile;
        this.rank = rank;
        this.eligible = eligible;
        this.console = console;
    }

}
