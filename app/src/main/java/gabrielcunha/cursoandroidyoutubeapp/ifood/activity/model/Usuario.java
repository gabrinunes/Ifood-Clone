package gabrielcunha.cursoandroidyoutubeapp.ifood.activity.model;

import com.google.firebase.database.DatabaseReference;

import gabrielcunha.cursoandroidyoutubeapp.ifood.activity.helper.ConfiguracaoFirebase;

public class Usuario {
    private String nome;
    private String endereco;
    private String idUsuario;

    public Usuario() {
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public void salvar() {

        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();
        DatabaseReference usuarioRef = firebaseRef
                .child("usuarios")
                .child(getIdUsuario());
        usuarioRef.setValue(this);


    }
}
