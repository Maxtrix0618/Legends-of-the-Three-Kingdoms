package GameCard.Stratagem;

import GameCard.Card;
import GameCard.CardName;
import GameCard.Point;
import GameCard.Suit;
import GamePlayer.Player;

import java.util.ArrayList;
import java.util.Scanner;

public class Card_JieDao extends Card {

    public Card_JieDao(Suit suit, Point point) {
        super(suit, point, CardName.JieDao);
    }


    @Override
    public boolean use(Scanner INPUT, ArrayList<Player> players, Player user, Player target) {
        int IN = user.askToChoosePlayer(false, false, false, false, this, 0, -1);
        if (IN != 0) {
            int P = -1;
            user.SelfPrint("指定被【杀】的目标：（座次号）（输入'0'取消）");
            while (P != 0 && (P < 1 || P > players.size() || !players.get(IN - 1).canAttack(players.get(P - 1)))) {
                if (!players.get(IN - 1).canAttack(players.get(P - 1)))
                    user.SelfPrint("<对方无法指定该角色为【杀】的目标>");
                user.SelfPrint("->");
                P = INPUT.nextInt();
            }
            if (P != 0) {
                user.abandon_card_from(user.HD_cards, this);
                players.get(IN - 1).beUsed_JieDao(user, players.get(P - 1), this);
                return true;
            }
        }
        return false;
    }




}
