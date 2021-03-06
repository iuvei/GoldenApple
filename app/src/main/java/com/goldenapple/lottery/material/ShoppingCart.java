package com.goldenapple.lottery.material;

import android.util.SparseArray;

import com.goldenapple.lottery.app.GoldenAppleApp;
import com.goldenapple.lottery.data.Balls;
import com.goldenapple.lottery.data.Extra;
import com.goldenapple.lottery.data.Lottery;
import com.goldenapple.lottery.data.Method;
import com.goldenapple.lottery.data.TraceIssue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ACE-PC on 2016/1/26.
 */
public class ShoppingCart {

    private static ShoppingCart instance = new ShoppingCart();

    private ShoppingCart() {
    }

    public static ShoppingCart getInstance() {
        return instance;
    }

    /**
     * 彩种ID
     */
    private Lottery lottery;
    /**
     * 奖期
     */
    private String issue;
    /**
     * 当前金额模式
     */
    private LucreMode lucreMode = LucreMode.fromCode(GoldenAppleApp.getUserCentre().getLucreMode());
    /**
     * 奖金模式
     */
    private int prizeMode = -1;
    /**
     * 计划注数
     */
    private long planNotes = 0;
    /**
     * 倍数
     */
    private int multiple = 1;
    /**
     * 追号奖期
     */
    private int traceNumber = 0;
    /**
     * 追中即停
     */
    private boolean stopOnWin = true;
    /**
     * 计划金额
     */
    private double planAmount = 0.00;
    /**
     * 奖金组
     */
    private int prizeGroup = GoldenAppleApp.getUserCentre().getPrizeGroup();
    /**
     * 投注组合字符串
     */
    private ArrayList<Balls> codeData = new ArrayList<>();
    private Map<String, Integer> ordersMap = new HashMap<>();
    /**
     * 投注号码
     */
    private List<Ticket> codesMap = new ArrayList<>();
    private ArrayList<TraceIssue> traceIssue = new ArrayList<>();
    /**
     * 追号玩法统计
     */
    private SparseArray<Integer> countMethod = new SparseArray<>();
    /**
     * 玩法记录
     */
    private SparseArray<Method> methodArray = new SparseArray<>();
    /**
     * 所追号码期数
     */
    private String[][] chaseMap = null;
    /**
     * 追号规则
     */
    private ChaseRuleData chaserule = new ChaseRuleData();

    public int getMultiple() {
        return multiple;
    }

    public int getTraceNumber() {
        return traceNumber;
    }

    public boolean isStopOnWin() {
        return stopOnWin;
    }

    public double getPlanAmount() {
        return planAmount;
    }

    public boolean isEmpty() {
        return codesMap.size() == 0;
    }

    public String getIssue() {
        return issue;
    }

    public void setIssue(String issue) {
        this.issue = issue;
        ordersPurify();
    }

    public SparseArray<Integer> getCountMethod() {
        return countMethod;
    }

    public void addTicket(Ticket ticket) {
        codesMap.add(ticket);
        codePurify();
        ruleCount();
    }

    public void addTraceIssue(ArrayList<TraceIssue> traceIssue) {
        if (this.traceIssue.size() > 0) {
            this.traceIssue.clear();
        }
        this.traceIssue.addAll(traceIssue);
        ordersPurify();
    }

    public int getPrizeMode() {
        return prizeMode;
    }

    public List<Ticket> getCodesMap() {
        return codesMap;
    }

    public long getPlanNotes() {
        return planNotes;
    }

    public LucreMode getLucreMode() {
        return lucreMode;
    }

    public ArrayList<Balls> getCodeData() {
        return codeData;
    }

    public Map<String, Integer> getOrdersMap() {
        return ordersMap;
    }

    public ChaseRuleData getChaseRule() {
        return chaserule;
    }

    public void setChaseRule(ChaseRuleData chaserule) {
        this.chaserule = chaserule;
    }

    public SparseArray<Method> getMethodArray() {
        return methodArray;
    }

    public int getPrizeGroup() {
        return prizeGroup;
    }

    public void setPrizeGroup(int prizeGroup) {
        this.prizeGroup = prizeGroup;
        codePurify();
    }

    public void setTraceOrdersMap(int traceNumber,Map<String, Integer> ordersMap) {
        this.traceNumber = traceNumber;
        this.ordersMap = ordersMap;
    }

    public void init(Lottery lottery) {
        if (this.lottery == null || this.lottery.getId() != lottery.getId()) {
            clear();
        }
        this.lottery = lottery;
        this.multiple = 1;
        this.traceNumber = 0;
        codePurify();
        ordersPurify();
        ruleCount();
    }

