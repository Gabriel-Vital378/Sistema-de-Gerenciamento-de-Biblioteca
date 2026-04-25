package infraestrutura.adaptador;

import dominio.Emprestimo;
import porta.saida.PortaEmprestimoRepositorio;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class EmprestimoRepositorioMemoria implements PortaEmprestimoRepositorio {

    private final Map<Long, Emprestimo> armazenamento = new HashMap<>();

    @Override
    public void salvar(Emprestimo emprestimo) {
        armazenamento.put(emprestimo.getId(), emprestimo);
    }

    @Override
    public Optional<Emprestimo> buscarPorId(Long id) {
        return Optional.ofNullable(armazenamento.get(id));
    }

    @Override
    public List<Emprestimo> listarTodos() {
        return new ArrayList<>(armazenamento.values());
    }

    @Override
    public void remover(Long id) {
        armazenamento.remove(id);
    }
}
