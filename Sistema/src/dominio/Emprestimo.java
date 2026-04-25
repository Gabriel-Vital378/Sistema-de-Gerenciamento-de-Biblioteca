package dominio;

import java.time.LocalDate;

public class Emprestimo {

    private Long id;
    private Livro livro;
    private Usuario usuario;
    private LocalDate dataRetirada;
    private LocalDate dataPrevistaDevolucao;
    private SituacaoEmprestimo situacao;

    public Emprestimo(Long id, Livro livro, Usuario usuario, LocalDate dataRetirada, LocalDate dataPrevistaDevolucao) {
        this.id = id;
        this.livro = livro;
        this.usuario = usuario;
        this.dataRetirada = dataRetirada;
        this.dataPrevistaDevolucao = dataPrevistaDevolucao;
        this.situacao = SituacaoEmprestimo.ATIVO;
    }

    public boolean estaAtrasado() {
        return SituacaoEmprestimo.ATIVO.equals(situacao) && LocalDate.now().isAfter(dataPrevistaDevolucao);
    }

    public Long getId() { return id; }
    public Livro getLivro() { return livro; }
    public Usuario getUsuario() { return usuario; }
    public LocalDate getDataRetirada() { return dataRetirada; }
    public LocalDate getDataPrevistaDevolucao() { return dataPrevistaDevolucao; }
    public SituacaoEmprestimo getSituacao() { return situacao; }

    public void setId(Long id) { this.id = id; }
    public void setLivro(Livro livro) { this.livro = livro; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
    public void setDataRetirada(LocalDate dataRetirada) { this.dataRetirada = dataRetirada; }
    public void setDataPrevistaDevolucao(LocalDate dataPrevistaDevolucao) { this.dataPrevistaDevolucao = dataPrevistaDevolucao; }
    public void setSituacao(SituacaoEmprestimo situacao) { this.situacao = situacao; }

    @Override
    public String toString() {
        return "Emprestimo{id=" + id + ", livro=" + livro.getTitulo() + ", usuario=" + usuario.getNome() +
               ", dataRetirada=" + dataRetirada + ", dataPrevistaDevolucao=" + dataPrevistaDevolucao +
               ", situacao=" + situacao + "}";
    }
}
