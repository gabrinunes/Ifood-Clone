package gabrielcunha.cursoandroidyoutubeapp.ifood.activity.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;

import gabrielcunha.cursoandroidyoutubeapp.ifood.R;
import gabrielcunha.cursoandroidyoutubeapp.ifood.activity.helper.UsuarioFirebase;
import gabrielcunha.cursoandroidyoutubeapp.ifood.activity.helper.ConfiguracaoFirebase;
import gabrielcunha.cursoandroidyoutubeapp.ifood.activity.model.Empresa;

public class ConfiguracoesEmpresaActivity extends AppCompatActivity {

    private ImageView perfilEmpresa;
    private EditText nomeEmpresa,categoria,tempoEntrega,taxaEntrga;
    private Button buttonSalvar;
    private static final int SELECAO_GALERIA = 200;
    private StorageReference storageReference;
    private DatabaseReference firebaseRef;
    private String idUsuarioLogado;
    private String urlImagemSelecionado ="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracoes_empresa);

        //Configurações Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Configurações");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        incializaComponentes();

        storageReference = ConfiguracaoFirebase.getFirebaseStorage();
        idUsuarioLogado = UsuarioFirebase.getIdUusuario();
        firebaseRef = ConfiguracaoFirebase.getFirebase();

        recuperaDadosEmpresa();


        buttonSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    String nome = nomeEmpresa.getText().toString();
                    String taxa = taxaEntrga.getText().toString();
                    String tempo = tempoEntrega.getText().toString();
                    String cat = categoria.getText().toString();
                    if(!nome.isEmpty()){
                        if(!taxa.isEmpty()){
                            if(!cat.isEmpty()){
                                if(!tempo.isEmpty()){

                                    Empresa empresa = new Empresa();
                                    empresa.setIdUsuario(idUsuarioLogado);
                                    empresa.setNome(nome);
                                    empresa.setPrecoEntrega(Double.parseDouble(taxa));
                                    empresa.setCategoria(cat);
                                    empresa.setTempo(tempo);
                                    empresa.setUrlImagem(urlImagemSelecionado);
                                    empresa.salvar();
                                    finish();

                                }else{
                                    exibirMensagem("Digite um tempo de entrega");
                                }
                            }else{
                                exibirMensagem("Digite uma categoria");
                            }
                        }else{
                            exibirMensagem("Digite um taxa para entrega");
                        }
                    }else{
                        exibirMensagem("Digite um nome para empresa");
                    }


                }

        });



        perfilEmpresa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                        );
                if(i.resolveActivity(getPackageManager())!=null){
                     startActivityForResult(i,SELECAO_GALERIA);
                }
            }
        });
    }

    private void recuperaDadosEmpresa() {

        DatabaseReference empresaRef = firebaseRef
                .child("empresas")
                .child(idUsuarioLogado);
        empresaRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.getValue()!=null){
                    Empresa empresa = dataSnapshot.getValue(Empresa.class);
                    nomeEmpresa.setText(empresa.getNome());
                    categoria.setText(empresa.getCategoria());
                    taxaEntrga.setText(empresa.getPrecoEntrega().toString());
                    tempoEntrega.setText(empresa.getTempo());

                    urlImagemSelecionado = empresa.getUrlImagem();
                    if(urlImagemSelecionado!=""){
                        Picasso.get()
                                .load(urlImagemSelecionado)
                                .into(perfilEmpresa);
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode == RESULT_OK){
            Bitmap imagem = null;

            try{
                switch (requestCode){
                    case SELECAO_GALERIA:
                        Uri localImagem = data.getData();
                        imagem = MediaStore.Images
                                .Media
                                .getBitmap(
                                        getContentResolver(),
                                        localImagem
                                );
                        break;
                }
                if(imagem!=null){
                    perfilEmpresa.setImageBitmap(imagem);

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    imagem.compress(Bitmap.CompressFormat.JPEG,70,baos);
                    byte[] dadosImagem = baos.toByteArray();

                    StorageReference imagemRef = storageReference
                            .child("imagens")
                            .child("empresas")
                            .child(idUsuarioLogado + "jpeg");

                    UploadTask uploadTask = imagemRef.putBytes(dadosImagem);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ConfiguracoesEmpresaActivity.this,
                                    "Erro ao fazer upload da imagem",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            urlImagemSelecionado = taskSnapshot.getDownloadUrl().toString();
                            Toast.makeText(ConfiguracoesEmpresaActivity.this,
                                  "Sucesso ao fazer upload da imagem",
                                  Toast.LENGTH_SHORT).show();
                        }
                    });


                }

            }catch (Exception e){
                e.printStackTrace();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }





    private void exibirMensagem(String texto){
        Toast.makeText(this,texto,Toast.LENGTH_SHORT).show();
    }

    private void incializaComponentes() {

        perfilEmpresa = findViewById(R.id.imagePerfilEmpresa);
        nomeEmpresa = findViewById(R.id.NomeEmpresa);
        categoria = findViewById(R.id.Categoria);
        tempoEntrega = findViewById(R.id.TempoEntrega);
        taxaEntrga = findViewById(R.id.TaxaEntrega);
        buttonSalvar = findViewById(R.id.button);
    }
}
