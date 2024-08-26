package ca.jrvs.apps.jdbc.stockquote.service;

import ca.jrvs.apps.jdbc.sotckquote.dao.Position;
import ca.jrvs.apps.jdbc.sotckquote.dao.PositionDao;
import ca.jrvs.apps.jdbc.sotckquote.dao.Quote;
import ca.jrvs.apps.jdbc.sotckquote.dao.QuoteDao;

import java.util.Optional;

public class PositionService {
    private final PositionDao positionDao;
    private final QuoteDao quoteDao;

    public PositionService(PositionDao positionDao, QuoteDao quoteDao){
        this.positionDao = positionDao;
        this.quoteDao = quoteDao;
    }

    public Position buy(String ticker, int numberOfShares, double price){
        Optional<Quote> quoteOptional = quoteDao.findById(ticker);
        if(!quoteOptional.isPresent()){
            throw new IllegalArgumentException("No data available for the symbol: " + ticker);
        }
        Quote quote = quoteOptional.get();
        if(numberOfShares > quote.getVolume()){
            throw new IllegalArgumentException("Cannot buy the number of shares: " + numberOfShares + ". It is more than the available volume: " + quote.getVolume());
        }

        Optional<Position> positionOptional = positionDao.findById(ticker);
        Position position;
        if(positionOptional.isPresent()){
            position = positionOptional.get();
            position.setNumOfShares(position.getNumOfShares() + numberOfShares);
            position.setValuePaid(position.getValuePaid() + (numberOfShares * price));
        } else{
            position = new Position();
            position.setTicker(ticker);
            position.setNumOfShares(numberOfShares);
            position.setValuePaid(numberOfShares * price);
        }
        return positionDao.save(position);
    }

    public void sell(String ticker){
        positionDao.deleteById(ticker);
    }
}
