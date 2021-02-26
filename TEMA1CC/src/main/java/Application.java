import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import com.sun.net.httpserver.HttpServer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

class Application {
    static Logger logger = Logger.getLogger("Logging parts");

    public static HashMap<String,String> ApiCall(String urlString) throws IOException {
        URL url = new URL(urlString);
        long start = System.currentTimeMillis();
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        int status = con.getResponseCode();
        long millis = System.currentTimeMillis() - start;

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuilder content = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();
        con.disconnect();
        String respText =content.toString();
        String respCode=Integer.toString(status);
        String latency= Long.toString(millis);
        HashMap<String,String> response=new HashMap<String,String>();
        response.put("apiResp",respText);
        response.put("apiStatus",respCode);
        response.put("latency",latency);
        return  response;

    }

    public static  String getLatestStatus(String route){
        String latestStatus="unknown";
        try {
            File myObj = new File("D:\\activity.log");
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                if(data.contains(route+ " is")){
                    latestStatus=data;
                }
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        return latestStatus;
    }

    public static String getNOfParallelRuns(){
        int nOfRuns=0;
        try {
            File myObj = new File("D:\\activity.log");
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                if(data.contains("start of parallel run"))
                    nOfRuns++;
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        return Integer.toString(nOfRuns);
    }

    public static String getNumberOfApiCalls(String api){
        String type="";
        int nOfCalls=0;
        if(api.equals("random")){
            type="Call to Random Api";
        }else if(api.equals("youtube")){
            type="Call to Youtube Api";
        }else if(api.equals("location")){
            type="Call to Location Api";
        }
        try {
            File myObj = new File("D:\\activity.log");
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                if(data.contains(type))
                    nOfCalls++;
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        return Integer.toString(nOfCalls);
    }

    public static String getAverageLatency(String api){
        String latencyType="";
        if(api.equals("random")){
            latencyType="latency_random";
        }else if(api.equals("youtube")){
            latencyType="latency_youtube";
        }else if(api.equals("location")){
            latencyType="latency_location";
        }
        List<String> latencies=new ArrayList<String>();
        try {
            File myObj = new File("D:\\activity.log");
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                if(data.contains(latencyType))
                    latencies.add(data.split(": ")[2].replace("ms",""));
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        int averageLatency=0;
        int n=0;
        for(String s : latencies){
            int i=Integer.parseInt(s);
            averageLatency=averageLatency+i;
            n++;
        }
        averageLatency=averageLatency/n;
        String average=Integer.toString(averageLatency);
        return  average;
    }

    public static String doCycle(Logger logger,String route) throws IOException {

        FileInputStream ip= new FileInputStream("D:\\TemeCC\\TEMA1CC\\src\\main\\java\\config.properties");
        Properties prop=new Properties();
        prop.load(ip);
        //keys
        String yt_api_key=prop.getProperty("yt_key");
        String geo_api_key=prop.getProperty("geo_key");
        //calls
        String locationCall="http://api.positionstack.com/v1/forward?access_key="+geo_api_key+"&&query=Suceava";
        String randomNumberCall="https://www.random.org/integers/?num=1&min=1&max=6&col=1&base=10&format=plain&rnd=new";

        String latitude="";
        String longitude="";
        String maxResult="";
        String resp="{\"error\":\"Something went wrong\"}";
        logger.info("request to server on " + route);
        logger.info("Call to Location Api");
        HashMap<String,String> locationMap=ApiCall(locationCall);
        String locationResp=locationMap.get("apiResp");
        String locationStatus=locationMap.get("apiStatus");
        String locationLatency=locationMap.get("latency");
        logger.info("Response code location API : " + locationStatus);
        logger.info("latency_location: " + locationLatency + "ms");

        try {

            JSONObject jsonObject=new JSONObject(locationResp);
            JSONArray jsonArray=new JSONArray(jsonObject.getString("data"));
            latitude=jsonArray.getJSONObject(0).getString("latitude");
            longitude=jsonArray.getJSONObject(0).getString("longitude");

        } catch (JSONException e) {
            System.out.println("Location Api didnt return data...retrying...");
        }
        logger.info("Call to Random Api");
        HashMap<String,String> randomMap=ApiCall(randomNumberCall);
        String randomResp=randomMap.get("apiResp");
        String randomStatus=randomMap.get("apiStatus");
        String randomLatency=randomMap.get("latency");
        logger.info("Response code random API : " + randomStatus);
        logger.info("latency_random: " + randomLatency + "ms");
        maxResult=randomResp;
        //yt call
        String youtubeCall="https://youtube.googleapis.com/youtube/v3/search?" +
                "part=snippet&location="+latitude+","+longitude+"&locationRadius=50km&maxResults="+maxResult+"&order=rating&type=video&fields=items.id.videoId%2Citems.snippet.title&key=" + yt_api_key;
        logger.info("Call to Youtube Api");
        HashMap<String,String> youtubeMap=ApiCall(youtubeCall);
        String youtubeResp=youtubeMap.get("apiResp");
        String youtubeStatus=youtubeMap.get("apiStatus");
        String youtubeLatency=youtubeMap.get("latency");
        logger.info("Response code youtube API : " + youtubeStatus);
        logger.info("latency_youtube: " + youtubeLatency + "ms");

        resp = youtubeResp;

        if(resp.equals("")){
            resp="{\"error\":\"Something went wrong\"}";
        }
        if(youtubeStatus.equals("200") && locationStatus.equals("200") && randomStatus.equals("200")){
            logger.info(route+ " is alive.");
        }else{
            logger.info(route+" is not alive.");
        }
        return resp;
    }

    static class Task implements Callable<Integer> {
        private static Random rand = new Random();
        private final int no;
        private Logger logger;
        Task(int no,Logger logger) {
            this.no = no;
            this.logger=logger;
        }
        @Override
        public Integer call() throws Exception {

            doCycle(logger,"/api/parallel");
            //Thread.sleep(rand.nextInt(5000));
            System.out.println("Task " + no + " finished");
            System.out.println(Thread.currentThread());

            return no;
        }
    }


    private static void processBatch(ExecutorService executor, int batchNo,Logger logger) throws InterruptedException {
        Collection batch = new ArrayList<>();
        batch.add(new Task(batchNo * 50 + 1,logger));
        batch.add(new Task(batchNo * 50 + 2,logger));
        batch.add(new Task(batchNo * 50 + 3,logger));
        batch.add(new Task(batchNo * 50 + 4,logger));
        batch.add(new Task(batchNo * 50 + 5,logger));
        List<Future> futures = executor.invokeAll(batch);
        System.out.println("Batch " + batchNo + " proceseed");
    }

    public static void main(String[] args) throws IOException {
        FileHandler handler = new FileHandler("D:\\activity.log", true);
        logger.addHandler(handler);
        SimpleFormatter formatter = new SimpleFormatter();
        handler.setFormatter(formatter);

        logger.setUseParentHandlers(false);

        int serverPort = 8000;
        HttpServer server = HttpServer.create(new InetSocketAddress(serverPort), 0);
        server.createContext("/api/cycle", (exchange -> {
            if ("GET".equals(exchange.getRequestMethod())) {
               String resp=doCycle(logger,"/api/cycle");
                exchange.sendResponseHeaders(200, resp.getBytes().length);
                OutputStream output = exchange.getResponseBody();
                output.write(resp.getBytes());
                output.flush();
                logger.info("server response code = 200");

            }else {
                exchange.sendResponseHeaders(405, -1);// 405 Method Not Allowed
                logger.info("server response code = 405");
            }
            exchange.close();

        }));
        server.createContext("/api/parallel", (exchange -> {
            logger.info("call to /api/parallel");
            ExecutorService executor = Executors.newFixedThreadPool(5);
            try {
                logger.info("start of parallel run");
                logger.info("first batch");
                processBatch(executor, 1,logger);
                logger.info("second batch");
                processBatch(executor, 2,logger);
                logger.info("third batch");
                processBatch(executor, 3,logger);
                executor.shutdown();
                logger.info("end of parallel run");

                exchange.sendResponseHeaders(200,"{\"message\":\"Ok\"}".getBytes().length);
                OutputStream output = exchange.getResponseBody();
                output.write("{\"message\":\"Ok\"}".getBytes());
                output.flush();
                exchange.close();

            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            exchange.close();


        }));
        server.createContext("/api/metrics", (exchange -> {
            String averageYoutube=getAverageLatency("youtube");
            String averageRandom=getAverageLatency("random");
            String averageLocation=getAverageLatency("location");
            String nOfRandomCalls=getNumberOfApiCalls("random");
            String nOfYtCalls=getNumberOfApiCalls("youtube");
            String nOfLocCalls=getNumberOfApiCalls("location");
            String nOfParallelRuns=getNOfParallelRuns();
            String parallelStatus=getLatestStatus("/api/parallel");
            String cycleStatus=getLatestStatus("/api/cycle");
            if(!parallelStatus.contains("unknown") && !parallelStatus.contains("not alive")){
                parallelStatus="alive";
            }else{
                if(!parallelStatus.contains("unknown")){
                    parallelStatus="not alive";
                }
            }
            if(!cycleStatus.contains("unknown") && !cycleStatus.contains("not alive")){
                cycleStatus="alive";
            }else{
                if(!cycleStatus.contains("unknown")){
                    cycleStatus="not alive";
                }
            }
            JSONObject jsonObject=new JSONObject();
            try {
                jsonObject.put("yt_lat",averageYoutube);
                jsonObject.put("rand_lat",averageRandom);
                jsonObject.put("loc_lat",averageLocation);
                jsonObject.put("rand_calls",nOfRandomCalls);
                jsonObject.put("loc_calls",nOfLocCalls);
                jsonObject.put("yt_calls",nOfYtCalls);
                jsonObject.put("parallel_count",nOfParallelRuns);
                jsonObject.put("statusCycle",cycleStatus);
                jsonObject.put("statusParallel",parallelStatus);

            } catch (JSONException e) {
                e.printStackTrace();
            }


            exchange.sendResponseHeaders(200,jsonObject.toString().getBytes().length);
            OutputStream output = exchange.getResponseBody();
            output.write(jsonObject.toString().getBytes());
            output.flush();
            exchange.close();


        }));

        server.setExecutor(null); // creates a default executor
        server.start();
    }

}