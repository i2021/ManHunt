package de.shiirroo.manhunt.command.subcommands.vote;

import de.shiirroo.manhunt.ManHuntPlugin;
import de.shiirroo.manhunt.command.CommandBuilder;
import de.shiirroo.manhunt.command.SubCommand;
import de.shiirroo.manhunt.event.menu.MenuManagerException;
import de.shiirroo.manhunt.event.menu.MenuManagerNotSetupException;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import java.io.IOException;

public class VoteCommand extends SubCommand {

    private static Vote vote;

    @Override
    public String getName() {
        return "Vote";
    }

    @Override
    public String getDescription() {
        return "Vote for one of the available votes or create a new one";
    }

    @Override
    public String getSyntax() {
        return "/MahHunt Vote or Vote [Votename]";
    }

    @Override
    public Boolean getNeedOp() {
        return false;
    }

    @Override
    public CommandBuilder getSubCommandsArgs(String[] args) {
        CommandBuilder cm = new CommandBuilder("Vote");
        CommandBuilder create = new CommandBuilder("Create");
        create.addSubCommandBuilder(new CommandBuilder("Skip-Day"));
        create.addSubCommandBuilder(new CommandBuilder("Skip-Night"));
        create.addSubCommandBuilder(new CommandBuilder("Pause"));
        cm.addSubCommandBuilder(create);
        return cm;

    }

    public static void resetVote() {
        vote = null;
    }

    public static Vote getVote() {
        return vote;
    }

    @Override
    public void perform(Player player, String[] args) throws IOException, InterruptedException, MenuManagerException, MenuManagerNotSetupException {
        if(ManHuntPlugin.getGameData().getGameStatus().isGameRunning() && ManHuntPlugin.getGameData().getGameStatus().getLivePlayerList().contains(player.getUniqueId())) {
            if (vote != null && args.length == 1) {
                if(vote.getVoteCreator().hasPlayerVote(player)){
                    player.sendMessage(ManHuntPlugin.getprefix() + "You have already voted.");
                } else {
                    vote.getVoteCreator().addVote(player);
                }
            } else if (vote == null) {
                if (Bukkit.getOnlinePlayers().stream().filter(e -> !e.getGameMode().equals(GameMode.SPECTATOR)).count()>= 2 && args.length == 3 && args[1].equalsIgnoreCase("Create")) {
                    Vote createVote = null;
                    switch (args[2].toLowerCase()) {
                        case "skip-night" -> createVote = new VoteSkipNight();
                        case "skip-day" -> createVote = new VoteSkipDay();
                        case "pause" -> setPause(player);
                    }
                    if(createVote != null){
                        createVote.startVote(player);
                        if(createVote.requirement()){
                            vote = createVote;
                        }
                    }
                } else {
                    player.sendMessage(ManHuntPlugin.getprefix() + "There are currently no votes.");
                }
            } else {
                player.sendMessage(ManHuntPlugin.getprefix() + "There is already a vote in progress.");
            }
        }
    }


    public void setPause(Player player) {
        Vote votePause = new VotePause();
        Vote voteContinue = new VoteContinue();
        if(votePause.requirement()){
            vote = votePause;
            vote.startVote(player);
        } else if(voteContinue.requirement()){
            vote = new VoteContinue();
            vote.startVote(player);
        }
    }
}