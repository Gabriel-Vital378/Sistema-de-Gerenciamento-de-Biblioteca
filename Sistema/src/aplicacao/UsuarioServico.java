package aplicacao;

import dominio.Usuario;
import porta.saida.PortaUsuarioRepositorio;

import java.util.List;
import java.util.Optional;

public class UsuarioServico {

    private final PortaUsuarioRepositorio usuarioRepositorio;

    public UsuarioServico(PortaUsuarioRepositorio usuarioRepositorio) {
        this.usuarioRepositorio = usuarioRepositorio;
    }

    public Usuario cadastrar(Usuario usuario) {
        usuarioRepositorio.salvar(usuario);
        System.out.println("Usuário cadastrado: " + usuario);
        return usuario;
    }

    public Optional<Usuario> buscarPorId(Long id) {
        return usuarioRepositorio.buscarPorId(id);
    }

    public List<Usuario> listarTodos() {
        return usuarioRepositorio.listarTodos();
    }

    public void remover(Long id) {
        usuarioRepositorio.remover(id);
    }
}
