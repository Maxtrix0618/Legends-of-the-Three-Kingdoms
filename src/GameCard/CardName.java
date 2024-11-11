package GameCard;

public enum CardName {

    KILL(1, "杀 ➻", "杀"),
    KILL_fire(1, "杀 🔥", "火杀"),
    KILL_lighting(1, "杀 ⚡", "雷杀"),
    DODGE(1, "闪 ✢", "闪"),
    PEACH(1, "桃 🍑", "桃"),
    WINE(1, "酒 🍺", "酒"),

    TaoYuan(2, "桃园结义", "桃园"),
    WanJian(2, "万箭齐发", "万箭"),
    JieDao(2, "借刀杀人", "借刀"),
    WuGu(2, "五谷丰登", "五谷"),
    NanMan(2, "南蛮入侵", "南蛮"),
    WuZhong(2, "无中生有", "无中"),
    ShunShou(2, "顺手牵羊", "顺"),
    GuoHe(2, "过河拆桥", "拆"),
    WuXie(2, "无懈可击", "无懈"),
    JueDou(2, "决斗", "决斗"),
    HuoGong(2, "火攻", "火攻"),
    TieSuo(2, "铁索连环", "连环"),

    Lighting(3, "闪电 ⏳", "⚡ 闪电"),
    IndulgePleasure(3, "乐不思蜀 ⏳", "♪ 乐"),
    RobProvisions(3, "兵粮寸断 ⏳", "☈ 兵"),


    ZhuGeNu(4, "诸葛连弩➟1 🗡", "🗡诸葛弩➟1"),
    QingGangJian(4, "青釭剑➟2 🗡", "🗡青釭剑➟2"),
    HanBingJian(4, "寒冰剑➟2 🗡", "🗡寒冰剑➟2"),
    CiXiongJian(4, "雌雄双股剑➟2 🗡", "🗡雌雄剑➟2"),
    GuDingDao(4, "古锭刀➟2 🗡", "🗡古锭刀➟2"),
    YanYueDao(4, "青龙偃月刀➟3 🗡", "🗡偃月刀➟3"),
    ZhangBaMao(4, "丈八蛇矛➟3 🗡", "🗡丈八矛➟3"),
    GuanShiFu(4, "贯石斧➟3 🗡", "🗡贯石斧➟3"),
    ZhuQueShan(4, "朱雀羽扇➟4 🗡", "🗡朱雀扇➟4"),
    FangTianJi(4, "方天画戟➟4 🗡", "🗡方天戟➟4"),
    QiLinGong(4, "麒麟弓➟5 🗡", "🗡麒麟弓➟5"),

    BaiYinShi(5, "白银狮子 🛡", "🛡白银狮"),
    RenWangDun(5, "仁王盾 🛡", "🛡仁王盾"),
    BaGuaZhen(5, "八卦阵 🛡", "🛡八卦阵"),
    TengJia(5, "藤甲 🛡", "🛡藤甲"),

    ZiXing(6, "紫骍-1 🐎", "🐎紫骍-1"),
    ChiTu(6, "赤兔-1 🐎", "🐎赤兔-1"),
    DaYuan(6, "大宛-1 🐎", "🐎大宛-1"),
    HuaLiu(7, "骅骝+1 🐎", "🐎骅骝+1"),
    DiLu(7, "的卢+1 🐎", "🐎的卢+1"),
    ZhuaHuang(7, "爪黄飞电+1 🐎", "🐎爪黄+1"),
    JueYing(7, "绝影+1 🐎", "🐎绝影+1"),

    MuNiuLiuMa(8, "木牛流马 ◙", "◙木牛流马");      // 暂不可用：木牛流马









    /**
     * 牌的类型：<br/>
     * 1：基本牌，
     * 2：非延时锦囊牌，
     * 3：延时锦囊牌，
     * 4：武器牌，
     * 5：防具牌，
     * 6：<-1马>坐骑牌，
     * 7：<+1马>坐骑牌，
     * 8：宝物牌.
     */
    public final int CardType;

    public final String Name;
    public final String shortName;

    CardName(int cardType, String name, String shortName) {
        this.CardType = cardType;
        this.Name = name;
        this.shortName = shortName;
    }








}

