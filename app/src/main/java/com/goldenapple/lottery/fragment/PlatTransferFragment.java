package com.goldenapple.lottery.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.michael.easydialog.EasyDialog;
import com.goldenapple.lottery.R;
import com.goldenapple.lottery.app.BaseFragment;
import com.goldenapple.lottery.app.GoldenAppleApp;
import com.goldenapple.lottery.base.net.GsonHelper;
import com.goldenapple.lottery.base.net.RestCallback;
import com.goldenapple.lottery.base.net.RestRequest;
import com.goldenapple.lottery.base.net.RestRequestManager;
import com.goldenapple.lottery.base.net.RestResponse;
import com.goldenapple.lottery.component.CustomDialog;
import com.goldenapple.lottery.component.DialogLayout;
import com.goldenapple.lottery.data.GetThirdPlatBalance;
import com.goldenapple.lottery.data.GetThirdPlatBalanceCommand;
import com.goldenapple.lottery.data.GetThirdPlatCommand;
import com.goldenapple.lottery.data.PlatForm;
import com.goldenapple.lottery.data.TransferFundsCommand;
import com.goldenapple.lottery.view.adapter.TransferAdapter;

import org.apache.commons.codec.digest.DigestUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by ACE-PC on 2017/1/16.
 * 平台转账
 */

public class PlatTransferFragment extends BaseFragment {

    private static final String TAG = PlatTransferFragment.class.getSimpleName();

    private static final int TRACE_GET_THIRD_RLAT = 1;
    private static final int TRACE_GET_THIRD_PLAT_BALANCE = 2;
    private static final int TRACE_POST_THIRD_SUBMIT = 3;


    @BindView(R.id.plat_type)
    LinearLayout platType;
    @BindView(R.id.plat_from)
    LinearLayout platFrom;
    @BindView(R.id.image_type)
    ImageView imageType;
    @BindView(R.id.image_plat)
    ImageView imageFrom;
    @BindView(R.id.transfer_amount)
    EditText transferAmount;
    @BindView(R.id.btn_submit)
    Button btnSubmit;
    @BindView(R.id.plat_funds)
    TextView platFunds;
    @BindView(R.id.channel_funds)
    TextView channelFunds;
    @BindView(R.id.text_from)
    TextView textFrom;
    @BindView(R.id.text_to)
    TextView textTo;

