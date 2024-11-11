package GamePlayer;

import GameCard.Card;
import GameCard.CardName;
import GameCard.Fundamental.Card_Dodge;
import GameCard.Fundamental.Card_Kill;
import GameCard.Fundamental.Card_Kill_fire;
import GameCard.Point;
import GameCard.Suit;
import Means.Detain_Card;
import Skill.SkillName;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

public class Player {
    public final Scanner INPUT;
    public final Random RD = new Random();

    public final String Name;
    public int Order;             // 座次
    public final int Team;        // 玩家队伍（>0：人类，<0：电脑）
    public final boolean AI;      // 玩家是否为电脑
    public boolean LIFE;        // 存活

    public int HP_Limit;
    public int HP;
    public int attack_limit;    // 每回合内使用【杀】次数限制
    public int drink_limit;     // 每回合内使用【酒】次数限制
    public boolean drunk;       // 酒后（非用于濒死自救）
    public boolean interlink;          // 横置状态
    public boolean ignoreToUseWuXie;   // 在某轮中选择忽略使用【无懈可击】

    public boolean inSelfTurn;          // 是否在自己回合内
    public boolean DrawStage_available;      // 摸牌阶段可用
    public boolean PlayStage_available;      // 出牌阶段可用

    public ArrayList<Player> players;       // 玩家列表
    public ArrayList<Card> pile;            // 牌堆
    public ArrayList<Card> discard_pile;    // 弃牌堆

    public ArrayList<Card> HD_cards = new ArrayList<>();    // 手牌区
    public ArrayList<Card> RJ_area = new ArrayList<>();     // 判定区
    public ArrayList<Card> EP_area = new ArrayList<>();     // 装备区
    public ArrayList<Detain_Card> Detain_List = new ArrayList<>();    // 扣置牌集之列集
    public ArrayList<SkillName> skillPool = new ArrayList<>();     // 武将当前技能池（仅出牌阶段主动发动的技能）



    /**
     * Player构造函数
     * @param team 玩家队伍（正数<如1>：人类，负数<如-1>：电脑）
     */
    public Player(Scanner INPUT, ArrayList<Player> players, ArrayList<Card> pile, ArrayList<Card> discard_pile,
                  String name, int order, int HP_Limit, int HP, int team) {
        this.INPUT = INPUT;
        this.Name = name;
        this.Order = order;
        this.Team = team;
        this.AI = (team < 0);
        this.HP_Limit = HP_Limit;
        this.HP = HP;
        this.attack_limit = 1;
        this.drink_limit = 1;
        this.LIFE = true;
        this.drunk = false;
        this.interlink = false;
        this.ignoreToUseWuXie = false;

        this.players = players;
        this.pile = pile;
        this.discard_pile = discard_pile;

        this.inSelfTurn = false;
        this.DrawStage_available = true;
        this.PlayStage_available = true;


    }




    /**
     * 游戏开始准备
     */
    public void GameStart() {
        if (!AI) {
            boolean CZ = true;
            while (CZ) {
                while (HD_cards.size() > 0)
                    transferCard(HD_cards.get(0), HD_cards, pile);
                drawCard_fromPile(4);
                CZ = askToConfirm("是否重铸初始手牌？", 0);
                if (CZ)
                    SystemPrintln("➧ [" + Name + "]将4张牌放回牌堆");
            }
        } else
            drawCard_fromPile(4);
        System.out.println();
    }


    /**
     * 某牌是否是某种牌名的牌（【杀】不论属性）
     */
    public boolean isCard(Card card, CardName cardName) {
        if (cardName == CardName.KILL)
            return (card.cardName == CardName.KILL || card.cardName == CardName.KILL_fire || card.cardName == CardName.KILL_lighting);
        else
            return (card.cardName == cardName);
    }
    public static boolean isCard_S(Card card, CardName cardName) {
        if (cardName == CardName.KILL)
            return (card.cardName == CardName.KILL || card.cardName == CardName.KILL_fire || card.cardName == CardName.KILL_lighting);
        else
            return (card.cardName == cardName);
    }



    // 【杀】指定目标之后
    /**
     * 使用【杀】指定目标
     */
    public void kill(Player sufferer, Card killCard, int hurtValue) {
        Player.printOpeCase(this, sufferer, "使用了", killCard.complete_Card_Message());
        if (haveEquipment(CardName.CiXiongJian) && askToConfirm("是否发动【雌雄双股剑】，令对方选择弃牌或让你摸牌？（确定：1，取消：0）", 1)) {      // 这里届时还要加一条件：【杀】指定的角色与使用者性别不同
            printOnSet_Equipment(4);
            boolean CI = sufferer.askToConfirm("请选择一项：[ 0.弃1张牌 | 1.对方摸1张牌 ]", 0);
            if (CI)
                drawCard_fromPile(1);
            else
                sufferer.askToDiscard(1);
        }
        if (haveEquipment(CardName.ZhuQueShan) && (killCard.cardName == CardName.KILL) && askToConfirm("是否发动【朱雀羽扇】，将此【杀 ➻】转化为【杀 🔥】？（确定：1，取消：0）", 1)) {
            printOnSet_Equipment(4);
            use_virtualCard(new Card_Kill_fire(killCard.suit, killCard.point), new Card[]{killCard}, sufferer);          // 使用转化【火杀】
        } else {
            sufferer.beKilled(this, killCard.hurtType, hurtValue);
        }
    }

    // 玩家成为【杀】的目标之后
    /**
     * 遭受【杀】指定为目标
     */
    public void beKilled(Player killer, int hurtType, int hurtValue) {
        if (!(protectedByArmorFromKILL(CardName.TengJia, killer) && hurtType == 1)) {
            SystemPrintln("————————————————————————————————————————————————");
            SystemPrintln("请[" + Name + "]响应[" + killer.Name + "]的【杀】");

            if (killer.drunk) {
                hurtValue ++;
                killer.drunk = false;
            }

            boolean Dodge = false;      // 是否打出了【闪】
            if (haveEquipment(CardName.BaGuaZhen) && askToConfirm("是否发动【八卦阵】进行判定，若为红色则视为打出一张【闪】？（确定：1，取消：0）", 0))
                if (RJ_Binomial("【八卦阵】", new Suit[]{Suit.HEART, Suit.DIAMOND}, null)) {
                    printOnSet_Equipment(5);
                    play_virtualCard(new Card_Dodge(Suit.E, Point.E), null);
                    Dodge = true;
                }
            if (!Dodge) {
                int IN = askToPlayCard(CardName.DODGE, "", 1);
                Dodge = (IN != 0);
            }

            if (!Dodge) {       // 未打出【闪】
                hurtByKILLCard(hurtValue, hurtType, killer);
            } else {            // 打出了一张【闪】
                if (killer.haveEquipment(CardName.YanYueDao) && killer.HD_cards.size() > 0) {
                    int C = killer.askToUseKill(this, "来发动【青龙偃月刀】追杀对方？", false, 1);
                    if (C != 0) {
                        killer.printOnSet_Equipment(4);
                        killer.HD_cards.get(C - 1).use(INPUT, players, killer, this);
                    }
                } else if (killer.haveEquipment(CardName.GuanShiFu) && (killer.HD_cards.size() + killer.EP_area.size() >= 2)) {
                    if (killer.askToConfirm("是否发动【贯石斧】弃置2张牌，然后此【杀】仍造成伤害？（确定：1，取消：0）", 0)) {
                        killer.printOnSet_Equipment(4);
                        for (int i = 1; i <= 2; i++)
                            killer.askToOpeSelfCard(2, true, true, false);
                        hurtByKILLCard(hurtValue, hurtType, killer);
                    }
                }
            }
        } else
            this.printOnSet_Equipment(5);

    }
    // 【杀】可造成伤害时
    private void hurtByKILLCard(int hurtValue, int type, Player killer) {
        if (killer.haveEquipment(CardName.HanBingJian) && killer.askToConfirm("是否发动【寒冰剑】，取消此伤害并弃置对方2张牌？（确定：1，取消：0）", 0)) {
            killer.printOnSet_Equipment(4);
            for (int i = 1; i <= 2; i++)
                killer.askToOpeOthersCard(this, 2, true, true, false);
        } else {
            if (killer.haveEquipment(CardName.GuDingDao) && HD_cards.size() == 0) {
                killer.printOnSet_Equipment(4);
                hurtValue ++;
            }
            if (killer.haveEquipment(CardName.QiLinGong) && (this.haveEquipment(6) || this.haveEquipment(7)))
                if (killer.askToConfirm("是否发动【麒麟弓】，弃置对方装备区里的一张坐骑牌？（确定：1，取消：0）", 1)) {
                    killer.printOnSet_Equipment(4);
                    killer.abandonOthersEPCard(this, new int[]{6, 7});
                }
            tryHurt(hurtValue, type, killer);
        }

    }


