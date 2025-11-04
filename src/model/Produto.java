public class Produto {
    private String nome;
    private double precoUnitario;
    private String unidade;
    private int quantidadeEstoque;
    private int qunatidadeMinima;
    private int quantidadeMaxima;
    private Categoria categoria;

    public void reajustarPreco(double percentual) {
        this.precoUnitario += this.precoUnitario * (percentual / 100);
    }

    // Construtor, getters e setters
}