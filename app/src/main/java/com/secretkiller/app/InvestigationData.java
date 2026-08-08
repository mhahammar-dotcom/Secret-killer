package com.secretkiller.app;

/** Optional story-layer data. It is deliberately independent of UI and persistence. */
public final class InvestigationData {
    public final String[] publicEvidence, privateInformation, killerObjectives;
    public final Action[] actions;
    public final ConditionalClue[] conditionalClues;
    public InvestigationData(String[] evidence,String[] privateInfo,Action[] actions,ConditionalClue[] conditional,String[] objectives){publicEvidence=evidence;privateInformation=privateInfo;this.actions=actions;conditionalClues=conditional;killerObjectives=objectives;}
    public static InvestigationData standard(){return new InvestigationData(new String[]{"افحصوا التفاصيل العامة قبل التصويت."},new String[]{"لاحظ التناقض بين الأقوال والدليل."},new Action[]{new Action("records","فحص السجل","يكشف السجل أن التوقيت وحده لا يثبت هوية الفاعل."),new Action("scene","تفتيش الموقع","تظهر آثار جديدة، لكنها قد تكون مضللة.")},new ConditionalClue[]{new ConditionalClue("records","دليل شرطي: يوجد تعديل في سجل الحادث بعد وقوعه.")},new String[]{"أخفِ الرابط بين الأدلة.","وجّه الشك نحو تفسير بريء.","حافظ على تناقض قصتك بعيدًا عن النقاش."});}
    public static final class Action { public final String id,label,result; public Action(String id,String label,String result){this.id=id;this.label=label;this.result=result;} }
    public static final class ConditionalClue { public final String actionId,text; public ConditionalClue(String actionId,String text){this.actionId=actionId;this.text=text;} }
}
