package ca.jrvs.apps.jdbc.stockquote.controller;

import ca.jrvs.apps.jdbc.stockquote.dao.Position;
import ca.jrvs.apps.jdbc.stockquote.dao.Quote;
import ca.jrvs.apps.jdbc.stockquote.service.PositionService;
import ca.jrvs.apps.jdbc.stockquote.service.QuoteService;

import java.util.Optional;
import java.util.Scanner;

public class StockQuoteController {

    private QuoteService quoteService;
    private PositionService positionService;

    public StockQuoteController(QuoteService qService, PositionService pService){
        this.quoteService = qService;
        this.positionService = pService;
    }

    public void initClient(){
        Scanner scanner = new Scanner(System.in);
        while(true){
            System.out.println("Enter a command: (fetch/buy/sell/view/exit)");
            String command = scanner.nextLine().trim().toLowerCase();

            try{
                switch (command){
                    case "fetch":
                        System.out.println("Enter ticker symbol:");
                        String ticker = scanner.nextLine().trim().toUpperCase();
                        fetchQuote(ticker);
                        break;

                    case "buy":
                        System.out.println("Enter ticker symbol:");
                        ticker = scanner.nextLine().trim().toUpperCase();
                        System.out.println("Enter number of shares to buy:");
                        int shares = Integer.parseInt(scanner.nextLine().trim());
                        System.out.println("Enter price per share:");
                        double price = Double.parseDouble(scanner.nextLine().trim());
                        buyStock(ticker, shares, price);
                        break;

                    case "sell":
                        System.out.println("Enter ticker symbol:");
                        ticker = scanner.nextLine().trim().toUpperCase();
                        sellStock(ticker);
                        break;

                    case "view":
                        System.out.println("Enter ticker symbol:");
                        ticker = scanner.nextLine().trim().toUpperCase();
                        viewPosition(ticker);
                        break;

                    case "exit":
                        System.out.println("Exiting application...");
                        return;

                    default:
                        System.out.println("Invalid command. Please try again.");
                }
            } catch (Exception e){
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private void viewPosition(String ticker) {
        Optional<Position> positionOptional = positionService.getPosition(ticker);
        if (!positionOptional.isPresent()){
            System.out.println("No position found for ticker: " + ticker);
            return;
        }
        Position position = positionOptional.get();
        System.out.println("Current Position: " + position);

        Optional<Quote> quoteOptional = quoteService.fetchQuoteDataFromAPI(ticker);
        if(quoteOptional.isPresent()){
            Quote quote = quoteOptional.get();
            double currentValue = position.getNumOfShares() * quote.getPrice();
            System.out.println("You paid: " + position.getValuePaid());
            System.out.println("Current value: " + currentValue);
            double profitOrLoss = currentValue - position.getValuePaid();
            System.out.println("Profit/Loss: " + profitOrLoss);
        } else {
            System.out.println("Unable to fetch the current quote for ticker: " + ticker);
        }
    }

    private void sellStock(String ticker) {
        try{
            viewPosition(ticker);
            System.out.println("Are you sure you want to sell? (yes/no)");
            Scanner scanner = new Scanner(System.in);
            String confirm = scanner.nextLine().trim().toLowerCase();
            if(!confirm.equals("yes")){
                System.out.println("Sell cancelled.");
                return;
            }

            positionService.sell(ticker);
            System.out.println("Sold all shares of: " + ticker);
        } catch (IllegalArgumentException e){
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void buyStock(String ticker, int shares, double price) {
        try {
            Optional<Quote> quoteOptional = quoteService.fetchQuoteDataFromAPI(ticker);
            if (!quoteOptional.isPresent()) {
                System.out.println("No quote data found for ticker: " + ticker);
                return;
            }

            Quote currentQuote = quoteOptional.get();
            double currentPrice = currentQuote.getPrice();
            System.out.println("Current price per share: " + currentPrice);

            if (price <= currentPrice) {
                System.out.println("Error: The price per share must be higher than the current price of " + currentPrice);
                return;
            }

            System.out.println("Are you sure you want to buy " + shares + " shares at " + price + " per share? (yes/no)");
            Scanner scanner = new Scanner(System.in);
            String confirmation = scanner.nextLine().trim().toLowerCase();
            if (!confirmation.equals("yes")) {
                System.out.println("Purchase cancelled.");
                return;
            }

            Position position = positionService.buy(ticker, shares, price);
            System.out.println("Bought stock: " + position);
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void fetchQuote(String ticker) {
        Optional<Quote> quote = quoteService.fetchQuoteDataFromAPI(ticker);
        if(quote.isPresent()){
            System.out.println("Quote fetched: " + quote.get());
        } else{
            System.out.println("No quote data found for ticker: " + ticker);
        }
    }
}
