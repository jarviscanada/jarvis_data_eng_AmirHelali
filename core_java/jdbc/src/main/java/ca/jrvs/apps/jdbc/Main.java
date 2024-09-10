package ca.jrvs.apps.jdbc;

import ca.jrvs.apps.jdbc.stockquote.controller.StockQuoteController;
import ca.jrvs.apps.jdbc.stockquote.dao.PositionDao;
import ca.jrvs.apps.jdbc.stockquote.dao.QuoteDao;
import ca.jrvs.apps.jdbc.stockquote.dao.QuoteHttpHelper;
import ca.jrvs.apps.jdbc.stockquote.service.PositionService;
import ca.jrvs.apps.jdbc.stockquote.service.QuoteService;
import okhttp3.OkHttpClient;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        Map<String, String> properties = new HashMap<>();
        try (InputStream inputStream = Main.class.getClassLoader().getResourceAsStream("properties.txt")) {
            if (inputStream == null) {
                throw new FileNotFoundException("Property file not found in the classpath.");
            }
            try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] tokens = line.split(":");
                    properties.put(tokens[0], tokens[1]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            Class.forName(properties.get("db-class"));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        OkHttpClient client = new OkHttpClient();
        String url = "jdbc:postgresql://"+properties.get("server")+":"+properties.get("port")+"/"+properties.get("database");
        try (Connection c = DriverManager.getConnection(url, properties.get("username"), properties.get("password"))) {
            QuoteDao qRepo = new QuoteDao(c);
            PositionDao pRepo = new PositionDao(c);
            QuoteHttpHelper rcon = new QuoteHttpHelper(properties.get("api-key"), client);
            QuoteService sQuote = new QuoteService(qRepo, rcon);
            PositionService sPos = new PositionService(pRepo, qRepo);
            StockQuoteController con = new StockQuoteController(sQuote, sPos);
            con.initClient();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
