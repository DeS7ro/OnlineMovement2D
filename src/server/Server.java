package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server
{
    private final ArrayList<ClientHandler> clients;

    public Server()
    {
        this.clients = new ArrayList<>();

        try(ServerSocket serverSocket = new ServerSocket(5678))
        {
            while (true)
            {
                Socket client = serverSocket.accept();
                System.out.println("Client " + client.getInetAddress().getHostAddress() + " connected");
                ClientHandler clientHandler = new ClientHandler(this, client);
                this.clients.add(clientHandler);
                clientHandler.start();
            }

        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args)
    {
        Server server = new Server();
    }

    public void removeHandler(ClientHandler clientHandler)
    {
        this.clients.remove(clientHandler);
    }

    public ArrayList<ClientHandler> getClients()
    {
        return clients;
    }
}
