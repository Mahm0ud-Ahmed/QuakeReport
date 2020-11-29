package com.example.android.quakereport;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;


public class SettingActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_activity);

        EarthquakePreferenceFragment preferenceFragment = new EarthquakePreferenceFragment();
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.add(R.id.contiener, preferenceFragment);
        transaction.commit();

    }


    public static class EarthquakePreferenceFragment extends PreferenceFragmentCompat
            implements androidx.preference.Preference.OnPreferenceChangeListener {

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            //عمل inflat لملف ال xml المراض عرضه داخل الأكتفتي
            setPreferencesFromResource(R.xml.settings_main, rootKey);

            //جلب العناصر داخل الأكتفتي وتجهيزها للاستعداد للاستماع للتغييرات الطارئه عليها من قبل المستخدم
            Preference minMagnitude = findPreference(getString(R.string.settings_min_magnitude_key));
            bindPreferenceSummaryToValue(minMagnitude);

            Preference orderBy = findPreference(getString(R.string.settings_order_by_key));
            bindPreferenceSummaryToValue(orderBy);
        }

        @Override
        public boolean onPreferenceChange(androidx.preference.Preference preference, Object newValue) {
            //تخزين القيمة الجديده داخل متغير نصي
            String stringValue = newValue.toString();
            //عمل مقارنة بين العنصر القادم وبين عناصر الأكتفتي
            if (preference instanceof androidx.preference.ListPreference) {
                //اذا كانت المقارنه صحيحه سيتم تخزين العنصر القادم داخل النوع الخاص به
                androidx.preference.ListPreference listPreference =
                        (androidx.preference.ListPreference) preference;
                //تخزين مكان القيمة المرسله
                int prefIndex = listPreference.findIndexOfValue(stringValue);
                if (prefIndex >= 0) {
                    // تخزين القوائم المعروضه بداخل العنصر داخل مصفوفه
                    CharSequence[] labels = listPreference.getEntries();
                    //ارسال التغيير المطلوب للتنفيذ بالمكان المناسب
                    preference.setSummary(labels[prefIndex]);
                }
            } else {
                preference.setSummary(stringValue);
            }
            return true;
        }

        private void bindPreferenceSummaryToValue(androidx.preference.Preference preference) {
            //مستمع للتغيير الطارئ على القيم
            preference.setOnPreferenceChangeListener(this);
            //جلب ملف الكتابه من داخل التطبيق
            SharedPreferences preferences =
                    androidx.preference.PreferenceManager
                    .getDefaultSharedPreferences(preference.getContext());
            //تخزين القيمة الجديده التي تم استماعها من المستمع وتمرير قيمه افتراضيه اذا لم يعثر القيمة المراده
            String preferenceString = preferences.getString(preference.getKey(), "");
            // تمرير العنصر الذي طرأ التغيير عليه وتمرير المفتاح الذي يحمل بداخله القيمه الجديده لدالة التنفيذ
            onPreferenceChange(preference, preferenceString);
        }



    }
}
