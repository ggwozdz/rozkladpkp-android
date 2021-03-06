/*******************************************************************************
 * This file is part of the RozkladPKP project.
 * 
 *     RozkladPKP is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     RozkladPKP is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License 
 *     along with RozkladPKP.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package org.tyszecki.rozkladpkp;

import org.tyszecki.rozkladpkp.R;

import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.PreferenceActivity;
import android.text.method.DigitsKeyListener;
import android.widget.EditText;

public class PreferencesActivity extends PreferenceActivity {
	
	protected void onCreate(Bundle savedInstanceState) {
		setTheme(RozkladPKPApplication.getThemeId());
		super.onCreate(savedInstanceState);
	    
	    // Load the preferences from an XML resource
	    addPreferencesFromResource(R.xml.preferences);
	    
	    EditText myEditText = (EditText) ((EditTextPreference)findPreference("discountValue")).getEditText();
	    myEditText.setKeyListener(DigitsKeyListener.getInstance(false,true));
	}
	
}
