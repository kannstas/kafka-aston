package nastya.ru.badgekeepercustomer.repository;

import nastya.ru.badgekeepercustomer.entity.SensorLog;
import nastya.ru.badgekeepercustomer.enumeration.ActionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@Repository
public interface SensorLogsRepository extends JpaRepository<SensorLog, UUID> {
   List<SensorLog> findSensorLogsByActionTypeIsAndTimestampGreaterThanEqual(ActionType actionType, Timestamp timestamp);
}