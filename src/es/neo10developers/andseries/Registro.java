package es.neo10developers.andseries;

import java.util.regex.Pattern;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Registro extends Activity{
	private Button btn_volver;
	private Button btn_registrar;
	private EditText txt_usuario;
    private EditText txt_pass;
    private EditText txt_pass2;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registro);
        
        btn_volver = (Button) findViewById(R.id.btn_volver);
        btn_registrar = (Button) findViewById(R.id.btn_registrar);
        txt_usuario = (EditText) findViewById (R.id.txt_usuario);
        txt_pass = (EditText) findViewById (R.id.txt_pass);
        txt_pass2 = (EditText) findViewById (R.id.txt_pass2);
        
        btn_volver.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				finish();
			}
		});
        
        btn_registrar.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				if(Utilidades.laRedEstaDisponible(Registro.this)){
					if(comprobacionOk()){
						String usuario = txt_usuario.getText().toString();
						String pass = txt_pass.getText().toString();
						
						String mensaje = Sincronizador.registrar(usuario, pass);
						
						if(mensaje.equals("Mensaje enviado.")){							
							Toast.makeText(Registro.this, "Se ha registrado correctamente. Active su cuenta mediante el correo que se le ha enviado. Gracias.", Toast.LENGTH_SHORT).show();
							finish();
						}else{
							Toast.makeText(Registro.this, mensaje, Toast.LENGTH_SHORT).show();
						}
					}
				}else{
					Toast.makeText(Registro.this, getString(R.string.sin_conexion), Toast.LENGTH_SHORT).show();
				}				
			}
		});
	}
	
	private boolean comprobacionOk(){
    	boolean ok = false;
		
    	String email = txt_usuario.getText().toString();
    	String pass = txt_pass.getText().toString();
    	String pass2 = txt_pass2.getText().toString();
    	
		if(email.length() > 0 && pass.length() > 0 && pass2.length() > 0){ 							
			if (checkEmail(email)){
				if(pass.equals(pass2)){
					if(pass.length() < 6){
						Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres.", Toast.LENGTH_SHORT).show();
						ok = false;
					}else{
						ok = true;
					}					
				}else{
					Toast.makeText(this, "La contraseña no coincide.", Toast.LENGTH_SHORT).show();
					ok = false;
				}
			}else{
				Toast.makeText(this, "La dirección de correo no es correcta.", Toast.LENGTH_SHORT).show();
				ok = false;
			}
		}else{
    		Toast.makeText(this, "Por favor, rellene todos los datos de acceso.", Toast.LENGTH_SHORT).show();
    		ok = false;
    	}
		
		return ok;
    }
	
	
	private boolean checkEmail(String email) {
		Pattern EMAIL_ADDRESS_PATTERN = Pattern.compile(
		          "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
		          "\\@" +
		          "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
		          "(" +
		          "\\." +
		          "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
		          ")+"
		      );
		
		return EMAIL_ADDRESS_PATTERN.matcher(email).matches();
	}
}
