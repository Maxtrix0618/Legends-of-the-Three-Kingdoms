import GameCard.*;
import GameCard.Equipment.Armor.*;
import GameCard.Equipment.Steed.*;
import GameCard.Equipment.Weapon.*;
import GameCard.Fundamental.*;
import GameCard.Stratagem.*;
import GameCard.TimeLapseStratagem.*;
import GamePlayer.J_XuSheng;
import GamePlayer.Player;

import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

/**
 * Summer, 2023
 * by Paperfish
 */
public class WarSystem {
    private final String VISION = "V_1.2";
    private final Scanner Input = new Scanner(System.in);

    public ArrayList<Player> players = new ArrayList<>();      // 玩家列表
    public ArrayList<Card> wholeCards = new ArrayList<>();     // 整副游戏牌（军争：160张，暂未加入【木牛流马】）
    public ArrayList<Card> pile = new ArrayList<>();           // 游戏牌堆，最顶上的牌索引为0，向下递增
    public ArrayList<Card> discard_pile = new ArrayList<>();   // 弃牌堆

    public int Round = 1;   // 轮数
    public int Turn = 1;    // 当前回合玩家座次号
    public Player master;   // 当前回合玩家

    public boolean GAMEOVER;    // 游戏结束

    public WarSystem() {
        initWholeCards();
        initPile();
        initPlayers();

        printMessage();

    }

    /**
     * 卡包信息
     */
    private void printMessage() {
        System.out.println("版本：" + VISION);
        System.out.println("牌堆游戏牌总数：" + wholeCards.size());
        System.out.println("本局玩家数：" + players.size());
        Player.delay(2000);
    }

    /**
     * 游戏主程序
     */
    public void GameStart() {
        System.out.println("◀————————————————{▶ 游戏开始 ◀}————————————————▶");
        System.out.println();
        for (Player player : players) {
            player.GameStart();
        }
        System.out.println();
        Player.delay(1800);

        System.out.println("✖—————————————————{【 开战 】}—————————————————✖");
        Player.delay(500);
        while (!GAMEOVER) {
            start_OneRound();
            Round ++;
            Turn = 1;
        }

    }

    /**
     * 执行一轮
     */
    private void start_OneRound() {

        while (Turn <= players.size()) {
            start_OneTurn(Turn);
            Turn ++;
        }
    }

    /**
     * 开始一名玩家的回合
     * @param turn 当前回合玩家的座次号
     */
    private void start_OneTurn(int turn) {
        master = players.get(turn - 1);      // 回合之主

        Show_GameSituation();
        System.out.println("▶▶ 当前是【 [" + master.Order + "] " + master.Name + " 】的回合 ◀◀");

        master.stage_Begin();          // 回合开始阶段
        master.stage_RandomJudge();    // 判定阶段
        master.stage_DrawCards();      // 摸牌阶段
        master.stage_PlayCard();       // 出牌阶段
        master.stage_Discard();        // 弃牌阶段
        master.stage_End();            // 回合结束阶段


        System.out.println();

    }












    /**
     * 打印游戏局面
     */
    private void Show_GameSituation() {
        System.out.println("════════════════════════════════════════════════");
        System.out.println("  【当前局面】" + "      轮数：" + Round + "      牌堆：" + pile.size());
        for (Player player : players)
            player.show_public_Situation();
        System.out.println("════════════════════════════════════════════════");
    }







    /**
     * 初始化牌堆
     */
    private void initPile() {

        ArrayList<Integer> origin_orderList = new ArrayList<>();
        for (int i = 0; i < wholeCards.size(); i++)
            origin_orderList.add(i);

        Random rd = new Random();
        ArrayList<Integer> random_orderList = new ArrayList<>();
        for (int i = 0; i < wholeCards.size(); i++) {
            int rd_n = rd.nextInt(origin_orderList.size());
            random_orderList.add(origin_orderList.get(rd_n));
            origin_orderList.remove(rd_n);
        }

        for (int i = 0; i < wholeCards.size(); i++)
            pile.add(wholeCards.get(random_orderList.get(i)));


    }


