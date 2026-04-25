package aplicacao;

import dominio.Emprestimo;
import dominio.Livro;
import dominio.SituacaoEmprestimo;
import dominio.Usuario;
import dominio.evento.DevolucaoRegistradaEvento;
import dominio.evento.EmprestimoRealizadoEvento;
import porta.entrada.PortaEmprestimo;
import porta.saida.PortaEmprestimoRepositorio;
import porta.saida.PortaLivroRepositorio;
import porta.saida.PortaNotificacao;
import porta.saida.PortaUsuarioRepositorio;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class EmprestimoServico implements PortaEmprestimo {

    private final PortaLivroRepositorio livroRepositorio;
    private final PortaUsuarioRepositorio usuarioRepositorio;
    private final PortaEmprestimoRepositorio emprestimoRepositorio;
    private final PortaNotificacao portaNotificacao;
    private final EventBus<EmprestimoRealizadoEvento> busEmprestimo;
    private final EventBus<DevolucaoRegistradaEvento> busDevolucao;

    private Long proximoId = 1L;

    public EmprestimoServico(
            PortaLivroRepositorio livroRepositorio,
            PortaUsuarioRepositorio usuarioRepositorio,
            PortaEmprestimoRepositorio emprestimoRepositorio,
            PortaNotificacao portaNotificacao,
            EventBus<EmprestimoRealizadoEvento> busEmprestimo,
            EventBus<DevolucaoRegistradaEvento> busDevolucao) {
        this.livroRepositorio = livroRepositorio;
        this.usuarioRepositorio = usuarioRepositorio;
        this.emprestimoRepositorio = emprestimoRepositorio;
        this.portaNotificacao = portaNotificacao;
        this.busEmprestimo = busEmprestimo;
        this.busDevolucao = busDevolucao;
    }

    @Override
    public Emprestimo realizarEmprestimo(Long usuarioId, Long livroId) {
        Usuario usuario = usuarioRepositorio.buscarPorId(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado: " + usuarioId));

        if (!usuario.isAtivo()) {
            throw new RuntimeException("Usuário suspenso não pode realizar empréstimos: " + usuario.getNome());
        }

        Livro livro = livroRepositorio.buscarPorId(livroId)
                .orElseThrow(() -> new RuntimeException("Livro não encontrado: " + livroId));

        boolean sucesso = livro.realizarEmprestimo();
        if (!sucesso) {
            throw new RuntimeException("Livro sem exemplares disponíveis: " + livro.getTitulo());
        }

        livroRepositorio.salvar(livro);

        LocalDate dataRetirada = LocalDate.now();
        LocalDate dataPrevistaDevolucao = dataRetirada.plusDays(14);

        Emprestimo emprestimo = new Emprestimo(proximoId++, livro, usuario, dataRetirada, dataPrevistaDevolucao);
        emprestimoRepositorio.salvar(emprestimo);

        busEmprestimo.publicar(new EmprestimoRealizadoEvento(
                emprestimo.getId(), usuarioId, livroId, dataRetirada));

        return emprestimo;
    }

    @Override
    public void registrarDevolucao(Long emprestimoId) {
        Emprestimo emprestimo = emprestimoRepositorio.buscarPorId(emprestimoId)
                .orElseThrow(() -> new RuntimeException("Empréstimo não encontrado: " + emprestimoId));

        if (SituacaoEmprestimo.DEVOLVIDO.equals(emprestimo.getSituacao())) {
            throw new RuntimeException("Este empréstimo já foi devolvido.");
        }

        boolean comAtraso = emprestimo.estaAtrasado();

        emprestimo.getLivro().registrarDevolucao();
        livroRepositorio.salvar(emprestimo.getLivro());

        emprestimo.setSituacao(SituacaoEmprestimo.DEVOLVIDO);
        emprestimoRepositorio.salvar(emprestimo);

        busDevolucao.publicar(new DevolucaoRegistradaEvento(
                emprestimoId, LocalDate.now(), comAtraso));
    }

    @Override
    public List<Emprestimo> listarEmprestimosAtivos() {
        return emprestimoRepositorio.listarTodos().stream()
                .filter(e -> SituacaoEmprestimo.ATIVO.equals(e.getSituacao()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Emprestimo> verificarAtrasos() {
        List<Emprestimo> atrasados = emprestimoRepositorio.listarTodos().stream()
                .filter(Emprestimo::estaAtrasado)
                .collect(Collectors.toList());

        atrasados.forEach(e -> {
            e.setSituacao(SituacaoEmprestimo.ATRASADO);
            emprestimoRepositorio.salvar(e);
            portaNotificacao.notificarAtraso(e.getUsuario(), e);
        });

        return atrasados;
    }
}
