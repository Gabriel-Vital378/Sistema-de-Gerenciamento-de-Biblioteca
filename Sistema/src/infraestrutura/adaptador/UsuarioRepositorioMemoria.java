package infraestrutura.adaptador;

import dominio.Usuario;
import porta.saida.PortaUsuarioRepositorio;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class UsuarioRepositorioMemoria implements PortaUsuarioRepositorio {

    private final Map<Long, Usuario> armazenamento = new HashMap<>();

    @Override
    public void salvar(Usuario usuario) {
        armazenamento.put(usuario.getId(), usuario);
    }

    @Override
    public Optional<Usuario> buscarPorId(Long id) {
        return Optional.ofNullable(armazenamento.get(id));
    }

    @Override
    public List<Usuario> listarTodos() {
        return new ArrayList<>(armazenamento.values());
    }

    @Override
    public void remover(Long id) {
        armazenamento.remove(id);
    }
}
