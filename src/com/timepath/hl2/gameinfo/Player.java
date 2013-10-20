package com.timepath.hl2.gameinfo;

import java.util.ArrayList;
import java.util.logging.Logger;

/**
 *
 * @author TimePath
 */
public class Player {

    private static final Logger LOG = Logger.getLogger(Player.class.getName());

    /**
     * Makes players enemies
     *
     * Function:
     * Adds a new enemy.
     * Makes your enemy's enemies your allies.
     * Makes your enemy's allies your enemies.
     * Informs all your allies of your new enemy.
     *
     * @param v Victim
     * @param k Killer
     */
    static void exchangeInfo(Player v, Player k) {
        ArrayList<Player> vAllies = new ArrayList<Player>(v.getAllies());
        ArrayList<Player> vEnemies = new ArrayList<Player>(v.getEnemies());

        ArrayList<Player> kAllies = new ArrayList<Player>(k.getAllies());
        ArrayList<Player> kEnemies = new ArrayList<Player>(k.getEnemies());

        if(k.getAllies().contains(v) || v.getAllies().contains(k)) { // Traitor
            for(int i = 0; i < k.getAllies().size(); i++) {
                k.getAllies().get(i).getAllies().remove(k);

                k.getAllies().get(i).getEnemies().remove(k);
                k.getAllies().get(i).getEnemies().add(k);
            }
            for(int i = 0; i < v.getAllies().size(); i++) {
                v.getAllies().get(i).getAllies().remove(k);

                v.getAllies().get(i).getEnemies().remove(k);
                v.getAllies().get(i).getEnemies().add(k);
            }

            for(int i = 0; i < k.getEnemies().size(); i++) {
                k.getEnemies().get(i).getEnemies().remove(k);

                k.getEnemies().get(i).getAllies().remove(k);
                k.getEnemies().get(i).getAllies().add(k);
            }
            for(int i = 0; i < v.getEnemies().size(); i++) {
                v.getEnemies().get(i).getEnemies().remove(k);

                v.getEnemies().get(i).getAllies().remove(k);
                v.getEnemies().get(i).getAllies().add(k);
            }

            k.getAllies().clear();
            k.getEnemies().clear();

            k.getAllies().addAll(kEnemies);
//            k.allies.addAll(vEnemies);
            k.getEnemies().addAll(kAllies);
//            k.enemies.addAll(vAllies);
        } else {
            v.addEnemy(k);
            k.addEnemy(v);

            v.addAllies(kEnemies);
            v.addEnemies(kAllies);

            k.addAllies(vEnemies);
            k.addEnemies(vAllies);
        }
    }

    private final ArrayList<Player> allies = new ArrayList<Player>();

    private final ArrayList<Player> enemies = new ArrayList<Player>();

    private String name;

    Player(String name) {
        this.name = name;
    }

    public void addAllies(ArrayList<Player> list) {
        for(int i = 0; i < list.size(); i++) {
            addAllyR(list.get(i));
            for(int j = 0; j < getEnemies().size(); j++) {
                getEnemies().get(j).addEnemyR(list.get(i));
            }
        }
    }
    public void addAlly(Player other) {
        if(other == this) {
            return;
        }
        if(!allies.contains(other)) {
            getAllies().add(other);
        }
        if(!other.hasAlly(this)) {
            other.getAllies().add(this);
        }
    }

    public void addAllyR(Player other) {
        addAlly(other);
        for(int j = 0; j < getAllies().size(); j++) {
            getAllies().get(j).addAlly(other);
        }
    }

    public void addEnemies(ArrayList<Player> list) {
        for(int i = 0; i < list.size(); i++) {
            addEnemyR(list.get(i));
            for(int j = 0; j < getAllies().size(); j++) {
                getAllies().get(j).addEnemyR(list.get(i));
            }
        }
    }

    public void addEnemy(Player other) {
        if(other == this) {
            return;
        }
        if(!enemies.contains(other)) {
            getEnemies().add(other);
        }
        if(!other.hasEnemy(this)) {
            other.getEnemies().add(this);
        }
    }

    public void addEnemyR(Player other) {
        addEnemy(other);
        for(int j = 0; j < getAllies().size(); j++) {
            getAllies().get(j).addEnemy(other);
        }
    }

    /**
     * @return the allies
     */public ArrayList<Player> getAllies() {
         return allies;
     }

    /**
     * @return the enemies
     */
    public ArrayList<Player> getEnemies() {
        return enemies;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    //<editor-fold defaultstate="collapsed" desc="toString()">

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getName());
        
        sb.append(" (e:{");
        ArrayList<Player> myEnemies = getEnemies();
        for(int i = 0; i < getEnemies().size(); i++) {
            sb.append(myEnemies.get(i).getName());
            if(i + 1 < getEnemies().size()) {
                sb.append(", ");
            }
        }
        sb.append("}, a:{");
        ArrayList<Player> myAllies = getAllies();
        for(int i = 0; i < getAllies().size(); i++) {
            sb.append(myAllies.get(i).getName());
            if(i + 1 < getAllies().size()) {
                sb.append(", ");
            }
        }
        sb.append("})");
        
        return sb.toString();
    }
    //</editor-fold>
    
    private boolean hasAlly(Player player) {
        return allies.contains(player);
    }

    private boolean hasEnemy(Player player) {
        return enemies.contains(player);
    }

}
