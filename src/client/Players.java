package client;

import entity.Player;

import java.util.ArrayList;

public class Players
{
    private final ArrayList<Player> players;

    public Players()
    {
        this.players = new ArrayList<>();
    }

    public void add(Player player)
    {
        if (player.getName().equals("null")) return;
        players.add(player);
    }

    public int contains(Player player)
    {
        for (int i = 0; i < players.size(); i++)
        {
            if (players.get(i).equals(player))
            {
                return i;
            }
        }

        return -1;
    }

    public boolean set(Player player)
    {
        int index = contains(player);
        if (index < 0) return false;
        players.set(index, player);
        return true;
    }

    public void clean()
    {
        this.players.removeIf(player -> player.getPosX() == -1 && player.getPosY() == -1);
    }

    public ArrayList<Player> getPlayers()
    {
        return players;
    }
}
