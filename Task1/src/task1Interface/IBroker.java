package task1Interface;

import task1.Channel;

public interface IBroker {
	Channel accept(int port);
    Channel connect(String name, int port);
}
