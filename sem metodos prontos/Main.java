// Main.java
import java.io.*;

public class Main {
    public static void main(String[] args) throws Exception {
        System.out.print("Digite uma expressão aritmética: ");
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String expressao = br.readLine();

        // Usa os métodos da outra classe
        AvaliacaoExpressoesAritmeticas.ListaNos vetor = AvaliacaoExpressoesAritmeticas.fragmentar(expressao);
        AvaliacaoExpressoesAritmeticas.No raiz = AvaliacaoExpressoesAritmeticas.construirArvore(vetor);

        System.out.println("\nÁrvore da expressão:");
        AvaliacaoExpressoesAritmeticas.exibirArvore(raiz, 0);

        System.out.println("\nResultado = " + AvaliacaoExpressoesAritmeticas.avaliar(raiz));
    }
}
