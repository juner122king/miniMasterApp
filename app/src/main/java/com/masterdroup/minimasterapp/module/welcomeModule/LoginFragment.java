package com.masterdroup.minimasterapp.module.welcomeModule;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.blankj.utilcode.utils.ActivityUtils;
import com.blankj.utilcode.utils.ToastUtils;
import com.gizwits.gizwifisdk.api.GizWifiSDK;
import com.gizwits.gizwifisdk.enumration.GizWifiErrorCode;
import com.gizwits.gizwifisdk.listener.GizWifiSDKListener;
import com.masterdroup.minimasterapp.App;
import com.masterdroup.minimasterapp.R;
import com.masterdroup.minimasterapp.module.home.HomeActivity;
import com.masterdroup.minimasterapp.util.Utils;
import com.yuyh.library.imgsel.utils.LogUtils;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by 11473 on 2016/11/29.
 */

public class LoginFragment extends Fragment implements Contract.LoginView {
    Contract.Presenter mPresenter;
    @Bind(R.id.btn_registered)
    Button mBtnRegistered;
    @Bind(R.id.et_phone)
    EditText mEtPhone;
    @Bind(R.id.et_pwd)
    EditText mEtPwd;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new WelcomePresenter(this);
        //每次启动activity都要注册一次sdk监听器，保证sdk状态能正确回调
        GizWifiSDK.sharedInstance().setListener(mListener);
    }

    private GizWifiSDKListener mListener = new GizWifiSDKListener() {
        @Override
        public void didUserLogin(GizWifiErrorCode result, String uid, String token) {
            LogUtils.d("GizWifiSDK", "机智云登录==>   uid:" + uid + "     result:" + result.toString() + "      token" + token);
            if (result == GizWifiErrorCode.GIZ_SDK_SUCCESS) {
                LogUtils.d("GizWifiSDK", "机智云登录==>成功");
                // 登录成功
                mPresenter.login(mEtPhone.getText().toString(), mEtPwd.getText().toString(), uid, token);


            } else {
                LogUtils.e("GizWifiSDK", "机智云登录==>失败");
                // 登录失败
                ToastUtils.showShortToast("机智云登录 失败");

            }


        }
    };


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_welcome_view2, container, false);
        ButterKnife.bind(this, view);
        return view;

    }

    @Override
    public void setPresenter(Contract.Presenter presenter) {
        mPresenter = Utils.checkNotNull(presenter);
    }

    @Override
    public Context onGetContext() {
        return getActivity();
    }

    /**
     * 登录成功
     */
    @Override
    public void onLoginSuccess(String name, String token, String giz_uid, String giz_token) {

        App.spUtils.putString(App.mContext.getString(R.string.key_token), token);


        App.spUtils.putString(App.mContext.getString(R.string.name), name);

        App.spUtils.putString(App.mContext.getString(R.string.giz_uid), giz_uid);

        App.spUtils.putString(App.mContext.getString(R.string.giz_token), giz_token);

        startActivity(new Intent(this.getActivity(), HomeActivity.class));
        this.getActivity().finish();

    }

    /**
     * 登录失败
     */
    @Override
    public void onLoginFailure(String info) {
        Toast.makeText(ActivityUtils.getTopActivity(), "登录失败", Toast.LENGTH_SHORT).show();
    }


    @OnClick(R.id.btn_registered)
    public void onClick() {

        mPresenter.gizLogin(mEtPhone.getText().toString(), mEtPwd.getText().toString());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

}
