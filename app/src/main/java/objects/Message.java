package objects;

import java.io.Serializable;

/**
 * This message class could be either a comment or a review. the two are regourped as they
 * the same parameters
 * */
public class Message implements Serializable{
    public int id,post_id;
    public User source;
    public String message,date;

    public Message(User source, String message, String date, int id) {
        this.source = source;
        this.message = message;
        this.date = date;
        this.id = id;
    }
    public Message(User source, String message, String date, int id, int post_id) {
        this.source = source;
        this.message = message;
        this.date = date;
        this.id = id;
        this.post_id = post_id;
    }
    public User getSource() {
        return source;
    }

    public void setSource(User source) {
        this.source = source;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
