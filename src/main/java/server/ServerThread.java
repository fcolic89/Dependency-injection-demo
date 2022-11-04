package server;

import framework.core.DIEngine;
import framework.core.model.StringPair;
import framework.response.HttpResponse;
import framework.request.enums.Method;
import framework.request.Header;
import framework.request.Helper;
import framework.request.Request;
import framework.request.exceptions.RequestNotValidException;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.Socket;
import java.util.HashMap;

public class ServerThread implements Runnable{

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    public ServerThread(Socket socket){
        this.socket = socket;

        try {
            in = new BufferedReader(
                    new InputStreamReader(
                            socket.getInputStream()));

            out = new PrintWriter(
                    new BufferedWriter(
                            new OutputStreamWriter(
                                    socket.getOutputStream())), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run(){
        try {

            Request request = this.generateRequest();
            if(request == null) {
                in.close();
                out.close();
                socket.close();
                return;
            }

            //Finding the correct controller or returning error 404
            boolean err = true;
            StringPair classPair = DIEngine.routes.get(new StringPair(request.getMethod().toString(), request.getLocation()));
            if(classPair != null) {
                Object obj = DIEngine.singletons.get(classPair.getFirst());
                if(obj != null){
                    out.println(obj.getClass().getDeclaredMethod(classPair.getSecond()).invoke(obj));
                    err = false;
                }
            }
            if(err)
                out.println(HttpResponse.notFound());

            in.close();
            out.close();
            socket.close();

        } catch (IOException | RequestNotValidException | NoSuchMethodException | IllegalAccessException |
                 InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    private Request generateRequest() throws IOException, RequestNotValidException {
        String command = in.readLine();
        if(command == null) {
            return null;
        }

        String[] actionRow = command.split(" ");
        Method method = Method.valueOf(actionRow[0]);
        String route = actionRow[1];
        Header header = new Header();
        HashMap<String, String> parameters = Helper.getParametersFromRoute(route);

        do {
            command = in.readLine();
            String[] headerRow = command.split(": ");
            if(headerRow.length == 2) {
                header.add(headerRow[0], headerRow[1]);
            }
        } while(!command.trim().equals(""));

        if(method.equals(Method.POST)) {
            int contentLength = Integer.parseInt(header.get("Content-length"));
            char[] buff = new char[contentLength];
            in.read(buff, 0, contentLength);
            String parametersString = new String(buff);

            HashMap<String, String> postParameters = Helper.getParametersFromString(parametersString);
            for (String parameterName : postParameters.keySet()) {
                parameters.put(parameterName, postParameters.get(parameterName));
            }
        }

        Request request = new Request(method, route, header, parameters);

        return request;
    }
}
