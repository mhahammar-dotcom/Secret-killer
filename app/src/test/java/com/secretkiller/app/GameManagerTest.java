package com.secretkiller.app;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Test;

public class GameManagerTest {
    @Test public void roleDistributionMatchesPlayerCount() {
        Story dreams = Story.catalog().get(0);
        assertEquals(4, dreams.rolesFor(4).size());
        assertEquals(2, guiltyCount(dreams.rolesFor(7)));
        assertEquals(3, guiltyCount(dreams.rolesFor(12)));
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

    private int guiltyCount(ArrayList<String> roles) {
        int count = 0;
        for (String role : roles) if (role.startsWith("القاتل") || role.equals("السارق") || role.equals("المتواطئ")) count++;
        return count;
    }
    private ArrayList<String> names(int count) {
        ArrayList<String> result = new ArrayList<>();
        for (int i = 0; i < count; i++) result.add("لاعب " + (i + 1));
        return result;
    }
}
