package org.example;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class SearchDiscount extends TelegramLongPollingBot {

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String chatId = update.getMessage().getChatId().toString();
            String messageText = update.getMessage().getText();

            if (messageText.equals("/start")) {
                sendMessage(chatId, "Привет! Я бот, который присылает список игр в Steam с 90% скидкой. Напиши /discount, чтобы получить список.");
            } else if (messageText.equals("/discount")) {
                String discountGames = getDiscountGames();
                sendMessage(chatId, discountGames);
            } else {
                sendMessage(chatId, "UNKNOWN MESSAGE");
            }
        }
    }

    private void sendMessage(String chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private String getDiscountGames() {
        StringBuilder gamesList = new StringBuilder("Список игр с 90% скидкой:\n");
        try {
            Document doc = Jsoup.connect("https://store.steampowered.com/search/?supportedlang=russian&specials=1&ndl=1").get();
            Elements games = doc.select(".search_result_row");

            for (Element game : games) {
                String discount = game.select(".discount_pct").text();
                if (discount.equals("-90%")) {
                    String gameName = game.select(".title").text();
                    String originalPrice = game.select(".discount_original_price").text();
                    String finalPrice = game.select(".discount_final_price").text();
                    gamesList.append(gameName).append(" (").append(originalPrice).append(" -> ").append(finalPrice).append(")\n");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return gamesList.toString();
    }

    @Override
    public String getBotUsername() {
        return "DiscountSteamBot";
    }

    @Override
    public String getBotToken() {
        return "7653821240:AAF1s7vEutOIS-pfv0O9ZlxMKn7cCTQ9zb0";
    }
}

