package com.secretkiller.app;

import android.app.*;
import android.os.*;
import android.graphics.*;
import android.view.*;
import android.widget.*;
import java.util.*;

public class MainActivity extends Activity {
    LinearLayout root;
    final int GOLD=Color.rgb(214,168,79), BG=Color.rgb(9,8,12), CARD=Color.rgb(23,21,29);
    final int WHITE=Color.rgb(247,241,229), MUTED=Color.rgb(170,165,176), RED=Color.rgb(212,91,99), GREEN=Color.rgb(104,181,139);

    String[] names={"اللاعب ١","اللاعب ٢","اللاعب ٣","اللاعب ٤","اللاعب ٥","اللاعب ٦","اللاعب ٧","اللاعب ٨"};
    String[] roles={"بريء — مهندس الأمن","بريء — عالمة الذاكرة","بريء — الطبيب العصبي","بريء — الصحفية المتخفية","بريء — عالم الفيزياء","القاتل","السارق","المتواطئ"};
    String[] secrets={
        "عطّلت إنذارًا قبل الحادث لأنك خفت أن يكتشف المدير مخالفة قديمة.",
        "حذفت جزءًا من ذاكرة الدكتور قبل التجربة لأنك خفت من محتواه.",
        "أعطيت أحد المشاركين دواءً مختلفًا دون إخباره.",
        "أنت لست صحفية فعلًا؛ تعملين على كشف الشركة.",
        "صممت جهازًا يعطل جزءًا من النظام دون إذن.",
        "فتحت الممر السري وأغلقت باب الذاكرة أثناء محاولة الدكتور إيقافك. تسبب ذلك في وفاته.",
        "سرقت شريحة الـCore بعد بدء الفوضى. لا تعرف أن القاتل هو من تسبب في موت الدكتور.",
        "بعد الجريمة رأيت دليلًا يربط شخصًا بالممر السري، فخفيته مقابل المال."
    };
    String[] know={
        "رأيت بطاقة أمنية قرب الممر الغربي الساعة 21:43.",
        "الدكتور كان يتوقع محاولة سرقة الـCore.",
        "وفاة الدكتور لم تكن نتيجة عطل طبيعي.",
        "رأيت مهندس الأمن يدخل البرج مرتين.",
        "الممر السري لا يظهر في الخريطة العادية.",
        "رأيت شخصًا يحمل الشريحة، لكنك لم ترَ وجهه.",
        "هناك شخص آخر استعمل صلاحية خاصة قبل سرقة الشريحة.",
        "أحد سجلات الأمن تم تعديله بعد الحادث."
    };
    String[] says={
        "كنت في غرفة المراقبة عندما بدأ النظام في الانهيار.",
        "كنت أحاول استعادة الذاكرة المحذوفة عندما وقع الحادث.",
        "كنت في المختبر الطبي، ولم أقترب من الممر الغربي.",
        "كنت أبحث عن دليل يدين الشركة عندما بدأ الإنذار.",
        "كنت أختبر جهاز تعطيل الإشارة ولم أكن قرب الدكتور.",
        "كنت في غرفة التحكم عندما بدأ النظام في الانهيار.",
        "كنت أبحث عن مخرج من المنطقة الشرقية عندما انقطع الاتصال.",
        "لم أرَ أي شيء مهم، وكنت أحاول فقط حماية نفسي."
    };

    boolean[] eliminated=new boolean[8];
    int round=1, wrongVotes=0;
    boolean atHome=true;

    @Override public void onCreate(Bundle state){
        super.onCreate(state);
        home();
    }

    @Override public void onBackPressed(){
        if(atHome){
            showExitAppDialog();
        }else{
            showExitStoryDialog();
        }
    }

    TextView text(String s,int size,int color,boolean bold){
        TextView v=new TextView(this);
        v.setText(s); v.setTextSize(size); v.setTextColor(color);
        v.setGravity(Gravity.CENTER);
        v.setTextDirection(View.TEXT_DIRECTION_RTL);
        v.setPadding(18,14,18,14);
        if(bold) v.setTypeface(Typeface.DEFAULT,Typeface.BOLD);
        return v;
    }

