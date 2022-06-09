package com.paypal.android.ui.settings

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.paypal.android.R

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
    }
}
