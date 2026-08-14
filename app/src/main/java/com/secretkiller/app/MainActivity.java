package com.secretkiller.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.BidiFormatter;
import android.text.TextDirectionHeuristics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Space;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public final class MainActivity extends Activity {
    private final Locale arabic = new Locale("ar");
    private final BidiFormatter bidi = BidiFormatter.getInstance(arabic);
    private LinearLayout root;
    private GameManager game;
    private boolean atHome = true;
    private int voteTurn;
    private final LinkedHashMap<Integer, Integer> votes = new LinkedHashMap<>();
    private final ArrayList<String> draftNames = new ArrayList<>();
    private final ArrayList<StoryCharacter> customCards = new ArrayList<>();
    private final ArrayList<String> customClues = new ArrayList<>();
    private String customTitle, customDescription; private int customPlayers, requestedGuilty;
    private int detectiveActionRound=-1;

    @Override public void onCreate(Bundle state) { super.onCreate(state); home(); }
    @Override public void onBackPressed() { if (atHome) exitAppDialog(); else exitStoryDialog(); }

    private int color(int id) { return getColor(id); }
    private int dp(int value) { return Math.round(value * getResources().getDisplayMetrics().density); }
    private String f(int id, Object... values) { return String.format(arabic, getString(id), values); }
    private String name(String value) { return bidi.unicodeWrap(value, TextDirectionHeuristics.FIRSTSTRONG_RTL); }

    private void base() {
        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(Gravity.CENTER_HORIZONTAL);
        root.setPadding(dp(16), dp(18), dp(16), dp(18));
        root.setBackgroundColor(color(R.color.background));
        root.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        scroll.addView(root, new ScrollView.LayoutParams(-1, -1));
        setContentView(scroll);
    }

    private TextView text(String value, int size, int textColor, boolean bold) {
        TextView view = new TextView(this);
        view.setText(value);
        view.setTextSize(size);
        view.setTextColor(textColor);
        view.setTypeface(Typeface.create("sans-serif", bold ? Typeface.BOLD : Typeface.NORMAL));
        view.setGravity(Gravity.CENTER);
        view.setTextDirection(View.TEXT_DIRECTION_FIRST_STRONG_RTL);
        view.setTextLocale(arabic);
        view.setIncludeFontPadding(true);
        view.setPadding(dp(18), dp(12), dp(18), dp(12));
        view.setLineSpacing(dp(3), 1.1f);
        return view;
    }

    private Button button(String label, boolean primary) {
        Button view = new Button(this);
        view.setAllCaps(false);
        view.setText(label);
        view.setTextSize(18);
        view.setTextColor(primary ? Color.BLACK : color(R.color.gold));
        view.setTypeface(Typeface.create("sans-serif", Typeface.BOLD));
        view.setGravity(Gravity.CENTER);
        view.setTextDirection(View.TEXT_DIRECTION_FIRST_STRONG_RTL);
        view.setTextLocale(arabic);
        view.setPadding(dp(16), 0, dp(16), 0);
        view.setMinHeight(dp(56));
        GradientDrawable background = new GradientDrawable();
        background.setColor(primary ? color(R.color.gold) : Color.rgb(40, 34, 24));
        background.setCornerRadius(dp(12));
        view.setBackground(background);
        view.setLayoutParams(margins(-1, dp(56), 0, dp(8), 0, dp(8)));
        return view;
    }

    private LinearLayout.LayoutParams margins(int width, int height, int left, int top, int right, int bottom) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, height);
        params.setMargins(dp(left), dp(top), dp(right), dp(bottom));
        return params;
    }
    private void addTitle(String value) { root.addView(text(value, 29, color(R.color.text_primary), true), margins(-1, -2, 0, 6, 0, 12)); }
    private void addPanel(String value) {
        TextView panel = text(value, 17, color(R.color.text_primary), false);
        GradientDrawable background = new GradientDrawable(); background.setColor(color(R.color.surface)); background.setCornerRadius(dp(12)); panel.setBackground(background);
        root.addView(panel, margins(-1, -2, 0, 6, 0, 6));
    }
    private void navigation() { Button home = button(getString(R.string.home), false); home.setOnClickListener(v -> exitStoryDialog()); root.addView(home, margins(-1, dp(48), 0, 0, 0, 8)); }

    private void home() {
        atHome = true; base();
        LinearLayout center = new LinearLayout(this); center.setOrientation(LinearLayout.VERTICAL); center.setGravity(Gravity.CENTER);
        center.addView(new Space(this), new LinearLayout.LayoutParams(1, 0, 1));
        TextView title = text(getString(R.string.app_name), 32, color(R.color.gold), true); title.setTextDirection(View.TEXT_DIRECTION_LTR); center.addView(title, new LinearLayout.LayoutParams(-1, -2));
        center.addView(text(getString(R.string.tagline), 21, color(R.color.text_primary), false), margins(-1, -2, 0, 6, 0, 24));
        Button start = button(getString(R.string.start_game), true); start.setOnClickListener(v -> storySelection()); center.addView(start);
        Button how = button(getString(R.string.how_to_play), false); how.setOnClickListener(v -> how()); center.addView(how);
        center.addView(new Space(this), new LinearLayout.LayoutParams(1, 0, 1));
        root.addView(center, new LinearLayout.LayoutParams(-1, 0, 1));
    }

    private void storySelection() {
        atHome = false; base(); navigation(); addTitle(getString(R.string.choose_story));
        Button create=button("اصنع قصتك الخاصة",true); create.setOnClickListener(v->customInfo()); root.addView(create);
        ArrayList<Story> saved=CustomStoryStore.load(this); if(!saved.isEmpty()){addTitle("قصصي الخاصة");for(Story story:saved){addPanel("✦ "+story.title+"\n\n"+story.description);Button play=button("ابدأ هذه القصة",false);play.setOnClickListener(v->storyIntroduction(story));root.addView(play);}}
        for (Story story : Story.catalog()) {
            addPanel("🎭 " + story.title + "\n\n" + story.description + "\n\n" + f(R.string.players_range, story.minPlayers, story.maxPlayers));
            Button choose = button(getString(R.string.choose_this_story), true); choose.setOnClickListener(v -> storyIntroduction(story)); root.addView(choose);
        }
    }

    private void storyIntroduction(Story story){atHome=false;base();navigation();addTitle("مقدمة القصة");addPanel(story.introduction.display());Button next=button("إعداد اللاعبين",true);next.setOnClickListener(v->playerSetup(story));root.addView(next);}

    private EditText field(String hint, boolean multi) { EditText e=new EditText(this);e.setHint(hint);e.setTextColor(color(R.color.text_primary));e.setHintTextColor(color(R.color.text_muted));e.setTextSize(17);e.setTextDirection(View.TEXT_DIRECTION_FIRST_STRONG_RTL);e.setTextLocale(arabic);e.setGravity(Gravity.CENTER);e.setPadding(dp(16),dp(8),dp(16),dp(8));e.setSingleLine(!multi);if(multi)e.setMinLines(3);GradientDrawable bg=new GradientDrawable();bg.setColor(color(R.color.surface_input));bg.setCornerRadius(dp(12));bg.setStroke(dp(1),color(R.color.input_stroke));e.setBackground(bg);root.addView(e,margins(-1,multi?dp(100):dp(52),0,4,0,4));return e; }
    private boolean missing(EditText... fields){for(EditText e:fields)if(e.getText().toString().trim().isEmpty())return true;return false;}
    private void notice(String message){new AlertDialog.Builder(this).setMessage(message).setPositiveButton("حسنًا",null).show();}
    private void customInfo(){atHome=false;base();navigation();addTitle("١ من ٥ — معلومات القصة");EditText title=field("عنوان القصة",false),description=field("وصف القصة",true),players=field("عدد اللاعبين (4 إلى 12)",false),guilty=field("عدد الشخصيات المذنبة",false);Button next=button("التالي: الشخصيات",true);next.setOnClickListener(v->{if(missing(title,description,players,guilty)){notice("أكمل جميع الحقول المطلوبة.");return;}try{customPlayers=Integer.parseInt(players.getText().toString().trim());requestedGuilty=Integer.parseInt(guilty.getText().toString().trim());}catch(Exception e){notice("أدخل أرقامًا صحيحة.");return;}if(customPlayers<4||customPlayers>12||requestedGuilty<1||requestedGuilty>=customPlayers){notice("عدد اللاعبين من ٤ إلى ١٢، وعدد المذنبين أقل من عدد اللاعبين.");return;}customTitle=title.getText().toString().trim();customDescription=description.getText().toString().trim();customCards.clear();customClues.clear();customCharacter(0);});root.addView(next);}
    private void customCharacter(int index){
        atHome=false;base();navigation();addTitle("٢ من ٥ — الشخصيات ("+(index+1)+"/"+customPlayers+")");
        EditText name=field("اسم الشخصية",false),
                profession=field("المهنة أو الوظيفة",false),
                publicIdentity=field("الهوية العلنية (ما يعرفه الجميع عنه)",true),
                secret=field("السر",true),
                knowledge=field("المعرفة الخاصة",true),
                statement=field("الجملة التي يقولها للمجموعة",true),
                objective=field("الهدف الشخصي",true),
                crimeObjective=field("هدف الجريمة (للشخصية المذنبة فقط)",true);
        final boolean[] guilty={index<requestedGuilty};
        Button toggle=button(guilty[0]?"الشخصية مذنبة":"الشخصية بريئة",false);
        toggle.setOnClickListener(v->{guilty[0]=!guilty[0];toggle.setText(guilty[0]?"الشخصية مذنبة":"الشخصية بريئة");});
        root.addView(toggle);
        if(index>0){Button remove=button("حذف الشخصية السابقة",false);remove.setOnClickListener(v->{customCards.remove(customCards.size()-1);customCharacter(index-1);});root.addView(remove);}
        Button next=button(index==customPlayers-1?"التالي: الأدلة":"حفظ الشخصية التالية",true);
        next.setOnClickListener(v->{
            if(missing(name,profession,publicIdentity,secret,knowledge,statement,objective)){notice("أكمل جميع معلومات الشخصية.");return;}
            if(guilty[0]&&crimeObjective.getText().toString().trim().isEmpty()){notice("أدخل هدف الجريمة لهذه الشخصية المذنبة.");return;}
            customCards.add(new StoryCharacter(name.getText().toString().trim(),profession.getText().toString().trim(),publicIdentity.getText().toString().trim(),secret.getText().toString().trim(),knowledge.getText().toString().trim(),statement.getText().toString().trim(),objective.getText().toString().trim(),guilty[0],guilty[0]?crimeObjective.getText().toString().trim():""));
            if(index+1<customPlayers)customCharacter(index+1);
            else {
                int count=0;for(StoryCharacter c:customCards)if(c.guilty)count++;
                if(count!=requestedGuilty){notice("يجب أن يطابق عدد الشخصيات المذنبة العدد الذي اخترته.");customCards.remove(customCards.size()-1);customCharacter(index);return;}
                customClues();
            }
        });
        root.addView(next);
    }
    private void customClues(){atHome=false;base();navigation();addTitle("٣ من ٥ — الأدلة");addPanel("أضف ثلاثة أدلة على الأقل. يمكنك إضافة المزيد.");LinearLayout list=new LinearLayout(this);list.setOrientation(LinearLayout.VERTICAL);root.addView(list);EditText clue=field("نص الدليل",true);Button add=button("إضافة دليل",false);add.setOnClickListener(v->{String value=clue.getText().toString().trim();if(value.isEmpty()){notice("اكتب الدليل أولًا.");return;}customClues.add(value);TextView item=text("• "+value,16,color(R.color.text_primary),false);list.addView(item);clue.setText("");});root.addView(add);Button next=button("التالي: النهاية",true);next.setOnClickListener(v->{if(customClues.size()<3){notice("أضف ثلاثة أدلة على الأقل.");return;}customEnding();});root.addView(next);}
    private void customEnding(){atHome=false;base();navigation();addTitle("٤ من ٥ — النهاية");EditText what=field("ماذا حدث حقًا؟",true),how=field("كيف حدثت الجريمة؟",true),why=field("لماذا حدثت؟",true);Button save=button("٥ من ٥ — حفظ القصة",true);save.setOnClickListener(v->{if(missing(what,how,why)){notice("أكمل معلومات النهاية.");return;}String ending="ماذا حدث حقًا؟\n"+what.getText().toString().trim()+"\n\nكيف حدثت الجريمة؟\n"+how.getText().toString().trim()+"\n\nلماذا حدثت؟\n"+why.getText().toString().trim();StoryCharacter[] cards=customCards.toArray(new StoryCharacter[0]);Clue[] clues=new Clue[customClues.size()];for(int i=0;i<clues.length;i++)clues[i]=new Clue(customClues.get(i));CustomStoryStore.save(this,Story.custom("custom_"+UUID.randomUUID(),customTitle,customDescription,cards,clues,ending));customSaved();});root.addView(save);}
    private void customSaved(){atHome=false;base();addTitle("تم حفظ قصتك");addPanel("ستجدها الآن في قسم «قصصي الخاصة».");Button back=button("العودة إلى القصص",true);back.setOnClickListener(v->storySelection());root.addView(back);}

    private void playerSetup(Story story) {
        atHome = false; base(); navigation(); addTitle(getString(R.string.player_setup));
        addPanel(f(R.string.story_format, name(story.title)) + "\n\n" + getString(R.string.player_names));
        final int[] selected = {story.minPlayers};
        draftNames.clear(); for (int i = 0; i < selected[0]; i++) draftNames.add("");
        Button count = button(f(R.string.choose_count, selected[0]), false); root.addView(count);
        TextView label = text(getString(R.string.player_names), 19, color(R.color.text_primary), true); root.addView(label, margins(-1, -2, 0, 0, 0, 2));
        LinearLayout fields = new LinearLayout(this); fields.setOrientation(LinearLayout.VERTICAL); root.addView(fields, margins(-1, -2, 0, 0, 0, 10));
        ArrayList<EditText> inputs = new ArrayList<>();
        Runnable render = () -> renderPlayerFields(fields, inputs, selected[0]);
        render.run();
        count.setOnClickListener(v -> {
            String[] choices = new String[story.maxPlayers - story.minPlayers + 1];
            for (int i = 0; i < choices.length; i++) choices[i] = String.valueOf(story.minPlayers + i);
            AlertDialog dialog = new AlertDialog.Builder(this).setTitle(getString(R.string.players_count)).setItems(choices, (d, which) -> {
                saveDraft(inputs); selected[0] = story.minPlayers + which; while (draftNames.size() < selected[0]) draftNames.add("");
                count.setText(f(R.string.choose_count, selected[0])); render.run();
            }).create();
            dialog.setOnShowListener(ignored -> configureDialog(dialog)); dialog.show();
        });
        Button start = button(getString(R.string.distribute_roles), true);
        start.setOnClickListener(v -> { saveDraft(inputs); ArrayList<String> names = new ArrayList<>(); for (int i = 0; i < selected[0]; i++) { String value = draftNames.get(i).trim(); names.add(value.isEmpty() ? f(R.string.player_hint, i + 1) : value); } game = new GameManager(story, names); rolePass(0); });
        root.addView(start);
    }

    private void saveDraft(ArrayList<EditText> inputs) { for (int i = 0; i < inputs.size(); i++) { if (i < draftNames.size()) draftNames.set(i, inputs.get(i).getText().toString()); } }
    private void renderPlayerFields(LinearLayout holder, ArrayList<EditText> inputs, int count) {
        holder.removeAllViews(); inputs.clear();
        for (int i = 0; i < count; i++) {
            EditText input = new EditText(this); input.setHint(f(R.string.player_hint, i + 1)); input.setText(i < draftNames.size() ? draftNames.get(i) : "");
            input.setTextColor(color(R.color.text_primary)); input.setHintTextColor(color(R.color.text_muted)); input.setTextSize(18); input.setGravity(Gravity.CENTER); input.setSingleLine(true);
            input.setTextDirection(View.TEXT_DIRECTION_FIRST_STRONG_RTL); input.setTextLocale(arabic); input.setPadding(dp(16), 0, dp(16), 0);
            GradientDrawable bg = new GradientDrawable(); bg.setColor(color(R.color.surface_input)); bg.setCornerRadius(dp(12)); bg.setStroke(dp(1), color(R.color.input_stroke)); input.setBackground(bg);
            holder.addView(input, margins(-1, dp(52), 0, 4, 0, 4)); inputs.add(input);
        }
    }

    private void rolePass(int index) { atHome = false; base(); navigation(); addTitle(getString(R.string.secret_distribution)); Player player = game.players.get(index); addPanel(f(R.string.pass_phone, name(player.name))); Button reveal = button(getString(R.string.reveal_my_role), true); reveal.setOnClickListener(v -> roleReveal(index)); root.addView(reveal); }
    private void roleReveal(int index) {
        atHome = false; base(); navigation(); addTitle(getString(R.string.secret_role)); Player player = game.players.get(index); StoryCharacter c = player.character;
        root.addView(text(name(c.name) + " — " + c.profession, 24, player.guilty ? color(R.color.red) : color(R.color.green), true));
        addPanel("هويتك العلنية\n\n" + c.publicIdentity);
        addPanel(f(R.string.your_secret, c.secret));
        addPanel(f(R.string.your_knowledge, c.knowledge));
        addPanel(f(R.string.your_statement, c.statement));
        addPanel("هدفك الشخصي\n\n" + c.objective);
        if (player.detective) addPanel("دور إضافي: أنت المحقق. لديك إجراء تحقيق واحد في كل جولة.");
        if (player.guilty) addPanel("هدف الجريمة\n\n" + c.crimeObjective);
        Button next = button(index < game.players.size() - 1 ? getString(R.string.hide_and_pass) : getString(R.string.start_case), true); next.setOnClickListener(v -> { if (index < game.players.size() - 1) rolePass(index + 1); else startRound(); }); root.addView(next);
    }
    private void startRound() { Clue clue = game.startNextRound(); atHome = false; base(); navigation(); addTitle(f(R.string.round, game.round)); addPanel(f(R.string.discussion, clue.text)); Button vote = button(getString(R.string.start_voting), true); vote.setOnClickListener(v -> beginVote()); root.addView(vote); }
    private void investigation(){Player detective=game.detective();if(detective==null){notice("لا يوجد محقق نشط في هذه الجولة.");return;}if(detectiveActionRound==game.round){notice("استخدم المحقق إجراءه لهذه الجولة بالفعل.");return;}atHome=false;base();navigation();addTitle("مرحلة التحقيق");addPanel("مرر الهاتف إلى: "+name(detective.name)+"\n\nاختر إجراء تحقيق واحدًا.");for(InvestigationData.Action action:game.story.investigation.actions){Button b=button(action.label,false);b.setOnClickListener(v->investigationResult(action));root.addView(b);}}
    private void investigationResult(InvestigationData.Action action){detectiveActionRound=game.round;game.investigationActionsUsed.add(action.id);atHome=false;base();navigation();addTitle("نتيجة تحقيق خاصة");addPanel(action.result);for(String info:game.story.investigation.privateInformation)addPanel("معلومة خاصة\n\n"+info);String conditional=game.conditionalClueFor(action.id);if(!conditional.isEmpty())addPanel(conditional);Button back=button("العودة إلى الجولة",true);back.setOnClickListener(v->showCurrentRound());root.addView(back);}
    private void showCurrentRound(){atHome=false;base();navigation();addTitle(f(R.string.round,game.round));addPanel("عاد المحقق إلى النقاش. يمكنكم الآن التصويت.");Button vote=button(getString(R.string.start_voting),true);vote.setOnClickListener(v->beginVote());root.addView(vote);}
    private void beginVote() { votes.clear(); voteTurn = 0; voteNext(); }
    private void voteNext() {
        atHome = false; base(); navigation(); addTitle(getString(R.string.voting));
        while (voteTurn < game.players.size() && game.players.get(voteTurn).eliminated) voteTurn++;
        if (voteTurn >= game.players.size()) { resolveVotes(); return; }
        Player voter = game.players.get(voteTurn); addPanel(f(R.string.voter_prompt, name(voter.name)));
        for (Player target : game.activePlayers()) if (target != voter) { Button vote = button(f(R.string.vote_target, name(target.name)), false); final int id = target.id; vote.setOnClickListener(v -> { votes.put(voter.id, id); voteTurn++; voteNext(); }); root.addView(vote); }
    }
    private void resolveVotes() {
        HashMap<Integer, Integer> tally = new HashMap<>(); for (int target : votes.values()) tally.put(target, tally.containsKey(target) ? tally.get(target) + 1 : 1);
        int accused = -1, maximum = 0; boolean tie = false;
        for (Map.Entry<Integer, Integer> entry : tally.entrySet()) { if (entry.getValue() > maximum) { maximum = entry.getValue(); accused = entry.getKey(); tie = false; } else if (entry.getValue() == maximum) tie = true; }
        if (accused < 0 || tie) { showTie(); return; } Player out = game.playerById(accused); game.eliminate(out.id); showVoteResult(out, maximum);
    }
    private void showTie() { atHome = false; base(); navigation(); addTitle(getString(R.string.vote_result)); addPanel(getString(R.string.tie)); Button hint = button(getString(R.string.extra_hint), true); hint.setOnClickListener(v -> extraHint()); root.addView(hint); }
    private void extraHint() { atHome = false; base(); navigation(); addTitle(getString(R.string.extra_hint_title)); addPanel(f(R.string.extra_hint_format, game.revealExtraClue().text)); Button round = button(getString(R.string.new_round), true); round.setOnClickListener(v -> startRound()); root.addView(round); }
    private void showVoteResult(Player out, int count) { atHome = false; base(); navigation(); addTitle(getString(R.string.vote_result)); addPanel(out.guilty ? f(R.string.guilty_out, name(out.name), count) : f(R.string.innocent_out, name(out.name), count, game.story.wrongVoteHints[Math.min(game.wrongVotes - 1, game.story.wrongVoteHints.length - 1)])); Button next = button(game.innocentsWin() || game.guiltyWin() ? getString(R.string.reveal_ending) : getString(R.string.next_round), true); next.setOnClickListener(v -> { if (game.innocentsWin() || game.guiltyWin()) reveal(); else startRound(); }); root.addView(next); }
    private void reveal() { atHome = false; base(); navigation(); addTitle(getString(R.string.truth)); StringBuilder roles = new StringBuilder("🎭 ").append(game.story.title).append("\n\n"); for (Player p : game.players) roles.append(name(p.name)).append(" — ").append(name(p.character.name)).append(" (").append(p.character.profession).append(")").append(p.guilty ? " 🔴" : " 🟢").append("\n"); addPanel(roles.toString()); if(game.story.isCustom()) addPanel(game.story.customEnding); else addPanel(getString(game.innocentsWin() ? R.string.innocents_win : R.string.guilty_win)); Button stories = button(getString(R.string.back_to_stories), true); stories.setOnClickListener(v -> storySelection()); root.addView(stories); }
    private void how() { atHome = true; base(); addTitle(getString(R.string.how_to_play)); addPanel(getString(R.string.instructions)); Button back = button(getString(R.string.back), true); back.setOnClickListener(v -> home()); root.addView(back); }
    private void exitStoryDialog() { AlertDialog dialog = new AlertDialog.Builder(this).setTitle(getString(R.string.exit_story_title)).setMessage(getString(R.string.exit_story_message)).setNegativeButton(getString(R.string.no), null).setPositiveButton(getString(R.string.yes_exit), (d, w) -> home()).create(); dialog.setOnShowListener(ignored -> configureDialog(dialog)); dialog.show(); }
    private void exitAppDialog() { AlertDialog dialog = new AlertDialog.Builder(this).setTitle(getString(R.string.exit_app_title)).setMessage(getString(R.string.exit_app_message)).setNegativeButton(getString(R.string.no), null).setPositiveButton(getString(R.string.yes_exit), (d, w) -> finish()).create(); dialog.setOnShowListener(ignored -> configureDialog(dialog)); dialog.show(); }
    private void configureDialog(AlertDialog dialog) { Window window = dialog.getWindow(); if (window != null) window.getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL); TextView message = dialog.findViewById(android.R.id.message); if (message != null) { message.setTextDirection(View.TEXT_DIRECTION_FIRST_STRONG_RTL); message.setTextLocale(arabic); } }
}
