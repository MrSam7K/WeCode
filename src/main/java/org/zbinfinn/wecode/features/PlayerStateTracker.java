package org.zbinfinn.wecode.features;

import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.CommandExecutionC2SPacket;
import net.minecraft.network.packet.s2c.play.ClearTitleS2CPacket;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.zbinfinn.wecode.WeCode;
import org.zbinfinn.wecode.helpers.MessageHelper;
import org.zbinfinn.wecode.playerstate.*;

import java.util.Set;

public class PlayerStateTracker extends Feature {
    private boolean expectingModeChange = false;
    private long timeoutModeChange;
    private boolean foundJoinMessage = false;
    private final Set<String> spawnCommands = Set.of(
            "s", "spawn",
            "leave"
    );

    @Override
    public void handlePacket(Packet<?> packet, CallbackInfo ci) {
        if (packet instanceof ClearTitleS2CPacket) {
            timeoutModeChange = System.currentTimeMillis() + 2000;
            expectingModeChange = true;
        }

        if (!(packet instanceof GameMessageS2CPacket(Text content, boolean overlay))) {
            return;
        }

        if (content.getString().startsWith("» Sending you to") && content.getString().endsWith("...")) {
            String node = content.getString().substring("» Sending you to ".length()).replace("...", "");
            WeCode.generalState.setNode(Node.getFromDisplayString(node));
            return;
        }

        if (!expectingModeChange) {
            return;
        }

        if (timeoutModeChange < System.currentTimeMillis()) {
            expectingModeChange = false;
            return;
        }

        expectingModeChange = false;

        if (content.getString().equals("◆ Welcome back to DiamondFire! ◆")) {
            foundJoinMessage = true;
            return;
        }

        if (content.getString().equals("» You are now in dev mode.")) {
            WeCode.modeState = new DevState();
            return;
        }
        if (content.getString().equals("» You are now in build mode.")) {
            WeCode.modeState = new BuildState();
            return;
        }
        if (content.getString().startsWith("» Joined game: ") && content.getString().contains(" by ")) {
            WeCode.modeState = new PlayState();
            return;
        }

        expectingModeChange = true;
    }

    @Override
    public void sentPacket(Packet<?> packet, CallbackInfo ci) {
        if (packet instanceof CommandExecutionC2SPacket commandPacket) {
            if (spawnCommands.contains(commandPacket.command())) {
                WeCode.modeState = new SpawnState();
                return;
            }
        }
    }

    @Override
    public void tick() {
        if (foundJoinMessage) {
            foundJoinMessage = false;
            SpawnState newState = new SpawnState();

            WeCode.modeState = newState;

            // "info" is the name of the scoreboard df uses at spawn
            Scoreboard scoreboard = WeCode.MC.player.getScoreboard();
            ScoreboardObjective objective = scoreboard.getNullableObjective("info");

            String nodeString = Formatting.strip(scoreboard.getScoreboardEntries(objective).stream()
                    .filter((entry -> Formatting.strip(entry.name().getString()).startsWith("Node") || Formatting.strip(entry.name().getString()).startsWith("Dev")))
                    .findFirst().get().name().getString());


            String node = nodeString.split(" - ")[0];
            WeCode.generalState.setNode(Node.getFromDisplayString(node));
        }
    }
}
