package pivotal.io;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

@Configuration
@ImportResource("jmsQueue/config/jmsQueue.xml")
@EnableAutoConfiguration
public class JmsQueueSinkForSpringXdApplication {
   
}