    private void codePurify() {
        codeData.clear();
        countMethod.clear();
        methodArray.clear();
        for (int i = 0, size = codesMap.size(); i < size; i++) {
            Ticket ticket = codesMap.get(i);
            Method method = ticket.getChooseMethod();
            int value = method.getId() + method.getPid();
            if (countMethod.size() > 0) {
                for (int c = 0; c < countMethod.size(); c++) {
                    Integer cm = countMethod.get(c);
                    if (value != cm) {
                        countMethod.put(i, value);
                        methodArray.put(i,method);
                    }
                }
            } else {
                countMethod.put(i, value);
                methodArray.put(i,method);
            }
            codeData.add(new Balls(i + 1, ticket.getChooseMethod().getId(), ticket.getCodes(), ticket.getChooseNotes(), lucreMode.getFactor(), multiple, prizeGroup, new Extra(ticket.getPostsiton(), ticket.getSeat())));
        }
    }

    private void ordersPurify() {
        /*if(traceNumber>0) {
            if(traceIssue.size()>=traceNumber) {
                for (int i = 0; i < traceNumber; i++) {
                    ordersMap.put(traceIssue.get(i).getNumber(), multiple);
                }
            }else{
                for (int i = 0; i < traceIssue.size(); i++) {
                    ordersMap.put(traceIssue.get(i).getNumber(), multiple);
                }
                this.traceNumber=traceIssue.size();
            }
        }else{
            ordersMap.put(issue, multiple);
        }*/

        /*if (traceNumber > 0) {
            if (traceIssue.size() >= traceNumber) {
                for (int i = 0; i < traceNumber; i++) {
                    ordersMap.put(traceIssue.get(i).getNumber(), 1);
                }
            } else {
                for (int i = 0; i < traceIssue.size(); i++) {
                    ordersMap.put(traceIssue.get(i).getNumber(), 1);
                }
                this.traceNumber = traceIssue.size();
            }
        }*/
        if(traceNumber == 0) {
            ordersMap.clear();
            ordersMap.put(issue, 1);
        }
    }

    public void setPlanBuyRule() {
        this.stopOnWin = GoldenAppleApp.getUserCentre().getStopOnWin();
        this.lucreMode = LucreMode.fromCode(GoldenAppleApp.getUserCentre().getLucreMode());
        this.prizeGroup = GoldenAppleApp.getUserCentre().getPrizeGroup();
        ruleCount();
        codePurify();
        ordersPurify();
    }

    public void setPlanBuyRule(int multiple, int tracenum) {
        this.multiple = multiple;
        this.traceNumber = tracenum;
        this.stopOnWin = GoldenAppleApp.getUserCentre().getStopOnWin();
        this.lucreMode = LucreMode.fromCode(GoldenAppleApp.getUserCentre().getLucreMode());
        this.prizeGroup = GoldenAppleApp.getUserCentre().getPrizeGroup();
        ruleCount();
        codePurify();
        ordersPurify();
    }

    public void setPlanBuyRule(int multiple, int tracenum, boolean stopOnWin) {
        this.multiple = multiple;
        this.traceNumber = tracenum;
        this.stopOnWin = stopOnWin;
        this.lucreMode = LucreMode.fromCode(GoldenAppleApp.getUserCentre().getLucreMode());
        this.prizeGroup = GoldenAppleApp.getUserCentre().getPrizeGroup();
        ruleCount();
        codePurify();
        ordersPurify();
    }

    public void ruleCount() {
        long notes = 0;
        for (Ticket ticket : codesMap) {
            notes += ticket.getChooseNotes();
        }

        double totalOrder = lucreMode.getFactor() * 2 * getMultiple() * notes;
        /*if (traceNumber > 0) {
            totalOrder *= traceNumber;
        }*/

        this.planNotes = notes;
        this.planAmount = totalOrder;
    }

    public void deleteCode(int position) {
        if (position >= 0 && position < codesMap.size()) {
            codesMap.remove(position);
            codePurify();
            ruleCount();
            ordersPurify();
        }
    }

    public void clear() {
        this.lottery = null;
        this.codeData.clear();
        this.planNotes = 0;
        this.multiple = 1;
        this.traceNumber = 0;
        this.stopOnWin = true;
        this.planAmount = 0.00;
        this.codesMap.clear();
        this.ordersMap.clear();
        this.countMethod.clear();
        this.methodArray.clear();
        this.traceIssue.clear();
        this.prizeGroup = 0;
    }
}
