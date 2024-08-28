package ca.jrvs.apps.jdbc.stockquote.service;

import ca.jrvs.apps.jdbc.stockquote.dao.Position;
import ca.jrvs.apps.jdbc.stockquote.dao.PositionDao;
import ca.jrvs.apps.jdbc.stockquote.dao.Quote;
import ca.jrvs.apps.jdbc.stockquote.dao.QuoteDao;

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
            throw new IllegalArgumentException("No data available for the symbol: " + ticker + ". Please fetch the quote first.");
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

    public Optional<Position> getPosition(String ticker){
        return positionDao.findById(ticker);
    }

    /*public static void main (String[] args){
        String url = "jdbc:postgresql://localhost:5432/stock_quote";
        try(Connection c = DriverManager.getConnection(url, "postgres", "rocky1234")){
            QuoteDao qDao = new QuoteDao(c);
            PositionDao pDao = new PositionDao(c);
            OkHttpClient client = new OkHttpClient();
            String api = "b5e19a9367msh7c400828f56fb0bp1a31ddjsna5da8e59418b";
            QuoteHttpHelper httpHelper = new QuoteHttpHelper(api, client);
            PositionService pService = new PositionService(pDao, qDao);
            pService.sell("AAPL");

        }catch (SQLException e){
            e.printStackTrace();
        }
    }*/
}
