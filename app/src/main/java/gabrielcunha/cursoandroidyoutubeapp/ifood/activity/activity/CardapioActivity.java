package gabrielcunha.cursoandroidyoutubeapp.ifood.activity.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;
import gabrielcunha.cursoandroidyoutubeapp.ifood.R;
import gabrielcunha.cursoandroidyoutubeapp.ifood.activity.adapter.AdapterProduto;
import gabrielcunha.cursoandroidyoutubeapp.ifood.activity.helper.ConfiguracaoFirebase;
import gabrielcunha.cursoandroidyoutubeapp.ifood.activity.helper.UsuarioFirebase;
import gabrielcunha.cursoandroidyoutubeapp.ifood.activity.listener.RecyclerItemClickListener;
import gabrielcunha.cursoandroidyoutubeapp.ifood.activity.model.Empresa;
import gabrielcunha.cursoandroidyoutubeapp.ifood.activity.model.ItemPedido;
import gabrielcunha.cursoandroidyoutubeapp.ifood.activity.model.Produto;
import gabrielcunha.cursoandroidyoutubeapp.ifood.activity.model.Usuario;

public class CardapioActivity extends AppCompatActivity {

    private RecyclerView recyclerCardapio;
    private ImageView imageEmpresaCardapio;
    private TextView textNomeEmpresaCardapio, textCarrinhoQtd, textCarrinhoTotal;
    private Empresa empresaSelecionada;
    private AdapterProduto adapterProduto;
    private List<Produto> produtos = new ArrayList<>();
    private List<ItemPedido> itensCarrinho = new ArrayList<>();
    private DatabaseReference firebaseRef;
    private AlertDialog dialog;
    private Usuario usuario;
    private String idUsuarioLogado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cardapio);

        firebaseRef = ConfiguracaoFirebase.getFirebase();
        idUsuarioLogado = UsuarioFirebase.getIdUusuario();
        inicializarComponentes();

        //Configura Adapter
        adapterProduto = new AdapterProduto(produtos, this);

        //Configurar RecyclerView
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerCardapio.setLayoutManager(layoutManager);
        recyclerCardapio.setHasFixedSize(true);
        recyclerCardapio.setAdapter(adapterProduto);

        //Configurar evento de clique

        recyclerCardapio.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        this,
                        recyclerCardapio,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                confirmarQuantidade(position);
                            }

                            @Override
                            public void onLongItemClick(View view, int position) {

                            }

                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                            }
                        }
                )
        );

        recuperarDadosUsuario();


        //Recuperar empresa selecionada
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            empresaSelecionada = (Empresa) bundle.getSerializable("empresa");

            textNomeEmpresaCardapio.setText(empresaSelecionada.getNome());
            String idEmpresa = empresaSelecionada.getIdUsuario();
            Log.i("get", "get:idUsuario " + idEmpresa);
            //Recuperar ProdutoCardapio
            recuperaProdutoCardapio(idEmpresa);

            String url = empresaSelecionada.getUrlImagem();
            Picasso.get().load(url).into(imageEmpresaCardapio);

        }


        //Configurações Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Cardápio");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    private void confirmarQuantidade(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Quantidade");
        builder.setMessage("Digite a quantidade");

        final EditText editQuantidade = new EditText(this);
        editQuantidade.setText("1");

        builder.setView(editQuantidade);

        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                String quantidade = editQuantidade.getText().toString();

                Produto produtoSelecionado = produtos.get(position);
                ItemPedido itemPedido = new ItemPedido();
                itemPedido.setIdProduto(produtoSelecionado.getIdProduto());
                itemPedido.setNomeProduto(produtoSelecionado.getNome());
                itemPedido.setPreco(produtoSelecionado.getPreco());
                itemPedido.setQuantidade(Integer.parseInt(quantidade));
                for (ItemPedido item : itensCarrinho) {

                    if (!item.getIdProduto().equals(produtoSelecionado.getIdProduto())) {
                        itensCarrinho.add(itemPedido);
                    }
                }


            }
        });

        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    private void recuperarDadosUsuario() {

        dialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Carregando dados")
                .setCancelable(false)
                .build();
        dialog.show();

        DatabaseReference usuariosRef = firebaseRef
                .child("usuarios")
                .child(idUsuarioLogado);
        usuariosRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    usuario = dataSnapshot.getValue(Usuario.class);
                }
                recuperarPedido();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    private void recuperarPedido() {

        dialog.dismiss();
    }

    private void recuperaProdutoCardapio(String id) {

        DatabaseReference produtoRef = firebaseRef
                .child("produtos")
                .child(id);

        produtoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                produtos.clear();

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    produtos.add(ds.getValue(Produto.class));
                }
                adapterProduto.notifyDataSetChanged();


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void inicializarComponentes() {
        recyclerCardapio = findViewById(R.id.recyclerCardapio);
        imageEmpresaCardapio = findViewById(R.id.imageEmpresaCardapio);
        textNomeEmpresaCardapio = findViewById(R.id.textNomeEmpresaCardapio);
        textCarrinhoQtd = findViewById(R.id.textCarrinhoQtd);
        textCarrinhoTotal = findViewById(R.id.textCarrinhoTotal);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_cardapio, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menuPedido:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
