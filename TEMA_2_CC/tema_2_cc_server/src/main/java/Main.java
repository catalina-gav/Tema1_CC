import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Main {
    private static Object StatusCode;
    public static String ConvertIsToString(HttpExchange exchange) throws IOException {

        InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8);
        BufferedReader br = new BufferedReader(isr);
        int b;
        StringBuilder buf = new StringBuilder();
        while ((b = br.read()) != -1) {
            buf.append((char) b);
        }

        br.close();
        isr.close();
        return buf.toString();
    }
    public static void main(String[] args) throws SQLException, IOException {
        Connection connection=Database.getInstance().getConnection();
        ArtistController artistController=new ArtistController();
        int serverPort = 8000;
        HttpServer server = HttpServer.create(new InetSocketAddress(serverPort), 0);
        server.createContext("/api/artists", (exchange -> {
            if(Database.getInstance().getConnection() == null){
                exchange.sendResponseHeaders(500, -1);
            }
            if ("POST".equals(exchange.getRequestMethod())) {

                String respText = "{\"message\" : \"artist inserted\"}";

                JSONObject artistJson= null;
                String pathParam=exchange.getRequestURI().toString().replace("/api/artists/","");
                if(!pathParam.equals("/api/artists")){
                    exchange.sendResponseHeaders(405, -1);
                }else {
                    try {
                        artistJson = new JSONObject(ConvertIsToString(exchange));
                    } catch (JSONException e) {
                        exchange.sendResponseHeaders(400, -1);
                    }
                    try {
                        assert artistJson != null;
                        Artist artist = new Artist(artistJson.getString("name"), artistJson.getString("country"));

                        Boolean created = artistController.create(artist);
                        if(created.equals(false)){
                            exchange.sendResponseHeaders(500, -1);
                        }
                    } catch (JSONException e) {
                        exchange.sendResponseHeaders(400, -1);
                    }
                    exchange.sendResponseHeaders(201, respText.getBytes().length);
                                    OutputStream output = exchange.getResponseBody();
                                    output.write(respText.getBytes());
                                    output.flush();
                }

            }else  if ("GET".equals(exchange.getRequestMethod())){
                String pathParam=exchange.getRequestURI().toString().replace("/api/artists/","");
                if(pathParam.equals("/api/artists")){
                    List<Artist> artists;
                    artists=artistController.findAll();
                    if(artists.isEmpty()){
                        exchange.sendResponseHeaders(204, -1);
                    }else{
                        JSONArray jsonArray=new JSONArray();
                        for(Artist artist : artists){
                            try {
                                JSONObject jsonObject=new JSONObject(artist.toString());
                                jsonArray.put(jsonObject);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        exchange.sendResponseHeaders(200, jsonArray.toString().getBytes().length);
                        OutputStream output = exchange.getResponseBody();
                        output.write(jsonArray.toString().getBytes());
                        output.flush();
                    }
                }
                try {
                    int param= Integer.parseInt(pathParam);
                    Artist artist=artistController.findById(param);

                    if(artist== null){
                        exchange.sendResponseHeaders(404, -1);
                    }else{
                        System.out.println(artist.toString());
                        JSONObject jsonObject=new JSONObject(artist.toString());

                        exchange.sendResponseHeaders(200, jsonObject.toString().getBytes().length);
                        OutputStream output = exchange.getResponseBody();
                        output.write(jsonObject.toString().getBytes());
                        output.flush();
                    }
                } catch (NumberFormatException | JSONException e) {
                    exchange.sendResponseHeaders(400, -1);
                }

            }else if ("DELETE".equals(exchange.getRequestMethod())){
                String pathParam=exchange.getRequestURI().toString().replace("/api/artists/","");
                if(pathParam.equals("/api/artists")){
                    exchange.sendResponseHeaders(405, -1);
                }
                try {
                    int param= Integer.parseInt(pathParam);
                    Boolean deleted=artistController.deleteArtist(param);
                    if(deleted.equals(true)){
                        exchange.sendResponseHeaders(200, -1);
                    }else{
                        exchange.sendResponseHeaders(404, -1);
                    }
                } catch (NumberFormatException e) {
                    exchange.sendResponseHeaders(404, -1);
                }
            }else if ("PUT".equals(exchange.getRequestMethod())) {
                String pathParam=exchange.getRequestURI().toString().replace("/api/artists/","");
                if(pathParam.equals("/api/artists")){
                    exchange.sendResponseHeaders(405, -1);
                }

                try {
                    int param= Integer.parseInt(pathParam);
                    int newId;
                    JSONObject artistJson = new JSONObject(ConvertIsToString(exchange));
                    if(artistJson.has("artist_id")){
                        newId=artistJson.getInt("artist_id");
                        Artist artist = new Artist(param, artistJson.getString("name"), artistJson.getString("country"));
                        int updated = artistController.updatePutId(artist,newId);
                        if (updated ==200) {
                            exchange.sendResponseHeaders(200, -1);
                        } else if(updated ==409) {
                            exchange.sendResponseHeaders(409, -1);
                        }else if(updated ==404){
                                exchange.sendResponseHeaders(404, -1);
                        }else{
                            exchange.sendResponseHeaders(500, -1);
                        }
                    }else {
                        Artist artist = new Artist(param, artistJson.getString("name"), artistJson.getString("country"));
                        Boolean updated = artistController.updatePut(artist);
                        if (updated.equals(true)) {
                            exchange.sendResponseHeaders(200, -1);
                        } else {
                            exchange.sendResponseHeaders(404, -1);
                        }
                    }
                } catch (NumberFormatException | JSONException e) {

                    exchange.sendResponseHeaders(400, -1);
                }

            }else  if ("PATCH".equals(exchange.getRequestMethod())) {
                String pathParam = exchange.getRequestURI().toString().replace("/api/artists/", "");
                if (pathParam.equals("/api/artists")) {
                    exchange.sendResponseHeaders(405, -1);
                }

                try {
                    int newId;
                    int param = Integer.parseInt(pathParam);
                    JSONObject artistJson = new JSONObject(ConvertIsToString(exchange));
                    Artist artist=new Artist(param,null,null);
                    if (!artistJson.has("name") && !artistJson.has("country") && !artistJson.has("artist_id")) {
                        exchange.sendResponseHeaders(400, -1);
                    }
                    if(artistJson.has("name"))
                        artist.setName(artistJson.getString("name"));
                    if(artistJson.has("country"))
                        artist.setCountry(artistJson.getString("country"));
                    if(artistJson.has("artist_id")){
                        newId=artistJson.getInt("artist_id");
                        int updated = artistController.updatePatchId(artist,newId);
                        if (updated == 200) {
                            exchange.sendResponseHeaders(200, -1);
                        }else if(updated ==404) {
                            exchange.sendResponseHeaders(404, -1);

                        }else if(updated == 409){
                            exchange.sendResponseHeaders(409, -1);
                        }else{
                            exchange.sendResponseHeaders(500, -1);
                        }
                    }else {

                        Boolean updated = artistController.updatePatch(artist);
                        if (updated.equals(true)) {
                            exchange.sendResponseHeaders(200, -1);
                        } else {
                            exchange.sendResponseHeaders(404, -1);
                        }
                    }
                } catch (NumberFormatException | JSONException e) {
                    exchange.sendResponseHeaders(400, -1);
                }
            }
                exchange.close();

        }));

        server.setExecutor(null); // creates a default executor
        server.start();
    }
}
