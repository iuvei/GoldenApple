package com.goldenapple.lottery.data;

import com.goldenapple.lottery.base.net.RequestConfig;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * 5.18.获用户列表接口（代理）
 */
@RequestConfig(api = "service?packet=User&action=GetUserList")
public class UserListCommand extends CommonAttribute {
    private int page;

//    private String username;
//
//    public String getUsername() {
//        return username;
//    }
//
//    public void setUsername(String username) {
//        this.username = username;
//    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }
}
