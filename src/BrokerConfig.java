/**
 * 
 */

/**
 * Some constants needed by the Manager and Worker objects.
 * 
 * @author david
 * @see manager.Manager
 * @see worker.Worker
 */
public interface BrokerConfig {
	/** the broker string for Apache ActiveMQ. */
	public static final String DEFAULT_BROKER = "tcp://localhost:61616";
	/** the name of the Waiter-to-Philosopher queue */
	public static final String REQUEST_QUEUE = "hw4.request";
	/** the name of the Philosopher-to-Waiter queue */
	public static final String ACTION_QUEUE = "hw4.nameOfPhilosopher"; //each philosopher has its own string for its own queue
}