    /**
     * 恢复体力（最多恢复到体力上限）
     */
    public void recover(int value) {
        int HP_origin = HP;
        HP += value;
        if (HP > HP_Limit) {
            HP = HP_Limit;
            SystemPrintln("❤ [" + Name + "]恢复了" + (HP_Limit - HP_origin) + "点体力");
        }
        else
            SystemPrintln("❤ [" + Name + "]恢复了" + value + "点体力");

        delay(600);
    }

    /**
     * 尝试造成伤害（普通伤害则直接进入hurt方法，属性伤害（传导起点横置时）会按特定顺序对所有玩家迭代此方法：只有横置状态角色会进入hurt方法）
     * @param value 伤害值
     * @param type 伤害类型（1：普通伤害，2：火焰伤害，3：雷电伤害）
     * @param murderer 造成伤害的角色，无来源则为null
     */
    public void tryHurt(int value, int type, Player murderer) {
        int conduction = (type == 1) ? 0 : 1;
        tryHurt(value, type, murderer, conduction);
    }
    /**
     * @param conduction 属性伤害的传导位（非属性伤害：0，传导起点：1，传导节点：2）
     */
    public void tryHurt(int value, int type, Player murderer, int conduction) {
        if (type == 1 || (conduction == 1 && !interlink))
            hurt(value, type, murderer);

        else if (conduction == 1 || conduction == 2) {
            if (interlink) {
                if (conduction == 1)
                    value = hurt(value, type, murderer);
                else
                    hurt(value, type, murderer);
            }
            if (conduction == 1) {                                                  // 传导起点的下一节点为：当前回合角色
                for (Player player : players)
                    if (player.inSelfTurn) {
                        player.tryHurt(value, type, murderer, 2);
                        break;
                    }
            } else if (!this.subsequentPlayer(1).inSelfTurn) {                       // 传导节点的下一节点为：当前节点角色的下家——直到回到当前回合角色时结束传导过程
                this.subsequentPlayer(1).tryHurt(value, type, murderer, 2);
            }
        }

    }


    /**
     * 受到伤害（可以大于体力值，即负血——需更多桃救援）
     * @param value 伤害值
     * @param type 伤害类型（1：普通伤害，2：火焰伤害，3：雷电伤害）
     * @param murderer 造成伤害的角色，无来源则为null
     * @return 传导属性伤害的累计值
     */
    private int hurt(int value, int type, Player murderer) {
        String hurtType = "";
        if (type == 1) hurtType = "🗡";
        else if (type == 2) hurtType = "火焰🔥";
        else if (type == 3) hurtType = "雷电⚡";

        if (haveEquipment(CardName.TengJia) && type == 2) {
            printOnSet_Equipment(5);
            value ++;
        }
        if (haveEquipment(CardName.BaiYinShi) && value > 1) {
            printOnSet_Equipment(5);
            value = 1;
        }

        HP -= value;

        if (type != 1)
            interlink = false;

        if (murderer != null) {
            printOpeCase(murderer, this, "造成了", value + "点" + hurtType + "伤害");
            if (value == 3)
                Marvel(1);
            else if (value >= 4)
                Marvel(2);
        } else
            printOpeCase(this, null, "受到了",  value + "点" + hurtType + "伤害");

        SystemPrint("➲");
        show_FM ();
        delay(1000);

        if (HP <= 0) {
            SystemPrintln("✦ [" + Name + "]濒死，需使用" + (1 - HP) + "张【桃】或【酒】");
            delay(800);

            int IN = 1;
            while (HP <= 0 && IN != 0) {
                IN = askToUsePeachOrWineCard();
            }
            if (HP <= 0)
                begForPeach();

            if (HP <= 0)
                die();
        }

        return value;
    }




    /**
     * 濒死求桃
     */
    private void begForPeach() {
        SystemPrintln("✦ [" + Name + "]濒死，开始轮询求援");
        delay(1000);
        for (Player player : players) {
            if (player != this) {
                int IN = 1;
                while (HP <= 0 && IN != 0) {
                    int AI_prefer = (this.Team == player.Team) ? 1 : 0;         // 只有同队之间，AI才会救
                    IN = player.askToUseCard(CardName.PEACH, this, "", AI_prefer);
                }
            }
        }

    }

    /**
     * 角色阵亡
     */
    private void die() {
        SystemPrintln("✘ 【" + Name + "】已阵亡");
        LIFE = false;
        players.remove(this);
        delay(1200);

        if (testTeamDefeat()) {
            SystemPrintln("✘ 队伍 <" + Team + "> 已落败！");
            delay(1600);
            if (testGameOver())
                GameOver();
        }

        for (Player player : players)           // 重新计算全场座次号
            player.Order = players.indexOf(player) + 1;
    }

    /**
     * 检查队伍是否落败
     */
    private boolean testTeamDefeat() {
        for (Player player : players)
            if (player.Team == Team)        // 只要队伍里仍有一人存活就不算落败
                return false;
        return true;
    }
    /**
     * 检查游戏是否结束
     */
    private boolean testGameOver() {
        for (Player player : players)
            if (player.Team != players.get(0).Team)
                return false;
        return true;                // 若全场剩余人都属于同一队伍，该队伍胜出
    }
    /**
     * 游戏结束
     */
    private void GameOver() {
        SystemPrintln();
        SystemPrintln("▷————————————————{▷ 游戏结束 ◁}————————————————◁");
        SystemPrintln("❖ 队伍 <" + players.get(0).Team + "> 最终胜出！");
        for (Player player : players)
            SystemPrintln("【" + player.Name + "】");

        int FINAL = 0;                      // 没办法终止一切结算回到WarSystem的主程序里了，只好就在这结束（用循环封锁进程）
        while (FINAL != 299792458) {
            FINAL = INPUT.nextInt();
        }

    }


    /**
     * 计算到一名其他玩家的距离
     */
    public int calculate_distance(Player target) {
        int distance = Math.abs(this.Order - target.Order);
        if (distance > players.size() / 2)
            distance = players.size() - distance;

        // 在这里依据坐骑牌、武将技能，对distance进行操作
        if (this.haveEquipment(6))
            distance --;
        if (target.haveEquipment(7))
            distance ++;


        if (distance < 1)
            distance = 1;       // 两名角色最小距离为1，任何角色到自己距离始终为1
        return distance;
    }

    /**
     * 计算攻击范围
     */
    public int calculate_attackRange() {
        int attackRange = 1;
        for (Card card : EP_area)
            if (card.cardName.CardType == 4)
                attackRange = card.weapon_attackRange;
        return attackRange;
    }


    /**
     * 计算手牌上限（一般情况下等于体力值，可受技能影响）
     */
    public int calculate_HDCard_limit() {
        return HP;
    }

    /**
     * 计算下家
     * @param go 下递值（1：下家，2：下下家，-1：上家，以此类推）
     */
    public Player subsequentPlayer(int go) {
        int TO = Order;
        if (go > 0) {
            while (go != 0) {
                TO ++;
                if (TO > players.size())
                    TO = 1;
                go --;
            }
        } else if (go < 0) {
            while (go != 0) {
                TO --;
                if (TO < 1)
                    TO = players.size();
                go ++;
            }
        }
        return players.get(TO - 1);
    }


    /**
     * 是否可以杀到
     */
    public boolean canAttack(Player target) {
        boolean canAttack = (target != this);       // 不能杀自己
        if (calculate_attackRange() < this.calculate_distance(target))
            canAttack = false;

        return canAttack;
    }


    /**
     * 从牌堆顶摸牌
     * @param num 摸牌数
     */
    public void drawCard_fromPile(int num) {
        testToShufflePile(num);
        SystemPrintln("➧ [" + Name + "]从牌堆顶摸牌" + num + "张");
        for (int i = 1; i <= num; i++) {
            pile.get(0).show_complete_Card_Message(this, true);
            transferCard(pile.get(0), pile, HD_cards);
        }
        SystemPrintln();
        delay(600);
    }

    /**
     * 从牌堆顶亮出牌
     * @param num 亮出牌数
     * @return 亮出牌集合
     */
    public ArrayList<Card> showCard_fromPile(int num) {
        testToShufflePile(num);
        SystemPrintln("➤ [" + Name + "]亮出了牌堆顶" + num + "张牌：");
        ArrayList<Card> warehouse = new ArrayList<>();
        for (int i = 0; i < players.size(); i++) {
            pile.get(0).show_complete_Card_Message(this, false);
            transferCard(pile.get(0), pile, warehouse);
        }
        SystemPrintln("\n");
        delay(1200);

        return warehouse;
    }

