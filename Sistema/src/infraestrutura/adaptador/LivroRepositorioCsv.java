package infraestrutura.adaptador;

import dominio.Livro;
import porta.saida.PortaLivroRepositorio;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LivroRepositorioCsv implements PortaLivroRepositorio {

    private final String caminhoArquivo;
    private static final String CABECALHO = "id,titulo,autor,isbn,quantidadeDisponivel";

    public LivroRepositorioCsv(String caminhoArquivo) {
        this.caminhoArquivo = caminhoArquivo;
        inicializarArquivo();
    }

    private void inicializarArquivo() {
        File arquivo = new File(caminhoArquivo);
        if (!arquivo.exists()) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(arquivo))) {
                writer.write(CABECALHO);
                writer.newLine();
            } catch (IOException e) {
                throw new RuntimeException("Erro ao criar arquivo CSV: " + caminhoArquivo, e);
            }
        }
    }

    @Override
    public void salvar(Livro livro) {
        List<Livro> todos = listarTodos();
        boolean encontrado = false;
        for (int i = 0; i < todos.size(); i++) {
            if (todos.get(i).getId().equals(livro.getId())) {
                todos.set(i, livro);
                encontrado = true;
                break;
            }
        }
        if (!encontrado) {
            todos.add(livro);
        }
        escreverTodos(todos);
    }

    @Override
    public Optional<Livro> buscarPorId(Long id) {
        return listarTodos().stream()
                .filter(l -> l.getId().equals(id))
                .findFirst();
    }

    @Override
    public List<Livro> listarTodos() {
        List<Livro> livros = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(caminhoArquivo))) {
            String linha;
            boolean primeira = true;
            while ((linha = reader.readLine()) != null) {
                if (primeira) { primeira = false; continue; } // pula cabeçalho
                if (!linha.trim().isEmpty()) {
                    livros.add(parseLinha(linha));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Erro ao ler arquivo CSV: " + caminhoArquivo, e);
        }
        return livros;
    }

    @Override
    public void remover(Long id) {
        List<Livro> todos = listarTodos();
        todos.removeIf(l -> l.getId().equals(id));
        escreverTodos(todos);
    }

    private Livro parseLinha(String linha) {
        String[] campos = linha.split(",", 5);
        return new Livro(
                Long.parseLong(campos[0].trim()),
                campos[1].trim(),
                campos[2].trim(),
                campos[3].trim(),
                Integer.parseInt(campos[4].trim())
        );
    }

    private void escreverTodos(List<Livro> livros) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(caminhoArquivo))) {
            writer.write(CABECALHO);
            writer.newLine();
            for (Livro l : livros) {
                writer.write(l.getId() + "," + l.getTitulo() + "," + l.getAutor() + "," +
                             l.getIsbn() + "," + l.getQuantidadeDisponivel());
                writer.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException("Erro ao escrever no arquivo CSV: " + caminhoArquivo, e);
        }
    }
}
