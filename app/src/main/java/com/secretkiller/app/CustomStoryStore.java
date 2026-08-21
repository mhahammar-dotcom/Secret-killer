package com.secretkiller.app;

import android.content.Context;
import android.content.SharedPreferences;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;

/** Local-only persistence boundary; it can later be replaced by import/export or sharing.
 * Reads the current character/investigation schema (name, profession, publicIdentity,
 * knowledge, guilty -- no secret, no objective) as well as older ones, so stories saved
 * before this redesign keep loading and playing. Older data may still contain "secret" or
 * "objective" values on disk; those are safely ignored on load rather than crashing, and
 * never re-written on save. An old story with no investigation rounds simply skips straight
 * to voting (see GameManager.currentInvestigationRound). */
public final class CustomStoryStore {
    private static final String PREFS="custom_stories", KEY="stories";
    private CustomStoryStore() {}
    public static ArrayList<Story> load(Context context) {
        ArrayList<Story> result=new ArrayList<>(); String raw=context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString(KEY,"[]");
        try {
            JSONArray list=new JSONArray(raw);
            for(int i=0;i<list.length();i++){
                JSONObject o=list.getJSONObject(i);
                JSONArray chars=o.getJSONArray("characters"), clues=o.getJSONArray("clues");
                StoryCharacter[] cards=new StoryCharacter[chars.length()];
                Clue[] clueCards=new Clue[clues.length()];
                for(int j=0;j<chars.length();j++){
                    JSONObject c=chars.getJSONObject(j);
                    boolean guilty=c.optBoolean("guilty",false);
                    String profession=c.has("profession")?c.optString("profession",""):c.optString("role","");
                    String publicIdentity=c.has("publicIdentity")?c.optString("publicIdentity",""):("أنت " + profession + ".");
                    // Current schema stores privateKnowledge as "knowledge". Older saves (before
                    // this redesign) only had "secret" -- migrate that into knowledge rather than
                    // losing it. Any "objective"/"crimeObjective"/"statement" values still present
                    // in old data are simply never read: safely ignored, per the redesign.
                    String knowledge=c.optString("knowledge", c.optString("secret",""));
                    cards[j]=new StoryCharacter(c.optString("name",""),profession,publicIdentity,knowledge,guilty);
                }
                for(int j=0;j<clues.length();j++) clueCards[j]=new Clue(clues.getString(j));
                InvestigationRound[] rounds;
                if(o.has("investigationRounds")){
                    JSONArray roundsJson=o.getJSONArray("investigationRounds");
                    rounds=new InvestigationRound[roundsJson.length()];
                    for(int j=0;j<roundsJson.length();j++){
                        JSONObject r=roundsJson.getJSONObject(j);
                        rounds[j]=new InvestigationRound(r.optInt("roundNumber",j+1),r.optString("title",""),r.optString("publicClue",""),r.optString("description",""),r.optString("discussionPrompt",""));
                    }
                } else {
                    rounds=new InvestigationRound[0];
                }
                result.add(Story.custom(o.getString("id"),o.getString("title"),o.getString("description"),cards,clueCards,o.optString("ending",""),rounds));
            }
        } catch(Exception ignored) {}
        return result;
    }
    public static void save(Context context, Story story) {
        ArrayList<Story> stories=load(context); stories.add(story); JSONArray list=new JSONArray();
        try {
            for(Story s:stories){
                JSONObject o=new JSONObject();
                o.put("id",s.id);o.put("title",s.title);o.put("description",s.description);o.put("ending",s.customEnding);
                JSONArray chars=new JSONArray(),clues=new JSONArray(),rounds=new JSONArray();
                for(StoryCharacter c:s.customCharacters){
                    JSONObject x=new JSONObject();
                    x.put("name",c.name);x.put("profession",c.profession);x.put("publicIdentity",c.publicIdentity);
                    x.put("knowledge",c.knowledge);x.put("guilty",c.guilty);
                    chars.put(x);
                }
                for(Clue c:s.clues)clues.put(c.text);
                for(InvestigationRound r:s.investigationRounds){
                    JSONObject x=new JSONObject();
                    x.put("roundNumber",r.roundNumber);x.put("title",r.title);x.put("publicClue",r.publicClue);
                    x.put("description",r.description);x.put("discussionPrompt",r.discussionPrompt);
                    rounds.put(x);
                }
                o.put("characters",chars);o.put("clues",clues);o.put("investigationRounds",rounds);list.put(o);
            }
        } catch(Exception ignored) { return; }
        context.getSharedPreferences(PREFS,Context.MODE_PRIVATE).edit().putString(KEY,list.toString()).apply();
    }
}