    /**
     * 从牌堆顶取一张作判定牌
     * @return 判定牌
     */
    public Card RandomJudge() {
        testToShufflePile(1);
        Card RJ_Card = pile.get(0);
        pile.remove(0);
        return RJ_Card;
    }



    /**
     * （在弃牌阶段）弃牌，可受技能影响
     */
    public void discard_InDiscardStage() {
        if (HD_cards.size() > calculate_HDCard_limit())
            askToDiscard(HD_cards.size() - calculate_HDCard_limit());
    }




    /**
     * 显示自身所有手牌（打印在控制台上）
     */
    public void show_HandCard() {
        SelfPrintln("—————————————————{[" + Name + "]的手牌区}—————————————————");
        for (Card card : HD_cards) {
            card.show_complete_Card_Message(this, true);
            SelfPrint("➛" + (HD_cards.indexOf(card) + 1) + "  ");
        }
        SelfPrintln("\n");

    }

    /**
     * 显示自身基础信息
     */
    public void show_FM () {
        if (interlink) System.out.print("░");
        else System.out.print("『");

        System.out.print("" + Order + "』 ");
        System.out.printf("%-10s", Name);
        if (HP_Limit <= 5) {
            for (int i = 1; i <= HP; i++)
                System.out.print("▮");
            for (int i = HP + 1; i <= HP_Limit; i++)
                if (i > 0)
                    System.out.print("▯");
            for (int i = HP_Limit + 1; i <= 5; i++)
                System.out.print(" ");
            if (HP_Limit == 1)
                System.out.print("  ");
        } else {
            System.out.print("▮x" + HP);
            if (HP_Limit - HP > 0)
                System.out.print(" ▯x" + (HP_Limit - HP));
            else
                System.out.print("    ");
        }
        System.out.printf("%8s", "❐ " + HD_cards.size());


        if (drunk)
            System.out.printf("%6s", "✺");

        System.out.println();
    }

    /**
     * 显示自身状态表（技能影响、标记等）
     */
    public void show_ST() {
        if (interlink) SystemPrint("░");
        ST();

        SystemPrintln();
    }
    public void ST() {
        for (Detain_Card cardList : Detain_List)
            SystemPrint("〔" + cardList.Name + " " + cardList.cards.size() + "〕");
    }

    /**
     * 显示自身在游戏中的公开状态（打印在控制台上）
     */
    public void show_public_Situation() {
        SystemPrintln("————————————————————————————————————————————————");
        show_FM ();
        show_ST();

        if (interlink) SystemPrint("░");
        EP_area = CardArea_Link(EP_area);
        for (Card card : EP_area)               // 展示装备区
            card.show_brief_Card_Message();
        SystemPrintln();

        if (interlink) SystemPrint("░");
        for (Card card : RJ_area)               // 展示判定区
            card.show_brief_Card_Message();
        SystemPrintln();

    }


    /**
     * 显示自身在游戏中的私密状态（打印在控制台上）
     */
    public void show_private_Situation() {
        show_public_Situation();
        show_HandCard();
    }

    /**
     * 卡牌排序
     * @param card_area 牌区
     */
    public ArrayList<Card> CardArea_Link(ArrayList<Card> card_area) {
        ArrayList<Card> newArraylist = new ArrayList<>();
        int type = 0;
        while (type < 9) {
            for (Card card : card_area)
                if (card.cardName.CardType == type) {
                    newArraylist.add(card);
                }
            type ++;
        }
        return newArraylist;
    }



    /**
     * 令玩家选择是否使用某种牌名的牌（展示其状态并聆听其做出的选择，若选择不使用则返回 0）
     * @param cardName 牌名（枚举类）
     * @return 玩家选择使用的牌顺序位，若拒绝使用则返回0
     */
    protected int askToUseCard(CardName cardName, Player target, String purpose, int AI_prefer) {
        if (!AI) {
            int IN = -1;
            show_private_Situation();
            SelfPrint("⊙ 请选择是否对[" + target.Name + "]使用一张【" + cardName.Name + "】" + purpose);
            while (IN != 0 && (IN < 1 || IN > HD_cards.size() || !isCard(HD_cards.get(IN - 1), cardName))) {
                SelfPrint("->");
                IN = INPUT.nextInt();
            }
            if (IN != 0) {
                HD_cards.get(IN - 1).use(INPUT, players, this, target, cardName);
                delay(800);
            }
            return IN;
        } else {
            delay(1200);
            if (AI_prefer == 1 && haveHDCard(cardName)) {
                int index = HD_cards.indexOf(hdCard(cardName));
                hdCard(cardName).use(INPUT, players, this, target);
                delay(800);
                return index + 1;
            } else
                return 0;
        }
    }
    /**
     * 令玩家选择是否打出某种牌名的牌（展示其状态并聆听其做出的选择，若选择打出则完成打出操作，若选择不打出则返回 0）
     * @param cardName 牌名（枚举类）
     * @return 玩家选择打出的牌顺序位，若拒绝打出则返回0
     */
    protected int askToPlayCard(CardName cardName, String purpose, int AI_prefer) {
        if (!AI) {
            int IN = -1;
            show_private_Situation();
            SelfPrint("⊙ 请选择是否打出一张【" + cardName.Name + "】" + purpose);
            while (IN != 0 && (IN < 1 || IN > HD_cards.size() || !isCard(HD_cards.get(IN - 1), cardName))) {
                SelfPrint("->");
                IN = INPUT.nextInt();
            }
            if (IN != 0) {
                HD_cards.get(IN - 1).play(this, cardName);
                delay(600);
            }
            return IN;
        } else {
            delay(1200);
            if (AI_prefer == 1 && haveHDCard(cardName)) {
                int index = HD_cards.indexOf(hdCard(cardName));
                hdCard(cardName).play(this);
                delay(600);
                return index + 1;
            } else
                return 0;
        }
    }
    /**
     * 令玩家选择是否使用一张任意属性的【杀】（展示其状态并聆听其做出的选择，若选择不使用则返回 0）
     * @param JustUse 若玩家确认则直接使用之
     * @return 玩家选择使用的牌顺序位，若拒绝使用则返回0
     */
    protected int askToUseKill(Player target, String purpose, boolean JustUse, int AI_prefer) {
        if (!AI) {
            show_private_Situation();
            SelfPrint("⊙ 请选择是否对[" + target.Name + "]使用一张【杀】" + purpose);
            int IN = -1;
            while (IN != 0 && (IN < 1 || IN > HD_cards.size() || !isCard(HD_cards.get(IN - 1), CardName.KILL))) {
                SelfPrint("->");
                IN = INPUT.nextInt();
            }
            if (IN != 0 && JustUse) {
                HD_cards.get(IN - 1).use(INPUT, players, this, target, CardName.KILL);
                delay(800);
            }
            return IN;
        } else {
            delay(1200);
            if (AI_prefer == 1 && hdKillCard() != null) {
                int index = HD_cards.indexOf(hdKillCard());
                hdKillCard().use(INPUT, players, this, target);
                delay(800);
                return index + 1;
            } else
                return 0;
        }
    }
    /**
     * 令玩家选择是否打出一张任意属性的【杀】（展示其状态并聆听其做出的选择，若选择打出则完成打出操作，若选择不打出则返回 0）
     * @return 玩家选择打出的牌顺序位，若拒绝打出则返回0
     */
    protected int askToPlayKill(String purpose) {
        if (!AI) {
            show_private_Situation();
            SelfPrint("⊙ 请选择是否打出一张【杀】" + purpose);
            int IN = -1;
            while (IN != 0 && (IN < 1 || IN > HD_cards.size() || !isCard(HD_cards.get(IN - 1), CardName.KILL))) {
                SelfPrint("->");
                IN = INPUT.nextInt();
            }
            if (IN != 0) {
                HD_cards.get(IN - 1).play(this, CardName.KILL);
                delay(600);
            }
            return IN;
        } else {
            delay(1200);
            if (hdKillCard() != null) {
                int index = HD_cards.indexOf(hdKillCard());
                hdKillCard().play(this);
                delay(800);
                return index + 1;
            } else
                return 0;
        }
    }
    /**
     * 令玩家选择是否对自己使用一张【桃】或【酒】（展示其状态并聆听其做出的选择，若选择使用则完成使用操作，若选择不使用则返回 0）
     * @return 玩家选择使用的牌顺序位，若拒绝使用则返回0
     */
    protected int askToUsePeachOrWineCard() {
        if (!AI) {
            show_private_Situation();
            SelfPrint("⊙ 请选择是否使用一张【桃】或【酒】，恢复一点体力");
            int IN = -1;
            while (IN != 0 && (IN < 1 || IN > HD_cards.size() || !isCard(HD_cards.get(IN - 1), CardName.PEACH) || !isCard(HD_cards.get(IN - 1), CardName.WINE))) {
                SelfPrint("->");
                IN = INPUT.nextInt();
            }
            if (IN != 0) {
                if (isCard(HD_cards.get(IN - 1), CardName.PEACH))
                    HD_cards.get(IN - 1).use(INPUT, players, this, this, CardName.PEACH);
                else if (isCard(HD_cards.get(IN - 1), CardName.WINE))
                    HD_cards.get(IN - 1).use(INPUT, players, this, this, CardName.WINE);
                delay(800);
            }
            return IN;
        } else {
            delay(1200);
            if (haveHDCard(CardName.PEACH)) {
                int index = HD_cards.indexOf(hdCard(CardName.PEACH));
                hdCard(CardName.PEACH).use(INPUT, players, this, this);
                delay(800);
                return index;
            }
            else if (haveHDCard(CardName.WINE)) {
                int index = HD_cards.indexOf(hdCard(CardName.WINE));
                hdCard(CardName.WINE).use(INPUT, players, this, this);
                delay(800);
                return index + 1;
            } else
                return 0;
        }
    }


