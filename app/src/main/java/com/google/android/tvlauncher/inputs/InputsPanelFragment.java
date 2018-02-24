package com.google.android.tvlauncher.inputs;

import android.os.Bundle;
import android.support.v17.preference.LeanbackPreferenceFragment;
import android.support.v7.preference.Preference;
import android.support.v7.preference.Preference.OnPreferenceClickListener;
import android.support.v7.preference.PreferenceScreen;

import com.google.android.tvlauncher.R;
import com.google.android.tvlauncher.analytics.FragmentEventLogger;
import com.google.android.tvlauncher.analytics.LogEvents;
import com.google.android.tvlauncher.analytics.UserActionEvent;
import com.google.android.tvlauncher.inputs.InputsManager.Configuration;
import com.google.android.tvlauncher.util.Partner;

public class InputsPanelFragment extends LeanbackPreferenceFragment implements InputsManager.OnChangedListener, OnPreferenceClickListener {
    private FragmentEventLogger mEventLogger;
    private InputsManager mInputsManager;

    public static InputsPanelFragment newInstance() {
        return new InputsPanelFragment();
    }

    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        PreferenceScreen screen = getPreferenceManager().createPreferenceScreen(getPreferenceManager().getContext());
        screen.setTitle((int) R.string.input_panel_title);
        setPreferences(screen);
        setPreferenceScreen(screen);
        this.mInputsManager.setOnChangedListener(this);
        this.mEventLogger = new FragmentEventLogger(this);
    }

    private void setPreferences(PreferenceScreen screen) {
        screen.removeAll();
        Partner partner = Partner.get(getActivity().getApplicationContext()); // getContext()
        // getContext()
        this.mInputsManager = new InputsManager(getActivity().getApplicationContext(), new Configuration(partner.showPhysicalTunersSeparately(), partner.disableDisconnectedInputs(), partner.getStateIconFromTVInput()));
        for (int i = 0; i < this.mInputsManager.getItemCount(); i++) {
            InputPreference preference = new InputPreference(getPreferenceManager().getContext(), this.mInputsManager.getInputState(i));
            preference.setIcon(this.mInputsManager.getItemDrawable(i));
            preference.setTitle(this.mInputsManager.getLabel(i));
            preference.setKey(Integer.toString(i));
            preference.setOnPreferenceClickListener(this);
            screen.addPreference(preference);
        }
    }

    public boolean onPreferenceClick(Preference preference) {
        String key = preference.getKey();
        if (key == null) {
            return super.onPreferenceTreeClick(preference);
        }
        int position = Integer.valueOf(key).intValue();
        this.mInputsManager.launchInputActivity(position);
        int inputType = this.mInputsManager.getInputType(position);
        if (inputType != -1) {
            this.mEventLogger.log(new UserActionEvent(LogEvents.SELECT_INPUT).putParameter(LogEvents.PARAMETER_INPUT_TYPE, inputType));
        }
        return true;
    }

    public void onDestroy() {
        super.onDestroy();
        this.mInputsManager.setOnChangedListener(null);
    }

    public void onInputsChanged() {
        setPreferenceScreen(getPreferenceScreen());
    }
}