    Button btn(String s, boolean primary){
    Button b=new Button(this);

    b.setText(s);
    b.setTextSize(18);
    b.setAllCaps(false);
    b.setTextColor(primary ? Color.BLACK : GOLD);

    b.setGravity(Gravity.CENTER);
    b.setTextDirection(View.TEXT_DIRECTION_RTL);
    b.setTypeface(Typeface.create("sans-serif",Typeface.NORMAL));

    b.setBackgroundColor(
        primary ? GOLD : Color.rgb(40,34,24)
    );

    LinearLayout.LayoutParams p=
        new LinearLayout.LayoutParams(-1,70);

    p.setMargins(0,7,0,7);
    b.setLayoutParams(p);

    return b;
    }

    void base(){
    ScrollView sv=new ScrollView(this);
    sv.setFillViewport(true);

    root=new LinearLayout(this);
    root.setOrientation(LinearLayout.VERTICAL);
    root.setPadding(22,22,22,22);
    root.setBackgroundColor(BG);
    root.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

    sv.addView(root,new ScrollView.LayoutParams(-1,-1));
    setContentView(sv);
    }

    // Adds a consistent home button to every story screen.
    void navigationBar(){
        LinearLayout nav=new LinearLayout(this);
        nav.setOrientation(LinearLayout.HORIZONTAL);
        nav.setGravity(Gravity.CENTER);
        nav.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);

        Button homeBtn=btn("⌂  الرئيسية",false);
        homeBtn.setOnClickListener(v->showExitStoryDialog());

