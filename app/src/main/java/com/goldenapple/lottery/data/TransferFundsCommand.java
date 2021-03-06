package com.goldenapple.lottery.data;

import com.android.volley.Request;
import com.goldenapple.lottery.base.net.RequestConfig;
import com.google.gson.annotations.SerializedName;

@RequestConfig(api = "service?packet=Fund&action=PlatTransfer", method = Request.Method.POST)
public class TransferFundsCommand extends CommonAttribute{
    @SerializedName("from_plat")
    private int fromPlat;
    @SerializedName("to_plat")
    private int toPlat;
    private String amount;

    public void setFromPlat(int fromPlat) {
        this.fromPlat = fromPlat;
    }

    public void setToPlat(int toPlat) {
        this.toPlat = toPlat;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }
}
