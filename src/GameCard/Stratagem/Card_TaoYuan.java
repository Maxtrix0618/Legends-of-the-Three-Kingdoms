package GameCard.Stratagem;

import GameCard.Card;
import GameCard.CardName;
import GameCard.Point;
import GameCard.Suit;
import GamePlayer.Player;

import java.util.ArrayList;
import java.util.Scanner;

public class Card_TaoYuan extends Card {

    public Card_TaoYuan(Suit suit, Point point) {
        super(suit, point, CardName.TaoYuan);
    }


    @Override
    public boolean use(Scanner INPUT, ArrayList<Player> players, Player user, Player target) {
        show_complete_Card_Message(user, true);
        user.SelfPrint(" ——— 使用此牌？（取消：0）");
        if (user.AI || INPUT.nextInt() != 0) {
            Player.printOpeCase(user, null, "使用了", complete_Card_Message());
            user.abandon_card_from(user.HD_cards, this);

            if (user.HP < user.HP_Limit)    // 血不满才能被【桃园结义】指定为目标
                user.beUsed_TaoYuan(user, this);
            Player player = user.subsequentPlayer(1);
            while (player != user) {
                Player nextPlayer = player.subsequentPlayer(1);
                if (player.HP < player.HP_Limit)
                    player.beUsed_TaoYuan(user, this);
                player = nextPlayer;
            }

            for (Player player1 : players)
                player1.ignoreToUseWuXie = false;
            return true;
        }
        return false;
    }





}
