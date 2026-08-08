package com.secretkiller.app;

/** Public briefing displayed before players enter their names. */
public final class StoryIntroduction {
    public final String setting, situation, incident, stakes, objective;
    public StoryIntroduction(String setting,String situation,String incident,String stakes,String objective){this.setting=setting;this.situation=situation;this.incident=incident;this.stakes=stakes;this.objective=objective;}
    public String display(){return "المكان\n"+setting+"\n\nالوضع\n"+situation+"\n\nالحادث\n"+incident+"\n\nالمخاطر\n"+stakes+"\n\nهدف اللاعبين\n"+objective;}
}
