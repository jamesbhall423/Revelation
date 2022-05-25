package james.games.connection;

import java.net.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class RevelationServer implements Runnable {
    public static final class AckDead implements Serializable {
        private static final long serialVersionUID = 1L;
        public boolean is(Object o) {
            return o.getClass().equals(getClass());
        }
    }
    public static final AckDead ACK_DEAD = new AckDead();
    private class User implements Runnable {
        private final Socket socket;
        private final ObjectInputStream input;
        private final ObjectOutputStream output;
        private boolean end = false;
        public boolean ack_dead = false;
        public User(Socket socket) throws IOException {
            this.socket = socket;
            output = new ObjectOutputStream(socket.getOutputStream());
            input = new ObjectInputStream(socket.getInputStream());
        }
        @Override
        public void run() {
            Object object = null;
            while (!end) {
                try {
                    object = input.readObject();
                    for (User user: users) user.writeObject(object);
                } catch (IOException | ClassNotFoundException e) {
                    end = true;
                    System.err.println(e);
                }
                if (ACK_DEAD.is(object)) end=true;
            }
            ack_dead = true;
            boolean all_dead = false;
            while (!all_dead) {
                all_dead = true;
                for (User user: users) if (!user.ack_dead) all_dead = false;
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                }
            }
            //for (User user: users) synchronized(user){user.end=true;}
            try {
                close();
            } catch (IOException e) {
                System.err.println(e);
            }
        }
        private void close() throws IOException {
            synchronized (this) {
                IOException ex = null;
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
        public void writeObject(Object object) throws IOException {
            synchronized (this) {
                output.writeObject(object);
            }
        }
    }
    private final User[] users;
    private final int players;
    private final int port;
    public RevelationServer(int players, int port) {
        System.out.println("Hello Server 1");
        this.players = players;
        this.port = port;
        users = new User[players];
        new Thread(this).start();
        System.out.println("Hello Server 2");
    }
    @Override
    public void run() {
        System.out.println("Running Server 1 port: "+port);
        // Establish the listen socket.
        try (ServerSocket welcomeSocket = new ServerSocket(port)) {
            System.out.println("Created Server Socket");
            for (int i = 0; i < players; i++) {
                System.out.println("Created Client "+i);
                Socket connectionSocket = null;
                // Listen for a TCP connection request.
                try {
                    connectionSocket = welcomeSocket.accept();
                    System.out.println("client connected");
                    users[i] = new User(connectionSocket);
                    System.out.println("user created");
                } catch (IOException e) {
                    System.err.println(e);
                    IOException ex = null;
                    for (int j = 0; j < i; j++) try {
                        users[j].close();
                    } catch (IOException e2) {
                        ex = e2;
                        System.err.println(e2);
                    }
                    if (connectionSocket!=null) connectionSocket.close();
                    if (ex!=null) throw ex;
                }
            }
            for (int i = 0; i < players; i++) {
                new Thread(users[i]).start();
            }
        } catch (IOException e) {
            System.err.println(e);
        }
        
        System.out.println("Server main: all connected");
    }
}