    private ArrayList<PlatForm> platForm = new ArrayList<>();
    private Map<Integer, GetThirdPlatBalance> platBalanceMap = new HashMap<>();
    private int positionFromId = 0, positionFrom = 0;
    private int positionToId = 0, positionTo = 0;
    private DecimalFormat df = new DecimalFormat("0.0000 ");
    private boolean status = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflateView(inflater, container, "频道转帐", R.layout.plat_transfer_fragment, true, true);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //允许输入数字并且可以为小数
        transferAmount.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_VARIATION_NORMAL);
        //contentEdit.setInputType(type);
        transferAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().contains(".")) {
                    if (s.length() - 1 - s.toString().indexOf(".") > 2) {
                        s = s.toString().subSequence(0, s.toString().indexOf(".") + 3);
                        transferAmount.setText(s);
                        transferAmount.setSelection(s.length());
                    }
                }
                if (s.toString().trim().substring(0).equals(".")) {
                    s = "0" + s;
                    transferAmount.setText(s);
                    transferAmount.setSelection(2);
                }

                if (s.toString().startsWith("0") && s.toString().trim().length() > 1) {
                    if (!s.toString().substring(1, 2).equals(".")) {
                        transferAmount.setText(s.subSequence(0, 1));
                        transferAmount.setSelection(1);
                        return;
                    }
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }
        });
    }

    /**
     * 初使化转帐平台数据
     */
    private void init() {
        if (platForm.size() == 0 && platBalanceMap.size() == 0) {
            showToast("无数据....");
            return;
        }

        if (platForm.size() >= 2) {
            PlatForm typePlat = platForm.get(platForm.size() - 1);
            positionFrom = platForm.size() - 1;
            positionFromId = typePlat.getId();
            textFrom.setText(typePlat.getName());

            PlatForm plat = platForm.get(0);
            positionTo = 0;
            positionToId = plat.getId();
            textTo.setText(plat.getName());
            updatedUI(positionToId, positionToId != -1 ? true : false);
        } else {
            CustomDialog.Builder builder = new CustomDialog.Builder(getContext());
            builder.setMessage("资金转移平台不足!");
            builder.setTitle("温馨提示");
            builder.setLayoutSet(DialogLayout.SINGLE);
            builder.setPositiveButton("知道了", (dialog, which) -> {
                dialog.dismiss();
                getActivity().finish();
            });
            builder.create().show();
        }
    }

    /*private void updatedUI(int from,int to) {
        GetThirdPlatBalance platFromBalance = platBalanceMap.get(from);
        if (platFromBalance != null) {
            platFunds.setText(platFromBalance.getBalance());
        } else {
            if (from != -1) {
                platFunds.setText("0");
            } else {
                platFunds.setText(String.format("%.4f", goldenappleApp.getUserCentre().getUserInfo().getAbalance()));
            }
        }

        GetThirdPlatBalance platToBalance = platBalanceMap.get(to);
        if (platToBalance != null) {
            platFunds.setText(platFromBalance.getPlatAvailable());
            channelFunds.setText(platToBalance.getBalance());
        } else {
            if (to != -1) {
                channelFunds.setText("0");
            } else {
                channelFunds.setText(String.format("%.4f", goldenappleApp.getUserCentre().getUserInfo().getAbalance()));
            }
        }
    }*/

    private void updatedUI(int id, TextView view) {
        GetThirdPlatBalance platBalance = platBalanceMap.get(id);
        if (platBalance != null) {
            view.setText(platBalance.getBalance());
        } else {
            view.setText(df.format(Double.valueOf(platBalanceMap.get(1).getPlatAvailable())));
        }
    }

    private void updatedUI(int id, boolean fool) {
        GetThirdPlatBalance platToBalance = platBalanceMap.get(id);
        if (platToBalance != null) {
            if (fool) {
                platFunds.setText(df.format(Double.valueOf(platToBalance.getPlatAvailable())));
                channelFunds.setText(df.format(Double.valueOf(platToBalance.getBalance())));
            } else {
                platFunds.setText(df.format(Double.valueOf(platToBalance.getBalance())));
                channelFunds.setText(df.format(Double.valueOf(platToBalance.getPlatAvailable())));
            }
        }
    }

    private void showBubble(View locationView) {
        if (platForm.size() == 0 && platBalanceMap.size() == 0) {
            showToast("无数据....");
            return;
        }
        View view = getActivity().getLayoutInflater().inflate(R.layout.plat_info_view, null);

        TransferAdapter adapter = new TransferAdapter(getContext(), platForm, status ? positionFromId : positionToId);
        ListView platList = view.findViewById(R.id.plat_info_list);
        LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) platList.getLayoutParams();
        linearParams.width = status ? platType.getWidth() : platFrom.getWidth();
        platList.setLayoutParams(linearParams);
        platList.setAdapter(adapter);

        EasyDialog easyDialog = new EasyDialog(getActivity())
                .setLayout(view)
                .setGravity(EasyDialog.GRAVITY_BOTTOM)
                .setBackgroundColor(getResources().getColor(R.color.background))
                .setLocationByAttachedView(locationView)
                .setAnimationTranslationShow(EasyDialog.DIRECTION_X, 350, 400, 100, -50, 50, 0)
                .setAnimationAlphaShow(350, 0.3f, 1.0f)
                .setAnimationTranslationDismiss(EasyDialog.DIRECTION_X, 350, -50, 400)
                .setAnimationAlphaDismiss(350, 1.0f, 0.0f)
                .setTouchOutsideDismiss(true)
                .setMatchParent(false)
                .setOutsideColor(getResources().getColor(R.color.halfTransparent))
                .show();

        adapter.setOnIssueNoClickListener((PlatForm plat, int position) -> {
            if (status) {
                positionFrom = position;
                positionFromId = plat.getId();
                textFrom.setText(plat.getName());
//                updatedUI(positionFromId, positionFromId != -1 ? true : false);
                updatedUI(positionFromId, platFunds);
            } else {
                positionTo = position;
                positionToId = plat.getId();
                textTo.setText(plat.getName());
//                updatedUI(positionToId, positionToId != -1 ? true : false);
                updatedUI(positionToId, channelFunds);
            }
            easyDialog.dismiss();
        });
    }

    @OnClick({R.id.plat_type, R.id.plat_from})
    public void selectDonw(View v) {
        switch (v.getId()) {
            case R.id.plat_type:
                status = true;
                showBubble(platType);
                break;
            case R.id.plat_from:
                status = false;
                showBubble(imageFrom);
                break;
        }
    }

    //获取转帐平台
    private void GetThirdPlatLoad() {
        GetThirdPlatCommand routeCommand = new GetThirdPlatCommand();
        TypeToken typeToken = new TypeToken<RestResponse<ArrayList<PlatForm>>>() {
        };
        RestRequest restRequest = RestRequestManager.createRequest(getActivity(), routeCommand, typeToken, restCallback, TRACE_GET_THIRD_RLAT, this);
        RestResponse restResponse = restRequest.getCache();
        if (restResponse != null && restResponse.getData() instanceof ArrayList<?>) {
            platForm = (ArrayList<PlatForm>) restResponse.getData();
        }
        restRequest.execute();
    }

    private void GetThirdPlatBalance(boolean status, int platId) {
        GetThirdPlatBalanceCommand routeCommand = new GetThirdPlatBalanceCommand();
        if (status) {
            routeCommand.setPlatId(platId + "");
        }
        TypeToken typeToken = new TypeToken<RestResponse<Map<Integer, GetThirdPlatBalance>>>() {
        };
        RestRequest restRequest = RestRequestManager.createRequest(getActivity(), routeCommand, typeToken, restCallback, TRACE_GET_THIRD_PLAT_BALANCE, this);
        restRequest.execute();
    }

    @OnClick(R.id.btn_submit)
    public void btnSubmit() {
        if (TextUtils.isEmpty(transferAmount.getText())) {
            showToast("请输入转移金额", Toast.LENGTH_SHORT);
            return;
        }

        double drawMoney = Double.parseDouble(transferAmount.getText().toString());
        if (drawMoney < 10) {
            Toast.makeText(getActivity(), "转移金额不能小于10元", Toast.LENGTH_SHORT).show();
            return;
        }

        if (platBalanceMap.size() == 0) {
            Toast.makeText(getActivity(), "暂无转帐平台", Toast.LENGTH_SHORT).show();
            return;
        }

        if (positionFromId == positionToId) {
            showToast("资金转移平台不能相同");
            return;
        }

        CustomDialog.Builder builder = new CustomDialog.Builder(getContext());
        builder.setMessage("确定要从" + platForm.get(positionFrom).getName() + "转账给" + platForm.get(positionTo).getName() + "吗？\n" + "本次转账金额：" + String.format("\t%.3f\t 元", drawMoney));
        builder.setTitle("转账确认");
        builder.setLayoutSet(DialogLayout.LEFT_AND_RIGHT);
        builder.setNegativeBackground(R.drawable.notidialog_bottom_right_fillet_btn_s);
        builder.setNegativeButton(" 确认转帐", (dialog, which) -> {
            try {
                TransferFundsCommand fundsCommand = new TransferFundsCommand();
                fundsCommand.setFromPlat(positionFromId);
                fundsCommand.setToPlat(positionToId);
                fundsCommand.setAmount(drawMoney + "");
                String requestCommand = GsonHelper.toJson(fundsCommand);
                requestCommand = requestCommand.replace(":", "=").replace(",", "&").replace("\"", "");
                fundsCommand.setSign(DigestUtils.md5Hex(URLEncoder.encode(requestCommand.substring(1, requestCommand.length() - 1) + "&packet=Fund&action=PlatTransfer", "UTF-8") + GoldenAppleApp.getUserCentre().getKeyApiKey()));
                TypeToken typeToken = new TypeToken<RestResponse<GetThirdPlatBalance>>() {
                };
                RestRequest restRequest = RestRequestManager.createRequest(getActivity(), fundsCommand, typeToken, restCallback, TRACE_POST_THIRD_SUBMIT, this);
                restRequest.execute();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            btnSubmit.setEnabled(false);
            dialog.dismiss();
        });
        builder.setPositiveBackground(R.drawable.notidialog_bottom_left_fillet_btn_s);
        builder.setPositiveButton("取消", (dialog, which) -> {
            dialog.dismiss();
        });
        builder.create().show();
    }

    private RestCallback restCallback = new RestCallback() {
        @Override
        public boolean onRestComplete(RestRequest request, RestResponse response) {
            switch (request.getId()) {
                case TRACE_GET_THIRD_PLAT_BALANCE:
                    platBalanceMap = (Map<Integer, GetThirdPlatBalance>) response.getData();
                    if (platBalanceMap.size() > 0) {
                        init();
                    }
                    break;
                case TRACE_GET_THIRD_RLAT:
                    platForm = (ArrayList<PlatForm>) response.getData();
                    GetThirdPlatBalance(false, 0);
                    break;
                case TRACE_POST_THIRD_SUBMIT:
                    GetThirdPlatBalance getPlatBalance = (GetThirdPlatBalance) response.getData();
                    if (platBalanceMap.containsKey(getPlatBalance.getId())) {
                        platBalanceMap.put(getPlatBalance.getId(), getPlatBalance);
                        Iterator<Map.Entry<Integer, GetThirdPlatBalance>> iterator = platBalanceMap.entrySet().iterator();
                        while(iterator.hasNext()) {
                            Map.Entry<Integer, GetThirdPlatBalance> next = iterator.next();
                            GetThirdPlatBalance person = next.getValue();
                            person.setPlatAvailable(getPlatBalance.getPlatAvailable());
                        }
                        updatedUI(getPlatBalance.getId(), positionToId != -1 ? true : false);
                        showToast(response.getError());
                    }
                    break;
            }
            return true;
        }

        @Override
        public boolean onRestError(RestRequest request, int errCode, String errDesc) {
            if (errCode == 3004 || errCode == 2016) {
                signOutDialog(getActivity(), errCode);
                return true;
            } else {
                showToast(errDesc);
            }
            return false;
        }

        @Override
        public void onRestStateChanged(RestRequest request, @RestRequest.RestState int state) {
            if (state == RestRequest.RUNNING && (request.getId() == TRACE_GET_THIRD_RLAT || request.getId() == TRACE_GET_THIRD_PLAT_BALANCE)) {
                dialogShow("进行中...");
            } else {
                btnSubmit.setEnabled(true);
                dialogHide();
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        GetThirdPlatLoad();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
