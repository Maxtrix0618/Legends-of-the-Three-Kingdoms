package GameCard.Stratagem;

import GameCard.Card;
import GameCard.CardName;
import GameCard.Point;
import GameCard.Suit;
import GamePlayer.Player;

import java.util.ArrayList;
import java.util.Scanner;

public class Card_TieSuo extends Card {

    public Card_TieSuo(Suit suit, Point point) {
        super(suit, point, CardName.TieSuo);
    }


    @Override
    public boolean use(Scanner INPUT, ArrayList<Player> players, Player user, Player target) {

        boolean F = user.askToConfirm("请选择一项：[ 0.重铸此牌 | 1.使用此牌 ]", 0);
        if (!F) {
            Player.printOpeCase(user, null, "重铸了", cardName.Name);
            user.abandon_card_from(user.HD_cards, this);
            user.drawCard_fromPile(1);
        } else {
            int[] Ps = new int[2];
            for (int i = 0; i < Ps.length; i++) {
                int p = user.askToChoosePlayer(true, false, false, false, this, i + 1, -1);
                if (p != 0)
                    Ps[i] = p;
                else if (i == 0) return false;          // 在选择第1目标时退出，则此【铁索连环】不使用，返还到手牌
                else break;                             // 在选择第2目标时退出，则此【铁索连环】正常使用，但不选择更多的目标
            }
            user.abandon_card_from(user.HD_cards, this);
            user.SelfPrint("<【铁索连环】指定目标为：");
            for (int p : Ps)
                if (p != 0)
                    user.SelfPrint("[" + players.get(p - 1).Name + "]");
            user.SelfPrintln(">");
            for (int p : Ps)
                if (p != 0) {
                    players.get(p - 1).beUsed_TieSuo(user, this);
                }
        }
        return true;

    }





}
