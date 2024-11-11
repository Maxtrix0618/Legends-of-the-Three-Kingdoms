package GameCard.Stratagem;

import GameCard.Card;
import GameCard.CardName;
import GameCard.Point;
import GameCard.Suit;
import GamePlayer.Player;

import java.util.ArrayList;
import java.util.Scanner;


public class Card_WuXie extends Card {
    public Card_WuXie(Suit suit, Point point) {
        super(suit, point, CardName.WuXie);
        this.canUse_forwardly = false;
    }


    public boolean use(Scanner Input, ArrayList<Player> players, Player user, Player target) {
        user.abandon_card_from(user.HD_cards, this);
        Player.printOpeCase(user, target, "使用了", complete_Card_Message());
        return true;
    }




}