    /**
     * 初始化游戏玩家
     */
    private void initPlayers() {
//        players.add(new ZhaoYun(Input ,players, pile, discard_pile, 1, 1));
        players.add(new J_XuSheng(Input ,players, pile, discard_pile, 1, 1));

        addPlayer(2, "夏侯恩", 4, 4, -1);
        addPlayer(3, "华雄", 5, 6, -1);
        addPlayer(4, "小卒", 1, 1, -1);
    }

    private void addPlayer(int order, String name, int HP, int HP_Limit, int Team) {
        players.add(new Player(Input ,players, pile, discard_pile, name, order, HP_Limit, HP, Team));
    }





    /**
     * 初始化整副牌
     */
    private void initWholeCards() {

        addFundamentalCard();       // 基本牌
        addStratagemCard();         // 锦囊牌
        addEquipmentCard();         // 装备牌


    }




    private void addFundamentalCard() {

        wholeCards.add(new Card_Kill(Suit.SPADE, Point.N_7));             // 普通杀
        wholeCards.add(new Card_Kill(Suit.SPADE, Point.N_8));
        wholeCards.add(new Card_Kill(Suit.SPADE, Point.N_9));
        wholeCards.add(new Card_Kill(Suit.SPADE, Point.N_9));
        wholeCards.add(new Card_Kill(Suit.SPADE, Point.N_10));
        wholeCards.add(new Card_Kill(Suit.SPADE, Point.N_10));
        wholeCards.add(new Card_Kill(Suit.SPADE, Point.N_10));
        wholeCards.add(new Card_Kill(Suit.HEART, Point.N_10));
        wholeCards.add(new Card_Kill(Suit.HEART, Point.N_10));
        wholeCards.add(new Card_Kill(Suit.HEART, Point.J));
        wholeCards.add(new Card_Kill(Suit.CLUB, Point.N_2));
        wholeCards.add(new Card_Kill(Suit.CLUB, Point.N_3));
        wholeCards.add(new Card_Kill(Suit.CLUB, Point.N_4));
        wholeCards.add(new Card_Kill(Suit.CLUB, Point.N_5));
        wholeCards.add(new Card_Kill(Suit.CLUB, Point.N_6));
        wholeCards.add(new Card_Kill(Suit.CLUB, Point.N_7));
        wholeCards.add(new Card_Kill(Suit.CLUB, Point.N_8));
        wholeCards.add(new Card_Kill(Suit.CLUB, Point.N_8));
        wholeCards.add(new Card_Kill(Suit.CLUB, Point.N_9));
        wholeCards.add(new Card_Kill(Suit.CLUB, Point.N_9));
        wholeCards.add(new Card_Kill(Suit.CLUB, Point.N_10));
        wholeCards.add(new Card_Kill(Suit.CLUB, Point.N_10));
        wholeCards.add(new Card_Kill(Suit.CLUB, Point.J));
        wholeCards.add(new Card_Kill(Suit.CLUB, Point.J));
        wholeCards.add(new Card_Kill(Suit.DIAMOND, Point.N_6));
        wholeCards.add(new Card_Kill(Suit.DIAMOND, Point.N_7));
        wholeCards.add(new Card_Kill(Suit.DIAMOND, Point.N_8));
        wholeCards.add(new Card_Kill(Suit.DIAMOND, Point.N_9));
        wholeCards.add(new Card_Kill(Suit.DIAMOND, Point.N_10));
        wholeCards.add(new Card_Kill(Suit.DIAMOND, Point.K));
        wholeCards.add(new Card_Dodge(Suit.HEART, Point.N_2));              // 闪
        wholeCards.add(new Card_Dodge(Suit.HEART, Point.N_2));
        wholeCards.add(new Card_Dodge(Suit.HEART, Point.N_8));
        wholeCards.add(new Card_Dodge(Suit.HEART, Point.N_9));
        wholeCards.add(new Card_Dodge(Suit.HEART, Point.J));
        wholeCards.add(new Card_Dodge(Suit.HEART, Point.Q));
        wholeCards.add(new Card_Dodge(Suit.HEART, Point.K));
        wholeCards.add(new Card_Dodge(Suit.DIAMOND, Point.N_2));
        wholeCards.add(new Card_Dodge(Suit.DIAMOND, Point.N_2));
        wholeCards.add(new Card_Dodge(Suit.DIAMOND, Point.N_3));
        wholeCards.add(new Card_Dodge(Suit.DIAMOND, Point.N_4));
        wholeCards.add(new Card_Dodge(Suit.DIAMOND, Point.N_5));
        wholeCards.add(new Card_Dodge(Suit.DIAMOND, Point.N_6));
        wholeCards.add(new Card_Dodge(Suit.DIAMOND, Point.N_6));
        wholeCards.add(new Card_Dodge(Suit.DIAMOND, Point.N_7));
        wholeCards.add(new Card_Dodge(Suit.DIAMOND, Point.N_7));
        wholeCards.add(new Card_Dodge(Suit.DIAMOND, Point.N_8));
        wholeCards.add(new Card_Dodge(Suit.DIAMOND, Point.N_8));
        wholeCards.add(new Card_Dodge(Suit.DIAMOND, Point.N_9));
        wholeCards.add(new Card_Dodge(Suit.DIAMOND, Point.N_10));
        wholeCards.add(new Card_Dodge(Suit.DIAMOND, Point.N_10));
        wholeCards.add(new Card_Dodge(Suit.DIAMOND, Point.J));
        wholeCards.add(new Card_Dodge(Suit.DIAMOND, Point.J));
        wholeCards.add(new Card_Dodge(Suit.DIAMOND, Point.J));
        wholeCards.add(new Card_Peach(Suit.HEART, Point.N_3));              // 桃
        wholeCards.add(new Card_Peach(Suit.HEART, Point.N_4));
        wholeCards.add(new Card_Peach(Suit.HEART, Point.N_5));
        wholeCards.add(new Card_Peach(Suit.HEART, Point.N_6));
        wholeCards.add(new Card_Peach(Suit.HEART, Point.N_6));
        wholeCards.add(new Card_Peach(Suit.HEART, Point.N_7));
        wholeCards.add(new Card_Peach(Suit.HEART, Point.N_8));
        wholeCards.add(new Card_Peach(Suit.HEART, Point.N_9));
        wholeCards.add(new Card_Peach(Suit.HEART, Point.Q));
        wholeCards.add(new Card_Peach(Suit.DIAMOND, Point.N_2));
        wholeCards.add(new Card_Peach(Suit.DIAMOND, Point.N_3));
        wholeCards.add(new Card_Peach(Suit.DIAMOND, Point.Q));

        wholeCards.add(new Card_Wine(Suit.DIAMOND, Point.N_9));             // 酒
        wholeCards.add(new Card_Wine(Suit.CLUB, Point.N_3));
        wholeCards.add(new Card_Wine(Suit.CLUB, Point.N_9));
        wholeCards.add(new Card_Wine(Suit.SPADE, Point.N_3));
        wholeCards.add(new Card_Wine(Suit.SPADE, Point.N_9));
        wholeCards.add(new Card_Kill_fire(Suit.DIAMOND, Point.N_4));        // 火杀
        wholeCards.add(new Card_Kill_fire(Suit.DIAMOND, Point.N_5));
        wholeCards.add(new Card_Kill_fire(Suit.HEART, Point.N_4));
        wholeCards.add(new Card_Kill_fire(Suit.HEART, Point.N_7));
        wholeCards.add(new Card_Kill_fire(Suit.HEART, Point.N_10));
        wholeCards.add(new Card_Kill_lightning(Suit.CLUB, Point.N_5));      // 雷杀
        wholeCards.add(new Card_Kill_lightning(Suit.CLUB, Point.N_6));
        wholeCards.add(new Card_Kill_lightning(Suit.CLUB, Point.N_7));
        wholeCards.add(new Card_Kill_lightning(Suit.CLUB, Point.N_8));
        wholeCards.add(new Card_Kill_lightning(Suit.SPADE, Point.N_4));
        wholeCards.add(new Card_Kill_lightning(Suit.SPADE, Point.N_5));
        wholeCards.add(new Card_Kill_lightning(Suit.SPADE, Point.N_6));
        wholeCards.add(new Card_Kill_lightning(Suit.SPADE, Point.N_7));
        wholeCards.add(new Card_Kill_lightning(Suit.SPADE, Point.N_8));

    }


