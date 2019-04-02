/*SPECS: Reported statistics
 total loop iterations
 total number of times a philosopher eats
 accumulated time spent waiting for the waiter or processing the critical section
 total time spent in the run method
 All instance and static variables shall be private. If you need to access a variable from another
 class, write an accessor method (getter or setter).
 */
/** uses milli seconds NOT nanoseconds */

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
public class Philosopher implements Runnable, BrokerConfig { // I need both
																// runnable and
																// the broker
																// configuration
	private String name; // This is the name of the philosopher
	private int thoughts;// number of thoughts the philosophers has
	private int meals;
	private Connection connection;
	private boolean exit = false;
	private Session session;
	private Destination requestQueue;
	private Destination actionQueue;
	private MessageProducer producer;
	private MessageConsumer consumer;
	private long timeInTot;
	private long timeInCrit;
	private long tempTime;
	public Philosopher(String n) {
		name = n;
		ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(DEFAULT_BROKER);
		exit = false;
		try {
			connection = factory.createConnection();
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			requestQueue = session.createQueue(REQUEST_QUEUE);
			actionQueue = session.createQueue("hw4-queue-" + name); //philosophers individual queues
			System.out.println(name + "made it passed making the action queue");
			producer = session.createProducer(requestQueue);
			consumer = session.createConsumer(actionQueue);
			timeInTot = 0;
			timeInCrit = 0;
			tempTime = 0;
			connection.start();
		} catch (JMSException jm) {
			jm.printStackTrace();
		}
	}
	public String getName() {
		return name;
	}

	public void eat() {
		meals++;
	}

	public void think() {
		thoughts++;
	}

	public void setName(String s) {
		name = s;
	}

	public void setExitCondition() {
		exit = true;
	}

	public long getTimeInLoop() {
		return timeInTot;
	}

	public int getThoughtCount() {
		return thoughts;
	}

	public int getMealCount() {

		return meals;
	}

	public long getTimeInCriticalSection() {

		return timeInCrit;
	}
	public void run() {

		tempTime = System.nanoTime();// start timer
		try {
			while (!exit) {
				tempTime = System.nanoTime();// start timer
				think();
				String firstRequestMessage = name + "," + "pickup";
				TextMessage firstTextMessage = session.createTextMessage(firstRequestMessage);
				producer.send(firstTextMessage);
				Message message = consumer.receive(100);
				if (message instanceof TextMessage) {
					TextMessage tm = (TextMessage) message;
					String msg = tm.getText();
					String[] msgArgs = msg.split(","); // I will split this by comma
					String namePassedIn = msgArgs[0];
					String action = msgArgs[1]; // either pickup action or set
					if (namePassedIn.equals(name)) {
						if (action.equals("true")) {
							eat();
							String requestMessage = name + "," + "setdown";
							TextMessage textMessage = session
									.createTextMessage(requestMessage);
							producer.send(textMessage);
						} else if (action.equals("false")) {
							timeInCrit = System.nanoTime() - tempTime;
							System.out.println("temp time " + tempTime);
						}
					}

				} else {
					System.out
							.println("Unexpected Messaging statment in the philosopher"
									+ message);
				}

			}

			if (exit) {
				timeInTot = System.nanoTime() - tempTime; // currentTime minus the begining
				System.out.println("I exited the while loop in philosopher "
						+ name);
				connection.close();
			}
		} catch (JMSException e) {
			e.printStackTrace();
		}

	}
}
