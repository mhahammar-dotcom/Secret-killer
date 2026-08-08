package com.secretkiller.app;

import android.content.Context;
import android.content.SharedPreferences;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;

/** Local-only persistence boundary; it can later be replaced by import/export or sharing. */
public final class CustomStoryStore {
    private static final String PREFS="custom_stories", KEY="stories";
    private CustomStoryStore() {}
    public static ArrayList<Story> load(Context context) {
        ArrayList<Story> result=new ArrayList<>(); String raw=context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString(KEY,"[]");
        try { JSONArray list=new JSONArray(raw); for(int i=0;i<list.length();i++){ JSONObject o=list.getJSONObject(i); JSONArray chars=o.getJSONArray("characters"), clues=o.getJSONArray("clues"); CustomCharacter[] cards=new CustomCharacter[chars.length()]; Clue[] clueCards=new Clue[clues.length()]; for(int j=0;j<chars.length();j++){JSONObject c=chars.getJSONObject(j); cards[j]=new CustomCharacter(c.getString("name"),c.getString("role"),c.getString("secret"),c.getString("knowledge"),c.getString("statement"),c.getBoolean("guilty"));} for(int j=0;j<clues.length();j++) clueCards[j]=new Clue(clues.getString(j)); result.add(Story.custom(o.getString("id"),o.getString("title"),o.getString("description"),cards,clueCards,o.getString("ending")));} } catch(Exception ignored) {}
        return result;
    }
    public static void save(Context context, Story story) {
        ArrayList<Story> stories=load(context); stories.add(story); JSONArray list=new JSONArray(); try { for(Story s:stories){JSONObject o=new JSONObject();o.put("id",s.id);o.put("title",s.title);o.put("description",s.description);o.put("ending",s.customEnding);JSONArray chars=new JSONArray(),clues=new JSONArray();for(CustomCharacter c:s.customCharacters){JSONObject x=new JSONObject();x.put("name",c.name);x.put("role",c.role);x.put("secret",c.secret);x.put("knowledge",c.knowledge);x.put("statement",c.statement);x.put("guilty",c.guilty);chars.put(x);}for(Clue c:s.clues)clues.put(c.text);o.put("characters",chars);o.put("clues",clues);list.put(o);} } catch(Exception ignored) { return; }
        context.getSharedPreferences(PREFS,Context.MODE_PRIVATE).edit().putString(KEY,list.toString()).apply();
    }
}
