package GameCard.Fundamental;

import GameCard.Card;
import GameCard.CardName;
import GameCard.Point;
import GameCard.Suit;
import GamePlayer.Player;

import java.util.ArrayList;
import java.util.Scanner;

public class Card_Kill extends Card {

    public Card_Kill(Suit suit, Point point) {
        super(suit, point, CardName.KILL);
        hurtType = 1;
    }



    @Override
    public boolean use(Scanner INPUT, ArrayList<Player> players, Player user, Player target) {
        if (target != null) {
            if (this.realCard)
                user.abandon_card_from(user.HD_cards, this);
            user.kill(target, this, 1);
            return true;
        } else {
            if (!(user.haveEquipment(CardName.FangTianJi) && user.HD_cards.size() == 1)) {
                int P = user.askToChoosePlayer(false, false, true, false, this, 0, -1);
                if (P != 0) {
                    user.SelfPrintln("<【杀】指定目标为：" + players.get(P - 1).Name + ">");
                    if (user.inSelfTurn)
                        user.attack_limit --;
                    user.abandon_card_from(user.HD_cards, this);
                    user.kill(players.get(P - 1), this, 1);
                    return true;
                }
                return false;
            } else {                        // 若killer装备了【方天画戟】且手牌数为1（最后一张【杀】）
                user.printOnSet_Equipment(4);
                int[] Ps = new int[3];
                for (int i = 0; i < Ps.length; i++) {
                    int p = user.askToChoosePlayer(false, false, true, false, this, i + 1, -1);
                    if (p != 0)
                        Ps[i] = p;
                    else if (i == 0) return false;      // 在选择第1目标时就退出，则此【杀】不使用，返还到手牌
                    else break;                         // 在选择第2、3目标时退出，则此【杀】正常使用，但不选择更多的目标
                }
                if (user.inSelfTurn)
                    user.attack_limit --;
                user.abandon_card_from(user.HD_cards, this);
                user.SelfPrint("<【杀】指定目标为：");
                for (int p : Ps)
                    if (p != 0)
                        user.SelfPrint("[" + players.get(p - 1).Name + "]");
                user.SelfPrintln(">");
                for (int p : Ps)
                    if (p != 0) {
                        user.kill(players.get(p - 1), this, 1);
                    }
                return true;
            }
        }
    }


    @Override
    public boolean canUse(Player user) {
        return (super.canUse(user) && (user.attack_limit > 0 || user.haveEquipment(CardName.ZhuGeNu)));
    }

}
