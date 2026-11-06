# Documento de Requisitos - App Financeiro Pessoal

Autor: Arthur · Data: 06/11/2026

## 1. Visão geral

App de controle financeiro pessoal, feito para uso próprio e para portfólio. O MVP responde três perguntas: qual é o meu custo de vida fixo mínimo, com que área da vida eu mais gasto e se estou gastando acima ou abaixo do que ganho.

**Dentro do MVP:** controle de entradas e saídas, categorias personalizadas, gastos fixos, cartão de crédito com faturas, controle de contas bancárias, transferências entre contas, filtros, metas de limite, relatório de receitas x despesas e previsão dos meses seguintes.

**Fora do MVP:** registro de investimentos (locais, categorias e saldo), considerado não vital nesta fase. Estornos, dívidas e metas de economia ficam como ideias para rodadas futuras.

**Pontos em aberto:**

- Uma conta inativa pode receber novas movimentações?
- Gastos fixos não mensais (IPTU, seguro anual) terão recorrência própria?
- Custos do cartão (anuidade, juros, IOF) terão uma área separada?

## 2. Regras de negócio (RN)

As regras que o sistema precisa respeitar, independente de tela ou tecnologia.

| ID | Área | Regra |
| --- | --- | --- |
| RN01 | Contas | O saldo inicial da conta é informado apenas no cadastro e não pode ser editado livremente depois. |
| RN02 | Contas | O saldo da conta é o saldo inicial mais as entradas, menos as saídas que afetam a conta, e deve sempre bater com o extrato. |
| RN03 | Movimentações | Toda entrada ou saída deve estar vinculada a uma conta bancária. |
| RN04 | Movimentações | Toda entrada ou saída deve ter uma categoria. |
| RN05 | Categorias | Uma categoria personalizada tem nome e descrição. |
| RN06 | Transferências | Uma transferência entre contas gera uma saída na conta de origem e uma entrada de mesmo valor na conta de destino, e não conta como receita nem como despesa. |
| RN07 | Gastos fixos | Um gasto fixo é um gasto comum marcado como fixo, com vencimento e status de pago. Não vem no cartão. |
| RN08 | Gastos fixos | O custo de vida fixo mínimo do mês é a soma dos gastos fixos do mês. |
| RN09 | Cartão | Uma compra no cartão não altera o saldo da conta no momento da compra. |
| RN10 | Cartão | Cada compra cai na fatura definida pelo dia de fechamento: compras após o fechamento vão para a fatura seguinte. |
| RN11 | Cartão | Uma compra parcelada gera uma parcela em cada fatura seguinte, com o número da parcela (ex: 3/10). |
| RN12 | Cartão | O pagamento da fatura é a saída que altera o saldo da conta vinculada. |
| RN13 | Cartão | Os itens da fatura aparecem no controle geral como saídas identificadas com o cartão e a fatura, sem descontar o saldo uma segunda vez. |
| RN14 | Cartão | O limite disponível é o limite do cartão menos o valor já comprometido, incluindo parcelas futuras. O sistema indica quando o limite não é suficiente. |
| RN15 | Metas | Metas de limite podem ser definidas para gastos fixos e para gastos gerais, e o realizado é comparado com a meta. |
| RN16 | Relatórios | No comparativo de receitas x despesas, transferências e pagamentos de fatura não contam como despesa; os itens da fatura contam. |

## 3. Requisitos funcionais (RF)

O que o usuário consegue fazer no sistema.