    /**
     * 令玩家选择是否打出某种花色的牌（展示其状态并聆听其做出的选择，若选择打出则完成打出操作，若选择不打出则返回 0）
     * @param suit 花色（枚举类）
     * @return 玩家选择打出的牌顺序位，若拒绝打出则返回0
     */
    protected int askToPlayCard(Suit suit, String purpose) {
        if (!AI) {
            show_private_Situation();
            SelfPrint("⊙ 请选择是否打出一张" + suit.message() + "牌" + purpose);
            int IN = -1;
            while (IN != 0 && (IN < 1 || IN > HD_cards.size() || !(HD_cards.get(IN - 1).suit == suit))) {
                SelfPrint("->");
                IN = INPUT.nextInt();
            }
            if (IN != 0) {
                HD_cards.get(IN - 1).play(this);
            }
            return IN;
        } else {
            for (Card card : HD_cards)
                if (card.suit == suit) {
                    card.play(this);
                    return (HD_cards.indexOf(card) + 1);
                }
            return 0;
        }
    }
    /**
     * 令玩家选择亮出一张手牌（不能拒绝）
     * @return 玩家选择打出的牌顺序位
     */
    protected int askToShowCard(String purpose) {
        int IN;
        if (!AI) {
            show_private_Situation();
            SelfPrint("⊙ 请亮出一张手牌" + purpose);
            IN = -1;
            while (IN < 1 || IN > HD_cards.size()) {
                SelfPrint("->");
                IN = INPUT.nextInt();
            }
        } else {
            IN = RD.nextInt(HD_cards.size()) + 1;
        }
        Player.printOpeCase(this, null, "亮出了", HD_cards.get(IN - 1).complete_Card_Message());
        return IN;
    }


    /**
     * 令玩家选择一名角色
     * @param canChooseSelf 是否可选自己为目标
     * @param limitedByDistance 是否受制于距离（不能选择到其距离为大于1的角色为目标）
     * @param limitedByAttackRange 是否受制于攻击范围（不能选择到其距离大于自己攻击范围的角色为目标）
     * @param limit_HD_NotEmpty 是否受制于空城（不能选择无手牌的角色为目标）
     * @param trigger 触发器
     * @param order 目标序号，若只选1个目标则此值设为0
     * @param AI_prefer AI偏好（1：倾向于选同队玩家，-1：倾向于选异队玩家，0：无所谓）
     * @return 玩家选择的角色座次号
     */
    public int askToChoosePlayer(boolean canChooseSelf, boolean limitedByDistance, boolean limitedByAttackRange, boolean limit_HD_NotEmpty, Card trigger, int order, int AI_prefer) {
        if (order == 0) SelfPrint("指定【" + trigger.cardName.shortName + "】的目标：（座次号）（输入'0'取消）");
        else SelfPrint("指定【" + trigger.cardName.shortName + "】的第" + order + "目标：（座次号）（输入'0'结束）");
        int IN = -1;
        int tryTimes = 0;
        int throwNum = 40;      // 抛出临界次数
        while ((tryTimes < throwNum) && (IN != 0 && (IN < 1 || IN > players.size() || (!canChooseSelf && players.get(IN - 1) == this)
                || (limitedByDistance && !(calculate_distance(players.get(IN - 1)) == 1)) || (limitedByAttackRange && calculate_attackRange() < calculate_distance(players.get(IN - 1)))
                || (limit_HD_NotEmpty && players.get(IN - 1).HD_cards.size() == 0) || !players.get(IN - 1).canBeTargeted(trigger, this)
                || (AI && ((AI_prefer == 1 && players.get(IN - 1).Team != Team) || (AI_prefer == -11 && players.get(IN - 1).Team == Team)))))) {
            if (IN >= 1 && IN <= players.size()) {
                if (!canChooseSelf && players.get(IN - 1) == this)
                    SelfPrint("<不能指定自己为目标>");
                else if (limitedByDistance && !(calculate_distance(players.get(IN - 1)) == 1))
                    SelfPrint("<与目标距离不足>");
                else if (limitedByAttackRange && calculate_attackRange() < calculate_distance(players.get(IN - 1)))
                    SelfPrint("<目标在攻击范围外>");
                else if (limit_HD_NotEmpty && players.get(IN - 1).HD_cards.size() == 0)
                    SelfPrint("<目标没有手牌>");
            }
            throwNum ++;
            SelfPrint("->");
            if (!AI)
                IN = INPUT.nextInt();
            else
                IN = RD.nextInt(players.size());
        }
        if (tryTimes >= throwNum)
            IN = 0;
        return IN;
    }

    /**
     * 是否可被某牌（触发器）指定为目标
     * @param trigger 触发器
     * @param user 使用触发器（指定该玩家为目标）的玩家
     */
    private boolean canBeTargeted(Card trigger, Player user) {
        if (protectedByArmorFromKILL(CardName.RenWangDun, user) && trigger.suit.Color.equals("BLACK") &&
                (trigger.cardName == CardName.KILL || trigger.cardName == CardName.KILL_fire || trigger.cardName == CardName.KILL_lighting)) {
            SelfPrint("<对方受【仁王盾】保护>");
            return false;
        }
        if (trigger.cardName == CardName.JieDao && !haveEquipment(4)) {
            SelfPrint("<对方没有武器牌>");
            return false;
        }
        if ((trigger.cardName == CardName.ShunShou || trigger.cardName == CardName.GuoHe) && HD_cards.size() == 0 && EP_area.size() == 0 && RJ_area.size() == 0) {
            SelfPrint("<对方的可用区域内没有牌>");
            return false;
        }

        return true;
    }



    /**
     * 让玩家选择一定范围内的一个整数
     * @param lowerLimit 下限
     * @param upperLimit 上限
     * @return 玩家选择的整数
     */
    public int askToChooseANumber(int lowerLimit, int upperLimit) {
        if (!AI) {
            int IN = lowerLimit - 1;
            while (IN < lowerLimit || IN > upperLimit) {
                SelfPrint("->");
                IN = INPUT.nextInt();
            }
            return IN;
        } else {
            delay(1200);
            return (lowerLimit + RD.nextInt(upperLimit - lowerLimit + 1));
        }
    }
    /**
     * 让玩家选择固定数组内的一个整数
     * @param ints 可选数组列表
     * @return 玩家选择的整数
     */
    public int askToChooseANumber(ArrayList<Integer> ints) {
        if (!AI) {
            while (true) {
                SelfPrint("->");
                int IN = INPUT.nextInt();
                for (int i : ints)
                    if (i == IN)
                        return IN;
            }
        } else {
            delay(1200);
            return (ints.get(RD.nextInt(ints.size() - 1)));
        }

    }

    /**
     * 令玩家作二元选择（是或否）
     * @param AI_prefer AI的偏好（1：true，-1：false，0：无所谓）
     * @return 回答“是”返回ture，回答“否”返回false.
     */
    public boolean askToConfirm(String message, int AI_prefer) {
        if (!AI) {
            SelfPrint("↸ " + message);
            int IN = -1;
            while (IN != 0 && IN != 1) {
                SelfPrint("->");
                IN = INPUT.nextInt();
            }
            return (IN == 1);
        } else {
            delay(1200);
            if (AI_prefer == 0) return (RD.nextInt(2) == 1);
            else return (AI_prefer == 1);
        }
    }

