package aplicacao;

import dominio.Livro;
import porta.saida.PortaLivroRepositorio;

import java.util.List;
import java.util.Optional;

public class LivroServico {

    private final PortaLivroRepositorio livroRepositorio;

    public LivroServico(PortaLivroRepositorio livroRepositorio) {
        this.livroRepositorio = livroRepositorio;
    }

    public Livro cadastrar(Livro livro) {
        livroRepositorio.salvar(livro);
        System.out.println("Livro cadastrado: " + livro);
        return livro;
    }

    public Optional<Livro> buscarPorId(Long id) {
        return livroRepositorio.buscarPorId(id);
    }

    public List<Livro> listarTodos() {
        return livroRepositorio.listarTodos();
    }

    public void remover(Long id) {
        livroRepositorio.remover(id);
    }
}
