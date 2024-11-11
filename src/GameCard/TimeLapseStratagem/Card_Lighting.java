package GameCard.TimeLapseStratagem;

import GameCard.Card;
import GameCard.CardName;
import GameCard.Point;
import GameCard.Suit;
import GamePlayer.Player;

import java.util.ArrayList;
import java.util.Scanner;

public class Card_Lighting extends Card {

    public Card_Lighting(Suit suit, Point point) {
        super(suit, point, CardName.Lighting);
    }


    @Override
    public boolean use(Scanner INPUT, ArrayList<Player> players, Player user, Player target) {
        if (user.haveTLSCard(CardName.Lighting))
            user.SelfPrintln("<使用失败：你的判定区内已有一张【闪电】>");
        else {
            show_complete_Card_Message(user, true);
            user.SelfPrint(" ——— 使用此牌？（取消：0）");
            if (INPUT.nextInt() != 0) {
                Player.printOpeCase(user, user, "使用了", complete_Card_Message());
                user.transferCard(this, user.HD_cards, user.RJ_area);
                return true;
            }
        }
        return false;
    }

}
