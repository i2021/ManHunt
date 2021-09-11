package de.shiirroo.manhunt.command.subcommands;

import de.shiirroo.manhunt.ManHuntPlugin;
import de.shiirroo.manhunt.utilis.Config;
import de.shiirroo.manhunt.teams.PlayerData;
import de.shiirroo.manhunt.command.CommandBuilder;
import de.shiirroo.manhunt.command.SubCommand;
import de.shiirroo.manhunt.event.Events;
import de.shiirroo.manhunt.utilis.Vote;
import de.shiirroo.manhunt.teams.model.ManHuntRole;
import org.bukkit.*;
import org.bukkit.entity.Player;

import java.util.Date;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

public class Ready extends SubCommand {

    private static HashMap<UUID, Long> playerReadyTime = new HashMap<>();
    public static Vote ready;

    @Override
    public String getName() {
        return "Ready";
    }

    @Override
    public String getDescription() {
        return "ManHunt ready to start";
    }

    @Override
    public String getSyntax() {
        return "/ManHunt Ready";
    }

    @Override
    public Boolean getNeedOp() {
        return false;
    }

    @Override
    public CommandBuilder getSubCommandsArgs(String[] args) {
        return null;
    }


    @Override
    public void perform(Player p, String[] args) {
        if(StartGame.gameRunning == null){
            if(ready != null){
                    if(!setReady(p))
                        p.sendMessage(ManHuntPlugin.getprefix() + "You're too fast, have a little patience");
            }
        } else {
            p.sendMessage(ManHuntPlugin.getprefix() + "You can´t change ready status while running match");
        }

    }

    public static boolean setReady(Player p){
        if(ready == null) setReadyVote();
        if(isPlayerHasCooldown(p)) {
            if (ready.hasPlayerVote(p)) {
                readyRemove(p, Bukkit.getOnlinePlayers().size());
                playerReadyTime.put(p.getUniqueId(), (new Date().getTime() + 5000L));
                Events.playerMenu.get(p.getUniqueId()).setMenuItems();
                ManHuntPlugin.getPlayerData().setUpdateRole(p, ManHuntPlugin.getTeamManager());
                return true;

            } else {
                if (readyAdd(p)) {
                    Events.playerMenu.get(p.getUniqueId()).setMenuItems();
                    ManHuntPlugin.getPlayerData().setUpdateRole(p, ManHuntPlugin.getTeamManager());
                    return true;
                }
            }
        }
        return false;
    }

    public static void setReadyVote(){
        ready = new Vote(false,ManHuntPlugin.getPlugin(), ChatColor.GREEN + "Game will start in " + ChatColor.GOLD+ "TIMER", Config.getVoteStartTime());
        ready.getbossBarCreator().onComplete(aBoolean -> {
                    ready = null;
                    if(aBoolean) {
                        if (StartGame.setPlayer())
                            StartGame.Start();
                    }
                }
        );
        ready.getbossBarCreator().onShortlyComplete(aBoolean -> {      Bukkit.getOnlinePlayers().forEach(current -> current.playSound(current.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f));});
    }




    public static boolean readyAdd(Player p){
            if(startGame()){
                ready.addVote(p);
                playerReadyTime.put(p.getUniqueId(), (new Date().getTime() +5000L));
                return true;
            }
        return false;
    }

    public static void readyRemove(Player p, Integer players){
            playerReadyTime.remove(p.getUniqueId());
            ready.removeVote(p);
            ready.getbossBarCreator().cancel();
            setOtherPlayerUnready(players);
    }

    public static boolean isPlayerHasCooldown(Player p){
        Long cooldown = playerReadyTime.get(p.getUniqueId());
        if(cooldown == null) return true;
        if((new Date().getTime() - cooldown) > 0) {
            return true;

        }
        return false;
    }

    public static boolean startGame(){
        if(Bukkit.getOnlinePlayers().size()>1 && (ManHuntPlugin.getPlayerData().getPlayersByRole(ManHuntRole.Speedrunner).size() != 0 || ManHuntPlugin.getPlayerData().getPlayersByRole(ManHuntRole.Unassigned).size() >= 1)){
                if (ManHuntPlugin.getPlayerData().getPlayersByRole(ManHuntRole.Speedrunner).size() == Bukkit.getOnlinePlayers().size() ||  ManHuntPlugin.getPlayerData().getPlayersByRole(ManHuntRole.Hunter).size() == Bukkit.getOnlinePlayers().size() ||  ManHuntPlugin.getPlayerData().getPlayersByRole(ManHuntRole.Assassin).size() == Bukkit.getOnlinePlayers().size()){
                    return false;
                }
                if((ready.getPlayers().size() +1) == Bukkit.getOnlinePlayers().size()){
                        ready.startVote();
                }
                return true;
        }
        return false;
    }



    private static void setOtherPlayerUnready(Integer players){
        if(players == 1 && ready.getPlayers().size() == 1){
            Optional<UUID> uuid = ready.getPlayers().stream().findFirst();
            if(uuid != null && ready.hasPlayerVote(Bukkit.getPlayer(uuid.get())))
                ready.removeVote(Bukkit.getPlayer(uuid.get()));
                Events.playerMenu.get(uuid.get()).setMenuItems();
                ManHuntPlugin.getPlayerData().setUpdateRole(Bukkit.getPlayer(uuid.get()), ManHuntPlugin.getTeamManager());
        }
    }



//ChatColor.GREEN + "Game will start in " + ChatColor.GOLD+ startGame
    //if(StartGame.setPlayer())  StartGame.Start();
    //config.getVoteStartTime()





}