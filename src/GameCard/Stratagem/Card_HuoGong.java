package GameCard.Stratagem;

import GameCard.Card;
import GameCard.CardName;
import GameCard.Point;
import GameCard.Suit;
import GamePlayer.Player;

import java.util.ArrayList;
import java.util.Scanner;

public class Card_HuoGong extends Card {

    public Card_HuoGong(Suit suit, Point point) {
        super(suit, point, CardName.HuoGong);
    }


    @Override
    public boolean use(Scanner INPUT, ArrayList<Player> players, Player user, Player target) {
        int IN = user.askToChoosePlayer(true, false, false, true, this, 0, -1);
        if (IN != 0) {
            user.abandon_card_from(user.HD_cards, this);
            players.get(IN - 1).beUsed_HuoGong(user, this);
            return true;
        }
        return false;
    }

}
