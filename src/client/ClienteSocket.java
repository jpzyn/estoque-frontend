package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.StringJoiner;

/**
 * Camada responsável por estabelecer a comunicação socket com o back-end.
 * O protocolo utilizado é textual e bem simples:
 * cada requisição é formada por pares chave=valor separados por ponto e vírgula.
 *
 * Exemplo: {@code acao=listarProdutos;categoriaId=12}
 */
public class ClienteSocket {

    private final String host;
    private final int port;

    public ClienteSocket() {
        this("localhost", 12345);
    }

    public ClienteSocket(String host, int port) {
        this.host = host;
        this.port = port;
    }

    /**
     * Envia uma requisição ao servidor e retorna a resposta em texto.
     *
     * @param acao  nome da ação conhecida pelo back-end
     * @param dados parâmetros adicionais
     * @return resposta textual do servidor
     * @throws IOException caso a conexão falhe
     */
    public String enviar(String acao, Map<String, String> dados) throws IOException {
        String payload = montarPayload(acao, dados);
        Socket socket = new Socket(host, port);
        try {
            // Configurar timeout para evitar travamento
            socket.setSoTimeout(5000); // 5 segundos
            
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));

            writer.println(payload);
            writer.flush();

            // Ler a resposta do servidor
            // O servidor envia a resposta completa e uma linha vazia no final
            StringBuilder resposta = new StringBuilder();
            String linha;
            boolean primeiraLinha = true;
            
            try {
                while ((linha = reader.readLine()) != null) {
                    // Linha vazia indica fim da resposta
                    if (linha.isEmpty()) {
                        break;
                    }
                    if (!primeiraLinha) {
                        resposta.append(System.lineSeparator());
                    }
                    resposta.append(linha);
                    primeiraLinha = false;
                }
            } catch (java.net.SocketTimeoutException e) {
                // Timeout: retorna o que já foi lido
            }
            
            return resposta.toString();
        } finally {
            socket.close();
        }
    }

    private String montarPayload(String acao, Map<String, String> dados) {
        StringJoiner joiner = new StringJoiner(";");
        joiner.add("acao=" + acao);
        if (dados != null) {
            dados.forEach((chave, valor) -> joiner.add(chave + "=" + valor));
        }
        return joiner.toString();
    }
}

