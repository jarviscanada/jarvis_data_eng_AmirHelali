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
                        buyStock(ticker, scanner);
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

    private boolean viewPosition(String ticker) {
        boolean result = false;
        Optional<Position> positionOptional = positionService.getPosition(ticker);
        if (!positionOptional.isPresent()){
            System.out.println("No position found for ticker: " + ticker);
            return result;
        }
        result = true;
        Position position = positionOptional.get();
        System.out.println("Current position details: " + position.getTicker() + ", " + position.getNumOfShares() + " shares");

        Optional<Quote> quoteOptional = quoteService.fetchQuoteDataFromAPI(ticker);
        if(quoteOptional.isPresent()){
            Quote quote = quoteOptional.get();
            double currentValue = position.getNumOfShares() * quote.getPrice();
            System.out.println("You paid: " + position.getValuePaid());
            System.out.println("Current value: " + currentValue);
            double profitOrLoss = currentValue - position.getValuePaid();
            System.out.println("Profit/Loss: " + profitOrLoss);
        } else {
            result = false;
            System.out.println("Unable to fetch the current quote for ticker: " + ticker);
        }
        return result;
    }

    private void sellStock(String ticker) {
        try{
            boolean viewPositionResult = viewPosition(ticker);
            if(viewPositionResult){
                System.out.println("Are you sure you want to sell? (yes/no)");
                Scanner scanner = new Scanner(System.in);
                String confirm = scanner.nextLine().trim().toLowerCase();
                if(!confirm.equals("yes")){
                    System.out.println("Sell cancelled.");
                    return;
                }

                positionService.sell(ticker);
                System.out.println("Sold all shares of: " + ticker);
            }
        } catch (IllegalArgumentException e){
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void buyStock(String ticker, Scanner scanner) {
        try {
            Optional<Quote> quoteOptional = quoteService.fetchQuoteDataFromAPI(ticker);
            if (!quoteOptional.isPresent()) {
                System.out.println("No quote data found for ticker: " + ticker);
                return;
            }
            Quote currentQuote = quoteOptional.get();
            System.out.println("Quote for " + ticker + ":");
            System.out.println("Current Price: " + currentQuote.getPrice());
            System.out.println("Open: " + currentQuote.getOpen());
            System.out.println("High: " + currentQuote.getHigh());
            System.out.println("Low: " + currentQuote.getLow());
            System.out.println("Volume: " + currentQuote.getVolume());

            System.out.println("Do you want to proceed with buying this stock? (yes/no)");
            String response = scanner.nextLine();

            if(!response.equalsIgnoreCase("yes")){
                System.out.println("Purchase cancelled.");
                return;
            }
            System.out.println("Enter the number of shares you want to buy:");
            int shares = Integer.parseInt(scanner.nextLine());
            System.out.println("Enter the price per share you are willing to pay:");
            double price = Double.parseDouble(scanner.nextLine());

            if (price < currentQuote.getPrice()) {
                System.out.println("The price must be higher than the current quote price. Buy operation aborted.");
                return;
            }
            Position position = positionService.buy(ticker, shares, price);
            System.out.println("Successfully bought: " + shares + " shares of " + ticker);
            System.out.println("Total cost: $" + position.getValuePaid());
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void fetchQuote(String ticker) {
        Optional<Quote> quote = quoteService.fetchQuoteDataFromAPI(ticker);
        if(quote.isPresent()){
            System.out.println("Quote fetched: " + quote.toString());
        } else{
            System.out.println("No quote data found for ticker: " + ticker);
        }
    }
}
