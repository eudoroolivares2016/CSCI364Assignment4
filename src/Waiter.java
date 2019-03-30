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

public class Waiter implements BrokerConfig, MessageListener {

	private ActiveMQConnectionFactory factory;
	private Connection connection;
	private Session session;
	private Destination producer;
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
		// factory = acf;
		ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(
				DEFAULT_BROKER);
		exit = false;
		try {
			connection = factory.createConnection();

			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

			recievingQueue = session.createQueue(REQUEST_QUEUE); // recieving
																	// queue

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

			consumer = session.createConsumer(recievingQueue); // recieing queue
																// for the
																// philosopher

			consumer.setMessageListener(this);

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

	public void onMessage(Message message) {

		try {

			while (!exit) {

				// message = consumer.receive(); //dont need this that is what
				// te onMessage method is for
				if (message instanceof TextMessage) {
					// System.out.println("Got an instance of text message!!!!!!");
					// //this works
					TextMessage tm = (TextMessage) message;
					String msg = tm.getText();
					System.out.println("This was the message I the waiter got "
							+ msg);
					String[] msgArgs = msg.split(","); // I will split this by a
														// comma
					String name = msgArgs[0];
					String action = msgArgs[1]; // either pickup action or set
												// down action
					// System.out.println("this is the name of the philosoper that sent me the message "
					// + name);

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
								// tell the philosopher to eat

							} else {
								String actionMessage = "Alpha,false";

								/** USE THESE FOR TESTING THE OTHER METHODS */
								TextMessage textMessage = session
										.createTextMessage(actionMessage);
								alphaProducer.send(textMessage);
								// send message to think
							}
						} else if (action.equals("setdown")) {
							f0.drop();
							f1.drop();
						}
						// read the instruction
						// send message back to alpha
						// complete the action of changing the status of the
						// Forks

					}

					else if (name.equals("Beta")) {

						// System.out.println("getting messages from beta");
						if (action.equals("pickup")) {
							if (f0.getUseStatus() == false
									&& f1.getUseStatus() == false) {
								f0.grab();
								f1.grab();
								String actionMessage = "Beta,true";

								TextMessage textMessage = session
										.createTextMessage(actionMessage);
								betaProducer.send(textMessage);
								// tell the philosopher to eat

							} else {
								String actionMessage = "Beta,false";

								/** USE THESE FOR TESTING THE OTHER METHODS */
								TextMessage textMessage = session
										.createTextMessage(actionMessage);
								betaProducer.send(textMessage);
								// send message to think
							}
						} else if (action.equals("setdown")) {
							f0.drop();
							f1.drop();
						}
						// read the instruction
						// send message back to alpha
						// complete the action of changing the status of the
						// Forks

					}

					else if (name.equals("Charlie")) {

						if (action.equals("pickup")) {
							if (f1.getUseStatus() == false
									&& f2.getUseStatus() == false) {
								f1.grab();
								f2.grab();
								String actionMessage = "Charlie,true";

								TextMessage textMessage = session
										.createTextMessage(actionMessage);
								charlieProducer.send(textMessage);
								// tell the philosopher to eat

							} else {
								String actionMessage = "Charlie,false";

								/** USE THESE FOR TESTING THE OTHER METHODS */
								TextMessage textMessage = session
										.createTextMessage(actionMessage);
								charlieProducer.send(textMessage);
								// send message to think
							}
						} else if (action.equals("setdown")) {
							f1.drop();
							f2.drop();
						}
						// read the instruction
						// send message back to alpha
						// complete the action of changing the status of the
						// Forks

					}

					else if (name.equals("Delta")) {
						if (action.equals("pickup")) {
							if (f2.getUseStatus() == false
									&& f3.getUseStatus() == false) {
								f2.grab();
								f3.grab();
								String actionMessage = "Delta,true";

								TextMessage textMessage = session
										.createTextMessage(actionMessage);
								deltaProducer.send(textMessage);
								// tell the philosopher to eat

							} else {
								String actionMessage = "Delta,false";

								/** USE THESE FOR TESTING THE OTHER METHODS */
								TextMessage textMessage = session
										.createTextMessage(actionMessage);
								deltaProducer.send(textMessage);
								// send message to think
							}
						} else if (action.equals("setdown")) {
							f2.drop();
							f3.drop();
						}
						// read the instruction
						// send message back to alpha
						// complete the action of changing the status of the
						// Forks

					}

					else if (name.equals("Echo")) {
						if (action.equals("pickup")) {
							if (f3.getUseStatus() == false
									&& f4.getUseStatus() == false) {
								f3.grab();
								f4.grab();
								String actionMessage = "Echo,true";

								TextMessage textMessage = session
										.createTextMessage(actionMessage);
								echoProducer.send(textMessage);
								// tell the philosopher to eat

							} else {
								String actionMessage = "Echo,false";

								/** USE THESE FOR TESTING THE OTHER METHODS */
								TextMessage textMessage = session
										.createTextMessage(actionMessage);
								echoProducer.send(textMessage);
								// send message to think
							}
						} else if (action.equals("setdown")) {
							f3.drop();
							f4.drop();
						}
						// read the instruction
						// send message back to alpha
						// complete the action of changing the status of the
						// Forks
					}

				} else {
					System.out.println("Unexpected message type");
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
