package porta.entrada;
// Etapa 2 - Porta de Entrada: interface que define os casos de uso
import dominio.Emprestimo;
import java.util.List;

public interface PortaEmprestimo {
    Emprestimo realizarEmprestimo(Long usuarioId, Long livroId);
    void registrarDevolucao(Long emprestimoId);
    List<Emprestimo> listarEmprestimosAtivos();
    List<Emprestimo> verificarAtrasos();
}
