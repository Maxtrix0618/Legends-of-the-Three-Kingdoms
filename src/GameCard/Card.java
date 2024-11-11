package GameCard;

import java.util.ArrayList;
import java.util.Scanner;

import GameCard.Fundamental.Card_Dodge;
import GameCard.Fundamental.Card_Kill;
import GameCard.Fundamental.Card_Peach;
import GameCard.Fundamental.Card_Wine;
import GamePlayer.Player;

/**
 * 游戏卡牌
 */
public class Card {

    public final Suit suit;             // 花色
    public final Point point;           // 点数
    public CardName cardName;           // 牌名
    public boolean realCard;            // 是否是原始卡牌（非转化牌、虚拟牌）
    public boolean canUse_forwardly;    // 是否可主动使用
    public int weapon_attackRange;                  // 攻击范围（仅武器牌使用，其他牌此值为0）
    public int hurtType;                            // 伤害类型（暂仅供【杀】使用，其他牌此值为0）
    public Player equipment_master;                 // 装备牌的主人（仅装备牌使用，其他牌此值为null）
    public Card[] originCards;                 // 原始牌（集）（仅转化牌使用，存储此转化牌原本的切实卡牌（集），其他牌此值为null）


    public Card(Suit suit, Point point, CardName cardName) {
        this.suit = suit;
        this.point = point;
        this.cardName = cardName;
        this.realCard = true;
        this.canUse_forwardly = true;
        this.weapon_attackRange = 0;
        this.hurtType = 0;
        this.equipment_master = null;
        this.originCards = null;
    }



    /**
     * 返回简要卡牌信息（用于判定牌、装备牌展示）
     */
    public String brief_Card_Message() {
        return ("|" + suit.Symbol + point.Name + "-" + cardName.shortName + "|");
    }
    public void show_brief_Card_Message() {         // 直接打印之（公开）
        Player.SystemPrint(brief_Card_Message());
    }

    /**
     * 返回完整卡牌信息（用于手牌显示）
     */
    public String complete_Card_Message() {
        String NRC_hint = (!realCard && originCards != null) ? "(转化)" : "";
        return ("〖" + NRC_hint + suit.Symbol + point.Name + " "  + cardName.Name + "〗");
    }
    public void show_complete_Card_Message(Player cardMaster, boolean privateMessage) {      // 直接打印之（可选公开或仅个人队伍能见）
        if (privateMessage)
            cardMaster.SelfPrint(complete_Card_Message());
        else
            Player.SystemPrint(complete_Card_Message());
    }


    /**
     * 使用牌（通用法）
     * 若成功使用返回true，取消了则返回false
     */
    public boolean use(Scanner Input, ArrayList<Player> players, Player user, Player target) {
        show_complete_Card_Message(user, true);
        if (realCard && !user.AI) {
            user.SelfPrint(" ——— 使用此牌？（取消：0）");
            if (Input.nextInt() != 0) {
                user.transferCard(this, user.HD_cards, user.discard_pile);
                Player.printOpeCase(user, target, "使用了", complete_Card_Message());
                return true;
            }
            return false;
        } else {
            user.transferCard(this, user.HD_cards, user.discard_pile);
            Player.printOpeCase(user, target, "使用了", complete_Card_Message());
            return true;
        }
    }
    public boolean use(Scanner Input, ArrayList<Player> players, Player user, Player target, CardName cardName) {
        if (Player.isCard_S(this, cardName))
            return (use(Input, players, user, target));
        else
            switch (cardName) {
                case KILL:
                    user.use_virtualCard(new Card_Kill(suit, point), new Card[]{this}, target);
                    break;
                case PEACH:
                    user.use_virtualCard(new Card_Peach(suit, point), new Card[]{this}, target);
                    break;
                case WINE:
                    user.use_virtualCard(new Card_Wine(suit, point), new Card[]{this}, target);
                    break;
            }
        return true;
    }


    /**
     * 打出牌（通用法）
     */
    public void play(Player player) {
        Player.printOpeCase(player, null, "打出了", this.complete_Card_Message());
        player.abandon_card_from(player.HD_cards, this);
    }
    public void play(Player player, CardName cardName) {
        if (Player.isCard_S(this, cardName))
            play(player);
        else
            switch (cardName) {
                case KILL:
                    player.play_virtualCard(new Card_Kill(suit, point), new Card[]{this});
                    break;
                case DODGE:
                    player.play_virtualCard(new Card_Dodge(suit, point), new Card[]{this});
                    break;
                case PEACH:
                    player.play_virtualCard(new Card_Peach(suit, point), new Card[]{this});
                    break;
                case WINE:
                    player.play_virtualCard(new Card_Wine(suit, point), new Card[]{this});
                    break;
            }

    }



    /**
     * 是否可用
     */
    public boolean canUse(Player user) {
        return canUse_forwardly;
    }


}
