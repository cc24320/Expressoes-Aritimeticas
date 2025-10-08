// Main.java
import java.util.*;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.print("Digite uma expressão aritmética: ");
        String entrada = sc.nextLine();

        // Usa a classe AvaliacaoExpressoesAritmeticas
        List<AvaliacaoExpressoesAritmeticas.No> vetor = AvaliacaoExpressoesAritmeticas.fragmentar(entrada);
        AvaliacaoExpressoesAritmeticas.No raiz = AvaliacaoExpressoesAritmeticas.construirArvore(vetor);
        double resultado = AvaliacaoExpressoesAritmeticas.avaliar(raiz);

        System.out.println("\nÁrvore binária da expressão:");
        AvaliacaoExpressoesAritmeticas.exibirArvore(raiz, 0);

        System.out.println("\nResultado final: " + resultado);
        sc.close();
    }
}
