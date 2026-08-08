package com.secretkiller.app;

/** Character identity is public; alignment and crime objective remain private. */
public final class StoryCharacter {
    public final String name, profession, publicIdentity, secret, knowledge, statement, objective, crimeObjective;
    public final boolean guilty;
    public StoryCharacter(String name,String profession,String publicIdentity,String secret,String knowledge,String statement,String objective,boolean guilty,String crimeObjective){this.name=name;this.profession=profession;this.publicIdentity=publicIdentity;this.secret=secret;this.knowledge=knowledge;this.statement=statement;this.objective=objective;this.guilty=guilty;this.crimeObjective=crimeObjective;}
}
