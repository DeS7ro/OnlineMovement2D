package entity;

import java.awt.*;
import java.io.Serializable;
import java.util.Objects;

public class Player implements Serializable
{
    private final String name;

    private int posX, posY;
    private int speed;
    private final int boxSize;

    public Player(String name)
    {
        this.name = name;
        this.posX = this.posY = 0;
        this.speed = 5;
        this.boxSize = 20;
    }

    public Player(Player player)
    {
        this(player.name);
        setCoordinates(player.posX, player.posY);
    }

    public String getName()
    {
        return name;
    }

    public int getPosX()
    {
        return posX;
    }

    public void setPosX(int posX)
    {
        this.posX = posX;
    }

    public int getPosY()
    {
        return posY;
    }

    public void setPosY(int posY)
    {
        this.posY = posY;
    }

    public void setCoordinates(int posX, int posY)
    {
        this.posX = posX;
        this.posY = posY;
    }

    public Rectangle getRect()
    {
        return new Rectangle(posX, posY, boxSize, boxSize);
    }

    public int getSpeed()
    {
        return speed;
    }

    public void setSpeed(int speed)
    {
        this.speed = speed;
    }

    public int getBoxSize()
    {
        return boxSize;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return Objects.equals(name, player.name);
    }

    @Override
    public String toString()
    {
        return "Player{" +
                "name='" + name + '\'' +
                ", posX=" + posX +
                ", posY=" + posY +
                '}';
    }
}
