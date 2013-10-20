package com.timepath.hl2.gameinfo;

import com.timepath.hl2.ExternalConsole;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Logger;

/**
 *
 * @author TimePath
 */
@SuppressWarnings("serial")
public class ExternalScoreboard extends ExternalConsole {

    private static final Logger LOG = Logger.getLogger(ExternalScoreboard.class.getName());

    public static void main(String... args) throws IOException {
        ExternalScoreboard es = new ExternalScoreboard();
        es.connect(12345);
        es.setVisible(true);
    }

    private final ArrayList<Player> players = new ArrayList<Player>();

    public ExternalScoreboard() {
        super();
        this.setTitle("External killfeed");
    }

    private Player getPlayer(String name) {
        for(int i = 0; i < players.size(); i++) {
            Player p = players.get(i);
            if(p.getName().equals(name)) {
                return p;
            }
        }
        players.add(new Player(name));
        Player p = getPlayer(name);
        return p;
    }

    private void notify(String s) {
        Player killer = getPlayer(s.split(" killed ")[0]);
        Player victim = getPlayer(s.split(" killed ")[1].split(" with ")[0]);
        String weapon = s.split(" killed ")[1].split(" with ")[1];
        
        Player.exchangeInfo(victim, killer);
        
        boolean crit = weapon.endsWith("(crit)");
        weapon = weapon.substring(0, weapon.indexOf('.'));
        if(crit) {
            weapon = "*" + weapon + "*";
        }
        StringBuilder sb = new StringBuilder();
        sb.append(killer.getName()).append(" = ").append(weapon).append(" -> ").append(
                victim.getName());
        getOutput().append(sb.toString() + "\n");
    }

    @Override
    protected void parse(String lines) {
        getOutput().setText("");
        String[] strings = lines.split("\n");
        for(String s : strings) {
            if(s.contains(" killed ")) {
                notify(s);
            }
            if(s.endsWith(" suicided.")) {
                // possible team/class switch
            }
            if(s.endsWith(" connected")) {
            }
            if(s.startsWith("Dropped") && s.contains("from server")) {
            }
            // names defended/captured 'capname' for team#
            if(s.contains(" for team #")) {
                // team 0 = spectator, team 2 = red, team 3 = blu
            }
            if(s.equals("Teams have been switched.")) {
            }
        }

        getOutput().append("\nPlayers:\n");
        for(int i = 0; i < players.size(); i++) {
            getOutput().append(players.get(i).toString() + "\n");
        }

        Player me = getPlayer("TimePath");
        getOutput().append("\nAllies:\n");
        for(int i = 0; i < me.getAllies().size(); i++) {
            getOutput().append(me.getAllies().get(i) + "\n");
        }
        getOutput().append("\nEnemies:\n");
        for(int i = 0; i < me.getEnemies().size(); i++) {
            getOutput().append(me.getEnemies().get(i) + "\n");
        }
        getOutput().append("\n");
    }

}
