package com.secretkiller.app;

/** One publicly-shared investigation beat, shown to everyone before voting starts.
 * Distinct from Clue (which drives the existing post-vote round system) and from
 * StoryCharacter (which stays private) — this is public, story-specific, and never
 * reveals alignment. */
public final class InvestigationRound {
    public final int roundNumber;
    public final String title, publicClue, description, discussionPrompt;

    public InvestigationRound(int roundNumber, String title, String publicClue, String description, String discussionPrompt) {
        this.roundNumber = roundNumber;
        this.title = title;
        this.publicClue = publicClue;
        this.description = description == null ? "" : description;
        this.discussionPrompt = discussionPrompt == null ? "" : discussionPrompt;
    }
}
