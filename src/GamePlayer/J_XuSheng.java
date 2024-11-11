package GamePlayer;

import GameCard.Card;
import GameCard.Equipment.Weapon.Card_GuDingDao;
import GameCard.Fundamental.Card_Kill_fire;
import GameCard.Fundamental.Card_Wine;
import GameCard.Point;
import GameCard.Stratagem.Card_TieSuo;
import GameCard.Suit;
import Means.Detain_Card;
import Skill.SkillName;

import java.util.ArrayList;
import java.util.Scanner;

public class J_XuSheng extends Player {

    public J_XuSheng(Scanner INPUT, ArrayList<Player> players, ArrayList<Card> pile, ArrayList<Card> discard_pile, int order, int team) {
        super(INPUT, players, pile, discard_pile, "界-徐盛", order, 4, 4, team);
    }

    @Override
    protected void cheatCard() {
        HD_cards.add(new Card_GuDingDao(Suit.E, Point.E));
        HD_cards.add(new Card_Wine(Suit.E, Point.E));
        HD_cards.add(new Card_TieSuo(Suit.E, Point.E));
        HD_cards.add(new Card_Kill_fire(Suit.E, Point.E));

        show_HandCard();
    }


    @Override
    public void kill(Player sufferer, Card killCard, int hurtValue) {
        if (askToConfirm("是否发动『破军』？", 1)) {
            useSkill(SkillName.J_PoJun);
            Detain_Card PoJunCD = new Detain_Card("破军");
            while (PoJunCD.cards.size() < sufferer.HP && sufferer.HD_cards.size() > 0) {
                transferCard(sufferer.HD_cards.get(0), sufferer.HD_cards, PoJunCD.cards);
            }
            sufferer.Detain_List.add(PoJunCD);
            if (sufferer.HD_cards.size() <= this.HD_cards.size() && sufferer.EP_area.size() <= this.HD_cards.size())
                hurtValue ++;

            sufferer.show_FM();
            sufferer.show_ST();
        }
        super.kill(sufferer, killCard, hurtValue);
    }


    @Override
    public void stage_End() {
        System.out.println("◈ 回合结束阶段");
        for (Player player : players) {
            for (Detain_Card cardDetain : player.Detain_List) {
                if (cardDetain.Name.equals("破军")) {
                    player.HD_cards.addAll(cardDetain.cards);
                    player.Detain_List.remove(cardDetain);
                    break;
                }
            }
        }
        super.overSet();
        Player.delay(600);
    }


}