    /**
     * 令玩家操作自己区域的一张牌
     * @param OPE 操作：1-获取，2-弃置
     * @param HD_C 手牌区可选
     * @param EP_C 装备区可选
     * @param RJ_C 判定区可选
     */
    public void askToOpeSelfCard(int OPE ,boolean HD_C, boolean EP_C, boolean RJ_C) {
        show_private_Situation();
        if (!(HD_C && HD_cards.size() != 0) && !(EP_C && EP_area.size() != 0) && !(RJ_C && RJ_area.size() != 0)) {
            SelfPrintln("✕ 可用区域内没有牌");
        } else {
            String hint = "➣ 可从自己的：";

            if (HD_C && HD_cards.size() != 0)
                hint += " 1-手牌区 ";
            if (EP_C && EP_area.size() != 0)
                hint += " 2-装备区 ";
            if (RJ_C && RJ_area.size() != 0)
                hint += " 3-判定区 ";
            SelfPrint(hint + "选牌");
            if (OPE == 1) SelfPrintln("获得之");
            else if (OPE == 2) SelfPrintln("弃置之");

            int IN = -1;
            while (!((HD_C && HD_cards.size() != 0 && IN == 1)
                    || (EP_C && EP_area.size() != 0 && IN == 2)
                    || (RJ_C && RJ_area.size() != 0 && IN == 3))) {
                SelfPrintln("->");
                if (!AI)
                    IN = INPUT.nextInt();
                else
                    IN = RD.nextInt(4);
            }

            if (IN == 1) {
                for (Card card : HD_cards) {
                    card.show_brief_Card_Message();
                    SelfPrint(": " + HD_cards.indexOf(card) + "\n");
                }
                SelfPrintln("选择一张手牌：");
                int C = askToChooseANumber(1, HD_cards.size());
                if (OPE == 1)
                    transferCard(HD_cards.get(C - 1), HD_cards, HD_cards);
                else if (OPE == 2)
                    transferCard(HD_cards.get(C - 1), HD_cards, discard_pile);
            } else if (IN == 2) {
                for (Card card : EP_area) {
                    card.show_brief_Card_Message();
                    SelfPrint(": " + EP_area.indexOf(card) + "\n");
                }
                SelfPrintln("选择一张装备牌：");
                int C = askToChooseANumber(1, EP_area.size());
                if (OPE == 1)
                    transferCard(EP_area.get(C - 1), EP_area, HD_cards);
                else if (OPE == 2)
                    transferCard(EP_area.get(C - 1), EP_area, discard_pile);
            } else {
                for (Card card : RJ_area) {
                    card.show_brief_Card_Message();
                    SelfPrint(": " + RJ_area.indexOf(card) + "\n");
                }
                SelfPrintln("选择一张判定牌：");
                int C = askToChooseANumber(1, RJ_area.size());
                if (OPE == 1)
                    transferCard(RJ_area.get(C - 1), RJ_area, HD_cards);
                else if (OPE == 2)
                    transferCard(RJ_area.get(C - 1), RJ_area, discard_pile);
            }

            if (OPE == 1)
                printCardCase(this, null, "获得了", new Card[]{HD_cards.get(HD_cards.size() - 1)}, true);
            else if (OPE == 2)
                printCardCase(this, null, "弃置了", new Card[]{discard_pile.get(discard_pile.size() - 1)}, false);
        }
    }



    /**
     * 令玩家操作其他一名角色的一张牌
     * @param OPE 操作：1-获取，2-弃置
     * @param HD_C 手牌区可选
     * @param EP_C 装备区可选
     * @param RJ_C 判定区可选
     */
    public void askToOpeOthersCard(Player provider, int OPE ,boolean HD_C, boolean EP_C, boolean RJ_C) {
        provider.show_public_Situation();
        if (!(HD_C && provider.HD_cards.size() != 0) && !(EP_C && provider.EP_area.size() != 0) && !(RJ_C && provider.RJ_area.size() != 0)) {
            SelfPrintln("✕ 对方的可用区域内没有牌");
        } else {
            String hint = "➣ 可从[" + provider.Name + "]的：";

            if (HD_C && provider.HD_cards.size() != 0)
                hint += " 1-手牌区 ";
            if (EP_C && provider.EP_area.size() != 0)
                hint += " 2-装备区 ";
            if (RJ_C && provider.RJ_area.size() != 0)
                hint += " 3-判定区 ";
            SelfPrint(hint + "选牌");
            if (OPE == 1) SelfPrintln("获得之");
            else if (OPE == 2) SelfPrintln("弃置之");

            int IN = -1;
            while (!((HD_C && provider.HD_cards.size() != 0 && IN == 1)
                    || (EP_C && provider.EP_area.size() != 0 && IN == 2)
                    || (RJ_C && provider.RJ_area.size() != 0 && IN == 3))) {
                SelfPrint("->");
                if (!AI)
                    IN = INPUT.nextInt();
                else
                    IN = RD.nextInt(4);
            }

            if (IN == 1) {
                SelfPrintln("盲选一张手牌：1~" + provider.HD_cards.size());
                int C = askToChooseANumber(1, provider.HD_cards.size());
                if (OPE == 1)
                    transferCard(provider.HD_cards.get(C - 1), provider.HD_cards, HD_cards);
                else if (OPE == 2)
                    transferCard(provider.HD_cards.get(C - 1), provider.HD_cards, discard_pile);
            } else if (IN == 2) {
                for (Card card : provider.EP_area) {
                    card.show_brief_Card_Message();
                    SelfPrint(": " + provider.EP_area.indexOf(card) + "\n");
                }
                SelfPrintln("选择一张装备牌：");
                int C = askToChooseANumber(1, provider.EP_area.size());
                if (OPE == 1)
                    transferCard(provider.EP_area.get(C - 1), provider.EP_area, HD_cards);
                else if (OPE == 2)
                    transferCard(provider.EP_area.get(C - 1), provider.EP_area, discard_pile);
            } else {
                for (Card card : provider.RJ_area) {
                    card.show_brief_Card_Message();
                    SelfPrint(": " + provider.RJ_area.indexOf(card) + "\n");
                }
                SelfPrintln("选择一张判定牌：");
                int C = askToChooseANumber(1, provider.RJ_area.size());
                if (OPE == 1)
                    transferCard(provider.RJ_area.get(C - 1), provider.RJ_area, HD_cards);
                else if (OPE == 2)
                    transferCard(provider.RJ_area.get(C - 1), provider.RJ_area, discard_pile);
            }

            if (OPE == 1)
                printCardCase(this, provider, "获得了", new Card[]{HD_cards.get(HD_cards.size() - 1)}, true);
            else if (OPE == 2)
                printCardCase(this, provider, "弃置了", new Card[]{discard_pile.get(discard_pile.size() - 1)}, false);
        }
    }

    /**
     * 令玩家弃置其他玩家的一件某种（多种）类型的装备牌
     */
    public void abandonOthersEPCard(Player target , int[] eqpCardName_types) {
        ArrayList<Integer> eqpCsIndex = new ArrayList<>();
        for (Card card : target.EP_area)
            for (int eqpCardName_type : eqpCardName_types)
                if (card.cardName.CardType == eqpCardName_type) {
                    eqpCsIndex.add(target.EP_area.indexOf(card));
                    card.show_brief_Card_Message();
                    SelfPrint("-" + target.EP_area.indexOf(card));
                    SelfPrintln("");
                }
        SelfPrintln("选择一张装备牌：");
        int C = askToChooseANumber(eqpCsIndex);
        Player.printOpeCase(this, null, "弃置了", target.EP_area.get(C - 1).complete_Card_Message());
        abandon_card_from(target.EP_area, C);
    }



    /**
     * 令玩家弃置手牌
     * @param num 弃牌数，不足则全弃
     */
    public void askToDiscard(int num) {
        if (!AI) {
            show_HandCard();
            ArrayList<Card> willDiscard = new ArrayList<>();        // 将弃置之牌
            int IN = -1;        // 默认非范值
            while (willDiscard.size() < num && IN != 0) {
                IN = -1;
                SelfPrint("⊙ 还需弃置" + (num - willDiscard.size()) + "张牌");
                while (IN < 0 || IN > HD_cards.size() || willDiscard.contains(HD_cards.get(IN - 1))) {
                    SelfPrint("->");
                    IN = INPUT.nextInt();
                }
                if (IN != 0)
                    willDiscard.add(HD_cards.get(IN - 1));
            }
            if (IN == 0) {                          // 输入0，重新选择所弃牌
                SelfPrint("⭕ 重置中...");
                delay(800);
                askToDiscard(num);
            } else {
                for (Card card : willDiscard) {
                    Player.printOpeCase(this, null, "弃置了", card.complete_Card_Message());
                    abandon_card_from(HD_cards, card);
                }
                show_HandCard();
                delay(1200);
            }
        } else {
            while (num > 0) {
                delay(500);
                int IN = RD.nextInt(HD_cards.size()) + 1;
                Player.printOpeCase(this, null, "弃置了", HD_cards.get(IN - 1).complete_Card_Message());
                abandon_card_from(HD_cards, IN);
                num --;
            }
        }
    }


