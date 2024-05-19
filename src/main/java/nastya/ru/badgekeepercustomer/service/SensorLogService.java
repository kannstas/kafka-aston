package nastya.ru.badgekeepercustomer.service;

import nastya.ru.badgekeepercustomer.entity.SensorLog;
import nastya.ru.badgekeepercustomer.enumeration.ActionType;
import nastya.ru.badgekeepercustomer.repository.SensorLogsRepository;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

@Service
public class SensorLogService {
    private final SensorLogsRepository sensorLogsRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    Logger logger = Logger.getLogger(SensorLogService.class.getName());
    public SensorLogService(SensorLogsRepository sensorLogsRepository, KafkaTemplate<String, String> kafkaTemplate) {
        this.sensorLogsRepository = sensorLogsRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    public void saveForScheduler(SensorLog sensorLog) {
        logger.info("save scheduler log: start: sensorLog = %s".formatted(sensorLog));

        sensorLogsRepository.save(sensorLog);
    }

    public void readFromDb() {
        logger.info("read from db: start");

        Timestamp currentTimeMinus5Minutes = new Timestamp(System.currentTimeMillis() - 180000);

        List<SensorLog> logList = sensorLogsRepository.findSensorLogsByActionTypeIsAndTimestampGreaterThanEqual(ActionType.GO_OUT,currentTimeMinus5Minutes);

        Map<UUID, Integer> countMap = new HashMap<>();

        for (SensorLog sensorLog : logList) {
            countMap.put(sensorLog.getBadgeId(), countMap.getOrDefault(sensorLog.getBadgeId(), 0) + 1);
            if(countMap.get(sensorLog.getBadgeId()) >= 7) {
                logger.info("send message to the topic: start : message=%s".formatted("работник с badge_id=%s 7 раз покинул здание за 5 минут".formatted(sensorLog.getBadgeId())));
                kafkaTemplate.send("employee_go_out_more_than_7_times_in_5_minutes", String.valueOf(sensorLog.getBadgeId()), "работник с badge_id=%s 7 раз покинул здание за 5 минут".formatted(sensorLog.getBadgeId()));
                logger.info("message has been sent");
            }
        }
    }
}