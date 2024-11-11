package GameCard.Equipment.Weapon;

import GameCard.Card;
import GameCard.CardName;
import GameCard.Point;
import GameCard.Suit;
import GamePlayer.Player;

import java.util.ArrayList;
import java.util.Scanner;

public class Card_GuDingDao extends Card {

    public Card_GuDingDao(Suit suit, Point point) {
        super(suit, point, CardName.GuDingDao);
        weapon_attackRange = 2;
    }


    @Override
    public boolean use(Scanner INPUT, ArrayList<Player> players, Player user, Player target) {
        if (super.use(INPUT, players, user, user)) {
            user.use_Equipment(this);
            return true;
        }
        return false;
    }

}
