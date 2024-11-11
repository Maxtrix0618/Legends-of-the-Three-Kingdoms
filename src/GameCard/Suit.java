package GameCard;

public enum Suit {
    SPADE("♠", "黑桃", "BLACK"),
    HEART("♥", "红桃", "RED"),
    CLUB("♣", "梅花", "BLACK"),
    DIAMOND("♦", "方块",  "RED"),
    E("♋", "", "");                      // 空属性-虚拟牌用


    public final String Symbol;
    public final String Name;
    public final String Color;

    Suit(String symbol, String Name, String Color) {
        this.Symbol = symbol;
        this.Name = Name;
        this.Color = Color;
    }

    public String message() {
        return "[<" + Symbol + ">" + Name + "]";
    }



}
