package james.games.connection;

import java.util.concurrent.ArrayBlockingQueue;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;

public class InternetBuffer implements CBuffer {
    private class Client implements Runnable {

        @Override
        public void run() {
            while (!closed) {
                try {
                    Object obj = input.readObject();
                    if (RevelationServer.ACK_DEAD.is(obj)) close();
                    else recieveObject(new ChannelledObject("Inet Server",obj));
                    
                } catch (IOException | ClassNotFoundException e) {
                    System.err.println(e);
                }
            }
        }

    }
    private final Socket socket;
    private final ObjectOutputStream output;
    private final ObjectInputStream input;
    private boolean closed = false;
    private ArrayBlockingQueue<ChannelledObject> queue = new ArrayBlockingQueue<ChannelledObject>(100);
    @Override
    public boolean hasObjects() {
        return queue.peek()!=null;
    }

    @Override
    public ChannelledObject getObject() {
        return queue.remove();
    }

    @Override
    public CBuffer newBuffer(ChannelledObject o) {
        throw new UnsupportedOperationException("Method newBuffer cannot be implemented by Internet Buffer");
    }

    @Override
    public int sendObject(ChannelledObject o) {
        throw new UnsupportedOperationException("Method sendObject(ChannelledObject) cannot be implemented by Internet Buffer: try sendObject(Object,int)");
    }

    @Override
    public int sendObject(Object o, int channel) {
        synchronized (output) {
            if (closed) return -1;
            if ((channel&ECHO)==0) return 1;
            try {
                output.writeObject(o);
                return 0;
            } catch (IOException e) {
                return 2;
            }
        }
    }

    @Override
    public int recieveObject(ChannelledObject o) {
        System.out.println("Object Recieved:"+o.message);
        if (queue.offer(o)) return 0;
		else return 1;
    }
    public InternetBuffer(InetAddress address, int port) throws IOException {
        System.out.println("Entering Buffer Constructor port: "+port);
        socket = new Socket(address,port);
        System.out.println("Socket Created");
        input = new ObjectInputStream(socket.getInputStream());
        System.out.println("Getting Input");
        output = new ObjectOutputStream(socket.getOutputStream());
        System.out.println("Getting Output");
        new Thread(new Client()).start();
        System.out.println("Leaving Buffer Constructor");
    }
    private void close() throws IOException {
        synchronized (output) {
            closed = true;
            IOException ex = null;
            try {
                output.writeObject(RevelationServer.ACK_DEAD);
            } catch (IOException e) {
                ex = e;
                System.err.println(e);
            }
            try {
                input.close();
            } catch (IOException e) {
                ex = e;
                System.err.println(e);
            }
            try {
                output.close();
            } catch (IOException e) {
                ex = e;
                System.err.println(e);
            }
            try {
                socket.close();
            } catch (IOException e) {
                ex = e;
                System.err.println(e);
            }
            if (ex!=null) throw ex;
        }
        
    }
    public void signalEnd() {
        sendObject(RevelationServer.ACK_DEAD,ECHO);
    }
}
