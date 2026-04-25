package infraestrutura.adaptador;

import dominio.evento.EmprestimoRealizadoEvento;

public class ServicoDeNotificacao {

    public void aoRealizarEmprestimo(EmprestimoRealizadoEvento evento) {
        System.out.println("[NOTIFICAÇÃO] Empréstimo #" + evento.emprestimoId() +
                " realizado com sucesso! Data prevista de devolução: " +
                evento.dataRetirada().plusDays(14) + ".");
    }
}
