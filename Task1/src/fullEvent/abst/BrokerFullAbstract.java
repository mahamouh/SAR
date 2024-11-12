package fullEvent.abst;


import fullEvent.impl.ChannelFull;

public interface BrokerFullAbstract {
    
    public interface IBrokerAcceptListener {
		void accepted(ChannelFull queue);
	}

	public abstract boolean bind(int port, IBrokerAcceptListener listener);

	public abstract boolean unbind(int port);

	public interface IBrokerConnectListener {
		void connected(ChannelFull queue);
		void refused();
	}
	public abstract boolean connect(String name, int port, IBrokerConnectListener listener);
}
