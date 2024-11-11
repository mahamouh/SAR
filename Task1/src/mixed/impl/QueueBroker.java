package mixed.impl;

import java.util.HashMap;
import java.util.Map;

import threaded.impl.Broker;
import threaded.impl.BrokerManager;
import threaded.impl.Channel;

import mixed.abst.MessageQueue;


public class QueueBroker extends mixed.abst.QueueBroker {

	private Broker broker;
	private Map<Integer, Thread> bindThreads;

	public QueueBroker(String name) {
		super(name);
		this.broker = new Broker(name);
		this.bindThreads = new HashMap<Integer, Thread>();
	}

	public boolean unbind(int port) {
		if (bindThreads.containsKey(port)) {
			Thread t = bindThreads.get(port);
			t.interrupt();
			bindThreads.remove(port);
			return true;
		}
		return false;
	}

	public boolean bind(int port, AcceptListener listener) {
		synchronized(this) {
		if (bindThreads.containsKey(port)) {
			return false;
		}}

		Thread thread = createBindThread(port, listener);
		bindThreads.put(port, thread);
		thread.start();
		return true;
	}

	private Thread createBindThread(int port, AcceptListener listener) {
		return new Thread(() -> {
			while (bindThreads.containsKey(port)) {
				Channel channelAccept = (Channel) broker.accept(port);
				if (channelAccept != null) {
					handleAcceptedChannel(channelAccept, listener);
				}
			}
		});
	}

	private void handleAcceptedChannel(Channel channelAccept, AcceptListener listener) {
		MessageQueue mq = new mixed.impl.MessageQueue(channelAccept);
		Task task = new Task();
		task.post(() -> listener.accepted(mq));
	}

	public boolean connect(String name, int port, ConnectListener listener) {
		new Thread(() -> {
			Channel channelConnect = (Channel) broker.connect(name, port);
			handleConnectionResult(channelConnect, listener);
		}).start();
		return true;
	}

	private void handleConnectionResult(Channel channelConnect, ConnectListener listener) {
		Task task = new Task();
		if (channelConnect == null) {
			task.post(() -> listener.refused());
		} else {
			MessageQueue mq = new mixed.impl.MessageQueue(channelConnect);
			task.post(() -> listener.connected(mq));
		}
	}

	@Override
	public String name() {
		return broker.getBrokerName();
	}
}