    /**
     * 将牌置入弃牌堆（若是虚拟牌或转化牌则直接销毁）
     * @param originPlace 此牌原本所在的位置
     * @param IN 此牌在原位置列表中的顺序位
     */
    public void abandon_card_from(ArrayList<Card> originPlace, int IN) {
        abandon_card_from(originPlace, originPlace.get(IN - 1));
    }
    public void abandon_card_from(ArrayList<Card> originPlace, Card card) {
        if (card.realCard)
            transferCard(card, originPlace, discard_pile);
        else {
            originPlace.remove(card);           // 销毁虚拟牌/转化牌
            if (card.originCards != null) {
                discard_pile.addAll(Arrays.asList(card.originCards));   // 将转化牌的原始牌（集）放入弃牌堆
                card.originCards = null;
            }
        }
    }

    /**
     * 转移卡牌
     * @param originPlace 此牌原本所在的位置
     * @param newPlace 此牌到达的新位置
     */
    public void transferCard(Card card, ArrayList<Card> originPlace, ArrayList<Card> newPlace) {
        // 以下添加各种技能触发
        if (card.cardName == CardName.ZhangBaMao && card.equipment_master != null)
            card.equipment_master.skillPool.remove(SkillName.ZhangBa);
        if (card.cardName == CardName.BaiYinShi && card.equipment_master != null) {
            this.printOnSet_Equipment(5);
            card.equipment_master.recover(1);
        }
        card.equipment_master = null;


        originPlace.remove(card);
        newPlace.add(card);

    }





    /**
     * 检查牌堆的牌是否不足，是则重新洗牌
     * @param willDraw 即将取走牌的数量
     */
    public void testToShufflePile(int willDraw) {
        if (willDraw > pile.size()) {
            SystemPrintln("▣ 牌堆牌数不足，即将开始洗牌...");
            Player.delay(1200);
            shufflePile();
        }
    }
    /**
     * 牌堆用完，将弃牌堆的牌重洗并加入牌堆
     */
    private void shufflePile() {
        Random rd = new Random();
        while (discard_pile.size() > 0) {
            int rd_n = rd.nextInt(discard_pile.size());
            transferCard(discard_pile.get(rd_n), discard_pile, pile);
        }
        SystemPrintln("▣ 牌堆已刷新：" + pile.size());
    }


    /**
     * 线程停滞一段时间
     * @param mSecs 停滞时常（毫秒）
     */

