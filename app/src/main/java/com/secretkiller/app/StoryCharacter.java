package com.secretkiller.app;

/** Character identity is public; alignment stays private.
 *
 * The current architecture is: name, profession, publicIdentity, knowledge (= "private
 * knowledge" -- what the character knows about the case, which may be certain, uncertain, or
 * simply what they claim to have seen), and guilty. There is no "secret" concept and no
 * "objective" concept: the game is meant to be solved entirely through ambiguous evidence,
 * differing private knowledge, and player discussion -- not through characters managing or
 * revealing a personal secret.
 *
 * `secret`, `statement`, `objective`, and `crimeObjective` are legacy fields kept only so any
 * already-saved custom story (created before this redesign) keeps loading without crashing.
 * They are never populated for new content and never displayed by the UI, regardless of
 * whether they happen to be empty or not -- see MainActivity.roleReveal(). */
public final class StoryCharacter {
    public final String name, profession, publicIdentity, secret, knowledge, statement, objective, crimeObjective;
    public final boolean guilty;

    /** Legacy full shape -- used only when loading an older saved custom story so its data
     * doesn't crash. Never used for new/current content. */
    public StoryCharacter(String name,String profession,String publicIdentity,String secret,String knowledge,String statement,String objective,boolean guilty,String crimeObjective){this.name=name;this.profession=profession;this.publicIdentity=publicIdentity;this.secret=secret;this.knowledge=knowledge;this.statement=statement;this.objective=objective;this.guilty=guilty;this.crimeObjective=crimeObjective;}

    /** Current architecture: name, profession, public identity, private knowledge, guilty.
     * No secret, no objective. */
    public StoryCharacter(String name,String profession,String publicIdentity,String knowledge,boolean guilty){this(name,profession,publicIdentity,"",knowledge,"","",guilty,"");}
}
