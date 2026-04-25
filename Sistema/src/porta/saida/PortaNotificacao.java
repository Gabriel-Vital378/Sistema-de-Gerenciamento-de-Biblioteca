package porta.saida;

import dominio.Emprestimo;
import dominio.Usuario;

public interface PortaNotificacao {
    void notificarAtraso(Usuario usuario, Emprestimo emprestimo);
}
