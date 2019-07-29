package pl.statisfy.socketchat.project.model;

public class Message {

    private String messageText;
    private String email;
    private String picture;
    private String id;

    public Message() {
    }

    public Message(String id, String messageText, String email, String picture) {
        this.id = id;
        this.messageText = messageText;
        this.email = email;
        this.picture = picture;
    }

    public String getId() {
        return id;
    }

    public String getMessageText() {
        return messageText;
    }

    public String getEmail() {
        return email;
    }

    public String getPicture() {
        return picture;
    }

}
