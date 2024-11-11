package Skill;


public enum SkillName {
    ZhangBa("丈八", false, false, new String[]{}),

    LongDan("龙胆", false, false, new String[]{
            "能进能退，乃真正法器！",
            "吾乃常山赵子龙也！"
    }),
    J_PoJun("破军", false, false, new String[]{
            "犯大吴疆土者，盛必击而破之！",
            "若敢来犯，必叫你大败而归！"
    }),
    M_LieGong("烈弓", false, false, new String[]{
            "吾虽年迈，箭矢犹锋！",
            "矢贯坚石，劲冠三军！"
    });



    public final String Name;
    public final String[] words;

    public final boolean lock;        // 锁定技
    public final boolean limit;       // 限定技
    SkillName(String Name, boolean lock, boolean limit, String[] words) {
        this.Name = Name;
        this.lock = lock;
        this.limit = limit;
        this.words = words;
    }


    public String skillNameText() {
        return ("『" + Name + "』");
    }


}
