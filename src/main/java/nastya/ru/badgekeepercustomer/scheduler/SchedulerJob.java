package nastya.ru.badgekeepercustomer.scheduler;

import nastya.ru.badgekeepercustomer.api.message.AllBadgeMessages;
import nastya.ru.badgekeepercustomer.api.message.BadgeMessage;
import nastya.ru.badgekeepercustomer.entity.SensorLog;
import nastya.ru.badgekeepercustomer.enumeration.ActionType;
import nastya.ru.badgekeepercustomer.service.SensorLogService;
import org.springframework.http.*;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Random;
import java.util.UUID;
import java.util.logging.Logger;

@EnableScheduling
@Component
public class SchedulerJob {

    private KafkaTemplate<String, Object> kafkaTemplate;
    private SensorLogService sensorLogService;
    private ResponseEntity<AllBadgeMessages> response;
    private boolean badgesReceived = false;
    Logger logger = Logger.getLogger(SensorLogService.class.getName());

    public SchedulerJob(KafkaTemplate<String, Object> kafkaTemplate, SensorLogService sensorLogService) {
        this.kafkaTemplate = kafkaTemplate;
        this.sensorLogService = sensorLogService;
    }

    @Scheduled(fixedRate = 15000)
    public void getInfoAboutBadges() {

        badgesReceived = false;

        String url = "http://app:8080/scanner";
        logger.info("start sending a request by url:%s".formatted(url));

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<?> requestEntity = new HttpEntity<>(requestHeaders);

        response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                requestEntity,
                AllBadgeMessages.class
        );

        if (response.getBody() != null && !(response.getBody().getBadgeMessages().isEmpty())) {
            badgesReceived = true;

            logger.info("the response has been received:%s".formatted(
                    String.valueOf(response.getBody().getBadgeMessages()))
            );
        }
        logger.info("the response is empty:");
    }

    @Scheduled(fixedRate = 5000)
    public void processBadgeMessages() {
        if (badgesReceived) {
            logger.info("start generating sensor logs");
            if (response.getBody() != null && !(response.getBody().getBadgeMessages().isEmpty())) {
                for (BadgeMessage badgeMessage : response.getBody().getBadgeMessages()) {
                    SensorLog sensorLog = generateSensorLog(badgeMessage);
                    logger.info("send message to the topic %s: start : message=%s".formatted("sensor_log", String.valueOf(sensorLog)));
                    kafkaTemplate.send("sensor_log", String.valueOf(sensorLog));
                    logger.info("message has been sent");
                }
            }
        } else {
            logger.info("No badge messages available yet.");
        }
    }

    @Scheduled(fixedRate = 30000)
    public void readingMessagesFromDbAndSendingToKafka() {
        if (response.getBody() != null && !(response.getBody().getBadgeMessages().isEmpty())) {
            logger.info("start reading the db");
            sensorLogService.readFromDb();
            logger.info("finish reading the db");
        }
    }

    private SensorLog generateSensorLog(BadgeMessage badgeMessage) {
        SensorLog sensorLog = new SensorLog();
        sensorLog.setId(UUID.randomUUID());
        sensorLog.setBadgeId(badgeMessage.getId());
        sensorLog.setActionType(generateActionType());
        sensorLog.setTimestamp(Timestamp.from(Instant.now()));

        sensorLogService.saveForScheduler(sensorLog);
        return sensorLog;
    }

    private ActionType generateActionType() {
        return ActionType.values()[new Random().nextInt(ActionType.values().length)];
    }
}