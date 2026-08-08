package com.secretkiller.app;

import java.util.ArrayList;

public final class Story {
    public final String id, title, description;
    public final int minPlayers, maxPlayers;
    public final Clue[] clues;
    public final String[] wrongVoteHints;
    private final String[] innocentRoles;

    public Story(String id, String title, String description, int min, int max, String[] innocentRoles, Clue[] clues, String[] hints) {
        this.id = id; this.title = title; this.description = description; this.minPlayers = min; this.maxPlayers = max;
        this.innocentRoles = innocentRoles; this.clues = clues; this.wrongVoteHints = hints;
    }

    public ArrayList<String> rolesFor(int players) {
        ArrayList<String> roles = new ArrayList<>();
        int guilty = id.equals("dreams") ? (players <= 5 ? 1 : players <= 7 ? 2 : 3)
                : id.equals("museum") ? (players <= 6 ? 1 : players <= 9 ? 2 : 3)
                : (players <= 5 ? 1 : players <= 8 ? 2 : 3);
        if (id.equals("dreams")) { roles.add("القاتل"); if (guilty >= 2) roles.add("المتواطئ"); if (guilty >= 3) roles.add("السارق"); }
        else if (id.equals("museum")) { roles.add("السارق"); if (guilty >= 2) roles.add("المتواطئ"); if (guilty >= 3) roles.add("القاتل"); }
        else { roles.add("القاتل ١"); if (guilty >= 2) roles.add("القاتل ٢"); if (guilty >= 3) roles.add("المتواطئ"); }
        for (int i = 0; roles.size() < players; i++) roles.add(innocentRoles[i % innocentRoles.length]);
        return roles;
    }

    public static ArrayList<Story> catalog() {
        ArrayList<Story> stories = new ArrayList<>();
        stories.add(new Story("dreams", "مدينة الأحلام", "جريمة داخل حلم مشترك. الحقيقة موزعة بين الذاكرة والأمن والـCore.", 4, 12,
                new String[]{"مهندس الأمن", "عالمة الذاكرة", "الطبيب العصبي", "الصحفية المتخفية", "عالم الفيزياء"},
                new Clue[]{new Clue("21:43: تم فتح ممر غير موجود في الخريطة العامة بصلاحية خاصة."), new Clue("21:47: تحرك الـCore من الخزنة، ثم اختفى جزء من سجل الكاميرات."), new Clue("بعد الجريمة تم استخدام بطاقة لا يفترض أن تكون فعالة في ذلك الوقت.")},
                new String[]{"الممر فُتح فعلًا، لكن هذا لا يثبت أن صاحب البطاقة هو القاتل.", "الشريحة اختفت بعد بداية الفوضى؛ ركزوا على من استفاد من الفوضى.", "تعديل الكاميرات حدث بعد الحادث، وليس قبله."}));
        stories.add(new Story("museum", "سرقة المتحف الأسود", "لوحة نادرة اختفت أثناء إغلاق المتحف، لكن شخصًا داخل الفريق يعرف أكثر مما يقول.", 4, 12,
                new String[]{"أمين المتحف", "خبير الترميم", "الحارس الليلي", "صحفي الفن", "المصور", "خبيرة التأمين"},
                new Clue[]{new Clue("تم فتح خزنة اللوحة دون كسرها."), new Clue("كاميرا القاعة سجلت دقيقة واحدة مفقودة."), new Clue("نسخة من مفتاح الطوارئ ظهرت في غرفة الموظفين.")},
                new String[]{"امتلاك مفتاح لا يعني استخدامه.", "الدقيقة المفقودة أهم من الكاميرا التي سجلت كل شيء.", "السارق احتاج إلى شخص يعرف جدول الحراسة."}));
        stories.add(new Story("train", "قطار منتصف الليل", "حادث غامض داخل قطار لا يتوقف لمدة ست ساعات، وكل عربة تخفي جزءًا من الحقيقة.", 4, 12,
                new String[]{"رئيس القطار", "طبيب الطوارئ", "المهندس", "المصور", "المسافر الغامض", "موظف الاتصالات"},
                new Clue[]{new Clue("باب العربة ٤ فُتح من الداخل رغم أن النظام قال إنه مقفل."), new Clue("سجل الاتصالات توقف لمدة 90 ثانية."), new Clue("تم العثور على تذكرة باسم مستعار في العربة الأخيرة.")},
                new String[]{"الذي دخل العربة ليس بالضرورة من فتح الباب.", "توقف الاتصالات قد يكون نتيجة عطل، أو فعلًا مقصودًا.", "التذكرة المزورة تثبت هوية مخفية، لا تثبت وحدها الجريمة."}));
        return stories;
    }
}
