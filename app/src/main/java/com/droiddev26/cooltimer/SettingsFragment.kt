package com.droiddev26.cooltimer

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.preference.CheckBoxPreference
import androidx.preference.EditTextPreference
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat

class SettingsFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener,
    Preference.OnPreferenceChangeListener {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.root_preferences)

        val sharedPreferences = preferenceScreen.sharedPreferences
        val preferenceScreen = getPreferenceScreen()
        var count = preferenceScreen.preferenceCount

        for ( i in 0..count-1) {
            val preference = preferenceScreen.getPreference(i)
            if (!(preference is CheckBoxPreference)){
                val value = sharedPreferences!!.getString(preference.key, "")
                if (value != null) {
                    setPreferenceLabel(preference, value)
                }
            }
        }
        val preference: Preference? = findPreference("default_interval")
        preference!!.setOnPreferenceChangeListener(this)
    }


    fun setPreferenceLabel(preference: Preference, value: String) {
        if (preference is ListPreference){
            val listPreference = preference
            val index = listPreference.findIndexOfValue(value)
            if (index >= 0){
                listPreference.setSummary(listPreference.entries[index])
            }
        }else if (preference is EditTextPreference) {
            preference.setSummary(value)
        }
    }

    override fun onSharedPreferenceChanged(p0: SharedPreferences?, p1: String?) {
        val preference: Preference? = findPreference(p1!!)
        if (!(preference is CheckBoxPreference)) {
            val value = p0!!.getString(preference!!.key, "")
            if (value != null) {
                setPreferenceLabel(preference, value)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferenceScreen.sharedPreferences!!.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        preferenceScreen.sharedPreferences!!.unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onPreferenceChange(preference: Preference, newValue: Any?): Boolean {
        val toast = Toast.makeText(context, "Please enter integer number", Toast.LENGTH_LONG)
        if (preference.key.equals("default_interval")){
            val defIntervalStr = newValue.toString()
            try {
                val defaultInterval = defIntervalStr.toInt()
            } catch (nfe: java.lang.NumberFormatException){
                toast.show()
                return false
            }
        }
        return true
    }
}