package com.sation.knxcontroller.activity;


import android.app.Activity;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Layout;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.sation.knxcontroller.R;
import com.sation.knxcontroller.STKNXControllerApp;
import com.sation.knxcontroller.STKNXControllerConstant;
import com.sation.knxcontroller.services.RestartService;
import com.sation.knxcontroller.util.FileUtils;
import com.sation.knxcontroller.util.Log;
import com.sation.knxcontroller.util.NetWorkUtil;
import com.sation.knxcontroller.util.SystemUtil;
import com.sation.knxcontroller.util.uikit.ApkUtils;
import com.sation.knxcontroller.widget.PromptDialog;
import com.sation.knxcontroller.widget.settingview.ImageItemView;
import com.sation.knxcontroller.widget.settingview.SettingButton;
import com.sation.knxcontroller.widget.settingview.SettingData;
import com.sation.knxcontroller.widget.settingview.SettingViewItemData;

import java.io.File;
import java.util.List;
import java.util.Locale;

import static com.sation.knxcontroller.util.FileUtils.CopyStatus.COPY_SUCCESSFUL;
import static com.sation.knxcontroller.util.FileUtils.CopyStatus.SAME_FILE;
import static com.sation.knxcontroller.util.FileUtils.CopyStatus.SAME_PATH;

public class SettingsActivity extends BaseActivity {
    private final static String TAG = "SettingsActivity";

    private SharedPreferences mSettings;
    private SettingButton mSBKNXGateway = null;
    private SettingButton mSBPhysicalAddress = null;
    private SettingButton mSBScreenOff = null;
    private SettingButton mSBLanguage = null;
    private SettingButton mSBReboot = null;
    private SettingButton mSBChangePassword = null;
    private SettingButton mSBUpgradeSoftware = null;
    private SettingButton mSBUpgradeProject = null;
    private SettingButton mSBSystemStatus = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        this.mSettings = getSharedPreferences(STKNXControllerConstant.SETTING_FILE,
                android.content.Context.MODE_PRIVATE);
        this.mSBKNXGateway = (SettingButton) findViewById(R.id.SettingButton_KNXGateway);
        this.mSBPhysicalAddress = (SettingButton) findViewById(R.id.SettingButton_PhysicalAddress);
        this.mSBScreenOff = (SettingButton) findViewById(R.id.SettingButton_ScreenOff);
        this.mSBLanguage = (SettingButton) findViewById(R.id.SettingButton_Language);
        this.mSBReboot = (SettingButton) findViewById(R.id.SettingButton_Reboot);
        this.mSBChangePassword = (SettingButton) findViewById(R.id.SettingButton_ChangePassword);
        this.mSBUpgradeSoftware = (SettingButton) findViewById(R.id.SettingButton_UpgradeSoftware);
        this.mSBUpgradeProject = (SettingButton) findViewById(R.id.SettingButton_UpgradeProject);
        this.mSBSystemStatus = (SettingButton) findViewById(R.id.SettingButton_SystemStatus);

