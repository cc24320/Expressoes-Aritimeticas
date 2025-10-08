// Main.java
import java.io.*;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        try {
            System.out.print("Digite uma expressão aritmética: ");
            String entrada = br.readLine(); // lê a linha de entrada

            // Usa a classe AvaliacaoExpressoesAritmeticas
            List<AvaliacaoExpressoesAritmeticas.No> vetor = AvaliacaoExpressoesAritmeticas.fragmentar(entrada);
            AvaliacaoExpressoesAritmeticas.No raiz = AvaliacaoExpressoesAritmeticas.construirArvore(vetor);
            double resultado = AvaliacaoExpressoesAritmeticas.avaliar(raiz);

            System.out.println("\nÁrvore binária da expressão:");
            AvaliacaoExpressoesAritmeticas.exibirArvore(raiz, 0);

            System.out.println("\nResultado final: " + resultado);
        } catch (IOException e) {
            System.out.println("Erro na leitura da entrada: " + e.getMessage());
        }
    }
}
