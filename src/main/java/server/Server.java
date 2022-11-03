package server;

import framework.core.DIEngine;
import framework.core.model.StringPair;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Server {

    public static Map<String, Object> singletons = new HashMap<>();
    public static Map<StringPair, StringPair> routes= new HashMap<>();

    public static List<Class> controllers = new ArrayList<>();
    public static final int TCP_PORT = 8080;

    public static void main(String[] args) throws IOException {
        DIEngine en = new DIEngine();

        try {
            ServerSocket serverSocket = new ServerSocket(TCP_PORT);
            System.out.println("Server is running at http://localhost:"+TCP_PORT);
            while(true){
                Socket socket = serverSocket.accept();
                new Thread(new ServerThread(socket)).start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
