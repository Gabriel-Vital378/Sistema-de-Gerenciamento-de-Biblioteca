package dominio;

public class Usuario {

    private Long id;
    private String nome;
    private String email;
    private SituacaoUsuario situacao;

    public Usuario(Long id, String nome, String email) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.situacao = SituacaoUsuario.ATIVO;
    }

    public boolean isAtivo() {
        return SituacaoUsuario.ATIVO.equals(situacao);
    }

    public void suspender() {
        this.situacao = SituacaoUsuario.SUSPENSO;
    }

    public void ativar() {
        this.situacao = SituacaoUsuario.ATIVO;
    }

    public Long getId() { return id; }
    public String getNome() { return nome; }
    public String getEmail() { return email; }
    public SituacaoUsuario getSituacao() { return situacao; }

    public void setId(Long id) { this.id = id; }
    public void setNome(String nome) { this.nome = nome; }
    public void setEmail(String email) { this.email = email; }
    public void setSituacao(SituacaoUsuario situacao) { this.situacao = situacao; }

    @Override
    public String toString() {
        return "Usuario{id=" + id + ", nome='" + nome + "', email='" + email +
               "', situacao=" + situacao + "}";
    }
}
