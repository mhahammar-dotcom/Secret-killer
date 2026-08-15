package com.secretkiller.app;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

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
                assertNotNull(c.knowledge); assertFalse(c.knowledge.isEmpty());
            }
        }
    }

    @Test public void legacyStoriesStillCarryTheirOriginalObjectiveAndCrimeObjective() {
        // The 11 original pool-based stories (everything except the gold-standard one) were
        // never rewritten, and must keep behaving exactly as before.
        for (Story story : Story.catalog()) {
            if (story.id.equals("gala_toast")) continue;
            for (StoryCharacter c : story.charactersFor(story.maxPlayers, new Random(3))) {
                assertFalse(story.id + "/" + c.name, c.objective.isEmpty());
                if (c.guilty) assertFalse(story.id + "/" + c.name, c.crimeObjective.isEmpty());
            }
        }
    }

    @Test public void goldStandardStoryHasSixCharactersExactlyOneGuiltyAndNoObjectiveField() {
        Story gala = findStory("gala_toast");
        assertEquals(6, gala.minPlayers);
        assertEquals(6, gala.maxPlayers);
        ArrayList<StoryCharacter> cast = gala.charactersFor(6, new Random(4));
        assertEquals(6, cast.size());
        int guiltyCount = 0;
        for (StoryCharacter c : cast) {
            assertTrue(c.objective.isEmpty());
            assertTrue(c.statement.isEmpty());
            assertTrue(c.crimeObjective.isEmpty());
            assertFalse(c.knowledge.isEmpty());
            assertFalse(c.secret.isEmpty());
            if (c.guilty) guiltyCount++;
        }
        assertEquals(1, guiltyCount);
    }

    @Test public void goldStandardStoryHasAmbiguousEvidenceAndAFullEndingExplanation() {
        Story gala = findStory("gala_toast");
        assertTrue(gala.investigationRounds.length >= 3);
        for (InvestigationRound evidence : gala.investigationRounds) {
            assertFalse(evidence.title.isEmpty());
            assertFalse(evidence.publicClue.isEmpty());
            // Evidence must not name a character directly, per the "ambiguous evidence" rule.
            for (StoryCharacter c : gala.charactersFor(6, new Random(5))) assertFalse(evidence.publicClue.contains(c.name));
        }
        assertTrue(gala.isCustom());
        assertTrue(gala.customEnding.length() > 200);
    }

    @Test public void votingIsNeverGatedByHowMuchEvidenceHasBeenRevealed() {
        // The new architecture must not force players through every piece of evidence before
        // voting is allowed -- GameManager exposes no such gate, by design.
        Story gala = findStory("gala_toast");
        GameManager game = new GameManager(gala, names(6));
        assertEquals(0, game.investigationIndex); // nothing revealed yet
        Clue clue = game.startNextRound(); // voting/round flow works regardless
        assertNotNull(clue);
        for (Player p : game.players) if (p.guilty) game.eliminate(p.id);
        assertTrue(game.innocentsWin());
    }

    private Story findStory(String id) {
        for (Story s : Story.catalog()) if (s.id.equals(id)) return s;
        throw new AssertionError("story not found: " + id);
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

    @Test public void everyBuiltInStoryHasAtLeastThreeInvestigationRounds() {
        for (Story story : Story.catalog()) {
            assertTrue(story.id, story.investigationRounds.length >= 3);
            for (InvestigationRound r : story.investigationRounds) {
                assertFalse(r.title.isEmpty());
                assertFalse(r.publicClue.isEmpty());
            }
        }
    }

    @Test public void investigationRoundsPlayOutInOrderThenHandOffToVoting() {
        GameManager game = new GameManager(Story.catalog().get(0), names(4));
        int total = game.story.investigationRounds.length;
        for (int i = 0; i < total; i++) {
            InvestigationRound r = game.currentInvestigationRound();
            assertNotNull(r);
            assertEquals(i + 1, r.roundNumber);
            assertFalse(game.investigationFinished());
            game.investigationIndex++;
        }
        assertTrue(game.investigationFinished());
        assertNull(game.currentInvestigationRound());
    }

    @Test public void aStoryWithNoInvestigationRoundsGoesStraightToVoting() {
        StoryCharacter[] cast = new StoryCharacter[4];
        for (int i = 0; i < 4; i++) cast[i] = new StoryCharacter("شخصية " + i, "مهنة", "هوية", "سر", "معرفة", "قول", "هدف", i == 0, i == 0 ? "جريمة" : "");
        Story legacyCustomStory = Story.custom("legacy", "قصة قديمة", "وصف", cast, new Clue[]{new Clue("دليل")}, "النهاية", null);
        GameManager game = new GameManager(legacyCustomStory, names(4));
        assertTrue(game.investigationFinished());
        assertNull(game.currentInvestigationRound());
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
