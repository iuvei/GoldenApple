package com.goldenapple.lottery.data;

import com.goldenapple.lottery.app.GoldenAppleApp;
import com.goldenapple.lottery.base.net.RequestConfig;

/**
 * 当前用户所有信息查询
 * Created by Alashi on 2016/1/28.
 */
@RequestConfig(api = "service?packet=User&action=getCurrentUserInfo", response = UserInfo.class)
public class UserInfoCommand extends CommonAttribute{}
