package gabrielcunha.cursoandroidyoutubeapp.ifood.activity.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import gabrielcunha.cursoandroidyoutubeapp.ifood.R;
import gabrielcunha.cursoandroidyoutubeapp.ifood.activity.model.ConfiguracaoFirebase;

public class HomeActivity extends AppCompatActivity {

    private FirebaseAuth autenticacao;
    private MaterialSearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        inicializarComponentes();

        //Configurações Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Ifood");
        setSupportActionBar(toolbar);

    }

    private void inicializarComponentes() {
        searchView = findViewById(R.id.materialSearchView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_usuario,menu);

        //Configurar botao de pesquisa
        MenuItem item = menu.findItem(R.id.menuPesquisa);
        searchView.setMenuItem(item);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.menuSair:
                deslogarUsuario();
                break;
            case R.id.menuConfiguracoes:
                abrirConfiguracoes();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    private void abrirConfiguracoes() {
        startActivity(new Intent(HomeActivity.this,ConfiguracoesUsuarioActivity.class));
    }

    private void deslogarUsuario() {
        try{
            autenticacao.signOut();
            finish();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
