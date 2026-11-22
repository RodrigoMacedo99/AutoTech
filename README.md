# AutoTech

Sistema de linha de comando para gerenciamento de uma oficina mecânica. Permite cadastrar clientes, registrar a entrada de veículos (carros e motos) com suas manutenções, calcular o valor de saída com diferentes formas de pagamento, buscar, alterar, remover e ordenar veículos.

O projeto foi modelado a partir de um diagrama de classes UML (PlantUML) e implementado em Java.

## Requisitos

- Java 21 ou superior (JDK, para compilar)
- Não há dependências externas

## Estrutura do projeto

```
AutoTech/
├── README.md
└── src/
    └── autotech/
        ├── Programa.java            # Ponto de entrada (menu interativo)
        ├── GerenciadorOficina.java  # Regras de negócio e coleções de veículos/clientes
        ├── Veiculo.java             # Classe abstrata base dos veículos
        ├── Carro.java
        ├── Moto.java
        ├── Cliente.java
        ├── Motor.java
        ├── Manutencao.java
        ├── Servico.java
        ├── Imprimivel.java          # Interface: getLinhaTabela() e imprimir()
        ├── FormaPagamento.java      # Interface: calcularValorFinal() e gerarComprovante()
        ├── PagamentoPix.java
        ├── PagamentoCartao.java
        ├── PagamentoDinheiro.java
        ├── ComparadorPorPreco.java  # Comparator<Veiculo>
        └── ComparadorPorAno.java    # Comparator<Veiculo>
```

## Como compilar e executar

Na raiz do projeto:

```bash
javac -d out src/autotech/*.java
java -cp out autotech.Programa
```

Ao iniciar, o sistema carrega automaticamente **14 veículos** de exemplo (7 carros e 7 motos), cada um com um cliente fictício.

## Uso

O programa exibe o seguinte menu:

| Opção | Funcionalidade |
|-------|----------------|
| 1 | Cadastrar novo cliente (CPF, nome, telefone). CPFs duplicados são recusados. |
| 2 | Incluir veículo e manutenção. Se a placa já existir, o veículo existente é usado e apenas uma nova manutenção é registrada. |
| 3 | Alterar dados do veículo (marca, modelo, ano e preço), identificado pela placa. |
| 4 | Excluir veículo, identificado pela placa. |
| 5 | Realizar saída e pagamento: calcula o total das manutenções em aberto, aplica a forma de pagamento e gera o recibo. |
| 6 | Buscar veículo por placa ou por modelo (busca parcial, sem diferenciar maiúsculas de minúsculas). |
| 7 | Listar veículos em tabela, ou listar ordenados por preço ou por ano. |
| 0 | Sair. |

### Fluxo típico

1. **Entrada:** opção `2` com a placa do veículo. Informe os serviços (tipo e descrição) até deixar o tipo em branco. A manutenção precisa de pelo menos um serviço.
2. **Saída:** opção `5` com a placa e a forma de pagamento. O sistema marca a data de saída e gera um recibo.

## Regras de negócio

### Preço dos serviços

| Tipo de serviço | Preço (R$) |
|-----------------|-----------:|
| Troca de óleo | 150,00 |
| Revisão | 350,00 |
| Freio | 280,00 |
| Alinhamento | 120,00 |
| Troca de pneus | 400,00 |
| Qualquer outro tipo | 200,00 |

O total de uma manutenção é a soma dos preços dos seus serviços.

### Formas de pagamento

| Forma | Ajuste sobre o total |
|-------|----------------------|
| PIX | 5% de desconto |
| Cartão | 3% de acréscimo |
| Dinheiro | 10% de desconto |

## Arquivos gerados

Os arquivos são criados no diretório de execução:

- **`manutencoes.txt`**: uma linha por manutenção registrada no formato `id;placa;dataEntrada;quantidadeServicos;total`. O arquivo é acumulado entre execuções.
- **`recibo_<placa>_<id>.txt`**: recibo emitido na saída, com dados do veículo, cliente, datas, serviços, total e comprovante da forma de pagamento.

## Arquitetura

- **`Veiculo`** é abstrata e implementa `Imprimivel`. `Carro` (com número de portas) e `Moto` (com cilindradas) são as especializações. Cada veículo possui um `Motor` próprio e um `Cliente` associado, e mantém o histórico de `Manutencao`.
- **`Manutencao`** agrega `Servico`s e controla as datas de entrada e saída. A manutenção "em aberto" é aquela sem data de saída.
- **`FormaPagamento`** define a estratégia de cálculo do valor final e de geração de comprovante. As três implementações são intercambiáveis.
- **`GerenciadorOficina`** concentra as operações sobre as listas de veículos e clientes, além da persistência em arquivo.
- **`ComparadorPorPreco`** e **`ComparadorPorAno`** são usados para ordenação.

## Observações

- O projeto não possui testes automatizados.
- Não há persistência dos clientes e veículos entre execuções: a carga inicial é recriada a cada início. Apenas o histórico de manutenções é gravado em arquivo.
- Não há validação de formato para placa, CPF ou telefone.
