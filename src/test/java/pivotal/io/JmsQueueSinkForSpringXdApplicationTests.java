package pivotal.io;

import static org.junit.Assert.assertEquals;

import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.TextMessage;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(locations = { "jmsQueueTest.xml" }, classes = JmsQueueSinkForSpringXdApplication.class)
public class JmsQueueSinkForSpringXdApplicationTests {

	private static int RECEIVE_TIMEOUT = 500;

	private static final Logger logger = LoggerFactory
			.getLogger(JmsQueueSinkForSpringXdApplicationTests.class);

	@Autowired
	ConnectionFactory connectionFactory;

	@Autowired
	@Qualifier("destinationQueue")
	Destination source;

	@Autowired
	@Qualifier("input")
	DirectChannel input;

	@Test
	public void send5MessagesTest() throws JMSException {
		boolean noDelay = true;

		JmsTemplate jmsTemplate = new JmsTemplate(connectionFactory);
		jmsTemplate.setReceiveTimeout(RECEIVE_TIMEOUT);

		// clear up the queue before sending
		while (noDelay) {
			long start = System.currentTimeMillis();
			javax.jms.Message message = jmsTemplate.receive(source);
			if (System.currentTimeMillis() - start > 400) {
				noDelay = false;
			} else {
				logger.info("Recevied when clearing --"
						+ ((TextMessage) message).getText());
			}
		}

		// send five messages to channel input
		MessageCreator messageCreator = new MessageCreator();
		for (int i = 0; i < 5; i++) {
			input.send(messageCreator.createMessage());
		}

		// now receive and count
		noDelay = true;
		int counter=0;
		while (noDelay) {
			long start = System.currentTimeMillis();
			javax.jms.Message message = jmsTemplate.receive(source);
			if (System.currentTimeMillis() - start > 400) {
				noDelay = false;
			} else {
				assertEquals("hello queue world" + counter,((TextMessage) message).getText());	
				counter++;
			}
		}
		assertEquals(5,counter);
	}

	public class MessageCreator {
		private int counter = 0;

		public Message<String> createMessage() {
			return new GenericMessage<String>("hello queue world" + counter++);
		}
	}
}
