package GamePlayer;

import GameCard.Card;
import Means.Gain_Suit;

import java.util.ArrayList;
import java.util.Scanner;

public class M_HuangZhong extends Player {

    public M_HuangZhong(Scanner INPUT, ArrayList<Player> players, ArrayList<Card> pile, ArrayList<Card> discard_pile, int order, int team) {
        super(INPUT, players, pile, discard_pile, "谋-黄忠", order, 4, 4, team);
    }
    public Gain_Suit LieGongGS = new Gain_Suit("烈弓");


    @Override
    public void ST() {
        if (LieGongGS.suits.size() > 0) {
            SystemPrint("〔" + LieGongGS.Name + " " + LieGongGS.symbols() + "〕");
        }
        super.ST();
    }








}
