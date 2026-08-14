package com.secretkiller.app;

import android.content.Context;
import android.content.SharedPreferences;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;

/** Local-only persistence boundary; it can later be replaced by import/export or sharing.
 * Reads both the current character schema and the older (pre-redesign) one, so stories
 * saved before this update keep loading and playing. */
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
                    boolean guilty=c.getBoolean("guilty");
                    String profession=c.has("profession")?c.getString("profession"):c.optString("role","");
                    String publicIdentity=c.has("publicIdentity")?c.getString("publicIdentity"):("أنت " + profession + ".");
                    String objective=c.has("objective")?c.getString("objective"):"راقب الآخرين وحافظ على قصتك متماسكة.";
                    String crimeObjective=c.has("crimeObjective")?c.getString("crimeObjective"):(guilty?c.getString("secret"):"");
                    cards[j]=new StoryCharacter(c.getString("name"),profession,publicIdentity,c.getString("secret"),c.getString("knowledge"),c.getString("statement"),objective,guilty,crimeObjective);
                }
                for(int j=0;j<clues.length();j++) clueCards[j]=new Clue(clues.getString(j));
                result.add(Story.custom(o.getString("id"),o.getString("title"),o.getString("description"),cards,clueCards,o.getString("ending")));
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
                JSONArray chars=new JSONArray(),clues=new JSONArray();
                for(StoryCharacter c:s.customCharacters){
                    JSONObject x=new JSONObject();
                    x.put("name",c.name);x.put("profession",c.profession);x.put("publicIdentity",c.publicIdentity);
                    x.put("secret",c.secret);x.put("knowledge",c.knowledge);x.put("statement",c.statement);
                    x.put("objective",c.objective);x.put("guilty",c.guilty);x.put("crimeObjective",c.crimeObjective);
                    chars.put(x);
                }
                for(Clue c:s.clues)clues.put(c.text);
                o.put("characters",chars);o.put("clues",clues);list.put(o);
            }
        } catch(Exception ignored) { return; }
        context.getSharedPreferences(PREFS,Context.MODE_PRIVATE).edit().putString(KEY,list.toString()).apply();
    }
}
