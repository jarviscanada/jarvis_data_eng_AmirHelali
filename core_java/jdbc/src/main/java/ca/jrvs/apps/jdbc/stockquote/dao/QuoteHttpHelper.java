package ca.jrvs.apps.jdbc.stockquote.dao;

import ca.jrvs.apps.jdbc.example.dto.JsonParser;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Map;

public class QuoteHttpHelper {

    private static final Logger logger = LoggerFactory.getLogger(QuoteHttpHelper.class);

    private String apiKey;
    private OkHttpClient client;

    public QuoteHttpHelper(String apiKey, OkHttpClient client){
        this.apiKey = apiKey;
        this.client = client;
    }

    public Quote fetchQuoteInfo(String symbol) throws IllegalArgumentException{
        logger.info("Fetching quote info for symbol: {}", symbol);
        Quote quote;
        Request request = new Request.Builder().url("https://alpha-vantage.p.rapidapi.com/query?function=GLOBAL_QUOTE&symbol="+symbol+"&datatype=json")
                .addHeader("X-RapidAPI-Key", apiKey).addHeader("X-RapidAPI-Host", "alpha-vantage.p.rapidapi.com").build();

        try(Response response = client.newCall(request).execute()){
            if(!response.isSuccessful()){
                throw new IOException("Unsuccessful: " + response);
            }
            String responseBody = response.body().string();
            Map<String, Object> jsonMap = JsonParser.toObjectFromJson(responseBody, Map.class);
            Map<String, String> globalQuote = (Map<String, String>) jsonMap.get("Global Quote");
            if(globalQuote == null || globalQuote.isEmpty()){
                logger.warn("No data found for symbol: {}", symbol);
                throw new IllegalArgumentException("No data found for the symbol: " + symbol);
            }
            String globalQuoteString = JsonParser.toJson(globalQuote, true, false);
            quote = JsonParser.toObjectFromJson(globalQuoteString, Quote.class);
            quote.setTimestamp(new Timestamp(System.currentTimeMillis()));
            logger.info("Quote fetched successfully for symbol: {}", symbol);
        }catch (IOException e){
            logger.error("Failed to fetch data for symbol: {}", symbol, e);
            throw new IllegalArgumentException("Failed to fetch data for the symbol: " + symbol, e);
        }
        return quote;
    }
}
