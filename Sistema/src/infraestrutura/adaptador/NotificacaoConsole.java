package infraestrutura.adaptador;

import dominio.Emprestimo;
import dominio.Usuario;
import porta.saida.PortaNotificacao;

public class NotificacaoConsole implements PortaNotificacao {

    @Override
    public void notificarAtraso(Usuario usuario, Emprestimo emprestimo) {
        System.out.println("[NOTIFICAÇÃO] Usuário '" + usuario.getNome() +
                "' está com o livro '" + emprestimo.getLivro().getTitulo() +
                "' em atraso desde " + emprestimo.getDataPrevistaDevolucao() + ".");
    }
}
