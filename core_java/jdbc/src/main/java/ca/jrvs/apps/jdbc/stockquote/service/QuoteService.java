package ca.jrvs.apps.jdbc.stockquote.service;

import ca.jrvs.apps.jdbc.sotckquote.dao.Quote;
import ca.jrvs.apps.jdbc.sotckquote.dao.QuoteDao;
import ca.jrvs.apps.jdbc.sotckquote.dao.QuoteHttpHelper;

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
}
