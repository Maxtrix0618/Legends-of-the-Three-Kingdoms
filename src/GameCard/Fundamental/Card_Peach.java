package GameCard.Fundamental;

import GameCard.Card;
import GameCard.CardName;
import GameCard.Point;
import GameCard.Suit;
import GamePlayer.Player;

import java.util.ArrayList;
import java.util.Scanner;

public class Card_Peach extends Card {
    public Card_Peach(Suit suit, Point point) {
        super(suit, point, CardName.PEACH);
    }


    @Override
    public boolean use(Scanner INPUT, ArrayList<Player> players, Player user, Player target) {
        if (target == null)
            target = user;

        if(super.use(INPUT, players, user, target)) {
            target.recover(1);
            return true;
        }
        return false;
    }



    @Override
    public boolean canUse(Player user) {
        return (super.canUse(user) && user.HP < user.HP_Limit);

    }

}
