package task1Interface;

import task1.Broker;
import task1.MessageQueue;

public interface IQueueBroker {
	String name();
	MessageQueue accept(int port);
	MessageQueue connect(String name, int port);
	Broker getBroker();

}
