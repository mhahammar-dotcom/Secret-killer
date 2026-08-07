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
    GameManager game;
    boolean atHome=true;
    int voteTurn=0;
    LinkedHashMap<Integer,Integer> votes=new LinkedHashMap<>();

    @Override public void onCreate(Bundle state){ super.onCreate(state); home(); }

    @Override public void onBackPressed(){
        if(atHome) showExitAppDialog(); else showExitStoryDialog();
    }

    TextView text(String s,int size,int color,boolean bold){
        TextView v=new TextView(this); v.setText(s); v.setTextSize(size); v.setTextColor(color);
        v.setGravity(Gravity.CENTER); v.setTextDirection(View.TEXT_DIRECTION_RTL); v.setPadding(18,14,18,14);
        if(bold) v.setTypeface(Typeface.DEFAULT,Typeface.BOLD); return v;
    }

    Button btn(String s, boolean primary){
        Button b=new Button(this); b.setText(s); b.setTextSize(18); b.setAllCaps(false);
        b.setTextColor(primary?Color.BLACK:GOLD); b.setGravity(Gravity.CENTER); b.setTextDirection(View.TEXT_DIRECTION_RTL);
        b.setBackgroundColor(primary?GOLD:Color.rgb(40,34,24));
        LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,70); p.setMargins(0,7,0,7); b.setLayoutParams(p); return b;
    }

    void base(){
        ScrollView sv=new ScrollView(this); sv.setFillViewport(true);
        root=new LinearLayout(this); root.setOrientation(LinearLayout.VERTICAL); root.setPadding(22,22,22,22);
        root.setBackgroundColor(BG); root.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        sv.addView(root,new ScrollView.LayoutParams(-1,-1)); setContentView(sv);
    }

    void navigationBar(){
        Button homeBtn=btn("⌂  الرئيسية",false); homeBtn.setOnClickListener(v->showExitStoryDialog());
        root.addView(homeBtn,0,new LinearLayout.LayoutParams(-1,58));
    }

    void title(String s){ root.addView(text(s,25,WHITE,true)); }

    void panel(String s){
        TextView v=text(s,17,WHITE,false); v.setBackgroundColor(CARD);
        LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,-2); p.setMargins(0,8,0,8); root.addView(v,p);
    }

    void home(){
        atHome=true; base();
        LinearLayout center=new LinearLayout(this); center.setOrientation(LinearLayout.VERTICAL); center.setGravity(Gravity.CENTER);
        center.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        Space top=new Space(this); center.addView(top,new LinearLayout.LayoutParams(1,0,1));
        TextView logo=text("SECRET KILLER",34,GOLD,true); logo.setGravity(Gravity.CENTER); center.addView(logo,new LinearLayout.LayoutParams(-1,-2));
        TextView sub=text("كل قصة تخفي سرًا",21,WHITE,false); sub.setGravity(Gravity.CENTER); sub.setTextDirection(View.TEXT_DIRECTION_RTL);
        LinearLayout.LayoutParams sp=new LinearLayout.LayoutParams(-1,-2); sp.setMargins(0,10,0,25); center.addView(sub,sp);
        Button start=btn("ابدأ اللعبة",true); start.setOnClickListener(v->storySelection()); center.addView(start);
        Button how=btn("طريقة اللعب",false); how.setOnClickListener(v->how()); center.addView(how);
        Space bottom=new Space(this); center.addView(bottom,new LinearLayout.LayoutParams(1,0,1));
        root.addView(center,new LinearLayout.LayoutParams(-1,0,1));
    }

    void showExitStoryDialog(){
        new AlertDialog.Builder(this).setTitle("الخروج من القصة")
            .setMessage("هل أنت متأكد أنك تريد الخروج من القصة والعودة إلى الصفحة الرئيسية؟\n\nسيتم فقدان تقدم القضية الحالية.")
            .setNegativeButton("لا",null).setPositiveButton("نعم، اخرج",(d,w)->home()).show();
    }

    void showExitAppDialog(){
        new AlertDialog.Builder(this).setTitle("الخروج من Secret Killer").setMessage("هل أنت متأكد أنك تريد الخروج من التطبيق؟")
            .setNegativeButton("لا",null).setPositiveButton("نعم، اخرج",(d,w)->finish()).show();
    }

    void storySelection(){
        atHome=false; base(); navigationBar(); title("اختيار القصة");
        for(Story s: Story.catalog()){
            final Story chosen=s; panel("🎭 "+s.title+"\n\n"+s.description+"\n\nعدد اللاعبين: "+s.minPlayers+"–"+s.maxPlayers);
            Button b=btn("اختيار القصة",true); b.setOnClickListener(v->playerSetup(chosen)); root.addView(b);
        }
    }

    void playerSetup(Story story){
        atHome=false; base(); navigationBar(); title("إعداد اللاعبين");
        panel("القصة: "+story.title+"\n\nاختروا عدد اللاعبين ثم اكتبوا الأسماء.");
        Spinner countSpinner=new Spinner(this);
        String[] counts=new String[story.maxPlayers-story.minPlayers+1];
        for(int i=0;i<counts.length;i++) counts[i]=String.valueOf(story.minPlayers+i);
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,counts);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); countSpinner.setAdapter(adapter);
        root.addView(countSpinner,new LinearLayout.LayoutParams(-1,65));
        LinearLayout namesBox=new LinearLayout(this); namesBox.setOrientation(LinearLayout.VERTICAL); root.addView(namesBox);
        ArrayList<EditText> fields=new ArrayList<>();
        Runnable render=()->{
            namesBox.removeAllViews(); fields.clear(); int n=Integer.parseInt((String)countSpinner.getSelectedItem());
            for(int i=0;i<n;i++){ EditText e=new EditText(this); e.setHint("اسم اللاعب "+(i+1)); e.setTextColor(WHITE); e.setHintTextColor(MUTED); e.setTextSize(17); e.setGravity(Gravity.RIGHT); e.setSingleLine(true); namesBox.addView(e); fields.add(e); }
        };
        countSpinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener(){ public void onItemSelected(android.widget.AdapterView<?> p,View v,int pos,long id){render.run();} public void onNothingSelected(android.widget.AdapterView<?> p){} });
        render.run();
        Button start=btn("توزيع الأدوار السرية",true); start.setOnClickListener(v->{
            ArrayList<String> playerNames=new ArrayList<>(); for(int i=0;i<fields.size();i++){ String x=fields.get(i).getText().toString().trim(); playerNames.add(x.isEmpty()?"اللاعب "+(i+1):x); }
            game=new GameManager(story,playerNames); rolePass(0);
        }); root.addView(start);
    }

    void rolePass(final int index){
        atHome=false; base(); navigationBar(); title("توزيع سري");
        Player p=game.players.get(index); panel("مرر الهاتف إلى: "+p.name+"\n\nلا تسمح لأي لاعب آخر برؤية الشاشة.");
        Button b=btn("اكشف دوري",true); b.setOnClickListener(v->roleReveal(index)); root.addView(b);
    }

    void roleReveal(final int index){
        atHome=false; base(); navigationBar(); title("الدور السري");
        Player p=game.players.get(index); int c=p.guilty?RED:GREEN;
        root.addView(text(p.role,29,c,true));
        panel("سرك\n\n"+p.secret);
        panel("ما تعرفه\n\n"+p.knowledge);
        panel("جملتك\n\n«"+p.statement+"»");
        Button next=btn(index<game.players.size()-1?"إخفاء وتمرير الهاتف":"ابدأ القضية",true);
        next.setOnClickListener(v->{ if(index<game.players.size()-1) rolePass(index+1); else startRound(); }); root.addView(next);
    }

    void startRound(){
        game.nextRound();
        atHome=false; base(); navigationBar(); title("الجولة "+game.round);
        Clue clue=game.story.clues[Math.min(game.round-1,game.story.clues.length-1)];
        panel("🕵️ مرحلة النقاش\n\nاقرأوا جملكم، اسألوا بعضكم، ثم راجعوا الدليل التالي:\n\n"+clue.text);
        Button b=btn("ابدأ التصويت",true); b.setOnClickListener(v->beginVote()); root.addView(b);
    }

    void beginVote(){
        votes.clear(); voteTurn=0; voteNext();
    }

    void voteNext(){
        atHome=false; base(); navigationBar(); title("التصويت");
        if(voteTurn>=game.players.size()){ resolveVotes(); return; }
        while(voteTurn<game.players.size() && game.players.get(voteTurn).eliminated) voteTurn++;
        if(voteTurn>=game.players.size()){ resolveVotes(); return; }
        Player voter=game.players.get(voteTurn); panel("دور التصويت: "+voter.name+"\n\nاختر الشخص الذي تريد التصويت ضده.");
        for(Player target: game.activePlayers()){
            if(target==voter) continue;
            final int targetId=target.id; Button b=btn("○  "+target.name,false); b.setOnClickListener(v->{votes.put(voter.id,targetId); voteTurn++; voteNext();}); root.addView(b);
        }
    }

    void resolveVotes(){
        HashMap<Integer,Integer> tally=new HashMap<>();
        for(Integer target:votes.values()) tally.put(target,tally.containsKey(target)?tally.get(target)+1:1);
        int accused=-1,max=0; boolean tie=false;
        for(Map.Entry<Integer,Integer> e:tally.entrySet()){ if(e.getValue()>max){max=e.getValue();accused=e.getKey();tie=false;} else if(e.getValue()==max){tie=true;} }
        if(accused<0 || tie){ showTie(); return; }
        Player out=game.playerById(accused); game.eliminate(out.id);
        showVoteResult(out,max);
    }

    void showTie(){
        atHome=false; base(); navigationBar(); title("نتيجة التصويت");
        panel("⚖️ تعادل في الأصوات. لا يتم استبعاد أي لاعب هذه الجولة.\n\nالشك أصبح أكبر، لكن لا أحد خرج من القضية.");
        Button b=btn("كشف دليل إضافي",true); b.setOnClickListener(v->extraHint()); root.addView(b);
    }

    void showVoteResult(Player out,int count){
        atHome=false; base(); navigationBar(); title("نتيجة التصويت");
        if(out.guilty){ panel("⚠️ تم استبعاد "+out.name+" — كان من المذنبين.\n\nالأصوات: "+count); }
        else { panel("❌ تم استبعاد "+out.name+" — كان بريئًا.\n\nالأصوات: "+count+"\n\n💡 تلميح: "+game.story.wrongVoteHints[Math.min(game.wrongVotes,game.story.wrongVoteHints.length-1)]); }
        if(game.innocentsWin()){ Button end=btn("كشف النهاية",true); end.setOnClickListener(v->reveal()); root.addView(end); }
        else if(game.guiltyWin()){ Button end=btn("كشف النهاية",true); end.setOnClickListener(v->reveal()); root.addView(end); }
        else { Button b=btn("الجولة التالية",true); b.setOnClickListener(v->startRound()); root.addView(b); }
    }

    void extraHint(){
        atHome=false; base(); navigationBar(); title("دليل إضافي");
        int idx=Math.min(game.round,game.story.clues.length-1); panel("🧩 دليل إضافي\n\n"+game.story.clues[idx].text);
        Button b=btn("جولة جديدة",true); b.setOnClickListener(v->startRound()); root.addView(b);
    }

    void reveal(){
        atHome=false; base(); navigationBar(); title("الحقيقة");
        StringBuilder sb=new StringBuilder(); sb.append("🎭 ").append(game.story.title).append("\n\n");
        for(Player p:game.players) sb.append(p.name).append(" — ").append(p.role).append(p.guilty?" 🔴":" 🟢").append("\n");
        panel(sb.toString());
        String result=game.innocentsWin()?"🏆 الأبرياء يفوزون! تم كشف المذنبين قبل أن يسيطروا على القضية.":"☠️ المذنبون يفوزون! أصبح عددهم أو نفوذهم كافيًا للسيطرة على القضية.";
        panel(result);
        Button b=btn("العودة للقصص",true); b.setOnClickListener(v->storySelection()); root.addView(b);
    }

    void how(){
        atHome=true; base(); title("طريقة اللعب");
        panel("١. اختاروا قصة وعدد اللاعبين.\n\n٢. أدخلوا الأسماء.\n\n٣. مرروا الهاتف لكل لاعب ليعرف دوره وسره دون أن يراه الآخرون.\n\n٤. ناقشوا الأدلة والجمل.\n\n٥. يصوّت كل لاعب سرًا على شخص واحد. التطبيق يحسب النتيجة ويستبعد صاحب أعلى عدد من الأصوات.\n\n٦. إذا خرج بريء، يظهر تلميح جديد. تستمر الجولات حتى يفوز الأبرياء أو المذنبون.");
        Button b=btn("العودة",true); b.setOnClickListener(v->home()); root.addView(b);
    }
}
