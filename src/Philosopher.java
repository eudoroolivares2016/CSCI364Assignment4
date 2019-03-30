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
import javax.jms.*;

public class Philosopher implements Runnable, BrokerConfig { // I need both
																// runnable and
																// the broker
																// configuration
	private String name; // This is the name of the philosopher
	private int thoughts;// number of thoughts the philosophers has
	private int meals;
	private Fork leftFork;
	private Fork rightFork;
	private ActiveMQConnectionFactory factory;

	private Connection connection;
	private boolean exit = false;
	private long timeInLoop = 0;
	private long totalCriticalTime = 0;
	private Session session;
	private Destination requestQueue;
	private Destination actionQueue;
	private MessageProducer producer;
	private MessageConsumer consumer;

	public Philosopher(String n) {
		name = n;
		ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(DEFAULT_BROKER);
		exit = false;
		
		try{
			connection = factory.createConnection();
		session = connection.createSession(false,
				Session.AUTO_ACKNOWLEDGE);
		
		requestQueue = session.createQueue(REQUEST_QUEUE);
	 
		 
		 
		 actionQueue = session.createQueue("hw4-queue-"+name); //the philosophers individual queue	 
	 
	 System.out.println(name + "made it passed making the action queue");

 producer = session.createProducer(requestQueue);
	 consumer = session.createConsumer(actionQueue);
	 connection.start();
		}catch(JMSException jm){
			jm.printStackTrace();
		}

	}

	public String getName() {
		return name;
	}

	

	public void eat() {
		meals++;
		//try{
	  /*
	   * Thread.sleep(2000);
		}catch(InterruptedException e){
			e.printStackTrace();
		}*/
		// leftFork.drop();
		// rightFork.drop();
		// hasForks = false;
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
		return timeInLoop;
	}

	public int getThoughtCount() {
		return thoughts;
	}

	public int getMealCount() {

		return meals;
	}

	public long getTimeInCriticalSection() {

		return totalCriticalTime;
	}

	public void run() {
		//System.out.println(" I am at the top of the run method");
		long beginingLoopTime = System.currentTimeMillis(); //start a timer
		try {
			//System.out.println(" I am in the run method of " + name);
			String firstRequestMessage = name +"," + "pickup";
			//System.out.println("This is the message we will be sedning from the philosopher to the waiter " + firstRequestMessage);

			TextMessage firstTextMessage = session //send the inital message to the waiter to get the process started
					.createTextMessage(firstRequestMessage);
			producer.send(firstTextMessage); 
			
			
			
			while (!exit) {
				
				//System.out.println(" I am in the run method of " + name);
				/*String secondRequestMessage = name +"," + "pickup";
				//System.out.println("This is the message we will be sedning from the philosopher to the waiter " + firstRequestMessage);

				TextMessage secondTextMessage = session //send the inital message to the waiter to get the process started
						.createTextMessage(secondRequestMessage);
				producer.send(secondTextMessage); 
				
				*/
				Message message = consumer.receive(); //I think it is getting stuck here if they do not recieve any messages
				//System.out.println("I am " + name + " I have recieved a message from my queue");
				
               // System.out.println("Messages are being recieved by the philosopher"); //yes
				if (message instanceof TextMessage) {
					TextMessage tm = (TextMessage) message;
					String msg = tm.getText();
					String[] msgArgs = msg.split(","); // I will split this by 										// comma
					String namePassedIn = msgArgs[0];
					String action = msgArgs[1]; // either pickup action or set
				//	System.out.println("this is the message that the waiter got back from the philosopher " + msg);

					if (namePassedIn.equals(name)) { // this.name is the
															// name of the
															// philosopher and
						
					// the other name is

						if (action.equals("true")) { // the philosopher was told
														// by the waiter that it
														// can eat
							System.out.println("ok We matched one of the messages from the philosopher");
							eat(); // this will incerement the number of meals
									// accordingly
							
							String requestMessage = name +"," + "setdown";

							TextMessage textMessage = session
									.createTextMessage(requestMessage);
							producer.send(textMessage); // send the message to
														// the waiter to put the
														// forks down.

						} else if (action.equals("false")) {
							think(); // have the philosopher think when if they
										// are denied the acesss to forks
							// send the message to try to eat
							String requestMessage = name +"," + "pickup";

							TextMessage textMessage = session
									.createTextMessage(requestMessage);
							producer.send(textMessage); // send the message to
														// the waiter to put the
														// forks down.

						}
					}

				} else {
					System.out.println("Unexpected Messaging statment");
				}

			}
			
			if(exit){
			System.out.println("I exited the while loop in philosopher " + name);
			long endingLoopTime = System.currentTimeMillis();
			timeInLoop = endingLoopTime - beginingLoopTime; /** get the total time spent before exit is called on the while loop */
			connection.close();
			}

		} catch (JMSException e) {
			e.printStackTrace();
		}

	}
}
