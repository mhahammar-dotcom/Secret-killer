package com.secretkiller.app;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.HashSet;

public final class GameManager {
    public final Story story;
    public final ArrayList<Player> players = new ArrayList<>();
    public int round, wrongVotes;
    private int clueIndex;
    private boolean reuseCurrentClue;
    /** 0-based index into story.investigationRounds; advances as the pre-vote briefing plays out. */
    public int investigationIndex;
    public final HashSet<String> investigationActionsUsed = new HashSet<>();
    private final Random random = new Random();

    public GameManager(Story story, ArrayList<String> names) {
        this.story = story;
        ArrayList<StoryCharacter> cast = story.charactersFor(names.size(), random);
        Collections.shuffle(cast, random);
        for (int i = 0; i < names.size(); i++) players.add(new Player(i, names.get(i), cast.get(i)));
        assignInvestigationRoles();
    }
    private void assignInvestigationRoles(){ArrayList<Player> innocent=activePlayers(); innocent.removeIf(p->p.guilty); if(!innocent.isEmpty()) innocent.get(random.nextInt(innocent.size())).detective=true;}
    public Player detective(){for(Player p:players)if(p.detective&&!p.eliminated)return p;return null;}
    public String conditionalClueFor(String actionId){for(InvestigationData.ConditionalClue c:story.investigation.conditionalClues)if(c.actionId.equals(actionId))return c.text;return "";}

    /** The public investigation round due to be shown next, or null once every round in this
     * story has already been shown (including when a story has none, e.g. an older custom
     * story saved before this feature existed). */
    public InvestigationRound currentInvestigationRound() {
        return investigationIndex < story.investigationRounds.length ? story.investigationRounds[investigationIndex] : null;
    }
    public boolean investigationFinished() { return investigationIndex >= story.investigationRounds.length; }

    /** Starts a round with the next unrevealed clue, unless a tie already exposed it. */
    public Clue startNextRound() {
        round++;
        if (round > 1 && !reuseCurrentClue) clueIndex = Math.min(clueIndex + 1, story.clues.length - 1);
        reuseCurrentClue = false;
        return story.clues[clueIndex];
    }

    /** A tie may reveal one new clue; that clue becomes the next round's discussion clue. */
    public Clue revealExtraClue() {
        clueIndex = Math.min(clueIndex + 1, story.clues.length - 1);
        reuseCurrentClue = true;
        return story.clues[clueIndex];
    }
    public ArrayList<Player> activePlayers() { ArrayList<Player> active = new ArrayList<>(); for (Player p : players) if (!p.eliminated) active.add(p); return active; }
    public Player playerById(int id) { for (Player p : players) if (p.id == id) return p; return null; }
    public void eliminate(int id) { Player p = playerById(id); if (p != null && !p.eliminated) { p.eliminated = true; if (!p.guilty) wrongVotes++; } }
    public int guiltyAlive() { int count = 0; for (Player p : players) if (p.guilty && !p.eliminated) count++; return count; }
    public int innocentAlive() { int count = 0; for (Player p : players) if (!p.guilty && !p.eliminated) count++; return count; }
    public boolean innocentsWin() { return guiltyAlive() == 0; }
    public boolean guiltyWin() { return guiltyAlive() >= innocentAlive() && guiltyAlive() > 0; }
}
