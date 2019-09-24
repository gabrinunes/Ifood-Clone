package gabrielcunha.cursoandroidyoutubeapp.ifood.activity.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import gabrielcunha.cursoandroidyoutubeapp.ifood.R;
import gabrielcunha.cursoandroidyoutubeapp.ifood.activity.helper.ConfiguracaoFirebase;
import gabrielcunha.cursoandroidyoutubeapp.ifood.activity.helper.UsuarioFirebase;
import gabrielcunha.cursoandroidyoutubeapp.ifood.activity.model.Empresa;
import gabrielcunha.cursoandroidyoutubeapp.ifood.activity.model.Produto;

public class NovoProdutoEmpresaActivity extends AppCompatActivity {

    private EditText nomeProduto, descricaoProduto, precoProduto;
    private Button buttonProdSalvar;
    private String idUsuarioLogado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_novo_produto_empresa);

        //Configurações Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Novo Produto");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        idUsuarioLogado = UsuarioFirebase.getIdUusuario();
        incializarComponentes();

        buttonProdSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nome = nomeProduto.getText().toString();
                String descricao = descricaoProduto.getText().toString();
                String preco = precoProduto.getText().toString();
                if (!nome.isEmpty()) {
                    if (!descricao.isEmpty()) {
                        if (!preco.isEmpty()) {
                            Produto produto = new Produto();
                            produto.setIdUsuario(idUsuarioLogado);
                            produto.setNome(nome);
                            produto.setDescricao(descricao);
                            produto.setPreco(Double.parseDouble(preco));
                            produto.salvar();
                            finish();
                            exibirMensagem("Produto salvo com sucesso");
                        } else {
                            exibirMensagem("Digite uma descrição ");
                        }
                    } else {
                        exibirMensagem("Digite um preço ");
                    }
                } else {
                    exibirMensagem("Digite um nome para o produto");
                }


            }

        });
    }

    private void exibirMensagem(String texto) {
        Toast.makeText(this, texto, Toast.LENGTH_SHORT).show();
    }

    private void incializarComponentes() {

        nomeProduto = findViewById(R.id.nomeProduto);
        descricaoProduto = findViewById(R.id.descricaoProdtudo);
        precoProduto = findViewById(R.id.precoProduto);
        buttonProdSalvar = findViewById(R.id.buttonProdSalvar);
    }
}
