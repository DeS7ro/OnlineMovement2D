package entity;

import java.io.Serializable;

public class Message implements Serializable
{
    private final String name;
    private final String message;

    public Message(String name, String message)
    {
        this.name = name;
        this.message = message.split("\n")[0];
    }

    public Message(Message message)
    {
        this(message.name, message.message);
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
