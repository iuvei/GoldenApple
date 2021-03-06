package com.goldenapple.lottery.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.goldenapple.lottery.R;
import com.goldenapple.lottery.app.BaseFragment;
import com.goldenapple.lottery.base.net.RestCallback;
import com.goldenapple.lottery.base.net.RestRequest;
import com.goldenapple.lottery.base.net.RestRequestManager;
import com.goldenapple.lottery.base.net.RestResponse;
import com.goldenapple.lottery.component.CustomDialog;
import com.goldenapple.lottery.component.DialogLayout;
import com.goldenapple.lottery.data.DeleteRegisterLinkCommand;
import com.goldenapple.lottery.data.GetLinkUserListCommand;
import com.goldenapple.lottery.data.GetLinkUserResponse;
import com.goldenapple.lottery.data.LinkUserBean;
import com.goldenapple.lottery.game.PromptManager;
import com.goldenapple.lottery.view.adapter.LinkListAdapter;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Sakura on 2018/7/11.
 */

public class LinkManagement extends BaseFragment
{
    private static final int GET_LINKS = 1;
    private static final int DEL_LINKS = 2;
    @BindView(R.id.link_total)
    TextView linkTotal;
    @BindView(R.id.user_total)
    TextView userTotal;
    @BindView(R.id.link_list)
    RecyclerView linkList;
    Unbinder unbinder;
    
    private LinkListAdapter linkListAdapter;
    private LinearLayoutManager linearLayoutManager;
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle
            savedInstanceState)
    {
        View view = inflateView(inflater, container, true, "链接管理", R.layout.link_management, true, true);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }
    
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        
        linearLayoutManager = new LinearLayoutManager(getActivity());
        linkList.setLayoutManager(linearLayoutManager);
        linkList.setHasFixedSize(true);
        linkListAdapter = new LinkListAdapter();
        linkList.setAdapter(linkListAdapter);
        linkListAdapter.setOnDetailClickListner(new LinkListAdapter.OnDetailClickListner()
        {
            @Override
            public void onDetailClick(View view, LinkUserBean curData)
            {
                Bundle bundle = new Bundle();
                bundle.putSerializable("detail", curData);
                launchFragment(LinkDetailFragment.class, bundle);
            }
        });
        linkListAdapter.setOnDeleteClickListner(new LinkListAdapter.OnDeleteClickListner()
        {
            @Override
            public void onDeleteClick(View view, LinkUserBean curData)
            {
                CustomDialog.Builder builder = new CustomDialog.Builder(getActivity());
                builder.setTitle("");
                builder.setLayoutSet(DialogLayout.LEFT_AND_RIGHT);
                builder.setMessage("删除链接后，将不能从该链接注册账号。\n" + "确定删除吗？");
                builder.setNegativeButton("确认删除", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        dialogInterface.dismiss();
                        DeleteRegisterLinkCommand delCommand = new DeleteRegisterLinkCommand();
                        delCommand.setId(curData.getId());
                        executeCommand(delCommand, restCallback, DEL_LINKS);
                    }
                });
                builder.setPositiveButton("我再想想", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        dialogInterface.dismiss();
                    }
                });
                builder.create().show();
            }
        });
        
        getLinksRequest();
    }
    
    private void getLinksRequest()
    {
        GetLinkUserListCommand getLinkUserListCommand = new GetLinkUserListCommand();
        TypeToken typeToken = new TypeToken<RestResponse<GetLinkUserResponse>>() {};
        RestRequest restRequest = RestRequestManager.createRequest(getActivity(), getLinkUserListCommand, typeToken,
                restCallback, GET_LINKS, this);
        restRequest.execute();
    }
    
    private RestCallback restCallback = new RestCallback()
    {
        @Override
        public boolean onRestComplete(RestRequest request, RestResponse response)
        {
            switch (request.getId())
            {
                case GET_LINKS:
                    if (response != null && response.getData() != null)
                    {
                        GetLinkUserResponse getLinkUserResponse = (GetLinkUserResponse) response.getData();
                        ArrayList<LinkUserBean> list;
                        if (getLinkUserResponse != null)
                        {
                            list = getLinkUserResponse.getData();
                        } else
                        {
                            list = new ArrayList<>();
                        }
                        linkListAdapter.setData(list);
                        linkTotal.setText(String.valueOf(getLinkUserResponse.getTotalLinks()));
                        userTotal.setText(String.valueOf(getLinkUserResponse.getTotalUserCount()));
                    }
                    break;
                case DEL_LINKS:
                    getLinksRequest();
                    break;
            }
            return true;
        }
        
        @Override
        public boolean onRestError(RestRequest request, int errCode, String errDesc)
        {
            if (errCode == 7003)
            {
                showToast("正在更新服务器请稍等", Toast.LENGTH_LONG);
                return true;
            } else if (errCode == 7006)
            {
                CustomDialog dialog = PromptManager.showCustomDialog(getActivity(), "重新登录", errDesc, "重新登录", errCode);
                dialog.setCancelable(false);
                dialog.show();
                return true;
            }
            return false;
        }
        
        @Override
        public void onRestStateChanged(RestRequest request, int state)
        {
        
        }
    };
    
    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        unbinder.unbind();
    }
}
