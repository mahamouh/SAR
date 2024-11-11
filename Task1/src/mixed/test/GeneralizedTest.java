package mixed.test;

import java.util.UUID;

import mixed.impl.EventPump;
import mixed.impl.Message;
import mixed.impl.QueueBroker;
import mixed.impl.Task;

public class GeneralizedTest {

    private Task _client1;
    private Task _client2;
    private Task _client3;
    private Task _server;

    private EchoClient _clientRunnable;
    private EchoServer _serverRunnable;

    public static void main(String[] args) {
        GeneralizedTest test = new GeneralizedTest();
        test.setup();
        test.runTest();
    }

    // Setup brokers, clients, and server
    private void setup() {
        QueueBroker serverBroker = new QueueBroker("serverBroker");
        QueueBroker clientBroker = new QueueBroker("clientBroker");

        _clientRunnable = new EchoClient(clientBroker);
        _serverRunnable = new EchoServer(serverBroker);

        _server = new Task();
        _client1 = new Task();
        _client2 = new Task();
        _client3 = new Task();

        EventPump.getInstance().start();
    }

    // Run the test with multiple clients and a server
    private void runTest() {
        _client1.post(_clientRunnable);
        _client2.post(_clientRunnable);
        _client3.post(_clientRunnable);
        _server.post(_serverRunnable);

        try {
            EventPump.getInstance().join();  // Wait for all tasks to finish
            EventPump.getInstance().stopPump();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("ALL TESTS PASSED");
    }

    
    // Client class
    static class EchoClient implements Runnable {
        private QueueBroker _broker;

        public EchoClient(QueueBroker broker) {
            _broker = broker;
        }

        @Override
        public void run() {
            _broker.connect("serverBroker", 80, new EchoClientConnectListener());
        }
    }

    // Client connect listener
    static class EchoClientConnectListener implements QueueBroker.ConnectListener {

        @Override
        public void refused() {
            // Log or handle refusal cases if necessary
        }

        @Override
        public void connected(mixed.abst.MessageQueue queue) {
            // Send a test message once connected
            Message msg = new Message(UUID.randomUUID().toString().repeat(10).getBytes());
            queue.setListener(new EchoClientMessageListener(queue, msg));
            queue.send(msg);
        }
    }

    // Client message listener
    static class EchoClientMessageListener implements mixed.abst.MessageQueue.Listener {
        private mixed.abst.MessageQueue _queue;
        private Message _message;
        private static int cpt = 0;

        public EchoClientMessageListener(mixed.abst.MessageQueue queue, Message msg) {
            _queue = queue;
            _message = msg;
        }

        @Override
        public void received(byte[] bytes) {
            _queue.close();
            // Verify message integrity
            for (int i = 0; i < _message.getLength(); i++) {
                assert (bytes[i] == _message.getByteAt(i)) : "Data received is different from the sent data at position: " + i;
            }
            assert (_queue != null) : "Client Queue not initialized properly";
            assert (_queue.closed() == true) : "Client Queue is not disconnected correctly";

            System.out.println("Client passed");

            if (cpt++ >= 2) {
                EventPump.getInstance().stopPump();  // Stop the pump after 3 successful client tests
            }
        }

        @Override
        public void closed() {
            _queue.close();
        }

        @Override
        public void sent(Message message) {
            // Nothing to do on sent
        }
    }

    // Server class
    static class EchoServer implements Runnable {
        private QueueBroker _broker;

        public EchoServer(QueueBroker broker) {
            _broker = broker;
        }

        @Override
        public void run() {
            _broker.bind(80, new EchoServerAcceptListener(_broker));
        }
    }

    // Server accept listener
    static class EchoServerAcceptListener implements QueueBroker.AcceptListener {
        private QueueBroker _broker;
        private int cpt = 0;

        public EchoServerAcceptListener(QueueBroker broker) {
            _broker = broker;
        }

        @Override
        public void accepted(mixed.abst.MessageQueue queue) {
            queue.setListener(new EchoServerMessageListener(queue));

            if (cpt++ >= 2) {
                _broker.unbind(80);  // Unbind after handling 3 clients
            }
        }
    }

    // Server message listener
    static class EchoServerMessageListener implements mixed.abst.MessageQueue.Listener {
        private mixed.abst.MessageQueue _queue;
        private static int cpt = 0;

        public EchoServerMessageListener(mixed.abst.MessageQueue queue) {
            _queue = queue;
        }

        @Override
        public void received(byte[] bytes) {
            Task task = new Task();
            task.post(new Runnable() {
                @Override
                public void run() {
                    _queue.send(new Message(bytes));
                }
            });
        }

        @Override
        public void closed() {
            _queue.close();
        }

        @Override
        public void sent(Message message) {
            _queue.close();

            assert (_queue != null) : "Server Queue not initialized correctly";
            assert (_queue.closed() == true) : "Server Queue not disconnected correctly";

            if (cpt++ >= 2) {
                System.out.println("Server passed");
            }
        }
    }

  
}