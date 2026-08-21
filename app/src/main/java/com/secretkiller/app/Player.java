package com.secretkiller.app;

/** A human participant. `name` is the person's own name; `character` is the believable
 * in-story identity dealt to them (public identity, private knowledge, alignment). */
public final class Player {
    public final int id;
    public final String name;
    public final StoryCharacter character;
    public final boolean guilty;
    public boolean eliminated;

    public Player(int id, String name, StoryCharacter character) {
        this.id = id; this.name = name; this.character = character; this.guilty = character.guilty;
    }
}
