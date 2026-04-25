package infraestrutura.adaptador;

import dominio.evento.DevolucaoRegistradaEvento;
import dominio.evento.EmprestimoRealizadoEvento;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ServicoDeLog {

    private static final String ARQUIVO_LOG = "biblioteca.log";
    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public void aoRealizarEmprestimo(EmprestimoRealizadoEvento evento) {
        String mensagem = "[" + LocalDateTime.now().format(FORMATO) + "] EMPRESTIMO_REALIZADO" +
                " | emprestimoId=" + evento.emprestimoId() +
                " | usuarioId=" + evento.usuarioId() +
                " | livroId=" + evento.livroId() +
                " | dataRetirada=" + evento.dataRetirada();
        gravarLog(mensagem);
    }

    public void aoRegistrarDevolucao(DevolucaoRegistradaEvento evento) {
        String mensagem = "[" + LocalDateTime.now().format(FORMATO) + "] DEVOLUCAO_REGISTRADA" +
                " | emprestimoId=" + evento.emprestimoId() +
                " | dataDevolucao=" + evento.dataDevolucao() +
                " | comAtraso=" + evento.comAtraso();
        gravarLog(mensagem);
    }

    private void gravarLog(String mensagem) {
        System.out.println("[LOG] " + mensagem);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ARQUIVO_LOG, true))) {
            writer.write(mensagem);
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Erro ao gravar log: " + e.getMessage());
        }
    }
}
