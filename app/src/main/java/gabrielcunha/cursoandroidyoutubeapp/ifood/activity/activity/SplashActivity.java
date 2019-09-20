package gabrielcunha.cursoandroidyoutubeapp.ifood.activity.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import gabrielcunha.cursoandroidyoutubeapp.ifood.R;

public class SplashActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
              abrirAutenticao();
            }
        }, 3000);
    }

    private void abrirAutenticao(){
        Intent i = new Intent(SplashActivity.this,AutenticacaoActivity.class);
        startActivity(i);
        finish();
    }
}
