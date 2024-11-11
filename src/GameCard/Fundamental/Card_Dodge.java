package GameCard.Fundamental;


import GameCard.Card;
import GameCard.CardName;
import GameCard.Point;
import GameCard.Suit;

public class Card_Dodge extends Card {

    public Card_Dodge(Suit suit, Point point) {
        super(suit, point, CardName.DODGE);
        this.canUse_forwardly = false;
    }


}
