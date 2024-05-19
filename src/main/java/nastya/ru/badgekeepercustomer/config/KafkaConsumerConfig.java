package nastya.ru.badgekeepercustomer.config;


import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;

@Configuration
@EnableKafka
public class KafkaConsumerConfig {
    @Bean
    public NewTopic sensorLogTopic() {
       return new NewTopic("sensor_log", 1, (short) 1);
    }

    @Bean
    public NewTopic employeeGoOutTopic() {
        return new NewTopic("employee_go_out_more_than_7_times_in_5_minutes", 1, (short) 1);
    }
}