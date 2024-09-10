package ca.jrvs.apps.jdbc.stockquote.service;

import ca.jrvs.apps.jdbc.stockquote.dao.Position;
import ca.jrvs.apps.jdbc.stockquote.dao.PositionDao;
import ca.jrvs.apps.jdbc.stockquote.dao.Quote;
import ca.jrvs.apps.jdbc.stockquote.dao.QuoteDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class PositionService {
    private static final Logger logger = LoggerFactory.getLogger(PositionService.class);
    private final PositionDao positionDao;
    private final QuoteDao quoteDao;

    public PositionService(PositionDao positionDao, QuoteDao quoteDao){
        this.positionDao = positionDao;
        this.quoteDao = quoteDao;
    }

    public Position buy(String ticker, int numberOfShares, double price){
        logger.debug("Buying {} shares of {}", numberOfShares, ticker);
        Optional<Quote> quoteOptional = quoteDao.findById(ticker);
        if(!quoteOptional.isPresent()){
            logger.error("No data available for the symbol: {}", ticker);
            throw new IllegalArgumentException("No data available for the symbol: " + ticker + ". Please fetch the quote first.");
        }
        Quote quote = quoteOptional.get();
        if(numberOfShares > quote.getVolume()){
            logger.error("Cannot buy more shares than available volume for symbol: {}", ticker);
            throw new IllegalArgumentException("Cannot buy the number of shares: " + numberOfShares + ". It is more than the available volume: " + quote.getVolume());
        }

        Optional<Position> positionOptional = positionDao.findById(ticker);
        Position position;
        if(positionOptional.isPresent()){
            position = positionOptional.get();
            position.setNumOfShares(position.getNumOfShares() + numberOfShares);
            position.setValuePaid(position.getValuePaid() + (numberOfShares * price));
            logger.info("Updated position for symbol: {}", ticker);
        } else{
            position = new Position();
            position.setTicker(ticker);
            position.setNumOfShares(numberOfShares);
            position.setValuePaid(numberOfShares * price);
            logger.info("Created new position for symbol: {}", ticker);
        }
        return positionDao.save(position);
    }

    public void sell(String ticker){
        logger.debug("Selling all shares of {}", ticker);
        positionDao.deleteById(ticker);
        logger.info("Sold position for symbol: {}", ticker);
    }

    public Optional<Position> getPosition(String ticker){
        logger.debug("Fetching position for symbol: {}", ticker);
        return positionDao.findById(ticker);
    }
}