    private void addStratagemCard() {

        wholeCards.add(new Card_TaoYuan(Suit.HEART, Point.A));            // 桃园结义
        wholeCards.add(new Card_WanJian(Suit.HEART, Point.A));            // 万箭齐发
        wholeCards.add(new Card_WuGu(Suit.CLUB, Point.Q));                // 五谷丰登
        wholeCards.add(new Card_WuGu(Suit.CLUB, Point.K));
        wholeCards.add(new Card_NanMan(Suit.CLUB, Point.N_7));            // 南蛮入侵
        wholeCards.add(new Card_NanMan(Suit.SPADE, Point.N_7));
        wholeCards.add(new Card_NanMan(Suit.SPADE, Point.K));
        wholeCards.add(new Card_WuZhong(Suit.HEART, Point.N_7));          // 无中生有
        wholeCards.add(new Card_WuZhong(Suit.HEART, Point.N_8));
        wholeCards.add(new Card_WuZhong(Suit.HEART, Point.N_9));
        wholeCards.add(new Card_WuZhong(Suit.HEART, Point.J));
        wholeCards.add(new Card_JieDao(Suit.CLUB, Point.Q));              // 借刀杀人
        wholeCards.add(new Card_JieDao(Suit.CLUB, Point.K));
        wholeCards.add(new Card_ShunShou(Suit.DIAMOND, Point.N_3));       // 顺手牵羊
        wholeCards.add(new Card_ShunShou(Suit.DIAMOND, Point.N_4));
        wholeCards.add(new Card_ShunShou(Suit.SPADE, Point.N_3));
        wholeCards.add(new Card_ShunShou(Suit.SPADE, Point.N_3));
        wholeCards.add(new Card_ShunShou(Suit.SPADE, Point.J));
        wholeCards.add(new Card_GuoHe(Suit.CLUB, Point.N_3));             // 过河拆桥
        wholeCards.add(new Card_GuoHe(Suit.CLUB, Point.N_4));
        wholeCards.add(new Card_GuoHe(Suit.HEART, Point.Q));
        wholeCards.add(new Card_GuoHe(Suit.SPADE, Point.N_3));
        wholeCards.add(new Card_GuoHe(Suit.SPADE, Point.N_4));
        wholeCards.add(new Card_GuoHe(Suit.SPADE, Point.Q));
        wholeCards.add(new Card_WuXie(Suit.DIAMOND, Point.Q));            // 无懈可击
        wholeCards.add(new Card_WuXie(Suit.CLUB, Point.Q));
        wholeCards.add(new Card_WuXie(Suit.CLUB, Point.K));
        wholeCards.add(new Card_WuXie(Suit.HEART, Point.A));
        wholeCards.add(new Card_WuXie(Suit.HEART, Point.K));
        wholeCards.add(new Card_WuXie(Suit.SPADE, Point.J));
        wholeCards.add(new Card_WuXie(Suit.SPADE, Point.K));
        wholeCards.add(new Card_JueDou(Suit.DIAMOND, Point.A));             // 决斗
        wholeCards.add(new Card_JueDou(Suit.CLUB, Point.A));
        wholeCards.add(new Card_JueDou(Suit.SPADE, Point.A));
        wholeCards.add(new Card_HuoGong(Suit.DIAMOND, Point.Q));            // 火攻
        wholeCards.add(new Card_HuoGong(Suit.HEART, Point.N_2));
        wholeCards.add(new Card_HuoGong(Suit.HEART, Point.N_3));
        wholeCards.add(new Card_TieSuo(Suit.CLUB, Point.N_10));             // 铁索连环
        wholeCards.add(new Card_TieSuo(Suit.CLUB, Point.J));
        wholeCards.add(new Card_TieSuo(Suit.CLUB, Point.Q));
        wholeCards.add(new Card_TieSuo(Suit.CLUB, Point.K));
        wholeCards.add(new Card_TieSuo(Suit.SPADE, Point.J));
        wholeCards.add(new Card_TieSuo(Suit.SPADE, Point.Q));

        wholeCards.add(new Card_Lighting(Suit.HEART, Point.Q));             // 闪电
        wholeCards.add(new Card_Lighting(Suit.SPADE, Point.A));
        wholeCards.add(new Card_IndulgePleasure(Suit.CLUB, Point.N_6));     // 乐不思蜀
        wholeCards.add(new Card_IndulgePleasure(Suit.HEART, Point.N_6));
        wholeCards.add(new Card_IndulgePleasure(Suit.SPADE, Point.N_6));
        wholeCards.add(new Card_RobProvisions(Suit.CLUB, Point.N_4));       // 兵粮寸断
        wholeCards.add(new Card_RobProvisions(Suit.SPADE, Point.N_10));

    }


