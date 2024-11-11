package GameCard.Fundamental;

import GameCard.CardName;
import GameCard.Point;
import GameCard.Suit;

public class Card_Kill_lightning extends Card_Kill {

    public Card_Kill_lightning(Suit suit, Point point) {
        super(suit, point);
        cardName = CardName.KILL_lighting;
        hurtType = 3;
    }


}
