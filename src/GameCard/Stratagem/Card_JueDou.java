package GameCard.Stratagem;

import GameCard.Card;
import GameCard.CardName;
import GameCard.Point;
import GameCard.Suit;
import GamePlayer.Player;

import java.util.ArrayList;
import java.util.Scanner;

public class Card_JueDou extends Card {

    public Card_JueDou(Suit suit, Point point) {
        super(suit, point, CardName.JueDou);
    }


    @Override
    public boolean use(Scanner INPUT, ArrayList<Player> players, Player user, Player target) {
        int IN = user.askToChoosePlayer(false, false, false, false, this, 0, -1);
        if (IN != 0) {
            user.abandon_card_from(user.HD_cards, this);
            players.get(IN - 1).beUsed_JueDou(user, this);
            return true;
        }
        return false;
    }


}
