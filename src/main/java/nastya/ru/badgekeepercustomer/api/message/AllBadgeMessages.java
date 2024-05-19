package nastya.ru.badgekeepercustomer.api.message;

import org.springframework.stereotype.Component;

import java.util.List;
@Component
public class AllBadgeMessages {
    private List<BadgeMessage> badgeMessages;

    public AllBadgeMessages(List<BadgeMessage> badgeMessages) {
        this.badgeMessages = badgeMessages;
    }

    public AllBadgeMessages() {
    }

    public List<BadgeMessage> getBadgeMessages() {
        return badgeMessages;
    }

    public void setBadgeMessages(List<BadgeMessage> badgeMessages) {
        this.badgeMessages = badgeMessages;
    }

    @Override
    public String toString() {
        return "AllBadgeMessages{" +
               "badgeMessages=" + badgeMessages +
               '}';
    }
}