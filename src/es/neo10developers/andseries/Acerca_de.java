package es.neo10developers.andseries;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import es.neo10developers.andseries.R;

public class Acerca_de extends Activity{
	private Button btn_volver;
	private TextView lbl_twitter;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acerca_de);
        
        btn_volver = (Button)findViewById(R.id.btn_volver);
        lbl_twitter = (TextView)findViewById(R.id.lbl_twitter);
        
        btn_volver.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				finish();
			}
		});
        
        lbl_twitter.setText("@Neo10Developers");
        
        lbl_twitter.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				abreTwitter();
			}
		});        
	}
	
	private void abreTwitter(){
		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/Neo10Developers"));
		startActivity(intent);
    }
}