| ID | Área | Requisito |
| --- | --- | --- |
| RF01 | Contas | Cadastrar conta bancária com banco, saldo inicial, status e, opcionalmente, agência e número da conta. |
| RF02 | Contas | Ativar e inativar uma conta bancária. |
| RF03 | Contas | Visualizar o extrato da conta com todas as movimentações e o saldo resultante. |
| RF04 | Categorias | Cadastrar, editar e listar categorias personalizadas. |
| RF05 | Movimentações | Registrar entrada ou saída com nome, valor, conta, categoria, fonte (pix, boleto e afins) e descrição opcional. |
| RF06 | Transferências | Registrar transferência entre duas contas do usuário. |
| RF07 | Gastos fixos | Marcar um gasto como fixo, informando vencimento. |
| RF08 | Gastos fixos | Marcar um gasto fixo como pago ou não pago. |
| RF09 | Cartão | Cadastrar cartão com nome, banco vinculado, limite, dia de fechamento e dia de vencimento da fatura. |
| RF10 | Cartão | Registrar compra no cartão, à vista ou parcelada. |
| RF11 | Cartão | Visualizar a fatura atual e as próximas, com o valor comprometido e o limite disponível. |
| RF12 | Cartão | Registrar o pagamento da fatura a partir de uma conta. |
| RF13 | Filtros | Filtrar movimentações por conta. |
| RF14 | Filtros | Filtrar movimentações por categoria. |
| RF15 | Filtros | Filtrar movimentações por período: dia, semana, mês, semestre, ano ou intervalo de meses escolhido. |
| RF16 | Relatórios | Exibir o comparativo de receitas x despesas, mostrando quanto o usuário está acima ou abaixo. |
| RF17 | Gastos fixos | Exibir o custo de vida fixo mínimo e a medição mensal dos gastos fixos. |
| RF18 | Metas | Definir meta de limite para gastos fixos. |
| RF19 | Metas | Definir meta de limite para gastos gerais. |
| RF20 | Previsão | Exibir a previsão de gastos dos meses seguintes com base nos gastos fixos e nas parcelas futuras do cartão. |

## 4. Requisitos não funcionais (RNF)

Como o sistema deve se comportar. Estes são uma proposta inicial, para você ajustar.

| ID | Tipo | Requisito |
| --- | --- | --- |
| RNF01 | Segurança | O sistema não armazena dados sensíveis do cartão (número, código de segurança, validade). |
| RNF02 | Segurança | O acesso ao sistema é protegido por autenticação, pois os dados são financeiros e pessoais. |
| RNF03 | Precisão | Valores monetários são calculados e guardados com precisão exata de centavos, sem erros de arredondamento. |
| RNF04 | Consistência | Operações que geram mais de um registro (transferência, pagamento de fatura, compra parcelada) acontecem por inteiro ou não acontecem. |
| RNF05 | Rastreabilidade | O saldo só muda por meio de movimentações registradas, permitindo explicar qualquer saldo pelo extrato. |
| RNF06 | Localização | Valores em reais (R$) e datas no formato brasileiro (dd/mm/aaaa). |
| RNF07 | Usabilidade | Registrar um gasto deve ser rápido, em poucos passos, e funcionar bem no celular. |
| RNF08 | Manutenibilidade | Código organizado, com testes automatizados cobrindo as regras de saldo, fatura e transferência. |

## 5. Histórias de usuário (HU)

Cada história descreve uma necessidade do usuário e os critérios para considerar que ela está pronta.

### HU01 - Cadastrar conta bancária

Como usuário, quero cadastrar minhas contas bancárias com o saldo atual, para que o app reflita onde está meu dinheiro.

- Informo banco, saldo inicial e status; agência e conta são opcionais.
- Depois do cadastro, o saldo não pode ser editado diretamente.
- Consigo inativar uma conta sem perder seu histórico.

### HU02 - Registrar entrada ou saída

Como usuário, quero registrar o que entra e o que sai de cada conta, para saber quanto ganho e quanto gasto.

- Informo nome, valor, conta, categoria, fonte e, se quiser, uma descrição.
- O saldo da conta é atualizado automaticamente.
- A movimentação aparece no extrato da conta.

### HU03 - Criar categorias

Como usuário, quero criar minhas próprias categorias, para saber com que área da minha vida eu mais gasto.

- Informo nome e descrição da categoria.
- A categoria fica disponível ao registrar movimentações.

### HU04 - Transferir entre contas

Como usuário, quero registrar transferências entre minhas contas, para que os saldos fiquem certos sem distorcer meus gastos.

- A conta de origem perde o valor e a de destino ganha o mesmo valor.
- A transferência não aparece como receita nem como despesa nos relatórios.

