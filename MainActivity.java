package com.secretkiller.app;

import android.app.*;
import android.os.*;
import android.graphics.*;
import android.view.*;
import android.widget.*;
import android.text.Layout;
import android.graphics.drawable.GradientDrawable;
import java.util.*;

public class MainActivity extends Activity {
    LinearLayout root;
    final int GOLD=Color.rgb(214,168,79), BG=Color.rgb(9,8,12), CARD=Color.rgb(23,21,29);
    final int WHITE=Color.rgb(247,241,229), MUTED=Color.rgb(190,185,195), RED=Color.rgb(212,91,99), GREEN=Color.rgb(104,181,139);
    final Locale AR=new Locale("ar");
    GameManager game;
    boolean atHome=true;
    int voteTurn=0;
    LinkedHashMap<Integer,Integer> votes=new LinkedHashMap<>();

    @Override public void onCreate(Bundle state){ super.onCreate(state); getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL); home(); }
    @Override public void onBackPressed(){ if(atHome) showExitAppDialog(); else showExitStoryDialog(); }

    String rtl(String s){ return s==null?"":"\u200F"+s+"\u200F"; }

    TextView text(String s,int size,int color,boolean bold){
        TextView v=new TextView(this); v.setText(rtl(s)); v.setTextSize(size); v.setTextColor(color);
        v.setTypeface(Typeface.create("sans-serif",bold?Typeface.BOLD:Typeface.NORMAL));
        v.setGravity(Gravity.CENTER); v.setTextDirection(View.TEXT_DIRECTION_RTL); v.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        v.setTextLocale(AR); v.setIncludeFontPadding(true); v.setPadding(18,16,18,16); v.setLineSpacing(4f,1.12f);
        if(Build.VERSION.SDK_INT>=23){ v.setBreakStrategy(Layout.BREAK_STRATEGY_HIGH_QUALITY); v.setHyphenationFrequency(Layout.HYPHENATION_FREQUENCY_NONE); }
        return v;
    }

    TextView english(String s,int size,int color,boolean bold){
        TextView v=new TextView(this); v.setText(s); v.setTextSize(size); v.setTextColor(color);
        v.setTypeface(Typeface.create("sans-serif",bold?Typeface.BOLD:Typeface.NORMAL)); v.setGravity(Gravity.CENTER);
        v.setTextDirection(View.TEXT_DIRECTION_LTR); v.setLayoutDirection(View.LAYOUT_DIRECTION_LTR); v.setPadding(18,12,18,12); return v;
    }

    Button btn(String s,boolean primary){
        Button b=new Button(this); b.setAllCaps(false); b.setTextSize(20); b.setTextColor(primary?Color.BLACK:GOLD);
        b.setTypeface(Typeface.create("sans-serif",Typeface.BOLD)); b.setGravity(Gravity.CENTER);
        b.setTextDirection(View.TEXT_DIRECTION_RTL); b.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        b.setTextLocale(AR); b.setText(rtl(s));
        b.setPadding(20,12,20,12); b.setMinHeight(82); b.setMinimumHeight(82);
        GradientDrawable bg=new GradientDrawable();
        bg.setColor(primary?GOLD:Color.rgb(40,34,24)); bg.setCornerRadius(12f); b.setBackground(bg);
        LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,82); p.setMargins(0,8,0,8); b.setLayoutParams(p);
        return b;
    }

    void base(){
        ScrollView sv=new ScrollView(this); sv.setFillViewport(true); sv.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        root=new LinearLayout(this); root.setOrientation(LinearLayout.VERTICAL); root.setGravity(Gravity.CENTER_HORIZONTAL);
        root.setPadding(16,18,16,18); root.setBackgroundColor(BG); root.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        sv.addView(root,new ScrollView.LayoutParams(-1,-1)); setContentView(sv);
    }

    void navigationBar(){
        Button b=btn("⌂  الرئيسية",false); b.setOnClickListener(v->showExitStoryDialog());
        LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,70); p.setMargins(0,0,0,10); root.addView(b,p);
    }

    void title(String s){
        TextView t=text(s,30,WHITE,true);
        LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,-2); p.setMargins(0,8,0,16); root.addView(t,p);
    }

    void panel(String s){
        TextView v=text(s,18,WHITE,false); v.setBackgroundColor(CARD); v.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,-2); p.setMargins(0,8,0,8); root.addView(v,p);
    }

    void home(){
        atHome=true; base();
        LinearLayout center=new LinearLayout(this); center.setOrientation(LinearLayout.VERTICAL);
        center.setGravity(Gravity.CENTER); center.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        center.addView(new Space(this),new LinearLayout.LayoutParams(1,0,1));
        center.addView(english("SECRET KILLER",34,GOLD,true),new LinearLayout.LayoutParams(-1,-2));
        TextView sub=text("كل قصة تخفي سرًا",22,WHITE,false);
        LinearLayout.LayoutParams sp=new LinearLayout.LayoutParams(-1,-2); sp.setMargins(0,8,0,28); center.addView(sub,sp);
        Button start=btn("ابدأ اللعبة",true); start.setOnClickListener(v->storySelection()); center.addView(start);
        Button how=btn("طريقة اللعب",false); how.setOnClickListener(v->how()); center.addView(how);
        center.addView(new Space(this),new LinearLayout.LayoutParams(1,0,1));
        root.addView(center,new LinearLayout.LayoutParams(-1,0,1));
    }

    void showExitStoryDialog(){
        AlertDialog d=new AlertDialog.Builder(this).setTitle(rtl("الخروج من القصة"))
            .setMessage(rtl("هل أنت متأكد أنك تريد الخروج من القصة والعودة إلى الصفحة الرئيسية؟\n\nسيتم فقدان تقدم القضية الحالية."))
            .setNegativeButton(rtl("لا"),null).setPositiveButton(rtl("نعم، اخرج"),(x,w)->home()).create();
        d.setOnShowListener(x->rtlDialog(d)); d.show();
    }

    void showExitAppDialog(){
        AlertDialog d=new AlertDialog.Builder(this).setTitle(rtl("الخروج من Secret Killer"))
            .setMessage(rtl("هل أنت متأكد أنك تريد الخروج من التطبيق؟"))
            .setNegativeButton(rtl("لا"),null).setPositiveButton(rtl("نعم، اخرج"),(x,w)->finish()).create();
        d.setOnShowListener(x->rtlDialog(d)); d.show();
    }

    void rtlDialog(AlertDialog d){
        Window w=d.getWindow(); if(w!=null) w.getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        TextView m=d.findViewById(android.R.id.message);
        if(m!=null){m.setTextDirection(View.TEXT_DIRECTION_RTL);m.setGravity(Gravity.CENTER);m.setTextLocale(AR);}
        int id=getResources().getIdentifier("alertTitle","id","android");
        TextView t=d.findViewById(id);
        if(t!=null){t.setTextDirection(View.TEXT_DIRECTION_RTL);t.setGravity(Gravity.CENTER);t.setTextLocale(AR);}
    }

    void storySelection(){
        atHome=false; base(); navigationBar(); title("اختيار القصة");
        for(Story s:Story.catalog()){
            final Story chosen=s;
            panel("🎭 "+s.title+"\n\n"+s.description+"\n\nعدد اللاعبين: "+s.minPlayers+"–"+s.maxPlayers);
            Button b=btn("اختيار القصة",true); b.setOnClickListener(v->playerSetup(chosen)); root.addView(b);
        }
    }

    void playerSetup(Story story){
        atHome=false; base(); navigationBar(); title("إعداد اللاعبين");
        panel("القصة: "+story.title+"\n\nاختروا عدد اللاعبين ثم اكتبوا الأسماء.");

        Spinner spinner=new Spinner(this);
        spinner.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        String[] counts=new String[story.maxPlayers-story.minPlayers+1];
        for(int i=0;i<counts.length;i++) counts[i]=String.valueOf(story.minPlayers+i);

        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,counts){
            @Override public View getView(int p,View c,ViewGroup parent){
                TextView v=(TextView)super.getView(p,c,parent);
                v.setText(rtl(counts[p])); v.setTextSize(20); v.setTextColor(WHITE);
                v.setTextDirection(View.TEXT_DIRECTION_RTL); v.setGravity(Gravity.CENTER); v.setPadding(18,8,18,8);
                return v;
            }
            @Override public View getDropDownView(int p,View c,ViewGroup parent){
                TextView v=(TextView)super.getDropDownView(p,c,parent);
                v.setText(rtl(counts[p])); v.setTextSize(20); v.setTextColor(Color.BLACK);
                v.setTextDirection(View.TEXT_DIRECTION_RTL); v.setGravity(Gravity.CENTER); v.setPadding(18,12,18,12);
                return v;
            }
        };
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter); root.addView(spinner,new LinearLayout.LayoutParams(-1,72));

        LinearLayout names=new LinearLayout(this);
        names.setOrientation(LinearLayout.VERTICAL); names.setLayoutDirection(View.LAYOUT_DIRECTION_RTL); root.addView(names);
        ArrayList<EditText> fields=new ArrayList<>();

        Runnable render=()->{
            names.removeAllViews(); fields.clear();
            int n=Integer.parseInt((String)spinner.getSelectedItem());
            for(int i=0;i<n;i++){
                EditText e=new EditText(this);
                e.setHint(rtl("اسم اللاعب "+(i+1))); e.setTextColor(WHITE); e.setHintTextColor(MUTED);
                e.setTextSize(19); e.setGravity(Gravity.RIGHT|Gravity.CENTER_VERTICAL);
                e.setTextDirection(View.TEXT_DIRECTION_RTL); e.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                e.setTextLocale(AR); e.setSingleLine(true); e.setPadding(18,8,18,8);
                e.setMinHeight(72); e.setMinimumHeight(72);
                LinearLayout.LayoutParams ep=new LinearLayout.LayoutParams(-1,72); ep.setMargins(0,3,0,3);
                names.addView(e,ep); fields.add(e);
            }
        };

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            public void onItemSelected(AdapterView<?> p,View v,int pos,long id){render.run();}
            public void onNothingSelected(AdapterView<?> p){}
        });
        render.run();

        Button start=btn("توزيع الأدوار السرية",true);
        start.setOnClickListener(v->{
            ArrayList<String> n=new ArrayList<>();
            for(int i=0;i<fields.size();i++){
                String x=fields.get(i).getText().toString().trim();
                n.add(x.isEmpty()?"اللاعب "+(i+1):x);
            }
            game=new GameManager(story,n); rolePass(0);
        });
        root.addView(start);
    }

    void rolePass(final int i){
        atHome=false;base();navigationBar();title("توزيع سري");Player p=game.players.get(i);
        panel("مرر الهاتف إلى: "+p.name+"\n\nلا تسمح لأي لاعب آخر برؤية الشاشة.");
        Button b=btn("اكشف دوري",true);b.setOnClickListener(v->roleReveal(i));root.addView(b);
    }

    void roleReveal(final int i){
        atHome=false;base();navigationBar();title("الدور السري");Player p=game.players.get(i);
        root.addView(text(p.role,29,p.guilty?RED:GREEN,true));
        panel("سرك\n\n"+p.secret);panel("ما تعرفه\n\n"+p.knowledge);panel("جملتك\n\n«"+p.statement+"»");
        Button n=btn(i<game.players.size()-1?"إخفاء وتمرير الهاتف":"ابدأ القضية",true);
        n.setOnClickListener(v->{if(i<game.players.size()-1)rolePass(i+1);else startRound();});root.addView(n);
    }

    void startRound(){
        game.nextRound();atHome=false;base();navigationBar();title("الجولة "+game.round);
        Clue c=game.story.clues[Math.min(game.round-1,game.story.clues.length-1)];
        panel("🕵️ مرحلة النقاش\n\nاقرأوا جملكم، اسألوا بعضكم، ثم راجعوا الدليل التالي:\n\n"+c.text);
        Button b=btn("ابدأ التصويت",true);b.setOnClickListener(v->beginVote());root.addView(b);
    }

    void beginVote(){votes.clear();voteTurn=0;voteNext();}

    void voteNext(){
        atHome=false;base();navigationBar();title("التصويت");
        if(voteTurn>=game.players.size()){resolveVotes();return;}
        while(voteTurn<game.players.size()&&game.players.get(voteTurn).eliminated)voteTurn++;
        if(voteTurn>=game.players.size()){resolveVotes();return;}
        Player voter=game.players.get(voteTurn);
        panel("دور التصويت: "+voter.name+"\n\nاختر الشخص الذي تريد التصويت ضده.");
        for(Player target:game.activePlayers()){
            if(target==voter)continue;
            final int id=target.id;
            Button b=btn("○  "+target.name,false);
            b.setOnClickListener(v->{votes.put(voter.id,id);voteTurn++;voteNext();});root.addView(b);
        }
    }

    void resolveVotes(){
        HashMap<Integer,Integer> tally=new HashMap<>();
        for(Integer t:votes.values())tally.put(t,tally.containsKey(t)?tally.get(t)+1:1);
        int accused=-1,max=0;boolean tie=false;
        for(Map.Entry<Integer,Integer>e:tally.entrySet()){
            if(e.getValue()>max){max=e.getValue();accused=e.getKey();tie=false;}
            else if(e.getValue()==max)tie=true;
        }
        if(accused<0||tie){showTie();return;}
        Player out=game.playerById(accused);game.eliminate(out.id);showVoteResult(out,max);
    }

    void showTie(){
        atHome=false;base();navigationBar();title("نتيجة التصويت");
        panel("⚖️ تعادل في الأصوات. لا يتم استبعاد أي لاعب هذه الجولة.\n\nالشك أصبح أكبر، لكن لا أحد خرج من القضية.");
        Button b=btn("كشف دليل إضافي",true);b.setOnClickListener(v->extraHint());root.addView(b);
    }

    void showVoteResult(Player out,int count){
        atHome=false;base();navigationBar();title("نتيجة التصويت");
        if(out.guilty)panel("⚠️ تم استبعاد "+out.name+" — كان من المذنبين.\n\nالأصوات: "+count);
        else panel("❌ تم استبعاد "+out.name+" — كان بريئًا.\n\nالأصوات: "+count+"\n\n💡 تلميح: "+game.story.wrongVoteHints[Math.min(game.wrongVotes,game.story.wrongVoteHints.length-1)]);
        if(game.innocentsWin()||game.guiltyWin()){
            Button e=btn("كشف النهاية",true);e.setOnClickListener(v->reveal());root.addView(e);
        }else{
            Button b=btn("الجولة التالية",true);b.setOnClickListener(v->startRound());root.addView(b);
        }
    }

    void extraHint(){
        atHome=false;base();navigationBar();title("دليل إضافي");
        int i=Math.min(game.round,game.story.clues.length-1);
        panel("🧩 دليل إضافي\n\n"+game.story.clues[i].text);
        Button b=btn("جولة جديدة",true);b.setOnClickListener(v->startRound());root.addView(b);
    }

    void reveal(){
        atHome=false;base();navigationBar();title("الحقيقة");
        StringBuilder s=new StringBuilder();s.append("🎭 ").append(game.story.title).append("\n\n");
        for(Player p:game.players)s.append(p.name).append(" — ").append(p.role).append(p.guilty?" 🔴":" 🟢").append("\n");
        panel(s.toString());
        panel(game.innocentsWin()?"🏆 الأبرياء يفوزون! تم كشف المذنبين قبل أن يسيطروا على القضية.":"☠️ المذنبون يفوزون! أصبح عددهم أو نفوذهم كافيًا للسيطرة على القضية.");
        Button b=btn("العودة للقصص",true);b.setOnClickListener(v->storySelection());root.addView(b);
    }

    void how(){
        atHome=true;base();title("طريقة اللعب");
        panel("١. اختاروا قصة وعدد اللاعبين.\n\n٢. أدخلوا الأسماء.\n\n٣. مرروا الهاتف لكل لاعب ليعرف دوره وسره دون أن يراه الآخرون.\n\n٤. ناقشوا الأدلة والجمل.\n\n٥. يصوّت كل لاعب سرًا على شخص واحد. التطبيق يحسب النتيجة ويستبعد صاحب أعلى عدد من الأصوات.\n\n٦. إذا خرج بريء، يظهر تلميح جديد. تستمر الجولات حتى يفوز الأبرياء أو المذنبون.");
        Button b=btn("العودة",true);b.setOnClickListener(v->home());root.addView(b);
    }
}
