package client;

import entity.Message;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Messages
{
    private final LinkedList<Message> messages;
    private final int max;

    public Messages(int max)
    {
        this.messages = new LinkedList<>();
        this.max = max;
    }

    public void add(Message message)
    {
        if (message != null)
        {
            if (max > messages.size())
            {
                messages.addLast(message);
                return;
            }

            this.messages.removeFirst();
            this.messages.addLast(message);
        }
    }

    public LinkedList<Message> getList()
    {
        return this.messages;
    }

}
