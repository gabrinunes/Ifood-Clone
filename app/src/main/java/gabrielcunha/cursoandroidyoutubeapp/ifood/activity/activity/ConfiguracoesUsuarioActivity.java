package gabrielcunha.cursoandroidyoutubeapp.ifood.activity.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import gabrielcunha.cursoandroidyoutubeapp.ifood.R;
import gabrielcunha.cursoandroidyoutubeapp.ifood.activity.helper.ConfiguracaoFirebase;
import gabrielcunha.cursoandroidyoutubeapp.ifood.activity.helper.UsuarioFirebase;
import gabrielcunha.cursoandroidyoutubeapp.ifood.activity.model.Usuario;

public class ConfiguracoesUsuarioActivity extends AppCompatActivity {

    private EditText editUsuarioNome,editUsuarioEndereco;
    private Button buttonSalvar;
    private DatabaseReference firebaseRef;
    private String idUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracoes_usuario);

        incializarComponentes();

        firebaseRef = ConfiguracaoFirebase.getFirebase();
        idUsuario = UsuarioFirebase.getIdUusuario();
        recuperaDadosUsuarios();

        //Configurações Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Configurações usuário");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        buttonSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Valida se os campos foram preenchidos
                String nome = editUsuarioNome.getText().toString();
                String endereco = editUsuarioEndereco.getText().toString();
                if(!nome.isEmpty()){
                 if(!endereco.isEmpty()){
                     Usuario usuario = new Usuario();
                     usuario.setNome(nome);
                     usuario.setEndereco(endereco);
                     usuario.setIdUsuario(idUsuario);
                     usuario.salvar();
                     exibirMensagem("Dados atualizados com sucesso");
                     finish();

                 }else {
                     exibirMensagem("Digite um endereço");
                 }
                }else{
                    exibirMensagem("Digite um nome de usuário");
                }
            }
        });

    }

    private void recuperaDadosUsuarios() {
        DatabaseReference usuarioRef = firebaseRef
                .child("usuarios")
                .child(idUsuario);
        usuarioRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue()!=null){
                    Usuario usuario = dataSnapshot.getValue(Usuario.class);
                    editUsuarioNome.setText(usuario.getNome());
                    editUsuarioEndereco.setText(usuario.getEndereco());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void exibirMensagem(String texto){
        Toast.makeText(this,texto,Toast.LENGTH_SHORT).show();
    }

    private void incializarComponentes() {

        editUsuarioNome = findViewById(R.id.editNomeUsuario);
        editUsuarioEndereco = findViewById(R.id.editUsuarioEndereco);
        buttonSalvar = findViewById(R.id.buttonSalvarUser);

    }
}
