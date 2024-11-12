package fullEvent.impl;

import event.TaskEvent;
import fullEvent.abst.BrokerFullAbstract;
import fullEvent.abst.QueueBrokerFullAbstract;

public class QueueBrokerFull implements QueueBrokerFullAbstract {
	String name;
	BrokerFull brokerFull;
	
	public QueueBrokerFull(String name) {
		this.name = name;
		QueueBrokerFullManager.getSelf().addBroker(this);
		brokerFull = new BrokerFull(name);
		BrokerFullManager.getSelf().addBroker(brokerFull);
	}
	
	public String getName() {
		return this.name;
	}

	@Override
	public boolean bind(int port, IQueueBrokerAcceptListener listener) {
		MyQueueAcceptListener listenerAccept = new MyQueueAcceptListener(listener);
		return brokerFull.bind(port, listenerAccept);
	}

	@Override
	public boolean unbind(int port) {
		return brokerFull.unbind(port);
	}

	@Override
	public boolean connect(String name, int port, IQueueBrokerConnectListener listener) {
		MyQueueConnectListener listenerConnect= new MyQueueConnectListener(listener);
		return brokerFull.connect(name, port, listenerConnect);
	}
	
	
	class MyQueueConnectListener implements BrokerFullAbstract.IBrokerConnectListener {
		IQueueBrokerConnectListener listener;
		
		public MyQueueConnectListener(IQueueBrokerConnectListener listener) {
			this.listener = listener;
		}

		@Override
		public void refused() {
			TaskEvent task = new TaskEvent();
			task.post(() -> {
				listener.refused();
			});
		}

		@Override
		public void connected(ChannelFull queue) {
			MessageQueueFull message = new MessageQueueFull(queue);
			TaskEvent task = new TaskEvent();
			task.post(() -> {
				listener.connected(message);
			});
		}
		
	}
	
	class MyQueueAcceptListener implements BrokerFullAbstract.IBrokerAcceptListener {
		IQueueBrokerAcceptListener listener;
		public MyQueueAcceptListener(IQueueBrokerAcceptListener listener) {
			this.listener = listener;
		}
		@Override
		public void accepted(ChannelFull queue) {
			MessageQueueFull message = new MessageQueueFull(queue);
			TaskEvent task = new TaskEvent();
			task.post(() -> {
				listener.accepted(message);
			});
		}

	}

	public void clearRDV(MessageQueueFull queue) {
		this.brokerFull.clearRdv(queue.channelFull.port);
		
	}
	

}
