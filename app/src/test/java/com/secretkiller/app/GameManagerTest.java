package com.secretkiller.app;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.Random;

import org.junit.Test;

public class GameManagerTest {
    @Test public void castSizeAndGuiltyCountMatchPlayerCount() {
        Story dreams = Story.catalog().get(0);
        Random random = new Random(1);
        assertEquals(4, dreams.charactersFor(4, random).size());
        assertEquals(2, guiltyCount(dreams.charactersFor(7, random)));
        assertEquals(3, guiltyCount(dreams.charactersFor(12, random)));
    }

    @Test public void everyCharacterHasABelievableIdentitySeparateFromAlignment() {
        for (Story story : Story.catalog()) {
            for (StoryCharacter c : story.charactersFor(story.maxPlayers, new Random(2))) {
                assertNotNull(c.name); assertFalse(c.name.isEmpty());
                assertNotNull(c.profession); assertFalse(c.profession.isEmpty());
                assertNotNull(c.publicIdentity); assertFalse(c.publicIdentity.isEmpty());
                assertNotNull(c.secret); assertFalse(c.secret.isEmpty());
                assertNotNull(c.objective); assertFalse(c.objective.isEmpty());
                if (c.guilty) assertFalse(c.crimeObjective.isEmpty());
            }
        }
    }

    @Test public void eliminatingEveryGuiltyPlayerWinsForInnocents() {
        GameManager game = new GameManager(Story.catalog().get(0), names(4));
        for (Player player : game.players) if (player.guilty) game.eliminate(player.id);
        assertTrue(game.innocentsWin());
        assertFalse(game.guiltyWin());
    }

    @Test public void eliminatingAnInnocentTracksWrongVote() {
        GameManager game = new GameManager(Story.catalog().get(1), names(4));
        for (Player player : game.players) if (!player.guilty) { game.eliminate(player.id); break; }
        assertEquals(1, game.wrongVotes);
    }

    @Test public void tieClueIsNotRepeatedAsANewClueInTheNextRound() {
        GameManager game = new GameManager(Story.catalog().get(0), names(4));
        assertEquals(game.story.clues[0].text, game.startNextRound().text);
        assertEquals(game.story.clues[1].text, game.revealExtraClue().text);
        assertEquals(game.story.clues[1].text, game.startNextRound().text);
        assertEquals(game.story.clues[2].text, game.startNextRound().text);
    }

    private int guiltyCount(ArrayList<StoryCharacter> cast) {
        int count = 0;
        for (StoryCharacter c : cast) if (c.guilty) count++;
        return count;
    }
    private ArrayList<String> names(int count) {
        ArrayList<String> result = new ArrayList<>();
        for (int i = 0; i < count; i++) result.add("لاعب " + (i + 1));
        return result;
    }
}
