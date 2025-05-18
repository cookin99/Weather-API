import java.net.HttpURLConnection;
import java.net.URL;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonArray;
public class WeatherForecast {
    public static void main(String[] args){
        try{
            String baseURL = "https://api.open-meteo.com/v1/forecast?";
            String params = "latitude=39.168804&longitude=-86.536659&hourly=temperature_2m&temperature_unit=fahrenheit&timezone=EST";
            String tempType=null;
            if(args.length>1){
                if(args[6].equals("C")||args[6].equals("c")){
                    tempType="celsius";
                }else{
                    tempType="fahrenheit";
                }
                params = "latitude="+args[2]+"&longitude="+args[4]+"&hourly=temperature_2m&temperature_unit="+tempType+"&timezone=EST";
            }
            URL url = new URL(baseURL+params);
            HttpURLConnection connect = (HttpURLConnection) url.openConnection();
            connect.setRequestMethod("GET");
            int status = connect.getResponseCode();
            if(status!=200){
                throw new IOException("Not connected");
            }
            BufferedReader br = new BufferedReader(new InputStreamReader(connect.getInputStream(), StandardCharsets.UTF_8));
            StringBuilder contentFromResponse = new StringBuilder();
            String line;
            while((line = br.readLine())!= null){
                contentFromResponse.append(line);
            }
            br.close();
            JsonElement jE = JsonParser.parseString(contentFromResponse.toString());
            JsonObject jO = jE.getAsJsonObject();
            JsonArray times = jO.getAsJsonObject("hourly").getAsJsonArray("time");
            JsonArray temps = jO.getAsJsonObject("hourly").getAsJsonArray("temperature_2m");
            if(args.length>1){
                if(args[args.length-1].startsWith("C")){
                    System.out.println("Latitude: "+args[2]+" "+"Longitude: "+args[4]+" "+"7-Day Forcast in Celsius:");
                }else{
                    System.out.println("Latitude: "+args[2]+"Longitude: "+args[4]+"7-Day Forcast in Fahrenheit:");
                }

            }else{
                System.out.println("Bloomington 7-Day Forecast in Fahrenheit:");
            }
            for(int i = 0; i<times.size();i+=24){
                String date = times.get(i).getAsString().substring(0,10);
                System.out.println("Forecast for "+date+":");
                for(int f = 0; f<24;f+=3){
                    int ind = i+f;
                    if(ind<times.size()){
                        String time = times.get(ind).getAsString().substring(11, 16);
                        double temp = temps.get(ind).getAsDouble();
                        if(tempType!=null&&tempType.startsWith("c")){
                            System.out.println("\t"+time+": "+temp+"°C");
                        }else{
                            System.out.println("\t"+time+": "+temp+"°F");
                        }
                    }
                }
            }
        }catch (IOException e){
            throw new RuntimeException(e);
        }
    }
}
