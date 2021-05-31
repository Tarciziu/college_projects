package socialnetwork.domain;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Message extends Entity<Long> {
    private final String message;
    private Message reply;
    private final Long sender;
    private final List<Long> receivers;
    private final LocalDateTime date;

    public Message(String message, Long from) {
        this.message = message;
        this.reply = null;
        this.sender = from;
        this.receivers = new ArrayList<>();
        this.date = LocalDateTime.now();
    }

    public Message(String message, Long from, LocalDateTime date) {
        this.message = message;
        this.reply = null;
        this.sender = from;
        this.receivers = new ArrayList<>();
        this.date = date;
    }

    public String getMessage() { return message; }

    public Message getReply() { return reply; }

    public Long getSender() { return sender; }

    public List<Long> getReceivers() { return receivers; }

    public LocalDateTime getDate() { return date;}

    /**
     * Add receiver.
     *
     * @param user the user to be added as receiver of the message
     */
    public void addReceiver(Long user){ receivers.add(user); }

    /**
     * Set the message to reply.
     *
     * @param message the message to reply
     */
    public void setReply(Message message){ this.reply=message;}


}
