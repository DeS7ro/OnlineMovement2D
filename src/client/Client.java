package client;

import entity.Message;
import entity.Player;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.Socket;

public class Client extends JPanel implements Runnable
{
    public static final int WIDTH = 500;
    public static final int HEIGHT = 500;

    private Thread thread;

    private final Keyboard keyboard;

    private int currentFPS;

    private final Player player;

    private UI ui;
    private int scene;
    private final int SCENE_GAME = 1;
    private final int SCENE_MESSSAGE = 2;

    //Message
    private String message;
    private String lastMessage;

    //Connessione
    private Socket client;
    private final ObjectOutputStream out;
    private final ObjectInputStream in;
    private final Players players;

    public Client()
    {
        this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        this.setFocusable(true);

        thread = new Thread(this);

        keyboard = new Keyboard();

        currentFPS = 0;

        player = new Player(JOptionPane.showInputDialog(null, "Inserisci il nome", "Inserisci", JOptionPane.QUESTION_MESSAGE));
        players = new Players();

        this.setName(player.getName());

        this.ui = null;
        this.scene = SCENE_GAME;

        this.message = this.lastMessage = "";

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

            sendObject(new Player(this.player));
        }
        catch (IOException | ClassNotFoundException e)
        {
            throw new RuntimeException(e);
        }


        Thread receiverThread = new Thread(new Receiver());
        receiverThread.start();

        addKeyListener(keyboard);
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

        //Movement Player

        boolean move = false;
        if (keyboard.upPressed)
        {
            player.setPosY(player.getPosY() - 5);
            move = true;
        }
        if (keyboard.leftPressed)
        {
            player.setPosX(player.getPosX() - 5);
            move = true;
        }
        if (keyboard.downPressed)
        {
            player.setPosY(player.getPosY() + 5);
            move = true;
        }
        if (keyboard.rightPressed)
        {
            player.setPosX(player.getPosX() + 5);
            move = true;
        }

        if (move)
        {
            sendObject(new Player(this.player));
        }

        if (message.contains("\n"))
        {
            if (!message.equals("\n"))
            {
                Message messageSend = new Message(this.player.getName(), this.message);
                sendObject(messageSend);
                lastMessage = "You: " + this.message;
                message = "";
            }

        }

    }

    private void doDrawing(Graphics g)
    {
        Graphics2D g2D = (Graphics2D) g;
        ui = new UI(g2D);

        if (scene == SCENE_GAME || scene == SCENE_MESSSAGE)
        {
            ui.setFontSize(Font.PLAIN, 15f);
            for (Player player : this.players.getPlayers())
            {
                Rectangle rectangle = player.getRect();

                g2D.setColor(Color.green);
                g2D.fill(rectangle);


                g2D.setColor(Color.black);
                g2D.drawString(player.getName(), rectangle.x, rectangle.y);
            }

            ui.setFontSize(Font.BOLD, 15f);
            g2D.setColor(Color.orange);
            g2D.fill(this.player.getRect());

            g2D.setColor(Color.black);
            g2D.drawString("YOU (" + player.getName() + ")", player.getPosX(), player.getPosY());

            ui.setFontSize(15f);
            g2D.drawString("Last Message:", 10, 20);
            g2D.drawString(lastMessage, 10, 45);

            if (scene == SCENE_MESSSAGE)
            {
                g2D.setFont(new Font("Consolas",  Font.PLAIN, 15));
                g2D.setColor(Color.black);
                ui.drawDarkened(new Rectangle(0, 500 - 30, 500, 30), 0.4f);

                ui.setFontSize(Font.PLAIN, 15f);
                g2D.drawString("Message: " + message, 10, 500 - 10);
            }

        }

        ui = null;
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

    private void sendObject(Object o)
    {
        if (o != null)
        {
            try
            {
                out.writeObject(o);
            }
            catch (IOException e)
            {
                throw new RuntimeException(e);
            }
        }
    }

    public void closing()
    {
        Player closingPlayer = new Player(this.player);
        closingPlayer.setCoordinates(-1, -1);

        sendObject(closingPlayer);
    }

    private class Receiver implements Runnable
    {
        public void run()
        {
            try
            {
                Object input;
                while ((input = in.readObject()) != null)
                {
                    if (input instanceof Player player1)
                    {
                        System.out.println(player1);

                        if (!players.set(player1))
                        {
                            players.add(player1);

                        }
                    }
                    else if (input instanceof Message message1)
                    {
                        lastMessage = message1.getName() + ": " + message1.getMessage();
                    }


                }
            }
            catch (IOException | ClassNotFoundException e)
            {
                throw new RuntimeException(e);
            }

        }
    }

    private class Keyboard implements KeyListener
    {
        private final char separator = '_';

        boolean upPressed, leftPressed, downPressed, rightPressed;

        @Override
        public void keyTyped(KeyEvent e)
        {
            if (scene == SCENE_MESSSAGE)
            {
                char letter = e.getKeyChar();

                if (letter != KeyEvent.VK_ESCAPE && letter != separator )
                {
                    if (letter == KeyEvent.VK_BACK_SPACE)
                    {
                        if (message.length() > 1)
                        {
                            message = message.substring(0, message.length() - 2) + String.valueOf(separator);

                        }
                    }
                    else if (((letter >= 32 && letter <= 126) && message.length() < 50) || letter == KeyEvent.VK_ENTER)
                    {
                        if (message.length() > 0)
                        {
                            message = message.substring(0, message.length() - 1);
                        }

                        message += letter + String.valueOf(separator);

                        if (letter == KeyEvent.VK_ENTER)
                        {
                            message = message.substring(0, message.length() - 1);
                        }

                    }
                }

            }
        }

        @Override
        public void keyPressed(KeyEvent e)
        {
            int code = e.getKeyCode();

            if (scene == SCENE_GAME)
            {
                if (code == KeyEvent.VK_W)
                {
                    upPressed = true;
                }
                if (code == KeyEvent.VK_A)
                {
                    leftPressed = true;
                }
                if (code == KeyEvent.VK_S)
                {
                    downPressed = true;
                }
                if (code == KeyEvent.VK_D)
                {
                    rightPressed = true;
                }
            }

        }

        @Override
        public void keyReleased(KeyEvent e)
        {
            int code = e.getKeyCode();

            if (scene == SCENE_GAME)
            {
                if (code == KeyEvent.VK_W)
                {
                    upPressed = false;
                }
                if (code == KeyEvent.VK_A)
                {
                    leftPressed = false;
                }
                if (code == KeyEvent.VK_S)
                {
                    downPressed = false;
                }
                if (code == KeyEvent.VK_D)
                {
                    rightPressed = false;
                }

                if (code == KeyEvent.VK_T)
                {
                    scene = SCENE_MESSSAGE;
                }
            }
            else
            {
                if (code == KeyEvent.VK_ESCAPE)
                {
                    scene = SCENE_GAME;
                }
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
