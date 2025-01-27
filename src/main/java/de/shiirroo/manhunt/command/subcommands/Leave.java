package de.shiirroo.manhunt.command.subcommands;

import de.shiirroo.manhunt.ManHuntPlugin;
import de.shiirroo.manhunt.command.CommandBuilder;
import de.shiirroo.manhunt.command.SubCommand;
import de.shiirroo.manhunt.teams.model.ManHuntRole;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Leave extends SubCommand {

    @Override
    public String getName() {
        return "Leave";
    }

    @Override
    public String getDescription() {
        return "Leave Group";
    }

    @Override
    public String getSyntax() {
        return "/ManHunt Leave";
    }

    @Override
    public Boolean getNeedOp() {
        return false;
    }


    @Override
    public CommandBuilder getSubCommandsArgs(String[] args)  {
        return null;
    }


    @Override
    public void perform(Player player, String[] args) {
        if(ManHuntPlugin.getGameData().getPlayerData().getPlayerRoleByUUID(player.getUniqueId()) == null){
            player.sendMessage(ManHuntPlugin.getprefix() + "You are not in a group");
        } else if(!ManHuntPlugin.getGameData().getGameStatus().isGame()){
            if (!ManHuntPlugin.getGameData().getPlayerData().getPlayerRoleByUUID(player.getUniqueId()).equals(ManHuntRole.Unassigned)) {
                player.sendMessage(ManHuntPlugin.getprefix() + "You left the group: " + ChatColor.GOLD + ManHuntPlugin.getGameData().getPlayerData().getPlayerRoleByUUID(player.getUniqueId()));
                ManHuntPlugin.getGameData().getPlayerData().setRole(player, ManHuntRole.Unassigned,ManHuntPlugin.getTeamManager());
                TeamChat.leaveChat(player);
            }
            else {
                player.sendMessage(ManHuntPlugin.getprefix() + "You can´t leave this group");
            }
        } else {
            player.sendMessage(ManHuntPlugin.getprefix() + "You cannot leave the group during a game");
        }
    }

}