    private void addEquipmentCard() {

        wholeCards.add(new Card_ZhuGeNu(Suit.SPADE, Point.A));              // 武器牌
        wholeCards.add(new Card_ZhuGeNu(Suit.CLUB, Point.A));
        wholeCards.add(new Card_QingGangJian(Suit.SPADE, Point.N_6));
        wholeCards.add(new Card_HanBingJian(Suit.SPADE, Point.N_2));
        wholeCards.add(new Card_CiXiongJian(Suit.SPADE, Point.N_2));
        wholeCards.add(new Card_GuDingDao(Suit.SPADE, Point.A));
        wholeCards.add(new Card_YanYueDao(Suit.SPADE, Point.N_5));
        wholeCards.add(new Card_ZhangBaMao(Suit.SPADE, Point.Q));
        wholeCards.add(new Card_GuanShiFu(Suit.DIAMOND, Point.N_5));
        wholeCards.add(new Card_ZhuQueShan(Suit.DIAMOND, Point.A));
        wholeCards.add(new Card_FangTianJi(Suit.DIAMOND, Point.Q));
        wholeCards.add(new Card_QiLinGong(Suit.HEART, Point.N_5));

        wholeCards.add(new Card_BaiYinShi(Suit.CLUB, Point.A));             // 防具牌
        wholeCards.add(new Card_RenWangDun(Suit.HEART, Point.N_2));
        wholeCards.add(new Card_BaGuaZhen(Suit.CLUB, Point.N_2));
        wholeCards.add(new Card_BaGuaZhen(Suit.SPADE, Point.N_2));
        wholeCards.add(new Card_TengJia(Suit.CLUB, Point.N_2));
        wholeCards.add(new Card_TengJia(Suit.SPADE, Point.N_2));

        wholeCards.add(new Card_ZiXing(Suit.DIAMOND, Point.K));             // 坐骑牌
        wholeCards.add(new Card_ChiTu(Suit.HEART, Point.N_5));
        wholeCards.add(new Card_DaYuan(Suit.SPADE, Point.K));
        wholeCards.add(new Card_HuaLiu(Suit.DIAMOND, Point.K));
        wholeCards.add(new Card_DiLu(Suit.CLUB, Point.N_5));
        wholeCards.add(new Card_ZhuaHuang(Suit.HEART, Point.K));
        wholeCards.add(new Card_JueYing(Suit.SPADE, Point.N_5));

    }

















}
