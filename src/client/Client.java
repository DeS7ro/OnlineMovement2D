package client;

import entity.Player;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.Socket;

public class Client extends JPanel implements Runnable, KeyListener
{
    public static final int WIDTH = 500;
    public static final int HEIGHT = 500;

    private Thread thread;

    private int currentFPS;

    private final Player player;

    //Connessione
    private Socket client;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private Players players;

    public Client()
    {
        this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        this.setFocusable(true);

        thread = new Thread(this);

        currentFPS = 0;

        player = new Player(JOptionPane.showInputDialog(null, "Inserisci il nome", "Inserisci", JOptionPane.QUESTION_MESSAGE));
        players = new Players();

        this.setName(player.getName());

        try
        {
            client = new Socket("localhost", 5678);
            out = new ObjectOutputStream(client.getOutputStream());
            in = new ObjectInputStream(client.getInputStream());

            Player line;
            while (!(line = (Player) in.readObject()).equals(new Player("stop")))
            {
                this.players.add(line);
            }

            out.writeObject(new Player(this.player));
        }
        catch (IOException | ClassNotFoundException e)
        {
            throw new RuntimeException(e);
        }


        Thread receiverThread = new Thread(new Receiver());
        receiverThread.start();

        addKeyListener(this);
        setFocusable(true);
        requestFocus();
    }

    public void startThread()
    {
        this.thread.start();
    }

    private void update()
    {
        players.clean();
    }

    private void doDrawing(Graphics g)
    {
        Graphics2D g2D = (Graphics2D) g;


        for (Player player : this.players.getPlayers())
        {
            Rectangle rectangle = player.getRect();

            g2D.setColor(Color.green);
            g2D.fill(rectangle);


            g2D.setColor(Color.black);
            g2D.drawString(player.getName(), rectangle.x, rectangle.y);
        }

        g2D.setColor(Color.orange);
        g2D.fill(this.player.getRect());

        g2D.setColor(Color.black);
        g2D.drawString("TU (" + player.getName() + ")", player.getPosX(), player.getPosY());

        g2D.dispose();
    }

    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        doDrawing(g);
    }

    @Override
    public void run()
    {
        double drawInterval =  (double) 1000000000 / 60;  //60 = MAX FPS
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;
        long timer = 0;
        int drawCount = 0;

        while (thread != null)
        {
            currentTime = System. nanoTime () ;
            delta += (currentTime - lastTime) / drawInterval;
            timer += (currentTime - lastTime);
            lastTime = currentTime;

            if (delta >= 1)
            {
                update();
                repaint();
                delta--;
                drawCount++;
            }

            if (timer >= 1000000000)
            {
                currentFPS = drawCount;
                drawCount = 0;
                timer = 0;
            }

        }
    }

    public void closing()
    {
        Player closingPlayer = new Player(this.player);
        closingPlayer.setCoordinates(-1, -1);
        try
        {
            out.writeObject(closingPlayer);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void keyTyped(KeyEvent e)
    {

    }

    @Override
    public void keyPressed(KeyEvent e)
    {
        int code = e.getKeyCode();

        if (code == KeyEvent.VK_W)
        {
            player.setPosY(player.getPosY() - 5);
        }
        else if (code == KeyEvent.VK_A)
        {
            player.setPosX(player.getPosX() - 5);
        }
        else if (code == KeyEvent.VK_S)
        {
            player.setPosY(player.getPosY() + 5);
        }
        else if (code == KeyEvent.VK_D)
        {
            player.setPosX(player.getPosX() + 5);
        }

        try
        {
            out.writeObject(new Player(this.player));
        }
        catch (IOException ex)
        {
            throw new RuntimeException(ex);
        }

    }

    @Override
    public void keyReleased(KeyEvent e)
    {

    }

    private class Receiver implements Runnable
    {
        public void run()
        {
            try
            {
                Player input;
                while ((input = (Player) in.readObject()) != null)
                {
                    System.out.println(input);

                    if (!players.set(input))
                    {
                        players.add(input);

                    }

                }
            }
            catch (IOException | ClassNotFoundException e)
            {
                throw new RuntimeException(e);
            }

        }
    }
}

class Main
{
    private final JFrame frame;
    private final Client panel;

    public Main()
    {
        panel = new Client();
        frame = new JFrame();

        frame.addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e)
            {
                panel.closing();
            }
        });

        frame.setTitle("Client (" + panel.getName() + ")");
        frame.add(panel);
        frame.pack();
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public static void main(String[] args)
    {
        Main main = new Main();
        main.frame.setVisible(true);
        main.panel.startThread();
    }
}
