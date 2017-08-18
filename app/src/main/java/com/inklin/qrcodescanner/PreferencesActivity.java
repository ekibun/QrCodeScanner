package com.inklin.qrcodescanner;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.util.Log;

public class PreferencesActivity extends Activity {
    public static class PreferencesFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
        public SharedPreferences sp;

        @Override
        public void onCreate(Bundle saveInstanceState) {
            super.onCreate(saveInstanceState);
            // 加载xml资源文件
            addPreferencesFromResource(R.xml.preferences);
            sp = getPreferenceManager().getSharedPreferences();
            refreshSummary();
        }

        @Override
        public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference){
            Log.d("onPreferenceTreeClick",preference.getKey());
            if(preference.getKey().equals("version_code")){
                ((PreferencesActivity)getActivity()).showInfo();
                return true;
            }
            return false;
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if(key.equals("hide_launcher")){
                PackageManager pkg=getActivity().getPackageManager();
                if(sharedPreferences.getBoolean(key, false)){
                    pkg.setComponentEnabledSetting(new ComponentName(getActivity(), SplashActivity.class),
                            PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
                }else{
                    pkg.setComponentEnabledSetting(new ComponentName(getActivity(), SplashActivity.class),
                            PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
                }
            }else if(key.equals("hide_share")){
                PackageManager pkg=getActivity().getPackageManager();
                if(sharedPreferences.getBoolean(key, false)){
                    pkg.setComponentEnabledSetting(new ComponentName(getActivity(), ShareActivity.class),
                            PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
                }else{
                    pkg.setComponentEnabledSetting(new ComponentName(getActivity(), ShareActivity.class),
                            PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
                }
            }
            refreshSummary();
        }

        @Override
        public void onResume() {
            super.onResume();

            refreshSummary();
            getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onPause() {
            getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
            super.onPause();
        }

        public void refreshSummary(){

            ListPreference listPref = (ListPreference) findPreference("action_click");
            listPref.setSummary(listPref.getEntry());

            listPref = (ListPreference) findPreference("action_longclick");
            listPref.setSummary(listPref.getEntry());

            String versionName="";
            int versionCode=0;
            try {
                PackageInfo pi=getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
                versionName = pi.versionName;
                versionCode = pi.versionCode;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            Preference aboutPref = findPreference("version_code");
            aboutPref.setSummary(versionName + "(" + versionCode +")");
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);
    }

    public void showInfo(){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.about_dialog_title));
        builder.setMessage(getString(R.string.about_dialog_message));
        builder.setPositiveButton(getString(R.string.about_dialog_button), null);
        builder.show();
    }
}
