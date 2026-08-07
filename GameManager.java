package com.secretkiller.app;
import java.util.*;

public class GameManager {
    public final Story story; public final ArrayList<Player> players=new ArrayList<>(); public int round=0,wrongVotes=0;
    private final Random random=new Random();
    public GameManager(Story story,ArrayList<String> names){
        this.story=story; ArrayList<String> roles=story.rolesFor(names.size()); Collections.shuffle(roles,random);
        for(int i=0;i<names.size();i++){String role=roles.get(i); boolean guilty=!isInnocentRole(role); String secret=secretFor(role,story.id); String knowledge=knowledgeFor(role,story.id); String statement=statementFor(role,story.id); players.add(new Player(i,names.get(i),role,guilty,secret,knowledge,statement));}
    }
    private boolean isInnocentRole(String r){return !(r.equals("القاتل")||r.equals("القاتل ١")||r.equals("القاتل ٢")||r.equals("السارق")||r.equals("المتواطئ"));}
    private String secretFor(String role,String id){
        if(role.contains("القاتل")) return id.equals("train")?"تعرف كيف عطلت نظام العربة، لكن لا تكشف السبب.":"أنت المذنب الأساسي. هدفك أن تمر الجريمة دون أن يكتشف الآخرون الرابط بين الأدلة.";
        if(role.equals("السارق")) return "تريد الشيء المسروق لنفسك، لكنك لم ترتكب الجريمة الأصلية. لا تكشف أنك أخفيت دليلًا.";
        if(role.equals("المتواطئ")) return "ساعدت أحد المذنبين بطريقة غير مباشرة، لكنك لا تعرف كل تفاصيل الخطة.";
        return "لديك سر شخصي قد يجعلك تبدو مذنبًا، لكنه لا يعني أنك ارتكبت الجريمة.";
    }
    private String knowledgeFor(String role,String id){
        if(role.contains("القاتل")) return "تعرف أن دليلًا واحدًا على الأقل قد يضلل الأبرياء. استخدم النقاش لصالحك.";
        if(role.equals("السارق")) return "تعرف أن شخصًا آخر كان يتحرك في مكان لا ينبغي أن يكون فيه.";
        if(role.equals("المتواطئ")) return "تعرف جزءًا من الحقيقة، لكنك لا تعرف كل أدوار المذنبين.";
        return id.equals("museum")?"تعرف أن سجل الكاميرا لا يطابق سجل الحراسة.":id.equals("train")?"تعرف أن الاتصالات انقطعت قبل اكتشاف المشكلة.":"تعرف أن الممر أو النظام كان يعمل بطريقة غير طبيعية قبل الحادث.";
    }
    private String statementFor(String role,String id){
        if(role.contains("القاتل")) return id.equals("train")?"كنت في عربة الركاب عندما انقطع الاتصال.":"كنت بعيدًا عن مكان الحادث عندما بدأ كل شيء.";
        if(role.equals("السارق")) return id.equals("museum")?"كنت أتابع حركة الحارس ولم ألمس الخزنة.":"كنت أبحث عن مخرج عندما بدأت الفوضى.";
        if(role.equals("المتواطئ")) return "كنت أحاول مساعدة الفريق، ولم أكن أعرف أن الأمر سيتحول إلى جريمة.";
        return "كنت مشغولًا بمهمتي عندما وقع الحادث ولم أقترب من مكان الجريمة.";
    }
    public void nextRound(){round++;}
    public ArrayList<Player> activePlayers(){ArrayList<Player> a=new ArrayList<>();for(Player p:players)if(!p.eliminated)a.add(p);return a;}
    public Player playerById(int id){for(Player p:players)if(p.id==id)return p;return null;}
    public void eliminate(int id){Player p=playerById(id);if(p!=null&&!p.eliminated){p.eliminated=true;if(!p.guilty)wrongVotes++;}}
    public int guiltyAlive(){int n=0;for(Player p:players)if(p.guilty&&!p.eliminated)n++;return n;}
    public int innocentAlive(){int n=0;for(Player p:players)if(!p.guilty&&!p.eliminated)n++;return n;}
    public boolean innocentsWin(){return guiltyAlive()==0;}
    public boolean guiltyWin(){return guiltyAlive()>0 && guiltyAlive()>=innocentAlive();}
}