    public static void delay(int mSecs) {
        try {
            Thread.sleep(mSecs);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }





    /**
     * 打印角色操作事件
     * @param subject 主动方
     * @param object 被动方（若无被动方则为null）
     * @param operation 操作，如 “造成”，“使用”
     * @param thing 事件作用实体名，如卡牌名
     */
    public static void printOpeCase(Player subject, Player object, String operation, String thing) {
        if (object == null)
            SystemPrintln("==========[" + subject.Name + "]" + operation + thing + "==========");
        else if (subject == object)
            SystemPrintln("=========[" + subject.Name + "]对自己" + operation + thing + "=========");
        else
            SystemPrintln("========[" + subject.Name + "]对[" + object.Name + "]" + operation + thing + "========");

        delay(1000);
    }

    /**
     * 打印卡牌转移事件
     * @param subject 主动方
     * @param object 被动方（若无被动方则为null）
     * @param operation 操作，如 “获得”，“弃置”
     * @param cards 移动的卡牌集合
     */
    public static void printCardCase(Player subject, Player object, String operation, Card[] cards, boolean privateMessage) {
        if (object == null)
            SystemPrintln("➤ [" + subject.Name + "]" + operation + cards.length + "张牌");
        else
            SystemPrintln("➤ [" + subject.Name + "]" + operation + "[" + object.Name + "]的" + cards.length + "张牌");
        delay(400);

        if (privateMessage)
            for (Card card : cards)
                subject.SelfPrint(card.complete_Card_Message());
        else
            for (Card card : cards)
                SystemPrint(card.complete_Card_Message());
        SystemPrintln("\n");

        delay(800);
    }





    /**
     * 被使用了一张锦囊牌 <br/>
     * 向全场询问是否使用无懈可击，若被使用了无懈则返回false
     * @return 该角色是否被无懈可击
     */
    public boolean beUseStratagem(Player target, CardName cardName) {
        for (Player player : players)
            if (player.haveHDCard(CardName.WuXie) && !player.ignoreToUseWuXie) {
                int AI_prefer = (this.Team == player.Team && (cardName != CardName.TaoYuan && cardName != CardName.WuGu && cardName != CardName.WuZhong)) ? 1 : 0;

                int IN = player.askToUseCard(CardName.WuXie, target, ("抵消此【" + cardName.Name + "】？"), AI_prefer);
                if (IN != 0)
                    return (!beUseStratagem(player, CardName.WuXie));      // 询问是否无懈该无懈可击
                else
                    if (cardName == CardName.TaoYuan || cardName == CardName.NanMan || cardName == CardName.WanJian || cardName == CardName.WuGu)
                        player.ignoreToUseWuXie = player.askToConfirm("本轮是否忽略使用【无懈】？（确定：1，取消：0）", -1);
            }
        return true;
    }



    // 被使用【桃园结义】
    public void beUsed_TaoYuan(Player user, Card card) {
        if (beUseStratagem(this, card.cardName))
            recover(1);
        else
            SystemPrintln("◍ 抵消了【桃园结义】对[" + Name + "]的效果");
        delay(800);
    }

    // 被使用【万箭齐发】
    public void beUsed_WanJian(Player user, Card card) {
        if (protectedByArmor(CardName.TengJia, user))
            printOnSet_Equipment(5);
        else if (beUseStratagem(this, card.cardName)) {
            if (!haveHDCard(CardName.DODGE))
                tryHurt(1, 1, user);
            else {
                int IN = askToPlayCard(CardName.DODGE, "，否则受到伤害", 1);
                if (IN == 0) {
                    tryHurt(1, 1, user);
                }
            }
        } else
            SystemPrintln("◍ 抵消了【万箭齐发】对[" + Name + "]的效果");
        SystemPrintln();
        delay(800);
    }

    // 被使用【南蛮入侵】
    public void beUsed_NanMan(Player user, Card card) {
        if (protectedByArmor(CardName.TengJia, user))
            printOnSet_Equipment(5);
        else if (beUseStratagem(this, card.cardName)) {
            if (!haveHDCard(CardName.KILL))
                tryHurt(1, 1, user);
            else {
                int IN = askToPlayKill("，否则受到伤害");
                if (IN == 0) {
                    tryHurt(1, 1, user);
                }
            }
        } else
            SystemPrintln("◍ 抵消了【南蛮入侵】对[" + Name + "]的效果");
        SystemPrintln();
        delay(800);
    }

    // 被使用【五谷丰登】
    public void beUsed_WuGu(Player user, Card card, ArrayList<Card> WuGu_warehouse) {
        if (beUseStratagem(this, card.cardName)) {
            for (int i = 0; i < WuGu_warehouse.size(); i++) {
                WuGu_warehouse.get(i).show_complete_Card_Message(this, true);
                SelfPrint("-" + (i + 1));
            }
            SelfPrint("\n选择获取的牌顺序号：1~" + WuGu_warehouse.size());
            int IN = askToChooseANumber(1, WuGu_warehouse.size());
            transferCard(WuGu_warehouse.get(IN - 1), WuGu_warehouse, HD_cards);
            printCardCase(this, null, "获得了", new Card[]{HD_cards.get(HD_cards.size() - 1)}, false);
        } else
            SystemPrintln("◍ 抵消了【五谷丰登】对[" + Name + "]的效果");
        SystemPrintln();
        delay(800);
    }

    // 被使用【无中生有】
    public void beUsed_WuZhong(Player user, Card card) {
        if (beUseStratagem(this, card.cardName)) {
            user.drawCard_fromPile(2);

        } else
            SystemPrintln("◍ 抵消了【无中生有】对[" + Name + "]的效果");
        SystemPrintln();
        delay(800);
    }

    // 被使用【顺手牵羊】
    public void beUsed_ShunShou(Player user, Card card) {
        if (beUseStratagem(this, card.cardName)) {
            Player.printOpeCase(user, this, "使用了", card.complete_Card_Message());
            user.askToOpeOthersCard(this, 1, true, true, true);

        } else
            SystemPrintln("◍ 抵消了【顺手牵羊】对[" + Name + "]的效果");
        SystemPrintln();
        delay(800);
    }

    // 被使用【过河拆桥】
    public void beUsed_GuoHe(Player user, Card card) {
        if (beUseStratagem(this, card.cardName)) {
            Player.printOpeCase(user, this, "使用了", card.complete_Card_Message());
            user.askToOpeOthersCard(this, 2, true, true, true);

        } else
            SystemPrintln("◍ 抵消了【过河拆桥】对[" + Name + "]的效果");
        SystemPrintln();
        delay(800);
    }

    // 被使用【借刀杀人】
    public void beUsed_JieDao(Player user, Player killTarget, Card card) {
        if (beUseStratagem(this, card.cardName)) {
            Player.printOpeCase(user, this, "使用了", card.complete_Card_Message());
            int AI_prefer = (killTarget.Team == this.Team) ? 0 : 1;
            if (askToUseKill(killTarget, "，若不出则武器将被取走", true, AI_prefer) == 0) {
                transferCard(equipment(4), EP_area, user.HD_cards);
                printCardCase(user, this, "获得了", new Card[]{user.HD_cards.get(user.HD_cards.size() - 1)}, false);
            }
        } else
            SystemPrintln("◍ 抵消了【借刀杀人】对[" + Name + "]的效果");
        SystemPrintln();
        delay(800);
    }

    // 被使用【铁索连环】
    public void beUsed_TieSuo(Player user, Card card) {
        if (beUseStratagem(this, card.cardName)) {
            Player.printOpeCase(user, this, "使用了", card.complete_Card_Message());
            interlink = !interlink;
            if (interlink)
                SystemPrintln("░ 【" + Name + "】进入连环状态");
            else
                SystemPrintln("░ 【" + Name + "】取消连环状态");
        } else
            SystemPrintln("◍ 抵消了【铁索连环】对[" + Name + "]的效果");
        SystemPrintln();
        delay(800);
    }

    // 被使用【决斗】
    public void beUsed_JueDou(Player user, Card card) {
        if (beUseStratagem(this, card.cardName)) {
            Player.printOpeCase(user, this, "使用了", card.complete_Card_Message());
            int user_IN = -1;
            int this_IN = -1;
            while (user_IN != 0 && this_IN != 0) {
                this_IN = this.askToPlayKill("，否则受到决斗伤害");
                if (this_IN == 0)
                    this.tryHurt(1, 1, user);
                else {
                    user_IN = user.askToPlayKill("，否则受到决斗伤害");
                    if (user_IN == 0)
                        user.tryHurt(1, 1, user);
                }
            }
        } else
            SystemPrintln("◍ 抵消了【决斗】对[" + Name + "]的效果");
        SystemPrintln();
        delay(800);
    }

    // 被使用【火攻】
    public void beUsed_HuoGong(Player user, Card card) {
        if (beUseStratagem(this, card.cardName)) {
            Player.printOpeCase(user, this, "使用了", card.complete_Card_Message());
            Card showCard = HD_cards.get(askToShowCard("（【火攻】展示）") - 1);
            int IN = user.askToPlayCard(showCard.suit, "来造成火攻伤害");
            if (IN != 0)
                tryHurt(1, 2, user);
        } else
            SystemPrintln("◍ 抵消了【火攻】对[" + Name + "]的效果");
        SystemPrintln();
        delay(800);
    }


    /**
     * 判断判定区内是否有某种牌名的延时锦囊牌
     */
    public boolean haveTLSCard(CardName cardName) {
        for (Card card : RJ_area)
            if (card.cardName == cardName)
                return true;
        return false;
    }


    /**
     * 进行二元判定（只有中与不中两种结果）
     * @param triggerName 触发器名，如延时锦囊牌名、技能名
     * @param suits 能够判中的花色集，若无要求则设为null
     * @param points 能够判中的点数集，若无要求则设为null
     * @return 是否判中
     */
    public boolean RJ_Binomial(String triggerName, Suit[] suits, Point[] points) {
        SystemPrintln("✡ " + triggerName + "正在判定...");
        Player.delay(1400);

        Card RJ_Card = RandomJudge();        // 判定牌
        RJ_Card.show_complete_Card_Message(this, false);
        discard_pile.add(RJ_Card);
        Player.delay(800);

        if (suits == null)
            suits = new Suit[]{Suit.SPADE, Suit.HEART, Suit.CLUB, Suit.DIAMOND};
        if (points == null)
            points = new Point[]{Point.A, Point.N_2, Point.N_3, Point.N_4, Point.N_5, Point.N_6,
                                Point.N_7, Point.N_8, Point.N_9, Point.N_10, Point.J, Point.Q, Point.K};

        boolean suit_match = false;
        for (Suit suit : suits)
            if (suit == RJ_Card.suit) {
                suit_match = true;
                break;
            }
        boolean point_match = false;
        for (Point point : points)
            if (point == RJ_Card.point) {
                point_match = true;
                break;
            }

        if (suit_match && point_match) {
            SystemPrintln(" ✔");
            Player.delay(600);
            return true;
        } else {
            SystemPrintln(" ✘");
            Player.delay(600);
            return false;
        }

    }

    /**
     * 使用装备牌
     * @param eqmCard 装备牌
     */
    public void use_Equipment(Card eqmCard) {
        for (Card card : EP_area)
            if (card.cardName.CardType == eqmCard.cardName.CardType) {
                abandon_card_from(EP_area, card);
                SystemPrintln("➦ " + card.complete_Card_Message() + "被替换，进入弃牌堆");
                break;
            }
        transferCard(eqmCard, HD_cards, EP_area);
        eqmCard.equipment_master = this;

        if (eqmCard.cardName == CardName.ZhangBaMao)
            skillPool.add(SkillName.ZhangBa);

    }

    /**
     * 是否有某种牌名的手牌
     */
    public boolean haveHDCard(CardName cardName) {
        for (Card card : HD_cards)
            if (card.cardName == cardName)
                return true;
        return false;
    }
    /**
     * 是否有某种类型的装备牌
     */
    public boolean haveEquipment(int eqpCardName_Type) {
        for (Card card : EP_area)
            if (card.cardName.CardType == eqpCardName_Type)
                return true;
        return false;
    }
    /**
     * 是否有某种牌名的装备牌
     */
    public boolean haveEquipment(CardName eqpCardName) {
        for (Card card : EP_area)
            if (card.cardName == eqpCardName)
                return true;
        return false;
    }
    /**
     * 返回某牌名的手牌（取第一个）
     */
    public Card hdCard(CardName cardName) {
        for (Card card : HD_cards)
            if (card.cardName == cardName)
                return card;
        return null;
    }
    /**
     * 返回手牌中的任一属性的【杀】（取第一个）
     */
    public Card hdKillCard() {
        for (Card card : HD_cards)
            if (isCard(card, CardName.KILL))
                return card;
        return null;
    }
    /**
     * 返回某类型的装备牌
     */
    public Card equipment(int type) {
        for (Card card : EP_area)
            if (card.cardName.CardType == type)
                return card;
        return null;
    }

    /**
     * 是否被防具有效保护（可受武将技能影响）
     * @param murderer 试图行凶者
     */
    public boolean protectedByArmor(CardName armorCardName, Player murderer) {
        for (Card card : EP_area)
            if (card.cardName == armorCardName)
                return true;
        return false;
    }
    /**
     * 是否被防具有效保护于【杀】（可受青釭剑影响）
     */
    public boolean protectedByArmorFromKILL(CardName armorCardName, Player murderer) {
        if (murderer.haveHDCard(CardName.QingGangJian)) {
            murderer.printOnSet_Equipment(4);
            return false;
        }
        return (protectedByArmor(armorCardName, murderer));
    }


    /**
     * 打印发动装备技能提示
     * @param cardType 装备牌类型
     */
    public void printOnSet_Equipment(int cardType) {
        for (Card card : EP_area)
            if (card.cardName.CardType == cardType)
                SystemPrintln("☯ 【" + Name + "】发动了[" + card.cardName.Name + "]");
        delay(800);
    }



    /**
     * 使用虚拟牌/转化牌
     * @param virtualCard 新建的虚拟牌/转化牌
     * @param originCards 原始牌（集）（用作此转化牌的原始牌集，若是虚拟牌则设为null）
     */
    public void use_virtualCard(Card virtualCard, Card[] originCards, Player target) {
        virtualCard.realCard = false;
        virtualCard.originCards = originCards;

        virtualCard.use(INPUT, players, this, target);
    }
    /**
     * 打出虚拟牌/转化牌
     * @param virtualCard 新建的虚拟牌/转化牌
     * @param originCards 原始牌（集）（用作此转化牌的原始牌集，若是虚拟牌则设为null）
     */
    public void play_virtualCard(Card virtualCard, Card[] originCards) {
        virtualCard.realCard = false;
        virtualCard.originCards = originCards;

        ArrayList<Card> virtualArea = new ArrayList<>();    // 即将打出的虚拟/转化牌的临时位置
        virtualArea.add(virtualCard);

        printOpeCase(this, null, "打出了", virtualCard.complete_Card_Message());
        abandon_card_from(virtualArea, virtualCard);
    }


    /**
     * 附带成员过滤的控制台提示
     */
    public void SelfPrint(String message) {
        if (!AI)
            System.out.print(message);
    }
    public void SelfPrintln(String message) {
        SelfPrint(message + "\n");
    }
    /**
     * 公开的系统控制台提示
     */
    public static void SystemPrint(String message) {
        System.out.print(message);
    }
    private static void SystemPrintln(String message) {
        SystemPrint(message + "\n");
    }
    private static void SystemPrintln() {
        SystemPrintln("");
    }





    public void stage_Begin() {
        System.out.println("◈ 回合开始阶段");
        inSelfTurn = true;
        Player.delay(600);
    }

    public void stage_RandomJudge() {
        System.out.println("◈ 判定阶段");

        while (RJ_area.size() > 0) {
            System.out.println();
            Card tlsCard = RJ_area.get(RJ_area.size() - 1);       // 延时锦囊牌

            switch (tlsCard.cardName) {
                case Lighting: {
                    if (beUseStratagem(this, CardName.Lighting) && RJ_Binomial(tlsCard.complete_Card_Message(), new Suit[]{Suit.SPADE},
                            new Point[]{Point.N_2, Point.N_3, Point.N_4, Point.N_5, Point.N_6, Point.N_7, Point.N_8, Point.N_9})) {
                        abandon_card_from(RJ_area, tlsCard);
                        tryHurt(3, 3, null);
                    } else {
                        transferCard(tlsCard, RJ_area, subsequentPlayer(1).RJ_area);
                    }
                    break;
                }
                case IndulgePleasure: {
                    if (beUseStratagem(this, CardName.IndulgePleasure) && RJ_Binomial(tlsCard.complete_Card_Message(),
                            new Suit[]{Suit.SPADE, Suit.CLUB, Suit.DIAMOND}, null))
                        PlayStage_available = false;
                    abandon_card_from(RJ_area, tlsCard);
                    break;
                }
                case RobProvisions: {
                    if (beUseStratagem(this, CardName.RobProvisions) && RJ_Binomial(tlsCard.complete_Card_Message(),
                            new Suit[]{Suit.SPADE, Suit.HEART, Suit.DIAMOND}, null))
                        DrawStage_available = false;
                    abandon_card_from(RJ_area, tlsCard);
                    break;
                }
            }
        }
        Player.delay(800);
    }

    public void stage_DrawCards() {
        if (DrawStage_available) {
            System.out.println("◈ 摸牌阶段");
            drawCard_fromPile(2);
            Player.delay(1600);
        } else
            System.out.println("✕ 跳过摸牌阶段");
    }

    public void stage_PlayCard() {
        if (PlayStage_available) {
            System.out.println("◈ 出牌阶段");
            Player.delay(800);
            Show_NowSituation();

            if (AI)
                AI_PlayCardStage();
            else
                Player_PlayCardStage();

        } else
            System.out.println("✕ 跳过出牌阶段");


    }



    /**
     * 玩家操作的出牌阶段
     */
    public void Player_PlayCardStage() {
        int IN = -9;
        while (IN != 0) {
            IN = -9;        // 默认非范值
            show_private_Situation();
            SelfPrint("\n当前为你的出牌阶段. [输入牌的顺序位来使用牌，或输入：'-1'整理牌序，'-2'发动武将技能，'0'结束出牌阶段]");
            while (IN < -3 || IN > HD_cards.size()) {
                SelfPrint("->");
                IN = INPUT.nextInt();
            }

            switch (IN) {
                case 0:
                    break;
                case -1: {
                    HD_cards = CardArea_Link(HD_cards);
                    break;
                }
                case -2: {
                    trySkill();
                    break;
                }
                case -3: {
                    cheatCard();
                    break;
                }
                default: {
                    Card card = HD_cards.get(IN - 1);
                    if (card.canUse(this)) {
                        if (card.use(INPUT, players, this, null)) {
                            Player.delay(400);
                            SelfPrintln("");
                            Show_NowSituation();               // 一张成功使用后，重新打印场上局势
                            if (!LIFE)
                                break;
                        }
                    } else {
                        SelfPrintln("<无法使用此牌>");
                        Player.delay(600);
                    }
                }
            }
        }
    }

    // 获得作弊牌
    protected void cheatCard() {
    }



    /**
     * AI（人-工-智-障）操作的出牌阶段
     */
    public void AI_PlayCardStage() {
        HD_cards = CardArea_Link(HD_cards);

        if (haveHDCard(CardName.WINE))
            hdCard(CardName.WINE).use(INPUT, players, this, this);

        int C = HD_cards.size();
        while (C > 0) {
            System.out.println("剩余手牌" + HD_cards.toString());
            Card card = HD_cards.get(C - 1);
            System.out.println("检测到下一张手牌：" + card.cardName);
            if (card.canUse(this)) {
                System.out.println("发现可用：" + card.cardName);
                card.use(INPUT, players, this, null);
                System.out.println("使用完毕：" + card.cardName);
                Player.delay(1200);
                Show_NowSituation();
                if (!LIFE)
                    break;
            }
            C --;
        }
        System.out.println("结束出牌");

    }

    /**
     * 查阅技能
     */
    private void trySkill() {
        for (SkillName skillName : skillPool)
            SystemPrintln((skillPool.indexOf(skillName) + 1) + "➛" + skillName.skillNameText());
        int S = -1;
        while (S < 0 || S > skillPool.size()) {
            SelfPrint("->");
            S = INPUT.nextInt();
        }
        if (S != 0)
            useSkill(skillPool.get(S - 1));
    }

    /**
     * 使用技能（通用法）
     */
    protected void useSkill(SkillName skillName) {
        if (skillName == SkillName.ZhangBa) {
            show_HandCard();
            SelfPrintln("⊙ 请选择2张牌转化为【杀】");
            int[] INs = new int[2];
            int IN = -1;
            for (int i = 0; i < 2; i++) {
                SelfPrint("->");
                IN = INPUT.nextInt();
                if (IN == 0)
                    break;
                else
                    INs[i] = IN;
            }
            if (IN != 0) {
                use_virtualCard(new Card_Kill(Suit.E, Point.E), new Card[]{HD_cards.get(INs[0]), HD_cards.get(INs[1])}, null);
                printOnSet_Equipment(4);
                return;
            }
        }


        SystemPrintln("☯ [" + Name + "] 发动了技能 『" + skillName.Name + "』");
        SayWord(skillName);

    }




    /**
     * 技能台词
     */
    private void SayWord(SkillName skillName) {
        delay(400);
        int w = RD.nextInt(skillName.words.length);
        SystemPrintln("♜ [" + Name + "]言：“" + skillName.words[w] + "”");
        delay(1600);
    }


    public void stage_Discard() {
        System.out.println("◈ 弃牌阶段");
        Player.delay(800);
        discard_InDiscardStage();
    }

    public void stage_End() {
        System.out.println("◈ 回合结束阶段");
        overSet();
        Player.delay(600);
    }

    protected void overSet() {
        DrawStage_available = true;
        PlayStage_available = true;
        attack_limit = 1;
        drink_limit = 1;
        drunk = false;
        inSelfTurn = false;
    }



    /**
     * 刷新当前游戏局面
     */
    private void Show_NowSituation() {
        System.out.println("════════════════════════════════════════════════");
        for (Player player : players)
            player.show_public_Situation();
        System.out.println("════════════════════════════════════════════════");
    }







    /**
     * 系统惊叹
     */
    private void Marvel(int MI) {
        delay(100);
        System.out.println("╓────────────────────────╖");       // 1 + 24 + 1
        switch (MI) {
            case 1:
                System.out.println("║        癫狂屠戮！        ║");
                break;
            case 2:
                System.out.println("║      无双，万军取首！     ║");
                break;


        }

        System.out.println("╙────────────────────────╜");       // 1 + 24 + 1
        delay(1100);
    }


}
