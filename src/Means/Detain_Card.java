package Means;

import GameCard.Card;
import java.util.ArrayList;

/**
 * 扣置牌集
 */
public class Detain_Card {

    public String Name;
    public ArrayList<Card> cards;

    public Detain_Card(String name) {
        this.Name = name;
        this.cards = new ArrayList<>();
    }


}
