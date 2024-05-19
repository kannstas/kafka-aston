package nastya.ru.badgekeepercustomer.api.message;

import java.util.UUID;
public class BadgeMessage {
    private UUID id;

    public BadgeMessage(UUID id) {
        this.id = id;
    }

    public BadgeMessage() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "BadgeMessage{" +
               "id=" + id +
               '}';
    }
}