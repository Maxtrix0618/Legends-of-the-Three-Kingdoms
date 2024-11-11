package GameCard.Fundamental;

import GameCard.Card;
import GameCard.CardName;
import GameCard.Point;
import GameCard.Suit;
import GamePlayer.Player;

import java.util.ArrayList;
import java.util.Scanner;

public class Card_Wine extends Card {

    public Card_Wine(Suit suit, Point point) {
        super(suit, point, CardName.WINE);
    }


    @Override
    public boolean use(Scanner INPUT, ArrayList<Player> players, Player user, Player target) {
        target = user;

        if(super.use(INPUT, players, user, user)) {
            if (target.HP <= 0)
                target.recover(1);
            else {
                target.drink_limit--;
                target.drunk = true;
            }
            return true;
        }
        return false;
    }


    @Override
    public boolean canUse(Player user) {
        return (super.canUse(user) && (user.drink_limit > 0));
    }

}
