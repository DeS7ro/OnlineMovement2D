package server;

import entity.Player;

import java.io.*;
import java.net.Socket;

public class ClientHandler extends Thread
{
    private final Server server;

    private final ObjectOutputStream out;
    private final ObjectInputStream in;

    private Player player;

    public ClientHandler(Server server, Socket client)
    {
        this.server = server;

        try
        {
            this.out = new ObjectOutputStream(client.getOutputStream());
            this.in = new ObjectInputStream(client.getInputStream());
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }

        for (ClientHandler client1 : server.getClients())
        {
            try
            {
                if (client1.player != null)
                {
                    out.writeObject(new Player(client1.player));
                }

            }
            catch (IOException e)
            {
                throw new RuntimeException(e);
            }
        }

        try
        {
            out.writeObject(new Player("stop"));
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }


    }

    @Override
    public void run()
    {
        try
        {
            boolean stop = false;
            while (!stop)
            {
                Player input = (Player) in.readObject();

                if (input.getPosX() == -1 && input.getPosY() == -1)
                {
                    stop = true;
                }

                this.player = new Player(input);

                System.out.println(input);
                broadcast(input);

                if (stop)
                {
                    server.removeHandler(this);
                }
            }
        }
        catch (IOException | ClassNotFoundException e)
        {
            throw new RuntimeException(e);
        }


    }

    private void broadcast(Player player)
    {
        for (ClientHandler client : this.server.getClients())
        {
            if (client != this)
            {
                try
                {
                    client.out.writeObject(new Player(player));
                }
                catch (IOException e)
                {
                    throw new RuntimeException(e);
                }
            }

        }
    }




}
