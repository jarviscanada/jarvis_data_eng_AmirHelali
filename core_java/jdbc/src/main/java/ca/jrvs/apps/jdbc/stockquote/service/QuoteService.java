package ca.jrvs.apps.jdbc.stockquote.service;

import ca.jrvs.apps.jdbc.sotckquote.dao.Quote;
import ca.jrvs.apps.jdbc.sotckquote.dao.QuoteDao;
import ca.jrvs.apps.jdbc.sotckquote.dao.QuoteHttpHelper;
import okhttp3.OkHttpClient;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Optional;

public class QuoteService {
    private final QuoteDao dao;
    private final QuoteHttpHelper httpHelper;

    public QuoteService(QuoteDao dao, QuoteHttpHelper httpHelper){
        this.dao = dao;
        this.httpHelper = httpHelper;
    }

    public Optional<Quote> fetchQuoteDataFromAPI(String ticker){
        if (ticker == null || ticker.trim().isEmpty()){
            return Optional.empty();
        }
        try{
            Quote quote = httpHelper.fetchQuoteInfo(ticker);
            dao.save(quote);
            return Optional.ofNullable(quote);
        } catch (IllegalArgumentException e){
            throw new IllegalArgumentException("No data found for the symbol: " + ticker, e);
        }
    }

    /*public static void main (String[] args){
        String url = "jdbc:postgresql://localhost:5432/stock_quote";
        try(Connection c = DriverManager.getConnection(url, "postgres", "rocky1234")){
            QuoteDao qDao = new QuoteDao(c);
            OkHttpClient client = new OkHttpClient();
            String api = "b5e19a9367msh7c400828f56fb0bp1a31ddjsna5da8e59418b";
            QuoteHttpHelper httpHelper = new QuoteHttpHelper(api, client);
            QuoteService qService = new QuoteService(qDao, httpHelper);
            qService.fetchQuoteDataFromAPI("gnhueio");
        }catch (SQLException e){
            e.printStackTrace();
        }
    }*/
}
