package cc.minetale.flame.arguments;

import cc.minetale.commonlib.modules.rank.Rank;
import cc.minetale.flame.util.Strings;
import net.minestom.server.command.builder.NodeMaker;
import net.minestom.server.command.builder.arguments.Argument;
import net.minestom.server.command.builder.exception.ArgumentSyntaxException;
import net.minestom.server.command.builder.suggestion.SuggestionEntry;
import net.minestom.server.network.packet.server.play.DeclareCommandsPacket;
import net.minestom.server.network.packet.server.play.DeclareCommandsPacket.Node;
import net.minestom.server.utils.binary.BinaryWriter;
import org.jetbrains.annotations.NotNull;

public class ArgumentRank extends Argument<Rank> {

    public static final int UNKNOWN_RANK_ERROR = 1;

    public ArgumentRank(@NotNull String id) {
        super(id);

        this.setSuggestionCallback((commandSender, context, suggestion) -> {
            for(Rank rank : Rank.getRanks().values()) {
                String name = rank.getName();

                if(Strings.startsWithIgnoreCase(name, suggestion.getInput())) {
                    suggestion.addEntry(new SuggestionEntry(name));
                }
            }
        });
    }

    @NotNull
    @Override
    public Rank parse(@NotNull String input) throws ArgumentSyntaxException {
        Rank rank = Rank.getRank(input);

        if(rank != null)
            return rank;

        throw new ArgumentSyntaxException("Invalid or unknown rank", input, UNKNOWN_RANK_ERROR);
    }

    @Override
    public void processNodes(@NotNull NodeMaker nodeMaker, boolean executable) {
        DeclareCommandsPacket.Node argumentNode = simpleArgumentNode(this, executable, false, false);

        argumentNode.parser = "brigadier:string";
        argumentNode.properties = BinaryWriter.makeArray((packetWriter) -> {
            packetWriter.writeVarInt(0);
        });

        nodeMaker.addNodes(new Node[]{argumentNode});
    }

}
