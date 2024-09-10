package ca.jrvs.apps.jdbc.stockquote.service;

import ca.jrvs.apps.jdbc.stockquote.dao.Quote;
import ca.jrvs.apps.jdbc.stockquote.dao.QuoteDao;
import ca.jrvs.apps.jdbc.stockquote.dao.QuoteHttpHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class QuoteService {
    private static final Logger logger = LoggerFactory.getLogger(QuoteService.class);
    private final QuoteDao dao;
    private final QuoteHttpHelper httpHelper;

    public QuoteService(QuoteDao dao, QuoteHttpHelper httpHelper){
        this.dao = dao;
        this.httpHelper = httpHelper;
    }

    public Optional<Quote> fetchQuoteDataFromAPI(String ticker){
        if (ticker == null || ticker.trim().isEmpty()){
            logger.warn("Invalid ticker provided: {}", ticker);
            return Optional.empty();
        }
        try{
            logger.debug("Fetching quote data for ticker: {}", ticker);
            Quote quote = httpHelper.fetchQuoteInfo(ticker);
            dao.save(quote);
            logger.info("Quote data saved for ticker: {}", ticker);
            return Optional.ofNullable(quote);
        } catch (IllegalArgumentException e){
            logger.error("Error fetching data for ticker: {}", ticker, e);
            throw new IllegalArgumentException("No data found for the symbol: " + ticker, e);
        }
    }
}
