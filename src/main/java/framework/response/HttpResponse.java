package framework.response;

import com.google.gson.Gson;

public class HttpResponse extends Response{

    private Gson gson;
    private Object jsonObject;

    public HttpResponse(Object jsonObject)
    {
        this.gson = new Gson();
        this.jsonObject = jsonObject;
    }

    @Override
    public String render() {
        StringBuilder responseContent = new StringBuilder();

        responseContent.append("HTTP/1.1 200 OK\n");
        for (String key : this.header.getKeys()) {
            responseContent.append(key).append(":").append(this.header.get(key)).append("\n");
        }
        responseContent.append("\n");

        responseContent.append(this.gson.toJson(this.jsonObject));

        return responseContent.toString();
    }

    public static String notFound(){
        StringBuilder responseContent = new StringBuilder();

        responseContent.append("HTTP/1.1 404 NOT FOUND\n\n");
        responseContent.append("<html>").append("<body>");
        responseContent.append("<b>").append("Page not found").append("</b>").append("\n");
        responseContent.append("</body>").append("</html>\n");
//        responseContent.append("\n");

        return responseContent.toString();
    }


    public static String ok(String str){
        StringBuilder responseContent = new StringBuilder();

        responseContent.append("HTTP/1.1 200 OK\n\n");
        responseContent.append("<html>").append("<body>");
        responseContent.append("<b>").append(str).append("</b>").append("\n");
        responseContent.append("</body>").append("</html>\n");
        //        responseContent.append("\n");

        return responseContent.toString();
    }
}
