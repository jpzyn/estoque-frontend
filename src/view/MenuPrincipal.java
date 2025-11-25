package view;

import controller.EstoqueController;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;

/**
 * Camada de apresentação desenvolvida em Swing.
 * O objetivo é prover uma interface mínima para interação com o back-end
 * via sockets, atendendo às funcionalidades descritas no README.
 */
public class MenuPrincipal extends JFrame {

    private final EstoqueController controller;

    public MenuPrincipal() {
        super("Sistema de Controle de Estoque - Cliente");
        this.controller = new EstoqueController();
        configurarJanela();
        adicionarComponentes();
    }

    private void configurarJanela() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(960, 640));
        setLocationRelativeTo(null);
    }

    private void adicionarComponentes() {
        JTabbedPane abas = new JTabbedPane();
        abas.addTab("Produtos", criarPainelProdutos());
        abas.addTab("Categorias", criarPainelCategorias());
        abas.addTab("Movimentações", criarPainelMovimentacoes());
        abas.addTab("Relatórios", criarPainelRelatorios());
        
        // Botão Limpar Tudo no canto inferior direito
        JButton botaoLimparTudo = new JButton("Limpar Tudo");
        botaoLimparTudo.setPreferredSize(new Dimension(130, 25));
        botaoLimparTudo.setMaximumSize(new Dimension(130, 25));
        botaoLimparTudo.setMinimumSize(new Dimension(130, 25));
        botaoLimparTudo.addActionListener(e -> {
            int resposta = JOptionPane.showConfirmDialog(
                this,
                "Tem certeza que deseja limpar TODOS os dados?\n\nIsso irá remover:\n- Todos os produtos\n- Todas as categorias\n- Todas as movimentações\n\nEsta ação não pode ser desfeita!",
                "Confirmar Limpeza",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
            );
            if (resposta == JOptionPane.YES_OPTION) {
                String resultado = controller.limparTudo();
                JOptionPane.showMessageDialog(this, resultado, "Limpeza", JOptionPane.INFORMATION_MESSAGE);
                // Atualizar todos os dropdowns
                SwingUtilities.invokeLater(() -> {
                    // Recarregar a interface para atualizar dropdowns
                    // Isso será feito quando o usuário mudar de aba
                });
            }
        });
        
        JPanel painelPrincipal = new JPanel(new BorderLayout());
        painelPrincipal.add(abas, BorderLayout.CENTER);
        
        // Painel para o botão no canto inferior direito
        JPanel painelBotao = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        painelBotao.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 10));
        painelBotao.add(botaoLimparTudo);
        
        painelPrincipal.add(painelBotao, BorderLayout.SOUTH);
        add(painelPrincipal);
    }

    private JPanel criarPainelProdutos() {
        JTextField campoNome = new JTextField();
        JComboBox<String> comboCategoria = new JComboBox<>();
        JTextField campoEstoqueInicial = new JTextField();
        JTextField campoEstoqueMinimo = new JTextField();
        JTextField campoPreco = new JTextField();
        JTextArea areaResultado = criarAreaResultado();

        // Carregar categorias no dropdown
        atualizarCategorias(comboCategoria);

        JButton botaoCadastrar = new JButton("Cadastrar Produto");
        botaoCadastrar.addActionListener(e -> {
            try {
                String categoriaSelecionada = (String) comboCategoria.getSelectedItem();
                if (categoriaSelecionada == null || categoriaSelecionada.isEmpty() || 
                    categoriaSelecionada.equals("(Nenhuma categoria cadastrada)") ||
                    categoriaSelecionada.equals("(Erro ao carregar categorias)")) {
                    JOptionPane.showMessageDialog(this, "Selecione uma categoria válida. Cadastre uma categoria primeiro na aba 'Categorias'.", "Dados inválidos", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                String resposta = controller.cadastrarProduto(
                        campoNome.getText(),
                        categoriaSelecionada,
                        Integer.parseInt(campoEstoqueInicial.getText()),
                        Integer.parseInt(campoEstoqueMinimo.getText()),
                        Double.parseDouble(campoPreco.getText())
                );
                areaResultado.setText(resposta);
                // Atualizar categorias após cadastrar (caso tenha sido criada uma nova)
                atualizarCategorias(comboCategoria);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Estoque inicial, estoque mínimo e preço devem ser numéricos.", "Dados inválidos", JOptionPane.WARNING_MESSAGE);
            }
        });

        JButton botaoListar = new JButton("Listar Produtos");
        botaoListar.addActionListener(e -> atualizarAreaComLista(areaResultado, controller.listarProdutos()));

        // Botão pequeno de atualizar categorias ao lado do dropdown
        JButton botaoAtualizarCategorias = new JButton("🔄");
        botaoAtualizarCategorias.setPreferredSize(new Dimension(30, 25));
        botaoAtualizarCategorias.setMaximumSize(new Dimension(30, 25));
        botaoAtualizarCategorias.setMinimumSize(new Dimension(30, 25));
        botaoAtualizarCategorias.setToolTipText("Atualizar categorias");
        botaoAtualizarCategorias.addActionListener(e -> atualizarCategorias(comboCategoria));

        // Criar formulário com GridBagLayout para suportar JComboBox
        JPanel formulario = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.2;
        formulario.add(new JLabel("Nome"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.8;
        formulario.add(campoNome, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        gbc.weightx = 0.2;
        formulario.add(new JLabel("Categoria"), gbc);

        // Painel para categoria com dropdown e botão lado a lado
        JPanel painelCategoria = new JPanel(new BorderLayout(5, 0));
        painelCategoria.add(comboCategoria, BorderLayout.CENTER);
        painelCategoria.add(botaoAtualizarCategorias, BorderLayout.EAST);
        
        gbc.gridx = 1;
        gbc.weightx = 0.8;
        formulario.add(painelCategoria, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        gbc.weightx = 0.2;
        formulario.add(new JLabel("Estoque inicial"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.8;
        formulario.add(campoEstoqueInicial, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        gbc.weightx = 0.2;
        formulario.add(new JLabel("Estoque mínimo"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.8;
        formulario.add(campoEstoqueMinimo, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        gbc.weightx = 0.2;
        formulario.add(new JLabel("Preço"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.8;
        formulario.add(campoPreco, gbc);
        formulario.setBorder(BorderFactory.createTitledBorder("Dados"));

        JPanel botoes = criarPainelBotoes(botaoCadastrar, botaoListar);

        return montarPainelCompleto(formulario, botoes, areaResultado);
    }

    private JPanel criarPainelCategorias() {
        JTextField campoNome = new JTextField();
        JComboBox<String> comboTamanho = new JComboBox<>(new String[]{"PEQUENO", "MEDIO", "GRANDE"});
        JComboBox<String> comboEmbalagem = new JComboBox<>(new String[]{"LATA", "VIDRO", "PLASTICO"});
        JTextArea areaResultado = criarAreaResultado();

        JButton botaoCadastrar = new JButton("Cadastrar Categoria");
        botaoCadastrar.addActionListener(e -> {
            String resposta = controller.cadastrarCategoria(
                    campoNome.getText(), 
                    (String) comboTamanho.getSelectedItem(),
                    (String) comboEmbalagem.getSelectedItem()
            );
            areaResultado.setText(resposta);
        });

        JButton botaoListar = new JButton("Listar Categorias");
        botaoListar.addActionListener(e -> atualizarAreaComLista(areaResultado, controller.listarCategorias()));

        // Criar formulário com GridBagLayout para suportar JComboBox
        JPanel formulario = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.2;
        formulario.add(new JLabel("Nome"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.8;
        formulario.add(campoNome, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        gbc.weightx = 0.2;
        formulario.add(new JLabel("Tamanho"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.8;
        formulario.add(comboTamanho, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        gbc.weightx = 0.2;
        formulario.add(new JLabel("Embalagem"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.8;
        formulario.add(comboEmbalagem, gbc);
        formulario.setBorder(BorderFactory.createTitledBorder("Dados"));

        JPanel botoes = criarPainelBotoes(botaoCadastrar, botaoListar);

        return montarPainelCompleto(formulario, botoes, areaResultado);
    }

    private JPanel criarPainelMovimentacoes() {
        JComboBox<String> comboProduto = new JComboBox<>();
        JComboBox<String> comboTipo = new JComboBox<>(new String[]{"ENTRADA", "SAIDA"});
        JTextField campoQuantidade = new JTextField();
        JTextField campoObservacao = new JTextField();
        JTextArea areaResultado = criarAreaResultado();

        // Carregar produtos no dropdown
        atualizarProdutos(comboProduto);

        JButton botaoRegistrar = new JButton("Registrar Movimentação");
        botaoRegistrar.addActionListener(e -> {
            try {
                String produtoSelecionado = (String) comboProduto.getSelectedItem();
                if (produtoSelecionado == null || produtoSelecionado.isEmpty() || 
                    produtoSelecionado.equals("(Nenhum produto cadastrado)") ||
                    produtoSelecionado.equals("(Erro ao carregar produtos)")) {
                    JOptionPane.showMessageDialog(this, "Selecione um produto válido. Cadastre um produto primeiro na aba 'Produtos'.", "Dados inválidos", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                // Extrair apenas o nome do produto (antes do " - ")
                String nomeProduto = produtoSelecionado.split(" - ")[0].trim();
                String resposta = controller.registrarMovimentacao(
                        nomeProduto,
                        (String) comboTipo.getSelectedItem(),
                        Integer.parseInt(campoQuantidade.getText()),
                        campoObservacao.getText()
                );
                areaResultado.setText(resposta);
                // Atualizar produtos após registrar (caso tenha sido criado um novo)
                atualizarProdutos(comboProduto);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Quantidade deve ser numérica.", "Dados inválidos", JOptionPane.WARNING_MESSAGE);
            }
        });

        JButton botaoListar = new JButton("Listar Movimentações");
        botaoListar.addActionListener(e -> atualizarAreaComLista(areaResultado, controller.listarMovimentacoes()));

        // Botão pequeno de atualizar produtos ao lado do dropdown
        JButton botaoAtualizarProdutos = new JButton("🔄");
        botaoAtualizarProdutos.setPreferredSize(new Dimension(30, 25));
        botaoAtualizarProdutos.setMaximumSize(new Dimension(30, 25));
        botaoAtualizarProdutos.setMinimumSize(new Dimension(30, 25));
        botaoAtualizarProdutos.setToolTipText("Atualizar produtos");
        botaoAtualizarProdutos.addActionListener(e -> atualizarProdutos(comboProduto));

        JPanel formulario = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.2;
        formulario.add(new JLabel("Produto"), gbc);

        // Painel para produto com dropdown e botão lado a lado
        JPanel painelProduto = new JPanel(new BorderLayout(5, 0));
        painelProduto.add(comboProduto, BorderLayout.CENTER);
        painelProduto.add(botaoAtualizarProdutos, BorderLayout.EAST);
        
        gbc.gridx = 1;
        gbc.weightx = 0.8;
        formulario.add(painelProduto, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        gbc.weightx = 0.2;
        formulario.add(new JLabel("Tipo"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.8;
        formulario.add(comboTipo, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        gbc.weightx = 0.2;
        formulario.add(new JLabel("Quantidade"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.8;
        formulario.add(campoQuantidade, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        gbc.weightx = 0.2;
        formulario.add(new JLabel("Observação"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.8;
        formulario.add(campoObservacao, gbc);
        formulario.setBorder(BorderFactory.createTitledBorder("Dados da movimentação"));

        JPanel botoes = criarPainelBotoes(botaoRegistrar, botaoListar);
        return montarPainelCompleto(formulario, botoes, areaResultado);
    }

    private JPanel criarPainelRelatorios() {
        String[] relatorios = {
                "lista_precos",
                "balanco_fisico_financeiro",
                "produtos_abaixo_minimo",
                "quantidade_por_categoria",
                "produto_maior_movimentacao"
        };

        JComboBox<String> comboRelatorios = new JComboBox<>(relatorios);
        
        // Tabela para exibir os relatórios
        DefaultTableModel modeloTabela = new DefaultTableModel();
        JTable tabela = new JTable(modeloTabela);
        tabela.setFillsViewportHeight(true);
        tabela.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        JScrollPane scrollTabela = new JScrollPane(tabela);
        scrollTabela.setPreferredSize(new Dimension(0, 200));

        // Painel para gráfico
        GraficoPanel grafico = new GraficoPanel("", "barras");
        grafico.setPreferredSize(new Dimension(0, 300));

        JButton botaoGerar = new JButton("Gerar Relatório");
        botaoGerar.addActionListener(e -> {
            String tipoRelatorio = (String) comboRelatorios.getSelectedItem();
            String resultado = controller.gerarRelatorio(tipoRelatorio);
            atualizarTabelaRelatorio(modeloTabela, tipoRelatorio, resultado);
            atualizarGraficoRelatorio(grafico, tipoRelatorio, resultado);
        });

        JPanel painel = new JPanel(new BorderLayout(8, 8));
        JPanel topo = new JPanel(new GridLayout(2, 1, 8, 8));
        topo.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        topo.add(new JLabel("Selecione o relatório desejado:"));
        topo.add(comboRelatorios);
        
        // Painel central com gráfico e tabela
        JPanel painelCentral = new JPanel(new BorderLayout(8, 8));
        painelCentral.add(grafico, BorderLayout.NORTH);
        painelCentral.add(scrollTabela, BorderLayout.CENTER);
        
        painel.add(topo, BorderLayout.NORTH);
        painel.add(painelCentral, BorderLayout.CENTER);
        painel.add(botaoGerar, BorderLayout.SOUTH);
        return painel;
    }

    /**
     * Atualiza a tabela com os dados do relatório parseados
     */
    private void atualizarTabelaRelatorio(DefaultTableModel modelo, String tipoRelatorio, String resultado) {
        modelo.setRowCount(0);
        modelo.setColumnCount(0);
        
        if (resultado == null || resultado.trim().isEmpty() || resultado.startsWith("ERROR") || resultado.startsWith("Falha")) {
            modelo.setColumnIdentifiers(new Object[]{"Mensagem"});
            modelo.addRow(new Object[]{resultado != null ? resultado : "Nenhum dado disponível"});
            return;
        }
        
        String[] linhas = resultado.split("\\r?\\n");
        
        switch (tipoRelatorio) {
            case "lista_precos":
                parsearListaPrecos(modelo, linhas);
                break;
            case "balanco_fisico_financeiro":
                parsearBalancoFisicoFinanceiro(modelo, linhas);
                break;
            case "produtos_abaixo_minimo":
                parsearProdutosAbaixoMinimo(modelo, linhas);
                break;
            case "quantidade_por_categoria":
                parsearQuantidadePorCategoria(modelo, linhas);
                break;
            case "produto_maior_movimentacao":
                parsearProdutoMaisMovimentacoes(modelo, linhas);
                break;
            default:
                modelo.setColumnIdentifiers(new Object[]{"Dados"});
                for (String linha : linhas) {
                    if (!linha.trim().isEmpty() && !linha.trim().startsWith("===") && !linha.trim().startsWith("-")) {
                        modelo.addRow(new Object[]{linha.trim()});
                    }
                }
        }
    }

    private void parsearListaPrecos(DefaultTableModel modelo, String[] linhas) {
        modelo.setColumnIdentifiers(new Object[]{"Produto", "Preço", "Unidade", "Categoria"});
        
        for (String linha : linhas) {
            if (linha.trim().isEmpty() || linha.trim().startsWith("===") || linha.trim().startsWith("-") || 
                linha.trim().startsWith("PRODUTO")) {
                continue;
            }
            
            // Formato: "Nome | R$ X.XX | Unidade | Categoria"
            String[] partes = linha.split("\\|");
            if (partes.length >= 4) {
                String produto = partes[0].trim();
                String preco = partes[1].trim();
                String unidade = partes[2].trim();
                String categoria = partes[3].trim();
                modelo.addRow(new Object[]{produto, preco, unidade, categoria});
            }
        }
    }

    private void parsearBalancoFisicoFinanceiro(DefaultTableModel modelo, String[] linhas) {
        modelo.setColumnIdentifiers(new Object[]{"Produto", "Quantidade", "Valor Unitário", "Valor Total"});
        
        for (String linha : linhas) {
            if (linha.trim().isEmpty() || linha.trim().startsWith("===") || linha.trim().startsWith("-") || 
                linha.trim().startsWith("PRODUTO") || linha.trim().startsWith("TOTAL")) {
                continue;
            }
            
            // Formato: "Nome | Quantidade | R$ X.XX | R$ X.XX"
            String[] partes = linha.split("\\|");
            if (partes.length >= 4) {
                String produto = partes[0].trim();
                String quantidade = partes[1].trim();
                String valorUnitario = partes[2].trim();
                String valorTotal = partes[3].trim();
                modelo.addRow(new Object[]{produto, quantidade, valorUnitario, valorTotal});
            }
        }
        
        // Adicionar linha de total
        for (String linha : linhas) {
            if (linha.trim().startsWith("TOTAL")) {
                String[] partes = linha.split("\\|");
                if (partes.length >= 4) {
                    modelo.addRow(new Object[]{"TOTAL GERAL", "", "", partes[3].trim()});
                }
                break;
            }
        }
    }

    private void parsearProdutosAbaixoMinimo(DefaultTableModel modelo, String[] linhas) {
        modelo.setColumnIdentifiers(new Object[]{"Produto", "Estoque", "Mínimo"});
        
        for (String linha : linhas) {
            if (linha.trim().isEmpty() || linha.trim().startsWith("===") || linha.trim().startsWith("-") || 
                linha.trim().startsWith("PRODUTO") || linha.trim().startsWith("✓")) {
                continue;
            }
            
            // Formato: "Nome | Estoque | Mínimo"
            String[] partes = linha.split("\\|");
            if (partes.length >= 3) {
                String produto = partes[0].trim();
                String estoque = partes[1].trim();
                String minimo = partes[2].trim();
                modelo.addRow(new Object[]{produto, estoque, minimo});
            }
        }
    }

    private void parsearQuantidadePorCategoria(DefaultTableModel modelo, String[] linhas) {
        modelo.setColumnIdentifiers(new Object[]{"Categoria", "Qtd Produtos", "Total Unidades"});
        
        for (String linha : linhas) {
            if (linha.trim().isEmpty() || linha.trim().startsWith("===") || linha.trim().startsWith("-") || 
                linha.trim().startsWith("CATEGORIA")) {
                continue;
            }
            
            // Formato: "Categoria | Qtd Produtos | Total Unidades"
            String[] partes = linha.split("\\|");
            if (partes.length >= 3) {
                String categoria = partes[0].trim();
                String qtdProdutos = partes[1].trim();
                String totalUnidades = partes[2].trim();
                modelo.addRow(new Object[]{categoria, qtdProdutos, totalUnidades});
            }
        }
    }

    /**
     * Atualiza o gráfico com os dados do relatório
     */
    private void atualizarGraficoRelatorio(GraficoPanel grafico, String tipoRelatorio, String resultado) {
        if (resultado == null || resultado.trim().isEmpty() || resultado.startsWith("ERROR") || resultado.startsWith("Falha")) {
            grafico.setDados(new ArrayList<>());
            return;
        }

        String[] linhas = resultado.split("\\r?\\n");
        List<GraficoPanel.DadoGrafico> dadosGrafico = new ArrayList<>();

        switch (tipoRelatorio) {
            case "lista_precos":
                dadosGrafico = extrairDadosListaPrecos(linhas);
                grafico.setTitulo("Lista de Preços");
                grafico.setTipoGrafico("barras");
                break;
            case "balanco_fisico_financeiro":
                dadosGrafico = extrairDadosBalanco(linhas);
                grafico.setTitulo("Balanço Físico/Financeiro");
                grafico.setTipoGrafico("barras");
                break;
            case "produtos_abaixo_minimo":
                dadosGrafico = extrairDadosAbaixoMinimo(linhas);
                grafico.setTitulo("Produtos Abaixo do Mínimo");
                grafico.setTipoGrafico("barras");
                break;
            case "quantidade_por_categoria":
                dadosGrafico = extrairDadosQuantidadeCategoria(linhas);
                grafico.setTitulo("Quantidade por Categoria");
                grafico.setTipoGrafico("pizza");
                break;
            case "produto_maior_movimentacao":
                dadosGrafico = extrairDadosMaisMovimentacoes(linhas);
                grafico.setTitulo("Produto com Mais Movimentações");
                grafico.setTipoGrafico("barras");
                break;
        }

        grafico.setDados(dadosGrafico);
    }

    private List<GraficoPanel.DadoGrafico> extrairDadosListaPrecos(String[] linhas) {
        List<GraficoPanel.DadoGrafico> dados = new ArrayList<>();
        for (String linha : linhas) {
            if (linha.trim().isEmpty() || linha.trim().startsWith("===") || linha.trim().startsWith("-") || 
                linha.trim().startsWith("PRODUTO")) {
                continue;
            }
            String[] partes = linha.split("\\|");
            if (partes.length >= 2) {
                String produto = partes[0].trim();
                String precoStr = partes[1].replace("R$", "").trim();
                try {
                    double preco = Double.parseDouble(precoStr);
                    dados.add(new GraficoPanel.DadoGrafico(produto, preco));
                } catch (NumberFormatException e) {
                    // Ignorar
                }
            }
        }
        return dados;
    }

    private List<GraficoPanel.DadoGrafico> extrairDadosBalanco(String[] linhas) {
        List<GraficoPanel.DadoGrafico> dados = new ArrayList<>();
        for (String linha : linhas) {
            if (linha.trim().isEmpty() || linha.trim().startsWith("===") || linha.trim().startsWith("-") || 
                linha.trim().startsWith("PRODUTO") || linha.trim().startsWith("TOTAL")) {
                continue;
            }
            String[] partes = linha.split("\\|");
            if (partes.length >= 4) {
                String produto = partes[0].trim();
                String valorTotalStr = partes[3].replace("R$", "").trim();
                try {
                    double valorTotal = Double.parseDouble(valorTotalStr);
                    dados.add(new GraficoPanel.DadoGrafico(produto, valorTotal));
                } catch (NumberFormatException e) {
                    // Ignorar
                }
            }
        }
        return dados;
    }

    private List<GraficoPanel.DadoGrafico> extrairDadosAbaixoMinimo(String[] linhas) {
        List<GraficoPanel.DadoGrafico> dados = new ArrayList<>();
        for (String linha : linhas) {
            if (linha.trim().isEmpty() || linha.trim().startsWith("===") || linha.trim().startsWith("-") || 
                linha.trim().startsWith("PRODUTO") || linha.trim().startsWith("✓")) {
                continue;
            }
            String[] partes = linha.split("\\|");
            if (partes.length >= 3) {
                String produto = partes[0].trim();
                try {
                    int estoque = Integer.parseInt(partes[1].trim());
                    int minimo = Integer.parseInt(partes[2].trim());
                    // Mostrar diferença (quanto está abaixo)
                    dados.add(new GraficoPanel.DadoGrafico(produto, minimo - estoque));
                } catch (NumberFormatException e) {
                    // Ignorar
                }
            }
        }
        return dados;
    }

    private List<GraficoPanel.DadoGrafico> extrairDadosQuantidadeCategoria(String[] linhas) {
        List<GraficoPanel.DadoGrafico> dados = new ArrayList<>();
        for (String linha : linhas) {
            if (linha.trim().isEmpty() || linha.trim().startsWith("===") || linha.trim().startsWith("-") || 
                linha.trim().startsWith("CATEGORIA")) {
                continue;
            }
            String[] partes = linha.split("\\|");
            if (partes.length >= 3) {
                String categoria = partes[0].trim();
                try {
                    int totalUnidades = Integer.parseInt(partes[2].trim());
                    dados.add(new GraficoPanel.DadoGrafico(categoria, totalUnidades));
                } catch (NumberFormatException e) {
                    // Ignorar
                }
            }
        }
        return dados;
    }

    private List<GraficoPanel.DadoGrafico> extrairDadosMaisMovimentacoes(String[] linhas) {
        List<GraficoPanel.DadoGrafico> dados = new ArrayList<>();
        String produtoMaisEntrada = null;
        String produtoMaisSaida = null;
        int qtdEntradas = 0;
        int qtdSaidas = 0;

        for (String linha : linhas) {
            if (linha.contains("Mais Entradas")) {
                String[] partes = linha.split("\\|");
                if (partes.length >= 2) {
                    produtoMaisEntrada = partes[1].trim();
                }
            } else if (linha.contains("Mais Saídas")) {
                String[] partes = linha.split("\\|");
                if (partes.length >= 2) {
                    produtoMaisSaida = partes[1].trim();
                }
            } else if (linha.contains("Total de Entradas")) {
                String[] partes = linha.split(":");
                if (partes.length >= 2) {
                    try {
                        qtdEntradas = Integer.parseInt(partes[1].trim());
                    } catch (NumberFormatException e) {
                        // Ignorar
                    }
                }
            } else if (linha.contains("Total de Saídas")) {
                String[] partes = linha.split(":");
                if (partes.length >= 2) {
                    try {
                        qtdSaidas = Integer.parseInt(partes[1].trim());
                    } catch (NumberFormatException e) {
                        // Ignorar
                    }
                }
            }
        }

        if (produtoMaisEntrada != null && !produtoMaisEntrada.equals("N/A") && qtdEntradas > 0) {
            dados.add(new GraficoPanel.DadoGrafico(produtoMaisEntrada + " (Entradas)", qtdEntradas));
        }
        if (produtoMaisSaida != null && !produtoMaisSaida.equals("N/A") && qtdSaidas > 0) {
            dados.add(new GraficoPanel.DadoGrafico(produtoMaisSaida + " (Saídas)", qtdSaidas));
        }

        return dados;
    }

    private void parsearProdutoMaisMovimentacoes(DefaultTableModel modelo, String[] linhas) {
        modelo.setColumnIdentifiers(new Object[]{"Tipo", "Produto", "Quantidade"});
        
        String produtoMaisEntrada = "N/A";
        String produtoMaisSaida = "N/A";
        int qtdEntradas = 0;
        int qtdSaidas = 0;
        
        for (String linha : linhas) {
            if (linha.trim().isEmpty() || linha.trim().startsWith("===") || linha.trim().startsWith("-") || 
                linha.trim().startsWith("TIPO")) {
                continue;
            }
            
            if (linha.contains("Mais Entradas")) {
                String[] partes = linha.split("\\|");
                if (partes.length >= 2) {
                    produtoMaisEntrada = partes[1].trim();
                }
            } else if (linha.contains("Mais Saídas")) {
                String[] partes = linha.split("\\|");
                if (partes.length >= 2) {
                    produtoMaisSaida = partes[1].trim();
                }
            } else if (linha.contains("Total de Entradas")) {
                // Extrair quantidade
                String[] partes = linha.split(":");
                if (partes.length >= 2) {
                    try {
                        qtdEntradas = Integer.parseInt(partes[1].trim());
                    } catch (NumberFormatException e) {
                        // Ignorar
                    }
                }
            } else if (linha.contains("Total de Saídas")) {
                // Extrair quantidade
                String[] partes = linha.split(":");
                if (partes.length >= 2) {
                    try {
                        qtdSaidas = Integer.parseInt(partes[1].trim());
                    } catch (NumberFormatException e) {
                        // Ignorar
                    }
                }
            }
        }
        
        if (!produtoMaisEntrada.equals("N/A")) {
            modelo.addRow(new Object[]{"Mais Entradas", produtoMaisEntrada, qtdEntradas > 0 ? String.valueOf(qtdEntradas) : ""});
        }
        if (!produtoMaisSaida.equals("N/A")) {
            modelo.addRow(new Object[]{"Mais Saídas", produtoMaisSaida, qtdSaidas > 0 ? String.valueOf(qtdSaidas) : ""});
        }
    }

    private JPanel criarFormularioPadrao(String[] labels, JTextField[] campos) {
        JPanel formulario = new JPanel(new GridLayout(labels.length, 2, 8, 8));
        for (int i = 0; i < labels.length; i++) {
            formulario.add(new JLabel(labels[i]));
            formulario.add(campos[i]);
        }
        formulario.setBorder(BorderFactory.createTitledBorder("Dados"));
        return formulario;
    }

    private JPanel criarPainelBotoes(JButton... botoes) {
        JPanel painel = new JPanel(new GridLayout(1, botoes.length, 4, 4));
        for (JButton botao : botoes) {
            botao.setPreferredSize(new Dimension(120, 25));
            botao.setMaximumSize(new Dimension(120, 25));
            botao.setMinimumSize(new Dimension(120, 25));
            painel.add(botao);
        }
        painel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        return painel;
    }

    private JPanel montarPainelCompleto(JPanel formulario, JPanel botoes, JTextArea areaResultado) {
        JPanel painel = new JPanel(new BorderLayout());
        painel.add(formulario, BorderLayout.NORTH);
        painel.add(botoes, BorderLayout.CENTER);
        JScrollPane scrollLog = new JScrollPane(areaResultado);
        scrollLog.setPreferredSize(new Dimension(0, 200)); // Altura mínima de 200px
        scrollLog.setMinimumSize(new Dimension(0, 150));
        painel.add(scrollLog, BorderLayout.SOUTH);
        return painel;
    }

    private JTextArea criarAreaResultado() {
        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setBorder(BorderFactory.createTitledBorder("Server Log"));
        return area;
    }

    private void atualizarAreaComLista(JTextArea area, List<String> linhas) {
        if (linhas == null || linhas.isEmpty()) {
            area.setText("Nenhum dado retornado.");
        } else {
            area.setText(String.join(System.lineSeparator(), linhas));
        }
    }

    /**
     * Carrega as categorias do servidor e atualiza o dropdown.
     * Extrai apenas o nome da categoria do formato retornado pelo servidor.
     */
    private void atualizarCategorias(JComboBox<String> combo) {
        combo.removeAllItems();
        try {
            List<String> categorias = controller.listarCategorias();
            
            if (categorias != null && !categorias.isEmpty()) {
                for (String linha : categorias) {
                    if (linha != null && !linha.trim().isEmpty() && !linha.trim().startsWith("ERROR")) {
                        // Formato do servidor: "Nome - Tamanho | Embalagem"
                        // Extrair apenas o nome (antes do " - ")
                        String nomeCategoria = linha.split(" - ")[0].trim();
                        if (!nomeCategoria.isEmpty() && !nomeCategoria.startsWith("ERROR")) {
                            combo.addItem(nomeCategoria);
                        }
                    }
                }
            }
            
            // Se não houver categorias, adicionar item vazio
            if (combo.getItemCount() == 0) {
                combo.addItem("(Nenhuma categoria cadastrada)");
            }
        } catch (Exception e) {
            combo.addItem("(Erro ao carregar categorias)");
        }
    }

    /**
     * Carrega os produtos do servidor e atualiza o dropdown.
     * Mantém o formato completo retornado pelo servidor para melhor visualização.
     */
    private void atualizarProdutos(JComboBox<String> combo) {
        combo.removeAllItems();
        try {
            List<String> produtos = controller.listarProdutos();
            
            if (produtos != null && !produtos.isEmpty()) {
                for (String linha : produtos) {
                    if (linha != null) {
                        String linhaLimpa = linha.trim();
                        // Ignorar linhas vazias, erros ou mensagens de falha
                        if (linhaLimpa.isEmpty() || 
                            linhaLimpa.startsWith("ERROR") || 
                            linhaLimpa.startsWith("Falha na comunicação") ||
                            linhaLimpa.startsWith("SUCCESS|") && linhaLimpa.length() == 8) {
                            continue;
                        }
                        // Se a linha começa com "SUCCESS|", remover esse prefixo
                        if (linhaLimpa.startsWith("SUCCESS|")) {
                            linhaLimpa = linhaLimpa.substring(8).trim();
                        }
                        // Adicionar apenas se não estiver vazia após limpeza
                        if (!linhaLimpa.isEmpty()) {
                            combo.addItem(linhaLimpa);
                        }
                    }
                }
            }
            
            // Se não houver produtos, adicionar item vazio
            if (combo.getItemCount() == 0) {
                combo.addItem("(Nenhum produto cadastrado)");
            }
        } catch (Exception e) {
            combo.addItem("(Erro ao carregar produtos)");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MenuPrincipal().setVisible(true));
    }
}

