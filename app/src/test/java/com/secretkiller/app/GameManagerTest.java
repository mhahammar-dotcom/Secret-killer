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

    @Test public void everyCharacterHasABelievableIdentityWithPrivateKnowledgeAndNoSecretOrObjective() {
        // Applies to every story now -- the 12 original built-in stories and gala_toast were
        // all migrated off secret/objective/statement/crimeObjective in this pass.
        for (Story story : Story.catalog()) {
            for (StoryCharacter c : story.charactersFor(story.maxPlayers, new Random(2))) {
                assertFalse(story.id + "/" + c.name, c.name.isEmpty());
                assertFalse(story.id + "/" + c.name, c.profession.isEmpty());
                assertFalse(story.id + "/" + c.name, c.publicIdentity.isEmpty());
                assertFalse(story.id + "/" + c.name + ": knowledge should not be empty", c.knowledge.isEmpty());
                assertTrue(story.id + "/" + c.name + ": secret should be empty", c.secret.isEmpty());
                assertTrue(story.id + "/" + c.name + ": objective should be empty", c.objective.isEmpty());
                assertTrue(story.id + "/" + c.name + ": statement should be empty", c.statement.isEmpty());
                assertTrue(story.id + "/" + c.name + ": crimeObjective should be empty", c.crimeObjective.isEmpty());
            }
        }
    }

    @Test public void privateKnowledgeDiffersBetweenCharactersInTheSameStory() {
        // At small-to-moderate player counts every character is unique; only at very high
        // counts does the (pre-existing, documented) innocent-pool cycling kick in and repeat
        // a character, which is an accepted trade-off, not a content bug.
        for (Story story : Story.catalog()) {
            int n = Math.min(story.maxPlayers, story.minPlayers + 2);
            ArrayList<StoryCharacter> cast = story.charactersFor(n, new Random(6));
            java.util.HashSet<String> seen = new java.util.HashSet<>();
            for (StoryCharacter c : cast) assertTrue(story.id + ": duplicate knowledge text for " + c.name, seen.add(c.knowledge));
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
            assertTrue(c.secret.isEmpty());
            assertFalse(c.knowledge.isEmpty());
            if (c.guilty) guiltyCount++;
        }
        assertEquals(1, guiltyCount);
    }

    @Test public void everyStoryHasAmbiguousEvidenceThatNamesNoCharacter() {
        for (Story story : Story.catalog()) {
            ArrayList<StoryCharacter> cast = story.charactersFor(story.maxPlayers, new Random(5));
            for (InvestigationRound evidence : story.investigationRounds) {
                assertFalse(evidence.title.isEmpty());
                assertFalse(evidence.publicClue.isEmpty());
                for (StoryCharacter c : cast) assertFalse(story.id + ": evidence '" + evidence.title + "' names " + c.name, containsWholeWord(evidence.publicClue, c.name));
            }
        }
    }

    @Test public void everyStoryHasAFullFinalRevealExplanation() {
        // Pool-based stories carry `solution`; fixed-cast stories carry `customEnding`.
        // MainActivity.reveal() prefers solution when present, falling back to customEnding.
        for (Story story : Story.catalog()) {
            String ending = !story.solution.isEmpty() ? story.solution : story.customEnding;
            assertNotNull(story.id, ending);
            assertTrue(story.id + ": final reveal text too short", ending.length() > 200);
        }
    }

    private boolean containsWholeWord(String text, String word) {
        int idx = text.indexOf(word);
        while (idx != -1) {
            boolean leftOk = idx == 0 || !Character.isLetter(text.charAt(idx - 1));
            int end = idx + word.length();
            boolean rightOk = end >= text.length() || !Character.isLetter(text.charAt(end));
            if (leftOk && rightOk) return true;
            idx = text.indexOf(word, idx + 1);
        }
        return false;
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

    @Test public void allPlayerCountsFourToTwelveProduceAValidCast() {
        for (Story story : Story.catalog()) {
            if (story.isCustom()) continue; // fixed-cast stories only support their own exact size
            for (int n = 4; n <= 12; n++) {
                ArrayList<StoryCharacter> cast = story.charactersFor(n, new Random(n));
                assertEquals(story.id + " @" + n, n, cast.size());
            }
        }
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
