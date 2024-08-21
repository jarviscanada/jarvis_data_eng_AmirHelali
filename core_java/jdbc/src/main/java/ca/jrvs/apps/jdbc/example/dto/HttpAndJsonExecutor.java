package ca.jrvs.apps.jdbc.example.dto;


import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

public class HttpAndJsonExecutor {
    public static void main(String[] args) {
        String symbol = "MSFT";
        String apiKey = System.getenv("ALPHA_API_KEY");
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url("https://alpha-vantage.p.rapidapi.com/query?function=GLOBAL_QUOTE&symbol="+symbol+"&datatype=json")
                .addHeader("X-RapidAPI-Key", apiKey).addHeader("X-RapidAPI-Host", "alpha-vantage.p.rapidapi.com").build();
        try{
            try (Response response = client.newCall(request).execute()) {
                System.out.println(response.body().string());
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}
