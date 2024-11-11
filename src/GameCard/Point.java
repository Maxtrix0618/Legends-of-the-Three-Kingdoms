package GameCard;

public enum Point {
    A("A", 1),
    N_2("2", 2),
    N_3("3", 3),
    N_4("4", 4),
    N_5("5", 5),
    N_6("6", 6),
    N_7("7", 7),
    N_8("8", 8),
    N_9("9", 9),
    N_10("10", 10),
    J("J", 11),
    Q("Q", 12),
    K("K", 13),
    E("虚拟", 0);       // 空属性-虚拟牌用

    public final String Name;
    public final int Value;

    Point(String Name, int value) {
        this.Name = Name;
        this.Value = value;
    }

}
