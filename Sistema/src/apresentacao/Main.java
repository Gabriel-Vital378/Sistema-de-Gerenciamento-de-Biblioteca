package apresentacao;

import aplicacao.EmprestimoServico;
import aplicacao.EventBus;
import aplicacao.LivroServico;
import aplicacao.UsuarioServico;
import dominio.Emprestimo;
import dominio.Livro;
import dominio.Usuario;
import dominio.evento.DevolucaoRegistradaEvento;
import dominio.evento.EmprestimoRealizadoEvento;
import infraestrutura.adaptador.*;
import porta.entrada.PortaEmprestimo;
import porta.saida.PortaEmprestimoRepositorio;
import porta.saida.PortaLivroRepositorio;
import porta.saida.PortaNotificacao;
import porta.saida.PortaUsuarioRepositorio;

import java.util.List;

public class Main {

    public static void main(String[] args) {

        System.out.println("==============================================");
        System.out.println("  SISTEMA DE GERENCIAMENTO DE BIBLIOTECA");
        System.out.println("==============================================\n");

        // -------------------------------------------------------
        // ETAPA 1 & 2 — Composição com adaptadores em memória
        // -------------------------------------------------------
        System.out.println(">>> [ETAPA 1 & 2] Adaptador em MEMÓRIA\n");

        PortaLivroRepositorio livroRepo         = new LivroRepositorioMemoria();
        PortaUsuarioRepositorio usuarioRepo     = new UsuarioRepositorioMemoria();
        PortaEmprestimoRepositorio empRepo      = new EmprestimoRepositorioMemoria();
        PortaNotificacao notificacao            = new NotificacaoConsole();

        // -------------------------------------------------------
        // ETAPA 3 — EventBus e handlers desacoplados
        // -------------------------------------------------------
        EventBus<EmprestimoRealizadoEvento> busEmprestimo  = new EventBus<>();
        EventBus<DevolucaoRegistradaEvento> busDevolucao   = new EventBus<>();

        ServicoDeNotificacao servicoNotificacao = new ServicoDeNotificacao();
        ServicoDeLog servicoLog                 = new ServicoDeLog();

        // Registrar assinantes — EmprestimoServico não conhece esses handlers
        busEmprestimo.assinar(servicoNotificacao::aoRealizarEmprestimo);
        busEmprestimo.assinar(servicoLog::aoRealizarEmprestimo);
        busDevolucao.assinar(servicoLog::aoRegistrarDevolucao);

        // Montar serviços
        PortaEmprestimo emprestimoServico = new EmprestimoServico(
                livroRepo, usuarioRepo, empRepo, notificacao, busEmprestimo, busDevolucao);

        LivroServico livroServico     = new LivroServico(livroRepo);
        UsuarioServico usuarioServico = new UsuarioServico(usuarioRepo);

        // --- Cadastro de livros ---
        System.out.println("--- Cadastrando livros ---");
        Livro livro1 = livroServico.cadastrar(new Livro(1L, "Clean Code", "Robert C. Martin", "978-0132350884", 2));
        Livro livro2 = livroServico.cadastrar(new Livro(2L, "Domain-Driven Design", "Eric Evans", "978-0321125217", 1));

        // --- Cadastro de usuários ---
        System.out.println("\n--- Cadastrando usuários ---");
        Usuario u1 = usuarioServico.cadastrar(new Usuario(1L, "Ana Silva", "ana@email.com"));
        Usuario u2 = usuarioServico.cadastrar(new Usuario(2L, "Carlos Souza", "carlos@email.com"));

        // --- Realização de empréstimos ---
        System.out.println("\n--- Realizando empréstimos ---");
        Emprestimo emp1 = emprestimoServico.realizarEmprestimo(u1.getId(), livro1.getId());
        System.out.println("Empréstimo realizado: " + emp1);

        Emprestimo emp2 = emprestimoServico.realizarEmprestimo(u2.getId(), livro2.getId());
        System.out.println("Empréstimo realizado: " + emp2);

        // --- Listar empréstimos ativos ---
        System.out.println("\n--- Empréstimos ativos ---");
        List<Emprestimo> ativos = emprestimoServico.listarEmprestimosAtivos();
        ativos.forEach(e -> System.out.println("  " + e));

        // --- Devolução ---
        System.out.println("\n--- Registrando devolução ---");
        emprestimoServico.registrarDevolucao(emp1.getId());
        System.out.println("Devolução do empréstimo #" + emp1.getId() + " registrada.");

        // --- Verificar atrasos ---
        System.out.println("\n--- Verificando atrasos ---");
        List<Emprestimo> atrasados = emprestimoServico.verificarAtrasos();
        if (atrasados.isEmpty()) {
            System.out.println("Nenhum empréstimo em atraso.");
        } else {
            atrasados.forEach(e -> System.out.println("  Em atraso: " + e));
        }

        // -------------------------------------------------------
        // ETAPA 2 — Demonstração da troca de adaptador (CSV)
        // -------------------------------------------------------
        System.out.println("\n==============================================");
        System.out.println(">>> [ETAPA 2] Troca de adaptador: MEMÓRIA → CSV\n");

        PortaLivroRepositorio livroRepoCsv = new LivroRepositorioCsv("livros.csv");

        // Mesmo serviço — apenas o adaptador mudou
        PortaEmprestimo servicoCsv = new EmprestimoServico(
                livroRepoCsv, usuarioRepo, empRepo, notificacao, busEmprestimo, busDevolucao);

        LivroServico livroServicoCsv = new LivroServico(livroRepoCsv);

        System.out.println("--- Cadastrando livro via adaptador CSV ---");
        livroServicoCsv.cadastrar(new Livro(3L, "Arquitetura Limpa", "Robert C. Martin", "978-8550804606", 3));

        System.out.println("\n--- Livros persistidos no CSV ---");
        livroServicoCsv.listarTodos().forEach(l -> System.out.println("  " + l));

        System.out.println("\n--- Realizando empréstimo com adaptador CSV ---");
        Emprestimo emp3 = servicoCsv.realizarEmprestimo(u1.getId(), 3L);
        System.out.println("Empréstimo realizado: " + emp3);

        System.out.println("\n==============================================");
        System.out.println("  Execução concluída. Verifique biblioteca.log");
        System.out.println("==============================================");
    }
}
