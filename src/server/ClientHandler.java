package server;

import entity.Message;
import entity.Player;

import java.io.*;
import java.net.Socket;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

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
                Object input = in.readObject();
                Player player1;

                if (input instanceof Player)
                {
                    player1 = (Player) input;

                    if (player1.getPosX() == -1 && player1.getPosY() == -1)
                    {
                        System.out.println(getTime() + "\033[0;31mPlayer '" + player1.getName() + "' it disconnected.\033[0m");
                        stop = true;
                    }

                    this.player = new Player(player1);

                    broadcast(player1);

                    if (stop)
                    {
                        server.removeHandler(this);
                    }
                }
                else if (input instanceof Message message)
                {
                    System.out.println(getTime() + "\033[0;32m" + message.getName() + "\033[0m: " + message.getMessage() );
                    broadcast(message);
                }


            }
        }
        catch (IOException | ClassNotFoundException e)
        {
            throw new RuntimeException(e);
        }


    }

    private void broadcast(Object obj)
    {
        for (ClientHandler client : this.server.getClients())
        {
            if (client != this)
            {
                try
                {
                    client.out.writeObject(obj);
                }
                catch (IOException e)
                {
                    throw new RuntimeException(e);
                }
            }

        }
    }

    private String getTime()
    {
        return "[\033[0;34m" + LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + "\033[0m] > ";
    }


}