        this.mSBKNXGateway.setOnSettingButtonClickListener(new SettingButton.onSettingButtonClickListener() {

            @Override
            public void onSettingButtonClick() { // KNX网关设置
                final View view = LayoutInflater.from(SettingsActivity.this).inflate(R.layout.knx_gateway_setting, null);
                final EditText txtIP = (EditText) view.findViewById(R.id.txtIP);
                txtIP.setImeOptions(EditorInfo.IME_ACTION_NEXT);
                final EditText txtPort = (EditText) view.findViewById(R.id.txtPort);
                txtPort.setImeOptions(EditorInfo.IME_ACTION_DONE);
                final RadioButton rbGroupBroadcast = (RadioButton) view.findViewById(R.id.radioButtonUDPGroupBroadcast);
                final RadioButton rbPeerToPeer = (RadioButton) view.findViewById(R.id.radioButtonUDPPeerToPeer);
                rbGroupBroadcast.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){

                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if(isChecked) {
                            rbPeerToPeer.setChecked(false);
                        }
                    }
                });
                rbPeerToPeer.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){

                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if(isChecked) {
                            rbGroupBroadcast.setChecked(false);
                        }
                    }
                });

                String mKNXGatewayIP = mSettings.getString(STKNXControllerConstant.KNX_GATEWAY_IP, STKNXControllerConstant.KNX_GATEWAY_DEFAULT);
                int mKNXGatewayPort = mSettings.getInt(STKNXControllerConstant.KNX_GATEWAY_PORT, STKNXControllerConstant.KNX_GATEWAY_PORT_DEFAULT);
                int mKNXUDPWorkWay = mSettings.getInt(STKNXControllerConstant.KNX_UDP_WORK_WAY, STKNXControllerConstant.KNX_UDP_WORK_WAY_DEFAULT);
                txtIP.setText(mKNXGatewayIP);
                txtPort.setText(String.valueOf(mKNXGatewayPort));
                if(STKNXControllerConstant.KNX_UDP_WORK_WAY_GROUP_BROADCAST == mKNXUDPWorkWay) {
                    rbGroupBroadcast.setChecked(true);
                    rbPeerToPeer.setChecked(false);
                } else if (STKNXControllerConstant.KNX_UDP_WORK_WAY_PEER_TO_PEER == mKNXUDPWorkWay) {
                    rbGroupBroadcast.setChecked(false);
                    rbPeerToPeer.setChecked(true);
                } else {
                    rbGroupBroadcast.setChecked(false);
                    rbPeerToPeer.setChecked(false);
                }

                final PromptDialog mPromptDialog = new PromptDialog.Builder(SettingsActivity.this)
                        .setTitle(SettingsActivity.this.getResources().getString(R.string.setting_knx_gateway))
                        .setIcon(R.mipmap.launcher)
                        .setViewStyle(PromptDialog.VIEW_STYLE_NORMAL)
                        .setView(view)
                        .setButton1(SettingsActivity.this.getResources().getString(R.string.cancel),  new PromptDialog.OnClickListener() {

                            @Override
                            public void onClick(Dialog dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setButton2(SettingsActivity.this.getResources().getString(R.string.save), new PromptDialog.OnClickListener() {

                            @Override
                            public void onClick(Dialog dialog, int which) {
                                SharedPreferences.Editor editor = mSettings.edit();
                                editor.putString(STKNXControllerConstant.KNX_GATEWAY_IP, txtIP.getText().toString());
                                editor.putInt(STKNXControllerConstant.KNX_GATEWAY_PORT, Integer.valueOf(txtPort.getText().toString()));
                                if(rbGroupBroadcast.isChecked()) { // 选中组播的工作方式
                                    editor.putInt(STKNXControllerConstant.KNX_UDP_WORK_WAY,
                                            STKNXControllerConstant.KNX_UDP_WORK_WAY_GROUP_BROADCAST);
                                } else if (rbPeerToPeer.isChecked()) { // 选中点对点的工作方式
                                    editor.putInt(STKNXControllerConstant.KNX_UDP_WORK_WAY,
                                            STKNXControllerConstant.KNX_UDP_WORK_WAY_PEER_TO_PEER);
                                } else { // 默认情况：谁都没选中
                                    editor.putInt(STKNXControllerConstant.KNX_UDP_WORK_WAY,
                                            STKNXControllerConstant.KNX_UDP_WORK_WAY_GROUP_BROADCAST);
                                }
                                editor.apply();
                                dialog.dismiss();

                                //重启启动
                                startService(new Intent(SettingsActivity.this, RestartService.class));
                                finish();
                            }
                        })
                        .show();

                txtPort.setOnEditorActionListener(new TextView.OnEditorActionListener() {

                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

						/*判断是否是“DONE”键*/
                        if(actionId == EditorInfo.IME_ACTION_DONE) {
							/* 隐藏软键盘 */
                            InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(
                                    Context.INPUT_METHOD_SERVICE);
                            if (imm.isActive()) {
                                imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
                            }

                            SharedPreferences.Editor editor = mSettings.edit();
                            editor.putString(STKNXControllerConstant.KNX_GATEWAY_IP, txtIP.getText().toString());
                            editor.putInt(STKNXControllerConstant.KNX_GATEWAY_PORT, Integer.valueOf(txtPort.getText().toString()));
                            if(rbGroupBroadcast.isChecked()) { // 选中组播的工作方式
                                editor.putInt(STKNXControllerConstant.KNX_UDP_WORK_WAY,
                                        STKNXControllerConstant.KNX_UDP_WORK_WAY_GROUP_BROADCAST);
                            } else if (rbPeerToPeer.isChecked()) { // 选中点对点的工作方式
                                editor.putInt(STKNXControllerConstant.KNX_UDP_WORK_WAY,
                                        STKNXControllerConstant.KNX_UDP_WORK_WAY_PEER_TO_PEER);
                            } else { // 默认情况：谁都没选中
                                editor.putInt(STKNXControllerConstant.KNX_UDP_WORK_WAY,
                                        STKNXControllerConstant.KNX_UDP_WORK_WAY_GROUP_BROADCAST);
                            }
                            editor.apply();
                            mPromptDialog.dismiss();
                            //重启启动
                            startService(new Intent(SettingsActivity.this, RestartService.class));
                            ((Activity) SettingsActivity.this).finish();

                            return true;
                        }

                        return false;
                    }
                });
            }
        });

        this.mSBPhysicalAddress.setOnSettingButtonClickListener(new SettingButton.onSettingButtonClickListener() {

            @Override
            public void onSettingButtonClick() { // 物理地址

                final View view = LayoutInflater.from(SettingsActivity.this).inflate(R.layout.physical_address_setting, null);
                final EditText txtFirst = (EditText) view.findViewById(R.id.txtFirst);
                txtFirst.setImeOptions(EditorInfo.IME_ACTION_NEXT);
                final EditText txtSecond = (EditText) view.findViewById(R.id.txtSecond);
                txtSecond.setImeOptions(EditorInfo.IME_ACTION_NEXT);
                final EditText txtThree = (EditText) view.findViewById(R.id.txtThree);
                txtThree.setImeOptions(EditorInfo.IME_ACTION_DONE);

                int physicalAddressFirst = mSettings.getInt(
                        STKNXControllerConstant.KNX_PHYSICAL_ADDRESS_FIRST, STKNXControllerConstant.PHYSICAL_ADDRESS_VALUE_FIRST);
                int physicalAddressSecond = mSettings.getInt(
                        STKNXControllerConstant.KNX_PHYSICAL_ADDRESS_SECOND, STKNXControllerConstant.PHYSICAL_ADDRESS_VALUE_SECOND);
                int physicalAddressThree = mSettings.getInt(
                        STKNXControllerConstant.KNX_PHYSICAL_ADDRESS_THIRD, STKNXControllerConstant.PHYSICAL_ADDRESS_VALUE_THIRD);
                txtFirst.setText(String.valueOf(physicalAddressFirst));
                txtSecond.setText(String.valueOf(physicalAddressSecond));
                txtThree.setText(String.valueOf(physicalAddressThree));

                final PromptDialog mPromptDialog = new PromptDialog.Builder(SettingsActivity.this)
                        .setTitle(SettingsActivity.this.getResources().getString(R.string.setting_physical_address))
                        .setIcon(R.mipmap.launcher)
                        .setViewStyle(PromptDialog.VIEW_STYLE_NORMAL)
                        .setView(view)
                        .setButton1(SettingsActivity.this.getResources().getString(R.string.cancel),  new PromptDialog.OnClickListener() {

                            @Override
                            public void onClick(Dialog dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setButton2(SettingsActivity.this.getResources().getString(R.string.save), new PromptDialog.OnClickListener() {

                            @Override
                            public void onClick(Dialog dialog, int which) {
                                SharedPreferences.Editor editor = mSettings.edit();
                                editor.putInt(STKNXControllerConstant.KNX_PHYSICAL_ADDRESS_FIRST,
                                        Integer.valueOf(txtFirst.getText().toString()));
                                editor.putInt(STKNXControllerConstant.KNX_PHYSICAL_ADDRESS_SECOND,
                                        Integer.valueOf(txtSecond.getText().toString()));
                                editor.putInt(STKNXControllerConstant.KNX_PHYSICAL_ADDRESS_THIRD,
                                        Integer.valueOf(txtThree.getText().toString()));
                                editor.apply();
                                dialog.dismiss();
                                //重启启动
                                SettingsActivity.this.startService(new Intent(SettingsActivity.this, RestartService.class));
                                ((Activity) SettingsActivity.this).finish();

                            }
                        })
                        .show();

                txtThree.setOnEditorActionListener(new TextView.OnEditorActionListener() {

                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
						/*判断是否是“DONE”键*/
                        if(actionId == EditorInfo.IME_ACTION_DONE) {
							/* 隐藏软键盘 */
                            InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(
                                    Context.INPUT_METHOD_SERVICE);
                            if (imm.isActive()) {
                                imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
                            }

                            SharedPreferences.Editor editor = mSettings.edit();
                            editor.putInt(STKNXControllerConstant.KNX_PHYSICAL_ADDRESS_FIRST,
                                    Integer.valueOf(txtFirst.getText().toString()));
                            editor.putInt(STKNXControllerConstant.KNX_PHYSICAL_ADDRESS_SECOND,
                                    Integer.valueOf(txtSecond.getText().toString()));
                            editor.putInt(STKNXControllerConstant.KNX_PHYSICAL_ADDRESS_THIRD,
                                    Integer.valueOf(txtThree.getText().toString()));
                            editor.apply();
                            mPromptDialog.dismiss();
                            //重启启动
                            SettingsActivity.this.startService(new Intent(SettingsActivity.this, RestartService.class));
                            ((Activity) SettingsActivity.this).finish();

                            return true;
                        }

                        return false;
                    }
                });
            }
        });

        this.mSBScreenOff.setOnSettingButtonClickListener(new SettingButton.onSettingButtonClickListener() {

            @Override
            public void onSettingButtonClick() { // 屏幕背光
                try {
//                    final View view = LayoutInflater.from(SettingsActivity.this).inflate(R.layout.knx_refresh_status_setting, null);
                    final View view = LayoutInflater.from(SettingsActivity.this).inflate(R.layout.system_sleep_setting, null);
                    final RadioButton rb15s = (RadioButton) view.findViewById(R.id.systemsleepsetting_radioButton_fifteenseconds);
                    final RadioButton rb30s = (RadioButton) view.findViewById(R.id.systemsleepsetting_radioButton_thirtyseconds);
                    final RadioButton rb1m = (RadioButton) view.findViewById(R.id.systemsleepsetting_radioButton_oneminute);
                    final RadioButton rb2m = (RadioButton) view.findViewById(R.id.systemsleepsetting_radioButton_twominutes);
                    final RadioButton rb5m = (RadioButton) view.findViewById(R.id.systemsleepsetting_radioButton_fiveminutes);
                    final RadioButton rb10m = (RadioButton) view.findViewById(R.id.systemsleepsetting_radioButton_tenminutes);
                    final RadioButton rb30m = (RadioButton) view.findViewById(R.id.systemsleepsetting_radioButton_thirtyminutes);
                    final RadioButton rbns = (RadioButton) view.findViewById(R.id.systemsleepsetting_radioButton_neversleep);
//                    final EditText txtTimeSpan = (EditText) view.findViewById(R.id.txtTimeSpan);
//
                    int knxSettingScreenOffTimespan = Settings.System.getInt(SettingsActivity.this.getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT);
                    if(knxSettingScreenOffTimespan>1000) {
                        knxSettingScreenOffTimespan /= 1000;
                    }
//                    txtTimeSpan.setText(String.valueOf(knxSettingScreenOffTimespan));
                    if (knxSettingScreenOffTimespan <= 0) {
                        rbns.setChecked(true);
                    } else if (knxSettingScreenOffTimespan <= 15) {
                        rb15s.setChecked(true);
                    } else if (knxSettingScreenOffTimespan <= 30) {
                        rb30s.setChecked(true);
                    } else if (knxSettingScreenOffTimespan <= 60) {
                        rb1m.setChecked(true);
                    } else if (knxSettingScreenOffTimespan <= 120) {
                        rb2m.setChecked(true);
                    } else if (knxSettingScreenOffTimespan <= 300) {
                        rb5m.setChecked(true);
                    } else if (knxSettingScreenOffTimespan <= 600) {
                        rb10m.setChecked(true);
                    } else {
                        rb30m.setChecked(true);
                    }

                    rb15s.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            rb15s.setChecked(true);
                            rb30s.setChecked(false);
                            rb1m.setChecked(false);
                            rb2m.setChecked(false);
                            rb5m.setChecked(false);
                            rb10m.setChecked(false);
                            rb30m.setChecked(false);
                            rbns.setChecked(false);
                        }
                    });

                    rb30s.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            rb30s.setChecked(true);
                            rb15s.setChecked(false);
                            rb1m.setChecked(false);
                            rb2m.setChecked(false);
                            rb5m.setChecked(false);
                            rb10m.setChecked(false);
                            rb30m.setChecked(false);
                            rbns.setChecked(false);
                        }
                    });

                    rb1m.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            rb1m.setChecked(true);
                            rb15s.setChecked(false);
                            rb30s.setChecked(false);
                            rb2m.setChecked(false);
                            rb5m.setChecked(false);
                            rb10m.setChecked(false);
                            rb30m.setChecked(false);
                            rbns.setChecked(false);
                        }
                    });

                    rb2m.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            rb2m.setChecked(true);
                            rb15s.setChecked(false);
                            rb30s.setChecked(false);
                            rb1m.setChecked(false);
                            rb5m.setChecked(false);
                            rb10m.setChecked(false);
                            rb30m.setChecked(false);
                            rbns.setChecked(false);
                        }
                    });

                    rb5m.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            rb5m.setChecked(true);
                            rb15s.setChecked(false);
                            rb30s.setChecked(false);
                            rb1m.setChecked(false);
                            rb2m.setChecked(false);
                            rb10m.setChecked(false);
                            rb30m.setChecked(false);
                            rbns.setChecked(false);
                        }
                    });

                    rb10m.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            rb10m.setChecked(true);
                            rb15s.setChecked(false);
                            rb30s.setChecked(false);
                            rb1m.setChecked(false);
                            rb2m.setChecked(false);
                            rb5m.setChecked(false);
                            rb30m.setChecked(false);
                            rbns.setChecked(false);
                        }
                    });

                    rb30m.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            rb30m.setChecked(true);
                            rb15s.setChecked(false);
                            rb30s.setChecked(false);
                            rb1m.setChecked(false);
                            rb2m.setChecked(false);
                            rb5m.setChecked(false);
                            rb10m.setChecked(false);
                            rbns.setChecked(false);
                        }
                    });

                    rbns.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            rbns.setChecked(true);
                            rb15s.setChecked(false);
                            rb30s.setChecked(false);
                            rb1m.setChecked(false);
                            rb2m.setChecked(false);
                            rb5m.setChecked(false);
                            rb10m.setChecked(false);
                            rb30m.setChecked(false);
                        }
                    });

                    new PromptDialog.Builder(SettingsActivity.this)
                            .setTitle(SettingsActivity.this.getResources().getString(R.string.setting_back_light_time))
                            .setIcon(R.mipmap.launcher)
                            .setViewStyle(PromptDialog.VIEW_STYLE_NORMAL)
                            .setView(view)
                            .setButton1(SettingsActivity.this.getResources().getString(R.string.cancel),  new PromptDialog.OnClickListener() {

                                @Override
                                public void onClick(Dialog dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .setButton2(SettingsActivity.this.getResources().getString(R.string.save), new PromptDialog.OnClickListener() {

                                @Override
                                public void onClick(Dialog dialog, int which) {
                                    try {
//                                        int time = Integer.valueOf(txtTimeSpan.getText().toString());
                                        int time;
                                        if (rbns.isChecked()) {
                                            time = -1;
                                        } else if (rb15s.isChecked()) {
                                            time = 15;
                                        } else if (rb30s.isChecked()) {
                                            time = 30;
                                        } else if (rb1m.isChecked()) {
                                            time = 60;
                                        } else if (rb2m.isChecked()) {
                                            time = 120;
                                        } else if (rb5m.isChecked()) {
                                            time = 300;
                                        } else if (rb10m.isChecked()) {
                                            time = 600;
                                        } else {
                                            time = 1800;
                                        }
                                        if (time > 0) {
                                            Settings.System.putInt(SettingsActivity.this.getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, 1000 * time);
                                        } else {
                                            Settings.System.putInt(SettingsActivity.this.getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, -1); // 屏幕不休眠
                                        }

                                        dialog.dismiss();
                                    } catch (Exception ex) {
                                        ex.printStackTrace();
                                    }
                                }
                            })
                            .show();
                } catch (Exception e) {
                    Log.e(e.getLocalizedMessage());
                }
            }
        });

        this.mSBLanguage.setOnSettingButtonClickListener(new SettingButton.onSettingButtonClickListener() {

            @Override
            public void onSettingButtonClick() { // 语言选择
                final View view = LayoutInflater.from(SettingsActivity.this).inflate(R.layout.setting_language, null);
                final RadioButton chinese = (RadioButton) view.findViewById(R.id.settingLanguageRadioButtonChinese);
                final RadioButton english = (RadioButton) view.findViewById(R.id.settingLanguageRadioButtonEnglish);

                String language = mSettings.getString(STKNXControllerConstant.APP_APPEARANCE_LANGUAGE, Locale.US.toString());
                if(language.equals(Locale.SIMPLIFIED_CHINESE.toString())) {
                    chinese.setChecked(true);
                    english.setChecked(false);
                } else {
                    chinese.setChecked(false);
                    english.setChecked(true);
                }

                chinese.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if(isChecked) {
                            english.setChecked(false);
                        }
                    }
                });

                english.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if(isChecked) {
                            chinese.setChecked(false);
                        }
                    }
                });

                new PromptDialog.Builder(SettingsActivity.this)
                        .setTitle(SettingsActivity.this.getResources().getString(R.string.setting_language))
                        .setIcon(R.mipmap.launcher)
                        .setViewStyle(PromptDialog.VIEW_STYLE_NORMAL)
                        .setView(view)
                        .setButton1(SettingsActivity.this.getResources().getString(R.string.cancel), new PromptDialog.OnClickListener() {

                            @Override
                            public void onClick(Dialog dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setButton2(SettingsActivity.this.getResources().getString(R.string.save), new PromptDialog.OnClickListener() {

                            @Override
                            public void onClick(Dialog dialog, int which) {
                                SharedPreferences.Editor editor = mSettings.edit();
                                if(chinese.isChecked()) {
                                    Resources resources = SettingsActivity.this.getResources();//获得res资源对象
                                    Configuration config = resources.getConfiguration();//获得设置对象
                                    DisplayMetrics dm = resources .getDisplayMetrics();//获得屏幕参数：主要是分辨率，像素等。
                                    config.locale = Locale.SIMPLIFIED_CHINESE; //简体中文
                                    resources.updateConfiguration(config, dm);

                                    editor.putString(STKNXControllerConstant.APP_APPEARANCE_LANGUAGE, Locale.SIMPLIFIED_CHINESE.toString());
                                    STKNXControllerApp.getInstance().setLanguage(Locale.SIMPLIFIED_CHINESE.toString());
                                } else {
                                    Resources resources = SettingsActivity.this.getResources();//获得res资源对象
                                    Configuration config = resources.getConfiguration();//获得设置对象
                                    DisplayMetrics dm = resources .getDisplayMetrics();//获得屏幕参数：主要是分辨率，像素等。
                                    config.locale = Locale.US; //简体中文
                                    resources.updateConfiguration(config, dm);

                                    editor.putString(STKNXControllerConstant.APP_APPEARANCE_LANGUAGE, Locale.US.toString());
                                    STKNXControllerApp.getInstance().setLanguage(Locale.US.toString());
                                }
                                editor.apply();
                                dialog.dismiss();

                                SettingsActivity.this.startService(new Intent(SettingsActivity.this, RestartService.class));
                                ((Activity) SettingsActivity.this).finish();
                            }
                        })
                        .show();
            }

        });

        this.mSBReboot.setOnSettingButtonClickListener(new SettingButton.onSettingButtonClickListener() {

            @Override
            public void onSettingButtonClick() { // 定时重启
                final View view = LayoutInflater.from(SettingsActivity.this).inflate(R.layout.reboot_layout, null);
                final TextView txtRebootText = (TextView) view.findViewById(R.id.rebootlayout_textview_reboottext);
                final CheckBox cbRebootEnable = (CheckBox) view.findViewById(R.id.rebootlayout_checkBox_rebootenable);
                final TimePicker tpRebootTime = (TimePicker) view.findViewById(R.id.rebootlayout_timepicker_reboottime);
                tpRebootTime.setIs24HourView(true);

                boolean isEnabled = STKNXControllerApp.getInstance().getAutoRebootFlag();
                int hour = STKNXControllerApp.getInstance().getHourOfRebooting();
                int minute = STKNXControllerApp.getInstance().getMinuteOfReboot();

                String text = String.format("%s%02d%s%02d%s",
                        SettingsActivity.this.getResources().getString(R.string.everyday), hour,
                        SettingsActivity.this.getResources().getString(R.string.hour), minute,
                        SettingsActivity.this.getResources().getString(R.string.minute));
                txtRebootText.setText(text);
                cbRebootEnable.setChecked(isEnabled);
                tpRebootTime.setCurrentHour(hour);
                tpRebootTime.setCurrentMinute(minute);
                tpRebootTime.setEnabled(isEnabled);

                cbRebootEnable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        tpRebootTime.setEnabled(isChecked);
                    }

                });

                tpRebootTime.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {

                    @Override
                    public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                        String text = String.format("%s%02d%s%02d%s",
                                SettingsActivity.this.getResources().getString(R.string.everyday), hourOfDay,
                                SettingsActivity.this.getResources().getString(R.string.hour), minute,
                                SettingsActivity.this.getResources().getString(R.string.minute));
                        txtRebootText.setText(text);
                    }

                });

                new PromptDialog.Builder(SettingsActivity.this)
                        .setTitle(SettingsActivity.this.getResources().getString(R.string.setting_Reboot))
                        .setIcon(R.mipmap.launcher)
                        .setViewStyle(PromptDialog.VIEW_STYLE_NORMAL)
                        .setView(view)
                        .setButton1(SettingsActivity.this.getResources().getString(R.string.cancel), new PromptDialog.OnClickListener() {

                            @Override
                            public void onClick(Dialog dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setButton2(SettingsActivity.this.getResources().getString(R.string.save), new PromptDialog.OnClickListener() {

                            @Override
                            public void onClick(Dialog dialog, int which) {
                                boolean isChecked = cbRebootEnable.isChecked();
                                int hour = tpRebootTime.getCurrentHour();
                                int minute = tpRebootTime.getCurrentMinute();

                                STKNXControllerApp.getInstance().setAutoRebootFlag(isChecked);
                                STKNXControllerApp.getInstance().setHourOfReboot(hour);
                                STKNXControllerApp.getInstance().setMinuteOfReboot(minute);

                                SharedPreferences.Editor editor = mSettings.edit();
                                editor.putBoolean(STKNXControllerConstant.SYSTEM_REBOOT_FLAG, isChecked);
                                editor.putInt(STKNXControllerConstant.SYSTEM_REBOOT_HOUR, hour);
                                editor.putInt(STKNXControllerConstant.SYSTEM_REBOOT_MINUTE, minute);
                                editor.apply();
                                dialog.dismiss();
                            }
                        })
                        .show();
            }

        });

        this.mSBChangePassword.setOnSettingButtonClickListener(new SettingButton.onSettingButtonClickListener() {

            @Override
            public void onSettingButtonClick() { // 更改密码
                final View view = LayoutInflater.from(SettingsActivity.this).inflate(R.layout.changepassword_layout, null);
                final EditText etOriginal = (EditText) view.findViewById(R.id.changepasswordlayout_edittext_originalpassword);
                final EditText etNew = (EditText) view.findViewById(R.id.changepasswordlayout_edittext_newpassword);
                final EditText etConfirm = (EditText) view.findViewById(R.id.changepasswordlayout_edittext_confirmnewpassword);

                new PromptDialog.Builder(SettingsActivity.this)
                        .setTitle(SettingsActivity.this.getResources().getString(R.string.change_password))
                        .setIcon(R.mipmap.launcher)
                        .setViewStyle(PromptDialog.VIEW_STYLE_NORMAL)
                        .setView(view)
                        .setButton1(SettingsActivity.this.getResources().getString(R.string.cancel), new PromptDialog.OnClickListener() {

                            @Override
                            public void onClick(Dialog dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setButton2(SettingsActivity.this.getResources().getString(R.string.modify), new PromptDialog.OnClickListener() {

                            @Override
                            public void onClick(Dialog dialog, int which) {
                                String pw = mSettings.getString(STKNXControllerConstant.SYSTEM_SETTING_PASSWORD,
                                        STKNXControllerConstant.SYSTEM_SETTING_PASSWORD_VALUE);
                                if(etOriginal.getText().toString().trim().equals(pw)){
                                    String newpw = etNew.getText().toString().trim();
                                    if(etConfirm.getText().toString().trim().equals(newpw)) {
                                        SharedPreferences.Editor editor = mSettings.edit();
                                        editor.putString(STKNXControllerConstant.SYSTEM_SETTING_PASSWORD, newpw);
                                        editor.apply();
                                        dialog.dismiss();
                                    } else {
                                        Toast.makeText(SettingsActivity.this, SettingsActivity.this.getResources().getString(R.string.newpassword_not_match),
                                                Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(SettingsActivity.this, SettingsActivity.this.getResources().getString(R.string.originalpassword_not_right),
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .show();
            }

        });

        this.mSBUpgradeSoftware.setOnSettingButtonClickListener(new SettingButton.onSettingButtonClickListener() {
            @Override
            public void onSettingButtonClick() { // 升级软件
            upgradeSoftware();
            }
        });

        this.mSBUpgradeProject.setOnSettingButtonClickListener(new SettingButton.onSettingButtonClickListener() {
            @Override
            public void onSettingButtonClick() { // 升级工程文件
            upgradeProject();
            }
        });

        this.mSBSystemStatus.setOnSettingButtonClickListener(new SettingButton.onSettingButtonClickListener(){

            @Override
            public void onSettingButtonClick(){ // 系统状态
                final View view = LayoutInflater.from(SettingsActivity.this).inflate(R.layout.system_status, null);

                final CheckBox cbDisplaySystemTime = (CheckBox) view.findViewById(R.id.systemStatusCheckBoxDisplaySystemTime);
                boolean displaySystemTime = STKNXControllerApp.getInstance().getDisplayTimeFlag();
                cbDisplaySystemTime.setChecked(displaySystemTime);

                final TextView txtLoacalIP = (TextView) view.findViewById(R.id.systemStatusTextViewLocalIP);
                txtLoacalIP.setText(NetWorkUtil.getIpAddress(SettingsActivity.this));

                final TextView txtSoftware = (TextView) view.findViewById(R.id.systemStatusTextViewSoftwareVersion);
                txtSoftware.setText(SystemUtil.getVersion(SettingsActivity.this));

                final CheckBox cbRecordLastInterface = (CheckBox) view.findViewById(R.id.systemStatusCheckBoxRecordLastInterface);
                boolean bRecordLastInterface = STKNXControllerApp.getInstance().getRememberLastInterface();
                cbRecordLastInterface.setChecked(bRecordLastInterface);

                new PromptDialog.Builder(SettingsActivity.this)
                        .setTitle(SettingsActivity.this.getResources().getString(R.string.system_status))
                        .setIcon(R.mipmap.launcher)
                        .setViewStyle(PromptDialog.VIEW_STYLE_NORMAL)
                        .setView(view)
                        .setButton1(SettingsActivity.this.getResources().getString(R.string.cancel), new PromptDialog.OnClickListener() {

                            @Override
                            public void onClick(Dialog dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setButton2(SettingsActivity.this.getResources().getString(R.string.save), new PromptDialog.OnClickListener() {

                            @Override
                            public void onClick(Dialog dialog, int which) {
//                                RoomTilesListActivity roomTiles = (RoomTilesListActivity)mContext;
                                boolean display = cbDisplaySystemTime.isChecked();
                                boolean remember = cbRecordLastInterface.isChecked();
//                                if(display) {
//                                    roomTiles.displaySystemTime();
//                                } else {
//                                    roomTiles.undisplaySystemTime();
//                                }

                                STKNXControllerApp.getInstance().setDisplayTimeFlag(display);
                                STKNXControllerApp.getInstance().setRememberLastInterface(remember);

                                SharedPreferences.Editor editor = mSettings.edit();
                                editor.putBoolean(STKNXControllerConstant.DISPLAY_SYSTEM_TIME_FLAG, display);
                                editor.putBoolean(STKNXControllerConstant.REMEMBER_LAST_INTERFACE, remember);
                                editor.apply();
                                dialog.dismiss();
                            }
                        })
                        .show();
            }
        });

        initView();
    }

    @Override
    public void finish() {
        super.finish();

        overridePendingTransition(R.anim.scale_righttop_in, R.anim.scale_leftbottom_out);
    }

    private void initView() {
        SettingViewItemData mItemViewData = new SettingViewItemData();
        SettingData mItemData = new SettingData();
        mItemData.setTitle(getResources().getString(R.string.setting_knx_gateway));
        mItemViewData.setData(mItemData);
        mItemViewData.setItemView(new ImageItemView(this));
        this.mSBKNXGateway.setAdapter(mItemViewData);

        mItemViewData = new SettingViewItemData();
        mItemData = new SettingData();
        mItemData.setTitle(getResources().getString(R.string.setting_physical_address));
        mItemViewData.setData(mItemData);
        mItemViewData.setItemView(new ImageItemView(this));
        this.mSBPhysicalAddress.setAdapter(mItemViewData);

        mItemViewData = new SettingViewItemData();
        mItemData = new SettingData();
        mItemData.setTitle(getResources().getString(R.string.setting_back_light_time));
        mItemViewData.setData(mItemData);
        mItemViewData.setItemView(new ImageItemView(this));
        this.mSBScreenOff.setAdapter(mItemViewData);

        mItemViewData = new SettingViewItemData();
        mItemData = new SettingData();
        mItemData.setTitle(getResources().getString(R.string.setting_language));
        mItemViewData.setData(mItemData);
        mItemViewData.setItemView(new ImageItemView(this));
        this.mSBLanguage.setAdapter(mItemViewData);

        mItemViewData = new SettingViewItemData();
        mItemData = new SettingData();
        mItemData.setTitle(getResources().getString(R.string.setting_Reboot));
        mItemViewData.setData(mItemData);
        mItemViewData.setItemView(new ImageItemView(this));
        this.mSBReboot.setAdapter(mItemViewData);

        mItemViewData = new SettingViewItemData();
        mItemData = new SettingData();
        mItemData.setTitle(getResources().getString(R.string.change_password));
        mItemViewData.setData(mItemData);
        mItemViewData.setItemView(new ImageItemView(this));
        this.mSBChangePassword.setAdapter(mItemViewData);

        mItemViewData = new SettingViewItemData();
        mItemData = new SettingData();
        mItemData.setTitle(getResources().getString(R.string.upgrade_software));
        mItemViewData.setData(mItemData);
        mItemViewData.setItemView(new ImageItemView(this));
        this.mSBUpgradeSoftware.setAdapter(mItemViewData);

        mItemViewData = new SettingViewItemData();
        mItemData = new SettingData();
        mItemData.setTitle(getResources().getString(R.string.upgrade_project));
        mItemViewData.setData(mItemData);
        mItemViewData.setItemView(new ImageItemView(this));
        this.mSBUpgradeProject.setAdapter(mItemViewData);

        mItemViewData = new SettingViewItemData();
        mItemData = new SettingData();
        mItemData.setTitle(getResources().getString(R.string.system_status));
        mItemViewData.setData(mItemData);
        mItemViewData.setItemView(new ImageItemView(this));
        this.mSBSystemStatus.setAdapter(mItemViewData);
    }
}
