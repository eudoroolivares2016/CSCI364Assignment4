
/*
	Eudoro Olivares
	Programing assigment 2
	CSCI 364
	2/14/2019
	instantiates and initializes all data structures, in
	particular forks, philosophers, and threads. After starting each thread (Thread.start()), main
	should sleep for a number of milliseconds as specified on the command line. 
*/
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

import java.util.InputMismatchException;
import java.util.Scanner;
import javax.jms.*;


/** This is a java comment */


public class Dine implements BrokerConfig {
	
	
	public static void main(String args[]) {	
	
		ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(DEFAULT_BROKER);
		System.out.println(
		"How long should the main thread sleep after starting the philosopher threads? (in milliseconds) : ");
		Scanner getInput = new Scanner(System.in);
		long amountOfSleep = 0;
		try {
			amountOfSleep = getInput.nextLong(); /** parses as a long*/
			} catch (InputMismatchException ie ) {
			System.out.println("You must put in a number for the amount of sleep");
			System.exit(1);
		}
	
		
		getInput.close();
		/** instantiates Forks */
		

		Connection connection = null;
		
		try{
       // System.out.println("Breaks here for ambiguous reasons");
		connection = factory.createConnection();
		}catch(JMSException e){
			e.printStackTrace();
		}
		

		System.out.println("the connection was established");
		/** instantiates philosopher objects and assigns them a name */
		
		Philosopher p1 = new Philosopher("Alpha");
		//p1.setName("Alpha");
		Philosopher p2 = new Philosopher("Beta");
		//p2.setName("Beta");
		Philosopher p3 = new Philosopher("Charlie");
		//p3.setName("Charlie");
		Philosopher p4 = new Philosopher("Delta");
		//p4.setName("Delta");
		Philosopher p5 = new Philosopher("Echo");
		//p5.setName("Echo");
		
		System.out.println("makes the philosophers");
		
		Waiter waiter = new Waiter();
		
		System.out.println("makes the waiter");
		
		try{connection.start();
		}catch(JMSException jm){
			jm.printStackTrace();
		}
		System.out.println("starts connection");
		
		/** instantiates then starts the thread */
		Thread t1 = new Thread(p1);
		Thread t2 = new Thread(p2);
		Thread t3 = new Thread(p3);
		Thread t4 = new Thread(p4);
		Thread t5 = new Thread(p5);
		Thread waiterThread = new Thread(waiter);
		
		System.out.println("makes threads");
		
		
		t1.start();
		t2.start();
		t3.start();
		t4.start();
		t5.start();
		waiterThread.start();
		
		try {
			Thread.sleep(amountOfSleep);
			} catch (InterruptedException ie) {
			System.out.println(" main sleep interrupted");
		}
		/**
			* sends the stop signal to all of the threads by exiting the while loop in
			* their run methods (setting exit to true)
		*/
		p1.setExitCondition();
		p2.setExitCondition();
		p3.setExitCondition();
		p4.setExitCondition();
		p5.setExitCondition();
		waiter.setExitCondition();
		
		System.out.println("set the exit condition");
		
		try {
			t1.join();
			} catch (InterruptedException ie) {
			System.out.println("t1 interrupted");
		}
		
		System.out.println("I managed to join thread 1");
		try {
			t2.join();
			
			} catch (InterruptedException ie) {
			System.out.println("t2 interrupted");
		}
		try {
			t3.join();
			} catch (InterruptedException ie) {
			System.out.println("t3 interrupted");
		}
		try {
			t4.join();
			} catch (InterruptedException ie) {
			System.out.println("t4 interrupted");
		}
		try {
			t5.join();
			} catch (InterruptedException ie) {
			System.out.println("t5 interrupted");
		}
		
		try {
			waiterThread.join();
			} catch (InterruptedException ie) {
			System.out.println("t5 interrupted");
		}
		System.out.println("The threads were joined");
		/** prints out relevant stats for each philosopher */
		getStats(p1);
		System.out.println("\n");
		getStats(p2);
		System.out.println("\n");
		getStats(p3);
		System.out.println("\n");
		getStats(p4);
		System.out.println("\n");
		getStats(p5);
try{
        connection.close();
		System.exit(1);
	}catch(JMSException jm){
			jm.printStackTrace();
		}
}
	public static void getStats(Philosopher phil) {
		System.out.println("name: " + phil.getName());
		System.out.println("meals: " + phil.getMealCount());
		System.out.println("loop iterations/ number of thoughts : " + phil.getThoughtCount());
		System.out.println("Time spent in the loop: " + phil.getTimeInLoop());
		System.out.println("Time spent in the critcal section (milli seconds) : " + phil.getTimeInCriticalSection());
		double timeRatio = (double) phil.getTimeInCriticalSection() / phil.getTimeInLoop();
		System.out.println("Time ratio time in the critical / total time in loop " + timeRatio);
	}
}

