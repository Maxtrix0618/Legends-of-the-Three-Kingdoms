package GamePlayer;

import GameCard.Card;
import GameCard.CardName;
import GameCard.Fundamental.Card_Kill;
import Skill.SkillName;

import java.util.ArrayList;
import java.util.Scanner;

public class ZhaoYun extends Player {

    public ZhaoYun(Scanner INPUT, ArrayList<Player> players, ArrayList<Card> pile, ArrayList<Card> discard_pile, int order, int team) {
        super(INPUT, players, pile, discard_pile, "赵云", order, 4, 4, team);
        skillPool.add(SkillName.LongDan);
    }


    @Override
    public boolean isCard(Card card, CardName cardName) {
        if ((super.isCard(card, CardName.KILL) && cardName == CardName.DODGE) || (super.isCard(card, CardName.DODGE) && cardName == CardName.KILL)) {
            super.useSkill(SkillName.LongDan);
            return true;
        }
        else
            return super.isCard(card, cardName);
    }


    @Override
    protected void useSkill(SkillName skillName) {
        if (skillName == SkillName.LongDan) {
            int IN = -1;
            show_private_Situation();
            SelfPrint("⊙ 请选择一张【闪】转化为【杀】");
            while (IN != 0 && (IN < 1 || IN > HD_cards.size() || !(HD_cards.get(IN - 1).cardName == CardName.DODGE))) {
                SelfPrint("->");
                IN = INPUT.nextInt();
            }
            if (IN != 0) {
                super.useSkill(skillName);
                Card oriCard = HD_cards.get(IN - 1);
                Card_Kill newCard = new Card_Kill(oriCard.suit, oriCard.point);

                int P = askToChoosePlayer(false, false, true, false, newCard, 0, -1);
                use_virtualCard(newCard, new Card[]{oriCard}, players.get(P - 1));
                HD_cards.remove(oriCard);
                delay(800);
            }
        }
    }

}