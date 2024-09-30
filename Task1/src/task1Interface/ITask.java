package task1Interface;

import task1.Broker;
import task1.QueueBroker;
import task1.Task;

public interface ITask{
	Broker getBroker();
	QueueBroker getQueueBroker();
}
