package nastya.ru.badgekeepercustomer.entity;

import jakarta.persistence.*;
import nastya.ru.badgekeepercustomer.enumeration.ActionType;

import java.sql.Timestamp;
import java.util.UUID;

@Entity
@Table(name = "sensor_logs")
public class SensorLog {
    @Id
    @Column(name="id")
    private UUID id;
    @Column(name="badge_id")
    private UUID badgeId;

    @Column(name="action_type")
    @Enumerated(EnumType.STRING)
    private ActionType actionType;

    @Column(name = "timestamp")
    @Temporal(TemporalType.TIMESTAMP)
    private Timestamp timestamp;

    public SensorLog() {
    }

    public SensorLog(UUID id, UUID badgeId, ActionType actionType, Timestamp timestamp) {
        this.id = id;
        this.badgeId = badgeId;
        this.actionType = actionType;
        this.timestamp = timestamp;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getBadgeId() {
        return badgeId;
    }

    public void setBadgeId(UUID badgeId) {
        this.badgeId = badgeId;
    }

    public ActionType getActionType() {
        return actionType;
    }

    public void setActionType(ActionType actionType) {
        this.actionType = actionType;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "SensorLog{" +
               "id=" + id +
               ", badgeId=" + badgeId +
               ", actionType=" + actionType +
               ", timestamp=" + timestamp +
               '}';
    }
}