package com.secretkiller.app;

/** Character identity is public; alignment stays private.
 *
 * `statement`, `objective`, and `crimeObjective` are legacy fields from the earlier
 * (Phase 4/5) design and are kept only so the 12 original built-in stories and any
 * already-saved custom stories keep compiling and playing unchanged — they are never
 * required for new content. The gold-standard architecture's character shape is just:
 * name, profession, publicIdentity, secret, knowledge (= "private knowledge" — what the
 * character knows about the case and may voluntarily reveal), and guilty. Use the leaner
 * six-argument constructor below for any new story; it leaves the legacy fields empty,
 * and the UI skips a panel entirely when its text is empty rather than showing blank
 * "objective"/"statement" panels. */
public final class StoryCharacter {
    public final String name, profession, publicIdentity, secret, knowledge, statement, objective, crimeObjective;
    public final boolean guilty;

    /** Full legacy shape — used by the original 12 built-in stories and by stories loaded
     * from older saved custom-story data. */
    public StoryCharacter(String name,String profession,String publicIdentity,String secret,String knowledge,String statement,String objective,boolean guilty,String crimeObjective){this.name=name;this.profession=profession;this.publicIdentity=publicIdentity;this.secret=secret;this.knowledge=knowledge;this.statement=statement;this.objective=objective;this.guilty=guilty;this.crimeObjective=crimeObjective;}

    /** Gold-standard shape: name, profession, public identity, secret, private knowledge,
     * guilty. No objective, no statement, no separate crime objective — per the redesigned
     * architecture, only `secret` carries the guilty character's connection to the crime. */
    public StoryCharacter(String name,String profession,String publicIdentity,String secret,String knowledge,boolean guilty){this(name,profession,publicIdentity,secret,knowledge,"","",guilty,"");}
}
