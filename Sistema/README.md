# Sistema de Gerenciamento de Biblioteca

Atividade prática da disciplina **Arquiteturas de Software com Java** — PUC Goiás / ADS.

O projeto implementa um sistema de gerenciamento de biblioteca em **Java puro (sem frameworks)**, evoluindo por três estilos arquiteturais de forma incremental.

---

## Como compilar e executar

### Pré-requisitos

- Java 17 ou superior instalado
- Terminal (CMD, PowerShell ou bash)

### Compilar

Na raiz do projeto, execute:

```bash
mkdir -p out
javac --release 17 -d out $(find . -name "*.java")
```

No Windows (PowerShell):

```powershell
mkdir out
javac --release 17 -d out (Get-ChildItem -Recurse -Filter *.java | % { $_.FullName })
```

### Executar

```bash
cd out
java apresentacao.Main
```

Após a execução, será gerado um arquivo `biblioteca.log` com o histórico de operações registradas pelo `ServicoDeLog`.

---

## Estrutura de pacotes

```
biblioteca/
├── dominio/
│   ├── Livro.java
│   ├── Usuario.java
│   ├── Emprestimo.java
│   ├── SituacaoEmprestimo.java
│   ├── SituacaoUsuario.java
│   └── evento/
│       ├── EmprestimoRealizadoEvento.java
│       └── DevolucaoRegistradaEvento.java
├── porta/
│   ├── entrada/
│   │   └── PortaEmprestimo.java
│   └── saida/
│       ├── PortaLivroRepositorio.java
│       ├── PortaUsuarioRepositorio.java
│       ├── PortaEmprestimoRepositorio.java
│       └── PortaNotificacao.java
├── aplicacao/
│   ├── EventBus.java
│   ├── LivroServico.java
│   ├── UsuarioServico.java
│   └── EmprestimoServico.java
├── infraestrutura/
│   └── adaptador/
│       ├── LivroRepositorioMemoria.java
│       ├── LivroRepositorioCsv.java
│       ├── UsuarioRepositorioMemoria.java
│       ├── EmprestimoRepositorioMemoria.java
│       ├── NotificacaoConsole.java
│       ├── ServicoDeNotificacao.java
│       └── ServicoDeLog.java
└── apresentacao/
    └── Main.java
```

---

## Decisões de design

### Etapa 1 — Arquitetura em Camadas

A separação em camadas foi aplicada de forma estrita: as classes do pacote `dominio` não importam nada das camadas de `infraestrutura` ou `aplicacao`. A regra de negócio de verificação de disponibilidade do livro foi colocada diretamente na entidade `Livro`, no método `realizarEmprestimo()`, respeitando o princípio de que o domínio deve conter suas próprias regras.

Os repositórios foram implementados com `HashMap` para simular persistência em memória sem dependência de banco de dados.

### Etapa 2 — Arquitetura Hexagonal (Ports and Adapters)

O núcleo da aplicação (domínio + serviços) foi isolado completamente de detalhes de infraestrutura por meio de interfaces chamadas **portas**:

- **Portas de saída** (`porta/saida`): definem contratos para repositórios e notificações.
- **Portas de entrada** (`porta/entrada`): definem os casos de uso expostos pelo sistema (`PortaEmprestimo`).
- **Adaptadores** (`infraestrutura/adaptador`): implementam as portas com tecnologias concretas (HashMap, CSV, console).

A troca de adaptador é demonstrada na `Main`: o `EmprestimoServico` é instanciado primeiro com `LivroRepositorioMemoria` e depois com `LivroRepositorioCsv`, sem que nenhuma linha do serviço ou do domínio precise ser alterada.

O adaptador CSV (`LivroRepositorioCsv`) usa apenas a API padrão de I/O do Java (`BufferedReader`, `BufferedWriter`, `FileWriter`) para ler e gravar o arquivo `livros.csv`.

### Etapa 3 — Comunicação Assíncrona por Eventos

Foi implementado um `EventBus<T>` genérico baseado no padrão **Publisher/Subscriber**. O `EmprestimoServico` publica eventos ao concluir cada operação, sem nenhum conhecimento sobre quem os consome.

Os consumidores (`ServicoDeNotificacao` e `ServicoDeLog`) são registrados na `Main` via referências de método (`handler::metodo`), garantindo desacoplamento total entre publicador e consumidores.

O `ServicoDeLog` registra cada operação no arquivo `biblioteca.log` com timestamp no formato `yyyy-MM-dd HH:mm:ss`, usando modo de append (`FileWriter` com `true`).

Os eventos foram implementados como `records` do Java 16+, aproveitando a imutabilidade natural dessa estrutura.

---

## Dificuldades encontradas e como foram resolvidas

**1. Garantir que o domínio não importasse infraestrutura**
A separação foi feita desde o início pela estrutura de pacotes. As interfaces (portas) foram colocadas em um pacote próprio (`porta/`) que só depende de `dominio`, permitindo que os serviços de aplicação dependam apenas de abstrações.

**2. Adaptador CSV com atualização de registros existentes**
O CSV não tem suporte nativo a atualização de linhas. A solução foi implementar o método `salvar()` carregando todos os registros na memória, atualizando o registro correspondente e reescrevendo o arquivo inteiro — abordagem simples e adequada para o escopo da atividade.

**3. Desacoplamento total no EventBus**
O desafio era garantir que `EmprestimoServico` não importasse `ServicoDeNotificacao` nem `ServicoDeLog`. Isso foi resolvido usando `Consumer<T>` do Java (interface funcional), permitindo que a `Main` registre os handlers via referências de método sem que o serviço conheça os consumidores.
