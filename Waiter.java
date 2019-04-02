import javax.jms.JMSException;
import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.TextMessage;
import org.apache.activemq.ActiveMQConnectionFactory;
import javax.jms.MessageListener;
import javax.jms.*;

public class Waiter implements BrokerConfig, Runnable {

	// private ActiveMQConnectionFactory factory;
	private Connection connection;
	private Session session;
	// private Destination producer;
	private Fork f0;
	private Fork f1;
	private Fork f2;
	private Fork f3;
	private Fork f4;
	private boolean exit = false;
	private Destination recievingQueue;
	private Destination alphaQueue;
	private Destination betaQueue;
	private Destination charlieQueue;
	private Destination deltaQueue;
	private Destination echoQueue;
	private MessageProducer alphaProducer;
	private MessageProducer betaProducer;
	private MessageProducer charlieProducer;
	private MessageProducer deltaProducer;
	private MessageProducer echoProducer;
	private MessageConsumer consumer;

	public Waiter() {
		ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(
				DEFAULT_BROKER);
		exit = false;
		try {
			connection = factory.createConnection();
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			recievingQueue = session.createQueue(REQUEST_QUEUE); // message from the philosophers queue
			alphaQueue = session.createQueue("hw4-queue-Alpha");
			betaQueue = session.createQueue("hw4-queue-Beta");
			charlieQueue = session.createQueue("hw4-queue-Charlie");
			deltaQueue = session.createQueue("hw4-queue-Delta");
			echoQueue = session.createQueue("hw4-queue-Echo");

			alphaProducer = session.createProducer(alphaQueue);
			betaProducer = session.createProducer(betaQueue);
			charlieProducer = session.createProducer(charlieQueue);
			deltaProducer = session.createProducer(deltaQueue);
			echoProducer = session.createProducer(echoQueue);
			consumer = session.createConsumer(recievingQueue); //queue with messages rom the philosophers
			connection.start();
		} catch (JMSException jm) {
			jm.printStackTrace();
		}
		f0 = new Fork();
		f1 = new Fork();
		f2 = new Fork();
		f3 = new Fork();
		f4 = new Fork();
	}
	public void setExitCondition() {

		exit = true;
	}
	public void run() {
		try {
			while (!exit) {
				Message message = consumer.receive(100);
				if (message instanceof TextMessage) {
					TextMessage tm = (TextMessage) message;
					String msg = tm.getText();
					String[] msgArgs = msg.split(","); // I will split this by a
														// comma
					String name = msgArgs[0];
					String action = msgArgs[1];
					if (name.equals("Alpha")) {
						if (action.equals("pickup")) {
							if (f0.getUseStatus() == false
									&& f4.getUseStatus() == false) {
								f0.grab();
								f4.grab();
								String actionMessage = "Alpha,true";
								TextMessage textMessage = session
										.createTextMessage(actionMessage);
								alphaProducer.send(textMessage);
							} else {
								String actionMessage = "Alpha,false";
								TextMessage textMessage = session
										.createTextMessage(actionMessage);
								alphaProducer.send(textMessage);
							}
						} else if (action.equals("setdown")) {
							f0.drop();
							f4.drop();
						}
					}

					else if (name.equals("Beta")) {
						if (action.equals("pickup")) {
							if (f0.getUseStatus() == false
									&& f1.getUseStatus() == false) {
								f0.grab();
								f1.grab();
								String actionMessage = "Beta,true";

								TextMessage textMessage = session
										.createTextMessage(actionMessage);
								betaProducer.send(textMessage);
							} else {
								String actionMessage = "Beta,false";
								TextMessage textMessage = session
										.createTextMessage(actionMessage);
								betaProducer.send(textMessage);
							}
						}
						if (action.equals("setdown")) {
							System.out.println("we finally dropped some forks");
							f0.drop();
							f1.drop();
						}
					} else if (name.equals("Charlie")) {
						if (action.equals("pickup")) {
							if (f1.getUseStatus() == false
									&& f2.getUseStatus() == false) {
								f1.grab();
								f2.grab();
								String actionMessage = "Charlie,true";
								TextMessage textMessage = session
										.createTextMessage(actionMessage);
								charlieProducer.send(textMessage);
							} else {
								String actionMessage = "Charlie,false";
								TextMessage textMessage = session
										.createTextMessage(actionMessage);
								charlieProducer.send(textMessage);
							}
						} else {
							f1.drop();
							f2.drop();
						}
					} else if (name.equals("Delta")) {
						if (action.equals("pickup")) {
							if (f2.getUseStatus() == false
									&& f3.getUseStatus() == false) {
								f2.grab();
								f3.grab();
								String actionMessage = "Delta,true";
								TextMessage textMessage = session
										.createTextMessage(actionMessage);
								deltaProducer.send(textMessage);
							} else {
								String actionMessage = "Delta,false";
								TextMessage textMessage = session
										.createTextMessage(actionMessage);
								deltaProducer.send(textMessage);
							}
						} else {
							f2.drop();
							f3.drop();
						}
					} else if (name.equals("Echo")) {
						if (action.equals("pickup")) {
							if (f3.getUseStatus() == false
									&& f4.getUseStatus() == false) {
								f3.grab();
								f4.grab();
								String actionMessage = "Echo,true";
								TextMessage textMessage = session
										.createTextMessage(actionMessage);
								echoProducer.send(textMessage);

							} else {
								String actionMessage = "Echo,false";
								TextMessage textMessage = session
										.createTextMessage(actionMessage);
								echoProducer.send(textMessage);
							}
						} else {
							f3.drop();
							f4.drop();
						}
					}

				} else {
					System.out.println("Unexpected message type in waiter");
				}
			}
			if (exit) {
				System.out.println("The waiter exited");
				connection.close();
			}

		} catch (JMSException e) {
			e.printStackTrace();
		}

	}
}
