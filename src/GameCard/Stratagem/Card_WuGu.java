package GameCard.Stratagem;

import GameCard.Card;
import GameCard.CardName;
import GameCard.Point;
import GameCard.Suit;
import GamePlayer.Player;

import java.util.ArrayList;
import java.util.Scanner;

public class Card_WuGu extends Card {

    public Card_WuGu(Suit suit, Point point) {
        super(suit, point, CardName.WuGu);
    }


    @Override
    public boolean use(Scanner INPUT, ArrayList<Player> players, Player user, Player target) {
        show_complete_Card_Message(user, true);
        user.SelfPrint(" ——— 使用此牌？（取消：0）");
        if (user.AI || INPUT.nextInt() != 0) {
            Player.printOpeCase(user, null, "使用了", complete_Card_Message());
            ArrayList<Card> WuGu_warehouse = user.showCard_fromPile(players.size());
            user.abandon_card_from(user.HD_cards, this);

            user.beUsed_WuGu(user, this, WuGu_warehouse);
            Player player = user.subsequentPlayer(1);
            while (player != user) {
                Player nextPlayer = player.subsequentPlayer(1);
                player.beUsed_WuGu(user, this, WuGu_warehouse);
                player = nextPlayer;
            }

            for (Player player1 : players)
                player1.ignoreToUseWuXie = false;
            return true;
        }
        return false;
    }

}
