package infraestrutura.adaptador;

import dominio.Livro;
import porta.saida.PortaLivroRepositorio;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class LivroRepositorioMemoria implements PortaLivroRepositorio {

    private final Map<Long, Livro> armazenamento = new HashMap<>();

    @Override
    public void salvar(Livro livro) {
        armazenamento.put(livro.getId(), livro);
    }

    @Override
    public Optional<Livro> buscarPorId(Long id) {
        return Optional.ofNullable(armazenamento.get(id));
    }

    @Override
    public List<Livro> listarTodos() {
        return new ArrayList<>(armazenamento.values());
    }

    @Override
    public void remover(Long id) {
        armazenamento.remove(id);
    }
}
