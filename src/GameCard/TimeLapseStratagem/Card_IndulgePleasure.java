package GameCard.TimeLapseStratagem;

import GameCard.Card;
import GameCard.CardName;
import GameCard.Point;
import GameCard.Suit;
import GamePlayer.Player;

import java.util.ArrayList;
import java.util.Scanner;

public class Card_IndulgePleasure extends Card {

    public Card_IndulgePleasure(Suit suit, Point point) {
        super(suit, point, CardName.IndulgePleasure);
    }


    @Override
    public boolean use(Scanner INPUT, ArrayList<Player> players, Player user, Player target) {
        int IN = user.askToChoosePlayer(false, false, false, false, this, 0, -1);
        if (IN != 0 && players.get(IN - 1).haveTLSCard(CardName.IndulgePleasure))
            user.SelfPrintln("<使用失败：目标角色的判定区内已有一张【乐不思蜀】>");
        else if (IN != 0) {
            Player.printOpeCase(user, players.get(IN - 1), "使用了", complete_Card_Message());
            user.transferCard(this, user.HD_cards, players.get(IN - 1).RJ_area);
            return true;
        }
        return false;
    }

}
