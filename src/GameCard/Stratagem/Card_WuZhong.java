package GameCard.Stratagem;

import GameCard.Card;
import GameCard.CardName;
import GameCard.Point;
import GameCard.Suit;
import GamePlayer.Player;

import java.util.ArrayList;
import java.util.Scanner;

public class Card_WuZhong extends Card {

    public Card_WuZhong(Suit suit, Point point) {
        super(suit, point, CardName.WuZhong);
    }


    @Override
    public boolean use(Scanner INPUT, ArrayList<Player> players, Player user, Player target) {
        show_complete_Card_Message(user, true);
        user.SelfPrint(" ——— 使用此牌？（取消：0）");
        if (user.AI || INPUT.nextInt() != 0) {
            Player.printOpeCase(user, user, "使用了", complete_Card_Message());
            user.abandon_card_from(user.HD_cards, this);
            user.beUsed_WuZhong(user, this);
            return true;
        }
        return false;
    }

}