        nav.addView(homeBtn,new LinearLayout.LayoutParams(-1,58));
        root.addView(nav,0);
    }

    void title(String s){ root.addView(text(s,25,WHITE,true)); }

    void panel(String s){
        TextView v=text(s,17,WHITE,false);
        v.setBackgroundColor(CARD);
        LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,-2);
        p.setMargins(0,8,0,8); root.addView(v,p);
    }

    void reset(){
        Arrays.fill(eliminated,false);
        round=1; wrongVotes=0;
    }

    void home(){
    atHome=true;
    base();

    LinearLayout center=new LinearLayout(this);
    center.setOrientation(LinearLayout.VERTICAL);
    center.setGravity(Gravity.CENTER);
    center.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

    Space top=new Space(this);
    center.addView(top,new LinearLayout.LayoutParams(1,0,1));

    TextView logo=text(
        "SECRET KILLER",
        34,
        GOLD,
        true
    );
    logo.setGravity(Gravity.CENTER);
    center.addView(
        logo,
        new LinearLayout.LayoutParams(-1,-2)
    );

    TextView sub=text(
        "كل قصة تخفي سرًا",
        21,
        WHITE,
        false
    );
    sub.setGravity(Gravity.CENTER);
    sub.setTextDirection(View.TEXT_DIRECTION_RTL);

    LinearLayout.LayoutParams subParams=
        new LinearLayout.LayoutParams(-1,-2);
    subParams.setMargins(0,10,0,25);

    center.addView(sub,subParams);

    Button start=btn("ابدأ اللعبة",true);
    start.setOnClickListener(v->{
        reset();
        story();
    });
    center.addView(start);

    Button how=btn("طريقة اللعب",false);
    how.setOnClickListener(v->how());
    center.addView(how);

    Space bottom=new Space(this);
    center.addView(bottom,new LinearLayout.LayoutParams(1,0,1));

    root.addView(
        center,
        new LinearLayout.LayoutParams(-1,0,1)
    );
    }

    void showExitStoryDialog(){
        new AlertDialog.Builder(this)
            .setTitle("الخروج من القصة")
            .setMessage("هل أنت متأكد أنك تريد الخروج من القصة والعودة إلى الصفحة الرئيسية؟\n\nسيتم فقدان تقدم القضية الحالية.")
            .setNegativeButton("لا",null)
            .setPositiveButton("نعم، اخرج", (d,w)->home())
            .show();
    }

    void showExitAppDialog(){
        new AlertDialog.Builder(this)
            .setTitle("الخروج من Secret Killer")
            .setMessage("هل أنت متأكد أنك تريد الخروج من التطبيق؟")
            .setNegativeButton("لا",null)
            .setPositiveButton("نعم، اخرج",(d,w)->finish())
            .show();
    }

    void story(){
        atHome=false; base(); navigationBar(); title("اختيار القصة");
        panel("مدينة الأحلام\n\nجريمة داخل حلم مشترك. ثلاثة مذنبين، لكن كل مذنب لديه معرفة مختلفة.\n\n٨ لاعبين • ٣ مذنبين");
        Button b=btn("اختيار هذه القصة",true);
        b.setOnClickListener(v->setup()); root.addView(b);
    }

    void setup(){
        atHome=false; base(); navigationBar(); title("إعداد اللاعبين");
        panel("اكتبوا أسماء اللاعبين ثم ابدأوا القضية.");
        EditText[] e=new EditText[8];
        for(int i=0;i<8;i++){
            e[i]=new EditText(this); e[i].setText(names[i]); e[i].setTextColor(WHITE);
            e[i].setTextSize(16); e[i].setGravity(Gravity.RIGHT); e[i].setSingleLine(true);
            root.addView(e[i]);
        }
        Button b=btn("بدء القضية",true);
        b.setOnClickListener(v->{
            for(int i=0;i<8;i++){
                String x=e[i].getText().toString().trim();
                if(!x.isEmpty()) names[i]=x;
            }
            reset(); priv(0);
        });
        root.addView(b);
    }

    void priv(final int i){
        atHome=false; base(); navigationBar(); title("معلومات سرية");
        panel("مرر الهاتف إلى: "+names[i]+"\n\nلا تسمح لأي لاعب آخر برؤية الشاشة.");
        Button b=btn("اكشف دوري",true);
        b.setOnClickListener(v->{
            atHome=false; base(); navigationBar(); title("دورك السري");
            TextView r=text(roles[i],30,i>=5?RED:GREEN,true); root.addView(r);
            panel("سرك\n\n"+secrets[i]);
            panel("ما تعرفه\n\n"+know[i]);
            panel("جملتك\n\n«"+says[i]+"»");
            Button n=btn(i<7?"إخفاء وتمرير الهاتف":"بدء الجولة الأولى",true);
            n.setOnClickListener(x->{if(i<7)priv(i+1);else roundScreen();}); root.addView(n);
        });
        root.addView(b);
    }

    void roundScreen(){
        atHome=false; base(); navigationBar(); title("الجولة "+round);
        panel("🕵️ الأقوال\n\nكل لاعب يقرأ جملته بصوت عالٍ ثم ناقشوا التناقضات.\n\nالأبرياء لديهم أسرار أيضًا.");
        if(round==1){
            Button b=btn("كشف الأدلة",true); b.setOnClickListener(v->clues()); root.addView(b);
        }else{
            Button b=btn("ابدأ التصويت",true); b.setOnClickListener(v->vote()); root.addView(b);
        }
    }

    void clues(){
        atHome=false; base(); navigationBar(); title("الأدلة");
        panel("🧩 الساعة 21:43: تم فتح الممر السري بصلاحية خاصة.\n\n🧩 الساعة 21:47: تم نقل الـCore من الخزنة.\n\n🧩 بعد الحادث تم تعديل سجل الكاميرات.\n\nفتح الممر وحده لا يثبت أن مستخدمه هو السارق.");
        Button b=btn("المواجهة",true); b.setOnClickListener(v->interrogate()); root.addView(b);
    }

    void interrogate(){
        atHome=false; base(); navigationBar(); title("المواجهة");
        panel("اختروا لاعبًا لسؤاله.");
        for(int i=0;i<8;i++){
            if(eliminated[i]) continue;
            final int j=i; Button b=btn("اسأل "+names[i],false);
            b.setOnClickListener(v->question(j)); root.addView(b);
        }
        Button b=btn("تخطي إلى التصويت",true); b.setOnClickListener(v->vote()); root.addView(b);
    }

    void question(int i){
        atHome=false; base(); navigationBar(); title("السؤال");
        String q=i==5?"كيف تفسر وجودك قرب منطقة التحكم؟":i==6?"لماذا كنت في المنطقة الشرقية وقت نقل الـCore?":i==7?"لماذا تم تعديل سجل الأمن بعد الحادث؟":"ما السر الذي أخفيته؟";
        panel(names[i]+"\n\n«"+q+"»\n\nاللاعب يجيب بصوته.");
        Button b=btn("العودة للمواجهة",false); b.setOnClickListener(v->interrogate()); root.addView(b);
        Button c=btn("التصويت",true); c.setOnClickListener(v->vote()); root.addView(c);
    }

    void vote(){
        atHome=false; base(); navigationBar(); title("التصويت");
        panel("اختاروا لاعبًا واحدًا. اللاعبون المستبعدون لا يظهرون هنا.");
        int count=0;
        for(int i=0;i<8;i++){
            if(eliminated[i]) continue;
            count++; final int j=i;
            Button b=btn("○  "+names[i],false); b.setTextSize(18);
            b.setOnClickListener(v->resolve(j)); root.addView(b);
        }
        if(count==0) panel("لا يوجد لاعب متاح.");
    }

    void resolve(int i){
        if(eliminated[i]){vote();return;}
        if(i<5){eliminated[i]=true;wrongVotes++;wrongResult(i);}
        else guiltyResult(i);
    }

    String hint(int i){
        String[] h={
            "💡 كان يعرف شيئًا عن الأمن، لكنه لم يكن صاحب الصلاحية التي فتحت الممر.",
            "💡 حذف الذاكرة كان مشبوهًا، لكنه حدث قبل الجريمة. ركزوا على 21:43–21:47.",
            "💡 الدواء المختلف ليس سبب الوفاة. ابحثوا عن من تحكم في الممر.",
            "💡 وجود هدف سري لا يعني القتل. ابحثوا عن من استفاد من تعديل الكاميرات.",
            "💡 الجهاز يعطل الإشارة، لكنه لا يفتح الممر السري."
        };
        return h[i];
    }

    void wrongResult(int i){
        atHome=false; base(); navigationBar(); title("نتيجة التصويت");
        panel("❌ "+names[i]+" بريء وتم استبعاده.\n\n"+hint(i));
        Button b=btn("متابعة التحقيق",true);
        b.setOnClickListener(v->{round=2;roundScreen();}); root.addView(b);
    }

    void guiltyResult(int i){
        atHome=false; base(); navigationBar(); title("نتيجة التصويت");
        panel("⚠️ تم العثور على مذنب: "+names[i]+"\n\nلكن القضية لم تنتهِ. ما زال هناك مذنبون آخرون.");
        Button b=btn("متابعة التحقيق",true);
        b.setOnClickListener(v->{round=2;roundScreen();}); root.addView(b);
    }

    void finalVote(){
        atHome=false; base(); navigationBar(); title("الاتهام النهائي");
        panel("من تعتقدون أنه القاتل الحقيقي؟");
        for(int i=0;i<8;i++){
            if(eliminated[i]) continue;
            final int j=i; Button b=btn("○  "+names[i],false);
            b.setOnClickListener(v->reveal(j)); root.addView(b);
        }
    }

    void reveal(int i){
        atHome=false; base(); navigationBar(); title("النهاية");
        panel(i==5?"🏆 أمسكتم بالقاتل الحقيقي!":"🕯️ اتهام خاطئ. القاتل الحقيقي كان مختبئًا.");
        panel("الحقيقة\n\nالقاتل: "+names[5]+"\nالسارق: "+names[6]+"\nالمتواطئ: "+names[7]);
        Button b=btn("إعادة القضية",true); b.setOnClickListener(v->{reset();story();}); root.addView(b);
    }

    void how(){
        atHome=true; base(); title("طريقة اللعب");
        panel("١. اختاروا قصة\n٢. مرروا الهاتف لكل لاعب ليكشف دوره\n٣. اقرأوا الأقوال وناقشوا الأدلة\n٤. صوّتوا واستبعدوا الأبرياء بحذر");
        Button b=btn("العودة",true); b.setOnClickListener(v->home()); root.addView(b);
    }
}
