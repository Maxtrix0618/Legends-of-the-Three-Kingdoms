package Means;

import GameCard.Suit;
import java.util.ArrayList;

public class Gain_Suit {

    public String Name;
    public ArrayList<Suit> suits;

    public Gain_Suit(String name) {
        this.Name = name;
        this.suits = new ArrayList<>();
    }

    public String symbols() {
        String symbolList = "";
        for (Suit suit : suits)
            symbolList += suit.Symbol;
        return symbolList;
    }

}