### HU05 - Controlar gastos fixos

Como usuário, quero marcar gastos como fixos e acompanhar se já paguei, para saber meu custo de vida mínimo e não esquecer vencimentos.

- Ao marcar um gasto como fixo, informo o vencimento.
- Consigo marcar o gasto fixo como pago ou não pago.
- Vejo o total de gastos fixos do mês como meu custo de vida fixo mínimo.

### HU06 - Cadastrar cartão de crédito

Como usuário, quero cadastrar meus cartões sem informar dados sensíveis, para controlar faturas e limite.

- Informo nome, banco vinculado, limite, fechamento e vencimento.
- O sistema não pede número do cartão nem código de segurança.

### HU07 - Registrar compra no cartão

Como usuário, quero registrar compras no cartão, à vista ou parceladas, para saber quanto já está comprometido nas próximas faturas.

- A compra não altera o saldo da conta.
- A compra cai na fatura certa conforme o dia de fechamento.
- Compras parceladas aparecem nas faturas seguintes com o número da parcela.
- Vejo o limite disponível e sou avisado quando ele não é suficiente.

### HU08 - Pagar a fatura

Como usuário, quero registrar o pagamento da fatura, para que o saldo da conta bata com o extrato.

- O pagamento sai do saldo da conta escolhida.
- No controle geral, vejo cada item da fatura com sua categoria e a indicação de cartão e fatura.
- O saldo não é descontado duas vezes.

### HU09 - Filtrar meus gastos

Como usuário, quero filtrar minhas movimentações, para analisar meus gastos do jeito que eu precisar.

- Filtro por conta, por categoria e por período.
- Os períodos disponíveis são dia, semana, mês, semestre, ano e intervalo de meses.
- Os filtros podem ser combinados.

### HU10 - Comparar receitas, despesas e metas

Como usuário, quero comparar o que ganho com o que gasto e com minhas metas, para saber se estou acima ou abaixo do limite.

- Vejo o total de receitas, o total de despesas e a diferença no período.
- Defino metas de limite para gastos fixos e para gastos gerais.
- Vejo quanto estou acima ou abaixo de cada meta.

### HU11 - Prever os próximos meses

Como usuário, quero ver uma previsão dos meus gastos dos próximos meses, para me planejar antes de o dinheiro sair.

- A previsão considera os gastos fixos e as parcelas futuras do cartão.
- Vejo a previsão mês a mês.

## 6. Matriz de rastreabilidade

Cada história ligada aos requisitos e regras que ela cobre. Toda RN e todo RF aparecem em pelo menos uma história.

| História | Requisitos funcionais | Regras de negócio | Requisitos não funcionais |
| --- | --- | --- | --- |
| HU01 - Cadastrar conta bancária | RF01, RF02, RF03 | RN01, RN02, RN03 | RNF05 |
| HU02 - Registrar entrada ou saída | RF05 | RN02, RN03, RN04 | RNF03, RNF07 |
| HU03 - Criar categorias | RF04 | RN04, RN05 | - |
| HU04 - Transferir entre contas | RF06 | RN02, RN06 | RNF04 |
| HU05 - Controlar gastos fixos | RF07, RF08, RF17 | RN07, RN08 | - |
| HU06 - Cadastrar cartão de crédito | RF09 | RN14 | RNF01 |
| HU07 - Registrar compra no cartão | RF10, RF11 | RN09, RN10, RN11, RN14 | RNF04 |
| HU08 - Pagar a fatura | RF12 | RN02, RN12, RN13 | RNF04, RNF05 |
| HU09 - Filtrar meus gastos | RF13, RF14, RF15 | RN03, RN04 | - |
| HU10 - Comparar receitas, despesas e metas | RF16, RF18, RF19 | RN15, RN16 | - |
| HU11 - Prever os próximos meses | RF20 | RN07, RN11 | - |

RNF02 (autenticação), RNF06 (formato brasileiro) e RNF08 (código e testes) valem para o sistema inteiro, não para uma história específica.
