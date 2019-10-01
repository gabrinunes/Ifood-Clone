package gabrielcunha.cursoandroidyoutubeapp.ifood.activity.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;
import gabrielcunha.cursoandroidyoutubeapp.ifood.R;
import gabrielcunha.cursoandroidyoutubeapp.ifood.activity.adapter.AdapterPedido;
import gabrielcunha.cursoandroidyoutubeapp.ifood.activity.helper.ConfiguracaoFirebase;
import gabrielcunha.cursoandroidyoutubeapp.ifood.activity.helper.UsuarioFirebase;
import gabrielcunha.cursoandroidyoutubeapp.ifood.activity.model.Pedido;
import gabrielcunha.cursoandroidyoutubeapp.ifood.activity.model.Produto;

public class PedidosActivity extends AppCompatActivity {

    private RecyclerView recyclerPedidos;
    private AdapterPedido adapterPedido;
    private List<Pedido> pedidos = new ArrayList<>();
    private AlertDialog dialog;
    private DatabaseReference firebaseRef;
    private String idEmpresa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedidos);

        inicializarComponentes();
        firebaseRef = ConfiguracaoFirebase.getFirebase();
        idEmpresa = UsuarioFirebase.getIdUusuario();
        //Configura Adapter
        adapterPedido = new AdapterPedido(pedidos);

        //Configura o RecyclerView

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerPedidos.setHasFixedSize(true);
        recyclerPedidos.setLayoutManager(layoutManager);
        recyclerPedidos.setAdapter(adapterPedido);

        //Configuração Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Pedidos");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recuperarPedidos();


    }

    private void recuperarPedidos() {
        dialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Carregando dados")
                .setCancelable(false)
                .build();
        dialog.show();

        DatabaseReference pedidoRef = firebaseRef
                .child("pedidos")
                .child(idEmpresa);
        Query pedidoPesquisa = pedidoRef.orderByChild("status")
                .equalTo("confirmado");
        pedidoPesquisa.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                pedidos.clear();
                if (dataSnapshot.getValue() != null) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        Pedido pedido = ds.getValue(Pedido.class);
                        pedidos.add(pedido);
                    }
                    adapterPedido.notifyDataSetChanged();
                    dialog.dismiss();
                }
                //if (dataSnapshot.getValue() == null) {
                    //dialog.dismiss();
                    //Toast.makeText(PedidosActivity.this, "Não há pedidos no momento", Toast.LENGTH_SHORT).show();
                }
            //}


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    private void inicializarComponentes() {
        recyclerPedidos = findViewById(R.id.recyclerPedidos);
    }
}
