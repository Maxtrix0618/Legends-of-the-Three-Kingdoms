package GameCard.Fundamental;

import GameCard.CardName;
import GameCard.Point;
import GameCard.Suit;

public class Card_Kill_fire extends Card_Kill {

    public Card_Kill_fire(Suit suit, Point point) {
        super(suit, point);
        cardName = CardName.KILL_fire;
        hurtType = 2;
    }

}
