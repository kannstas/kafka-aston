package nastya.ru.badgekeepercustomer.service;

import nastya.ru.badgekeepercustomer.entity.SensorLog;
import nastya.ru.badgekeepercustomer.enumeration.ActionType;
import nastya.ru.badgekeepercustomer.repository.SensorLogsRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SensorLogServiceTest {
    @Mock
    private SensorLogsRepository sensorLogsRepository;

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @InjectMocks
    private SensorLogService sensorLogService;

    @Test
    void readFromDb() {

        List<SensorLog> testList = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            testList.add(new SensorLog(
                    UUID.randomUUID(),
                    UUID.randomUUID(),
                    ActionType.GO_OUT,
                    Timestamp.from(Instant.now())
            ));
        }

        when(sensorLogsRepository.findSensorLogsByActionTypeIsAndTimestampGreaterThanEqual(eq(ActionType.GO_OUT), any(Timestamp.class)))
                .thenReturn(testList);


        Map<UUID, Integer> countMap = new HashMap<>();

        for (SensorLog sensorLog : testList) {
            countMap.put(sensorLog.getBadgeId(), countMap.getOrDefault(sensorLog.getBadgeId(), 0) + 1);
            if (countMap.get(sensorLog.getBadgeId()) >= 7) {
                verify(kafkaTemplate.send(
                        eq("employee_go_out_more_than_7_times_in_5_minutes"),
                        eq(String.valueOf(sensorLog.getBadgeId())),
                        eq("работник с badge_id=%s 7 раз покинул здание за 5 минут".formatted(sensorLog.getBadgeId())))
                );
            }
        }

        sensorLogService.readFromDb();

        verify(sensorLogsRepository, atLeastOnce()).findSensorLogsByActionTypeIsAndTimestampGreaterThanEqual(eq(ActionType.GO_OUT), any(Timestamp.class));

    }
}