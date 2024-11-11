package mixed.test;

import mixed.abst.MessageQueue;
import mixed.impl.QueueBroker;
import mixed.abst.MessageQueue.Listener;
import mixed.abst.QueueBroker.AcceptListener;
import mixed.abst.QueueBroker.ConnectListener;
import mixed.impl.EventPump;
import mixed.impl.Message;
import mixed.impl.Task;

import java.util.UUID;

public class Test1 {

    private Task client1;
    private Task client2;
    private Task client3;
    private Task server;

    private QueueBroker serverBroker;
    private QueueBroker clientBroker;

    public static void main(String[] args) {
        Test1 test = new Test1();
        test.setup();

        test.client1.post(test.createClientRunnable());
        test.client2.post(test.createClientRunnable());
        test.client3.post(test.createClientRunnable());
        test.server.post(test.createServerRunnable());

        try {
            EventPump.getInstance().join();
            EventPump.getInstance().stopPump();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("TEST PASSED");

    }

    private void setup() {
        serverBroker = new QueueBroker("serverBroker");
        clientBroker = new QueueBroker("clientBroker");

        client1 = new Task();
        client2 = new Task();
        client3 = new Task();
        server = new Task();

        EventPump.getInstance().start();
    }

    private Runnable createClientRunnable() {
        return new EchoClient(clientBroker);
    }

    private Runnable createServerRunnable() {
        return new EchoServer(serverBroker);
    }

    private class EchoClient implements Runnable {
        private QueueBroker broker;

        public EchoClient(QueueBroker broker) {
            this.broker = broker;
        }

        @Override
        public void run() {
            broker.connect("serverBroker", 80, new EchoClientConnectListener());
        }
    }

    private class EchoClientConnectListener implements QueueBroker.ConnectListener {
        @Override
        public void connected(MessageQueue queue) {
            Message msg = new Message(UUID.randomUUID().toString().repeat(10).getBytes());
            queue.setListener(new EchoClientMessageListener(queue, msg));
            queue.send(msg);
        }

        @Override
        public void refused() {
            // Nothing to do here
        }
    }

    private class EchoClientMessageListener implements MessageQueue.Listener {
        private MessageQueue queue;
        private Message message;
        private static int cpt = 0;

        public EchoClientMessageListener(MessageQueue queue, Message msg) {
            this.queue = queue;
            this.message = msg;
        }

        @Override
        public void received(byte[] bytes) {
            queue.close();

            for (int i = 0; i < message.getLength(); i++) {
                assert (bytes[i] == message.getByteAt(i)) : "Data received different from the one sent: " + i;
            }

            assert (queue != null) : "Client Queue not initialized";
            assert (queue.closed()) : "Client Queue not disconnected";

            System.out.println("Client passed");

            if (cpt++ >= 2) {
                EventPump.getInstance().stopPump();
            }
        }

        @Override
        public void closed() {
            queue.close();
        }

        @Override
        public void sent(Message message) {
        }
    }

    private class EchoServer implements Runnable {
        private QueueBroker broker;

        public EchoServer(QueueBroker broker) {
            this.broker = broker;
        }

        @Override
        public void run() {
            broker.bind(80, new EchoServerAcceptListener(broker));
        }
    }

    private class EchoServerAcceptListener implements QueueBroker.AcceptListener {
        private QueueBroker broker;
        private int cpt = 0;

        public EchoServerAcceptListener(QueueBroker broker) {
            this.broker = broker;
        }

        @Override
        public void accepted(MessageQueue queue) {
            queue.setListener(new EchoServerMessageListener(queue));

            if (cpt++ >= 2) {
                broker.unbind(80);
            }
        }
    }

    private class EchoServerMessageListener implements MessageQueue.Listener {
        private MessageQueue queue;
        private static int cpt = 0;

        public EchoServerMessageListener(MessageQueue queue) {
            this.queue = queue;
        }

        @Override
        public void received(byte[] bytes) {
            Task task = new Task();
            task.post(() -> queue.send(new Message(bytes)));
        }

        @Override
        public void closed() {
            queue.close();
        }

        @Override
        public void sent(Message message) {
            queue.close();

            assert (queue != null) : "Server queue not initialized";
            assert (queue.closed()) : "Server queue not disconnected";

            if (cpt++ >= 2) {
                System.out.println("Server passed");
            }
        }
    }
}
