package com.secretkiller.app;
import java.util.*;

public class Story {
    public final String id,title,description; public final int minPlayers,maxPlayers; public final Clue[] clues; public final String[] wrongVoteHints;
    private final String[] innocentRoles;
    public Story(String id,String title,String description,int minPlayers,int maxPlayers,String[] innocentRoles,Clue[] clues,String[] hints){
        this.id=id;this.title=title;this.description=description;this.minPlayers=minPlayers;this.maxPlayers=maxPlayers;this.innocentRoles=innocentRoles;this.clues=clues;this.wrongVoteHints=hints;
    }
    public ArrayList<String> rolesFor(int n){
        ArrayList<String> r=new ArrayList<>();
        int guiltyCount = id.equals("dreams") ? (n<=5?1:(n<=7?2:3)) : id.equals("museum") ? (n<=6?1:(n<=9?2:3)) : (n<=5?1:(n<=8?2:3));
        if(id.equals("dreams")){ r.add("القاتل"); if(guiltyCount>=2)r.add("المتواطئ"); if(guiltyCount>=3)r.add("السارق"); }
        else if(id.equals("museum")){ r.add("السارق"); if(guiltyCount>=2)r.add("المتواطئ"); if(guiltyCount>=3)r.add("القاتل"); }
        else { r.add("القاتل ١"); if(guiltyCount>=2)r.add("القاتل ٢"); if(guiltyCount>=3)r.add("المتواطئ"); }
        for(int i=0;r.size()<n;i++) r.add(innocentRoles[i%innocentRoles.length]);
        return r;
    }
    public static ArrayList<Story> catalog(){
        ArrayList<Story> list=new ArrayList<>();
        list.add(new Story("dreams","مدينة الأحلام","جريمة داخل حلم مشترك. الحقيقة موزعة بين الذاكرة والأمن والـCore.",4,12,
            new String[]{"مهندس الأمن","عالمة الذاكرة","الطبيب العصبي","الصحفية المتخفية","عالم الفيزياء"},
            new Clue[]{new Clue("21:43: تم فتح ممر غير موجود في الخريطة العامة بصلاحية خاصة."),new Clue("21:47: تحرك الـCore من الخزنة، ثم اختفى جزء من سجل الكاميرات."),new Clue("بعد الجريمة تم استخدام بطاقة لا يفترض أن تكون فعالة في ذلك الوقت.")},
            new String[]{"الممر فُتح فعلًا، لكن هذا لا يثبت أن صاحب البطاقة هو القاتل.","الشريحة اختفت بعد بداية الفوضى؛ ركزوا على من استفاد من الفوضى.","تعديل الكاميرات حدث بعد الحادث، وليس قبله."}));
        list.add(new Story("museum","سرقة المتحف الأسود","لوحة نادرة اختفت أثناء إغلاق المتحف، لكن شخصًا داخل الفريق يعرف أكثر مما يقول.",4,12,
            new String[]{"أمين المتحف","خبير الترميم","الحارس الليلي","صحفي الفن","المصور","خبيرة التأمين"},
            new Clue[]{new Clue("تم فتح خزنة اللوحة دون كسرها."),new Clue("كاميرا القاعة سجلت دقيقة واحدة مفقودة."),new Clue("نسخة من مفتاح الطوارئ ظهرت في غرفة الموظفين.")},
            new String[]{"امتلاك مفتاح لا يعني استخدامه.","الدقيقة المفقودة أهم من الكاميرا التي سجلت كل شيء.","السارق احتاج إلى شخص يعرف جدول الحراسة."}));
        list.add(new Story("train","قطار منتصف الليل","حادث غامض داخل قطار لا يتوقف لمدة ست ساعات، وكل عربة تخفي جزءًا من الحقيقة.",4,12,
            new String[]{"رئيس القطار","طبيب الطوارئ","المهندس","المصور","المسافر الغامض","موظف الاتصالات"},
            new Clue[]{new Clue("باب العربة ٤ فُتح من الداخل رغم أن النظام قال إنه مقفل."),new Clue("سجل الاتصالات توقف لمدة 90 ثانية."),new Clue("تم العثور على تذكرة باسم مستعار في العربة الأخيرة.")},
            new String[]{"الذي دخل العربة ليس بالضرورة من فتح الباب.","توقف الاتصالات قد يكون نتيجة عطل، أو فعلًا مقصودًا.","التذكرة المزورة تثبت هوية مخفية، لا تثبت وحدها الجريمة."}));
        return list;
    }
}
