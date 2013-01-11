package es.neo10developers.andseries;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import es.neo10developers.andseries.R;

public class Opciones extends PreferenceActivity{
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.opciones);
	}
}