package entity;

import java.io.Serializable;

public class Message implements Serializable
{
    private final String name;
    private final String message;

    public Message(String name, String message)
    {
        this.name = name;
        this.message = message;
    }

    public Message(Message message)
    {
        if (message != null)
        {
            this.name = message.name;
            this.message = message.message;
            return;
        }

        this.name = null;
        this.message = null;
    }

    public String getName()
    {
        return name;
    }

    public String getMessage()
    {
        return message;
    }

    @Override
    public String toString()
    {
        return "Message{" +
                "name='" + name + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
