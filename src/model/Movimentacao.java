public class Movimentacao {
    private Produto produto;
    private LocalDateTime data;
    private int quantidade;
    private TipoMovimentacao tipo;

    public enum TipoMovimentacao {
        ENTRADA,
        SAIDA
    }

    public void validarMovimentacao() throws Exception {
        if (tipo == TipoMovimentacao.SAIDA) {
            int novaQuantidade = produto.getQuantidadeEstoque() - quantidade;
            if (novaQuantidade < produto.getQuantidadeMinima()) {
                throw new Exception("Atenção: Pouco estoque após a movimentação!");
        }
        } else {
            int novaQuantidade = produto.getQuantidadeEstoque() + quantidade;
            if (novaQuantidade > produto.getQuantidadeMaxima()) {
                throw new Exception("Atenção: Excede a capacidade máxima de estoque!");
            }
        }
    }
}