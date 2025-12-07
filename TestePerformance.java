public class TestePerformance {

    private static final int REPETICOES = 5;
    private static final int[] TAMANHOS = { 100, 1000, 10000 };

    private long sementeAleatoria = 12345; // Semente inicial fixa

    public static void main(String[] args) {
        TestePerformance tester = new TestePerformance();

        System.out.println("--- INICIANDO TESTES DE DESEMPENHO ---");

        for (int n : TAMANHOS) {
            tester.executarTestesParaTamanho(n);
        }

        System.out.println("--- TESTES CONCLUÍDOS ---");
    }

    // -----------------------------------------------------------------
    // GERAÇÃO MANUAL DE NÚMEROS PSEUDO-ALEATÓRIOS (Sem java.util.Random)
    // -----------------------------------------------------------------

    /**
     * Implementa um gerador pseudo-aleatório simples (LCG - Congruential
     * Generator).
     * Retorna o próximo inteiro pseudo-aleatório.
     */
    private int proximoNumeroAleatorio(int limite) {
        // Parâmetros de um LCG simples
        long a = 1664525;
        long c = 1013904223;
        long m = 4294967296L; // 2^32, usado implicitamente por ser um int, mas mantemos a clareza

        sementeAleatoria = (a * sementeAleatoria + c);

        // Retorna o valor absoluto do resto da divisão, garantindo que seja positivo e
        // dentro do limite
        long resultado = (sementeAleatoria % m);
        if (resultado < 0) {
            resultado = -resultado;
        }
        return (int) (resultado % limite);
    }

    private int[] copiarArray(int[] original, int novoTamanho) {
        int[] copia = new int[novoTamanho];
        int limite = (original.length < novoTamanho) ? original.length : novoTamanho;
        for (int i = 0; i < limite; i++) {
            copia[i] = original[i];
        }
        return copia;
    }

    private int[] gerarConjuntoDados(int n, String ordem) {
        int[] dados = new int[n];
        for (int i = 0; i < n; i++) {
            dados[i] = i + 1;
        }

        if (ordem.equals("Inversa")) {

            int temp;
            for (int i = 0; i < n / 2; i++) {
                temp = dados[i];
                dados[i] = dados[n - 1 - i];
                dados[n - 1 - i] = temp;
            }
        } else if (ordem.equals("Aleatoria")) {
            int temp;
            int randomIndexToSwap;
            for (int i = 0; i < n; i++) {
                randomIndexToSwap = proximoNumeroAleatorio(n);

                temp = dados[randomIndexToSwap];
                dados[randomIndexToSwap] = dados[i];
                dados[i] = temp;
            }
        }
        return dados;
    }

    public void executarTestesParaTamanho(int n) {
        String[] ordens = { "Ordenada", "Inversa", "Aleatoria" };

        System.out.println("\n#################################");
        System.out.println("## TESTES COM N = " + n + " ELEMENTOS ##");
        System.out.println("#################################");

        for (String ordem : ordens) {
            int[] dados = gerarConjuntoDados(n, ordem);

            System.out.println("\n--- Ordem de Inserção: " + ordem + " ---");

            double tempoInsercaoVetor = medirTempoInsercao(new Array(n), dados);

            double tempoInsercaoABB = medirTempoInsercao(new ArvoreBuscaBinaria(), dados);

            double tempoInsercaoAVL = medirTempoInsercao(new ArvoreAVL(), dados);
            System.out.printf("Tempo Inserção (ms) | Vetor: %.3f | ABB: %.3f | AVL: %.3f\n",
                    tempoInsercaoVetor, tempoInsercaoABB, tempoInsercaoAVL);

            Array vetorPopulada = new Array(n);
            ArvoreBuscaBinaria abbPopulada = new ArvoreBuscaBinaria();
            ArvoreAVL avlPopulada = new ArvoreAVL();

            for (int val : dados) {
                vetorPopulada.inserir(val);
                abbPopulada.inserir(val);
                avlPopulada.inserir(val);
            }

            double tempoBubble = medirTempoOrdenacao(vetorPopulada.getDados(), "Bubble");
            System.out.printf("Bubble Sort: %.3f ms\n", tempoBubble);

            double tempoMerge = medirTempoOrdenacao(vetorPopulada.getDados(), "Merge");
            System.out.printf("MergeSort: %.3f ms\n", tempoMerge);

            executarTestesBusca(n, dados, vetorPopulada, abbPopulada, avlPopulada);
        }
    }

    private double medirTempoInsercao(Object estrutura, int[] dados) {
        long tempoTotalNano = 0;

        for (int r = 0; r < REPETICOES; r++) {

            Object instancia;
            if (estrutura instanceof Array) {
                instancia = new Array(dados.length);
            } else if (estrutura instanceof ArvoreBuscaBinaria) {
                instancia = new ArvoreBuscaBinaria();
            } else { // AVLTree
                instancia = new ArvoreAVL();
            }

            long inicio = System.nanoTime();
            for (int valor : dados) {
                if (instancia instanceof Array) {
                    ((Array) instancia).inserir(valor);
                } else if (instancia instanceof ArvoreBuscaBinaria) {
                    ((ArvoreBuscaBinaria) instancia).inserir(valor);
                } else { // ArvoreAVL
                    ((ArvoreAVL) instancia).inserir(valor);
                }
            }
            long fim = System.nanoTime();
            tempoTotalNano += (fim - inicio);
        }

        return (tempoTotalNano / (double) REPETICOES) / 1_000_000.0;
    }

    /**
     * Mede o tempo médio de ordenação.
     */
    private double medirTempoOrdenacao(int[] dados, String algoritmo) {
        long tempoTotalNano = 0;

        for (int r = 0; r < REPETICOES; r++) {
            // CLONE MANUAL para garantir que o algoritmo sempre inicie com o array
            // desordenado original
            int[] dadosClonados = copiarArray(dados, dados.length);

            Array tempVetor = new Array(dadosClonados.length);
            for (int val : dadosClonados)
                tempVetor.inserir(val);

            long inicio = System.nanoTime();
            if (algoritmo.equals("Bubble")) {
                tempVetor.bubbleSort();
            } else { // Merge
                tempVetor.mergeSort();
            }
            long fim = System.nanoTime();
            tempoTotalNano += (fim - inicio);
        }

        return (tempoTotalNano / (double) REPETICOES) / 1_000_000.0;
    }

    /**
     * Mede o tempo médio de busca para uma chave específica em uma estrutura.
     */
    private double medirTempoBusca(Object estrutura, int chave, boolean isVetorOrdenado) {
        long tempoTotalNano = 0;

        for (int r = 0; r < REPETICOES; r++) {
            long inicio = System.nanoTime();

            if (estrutura instanceof Array) {
                Array vetor = (Array) estrutura;
                if (isVetorOrdenado) {
                    vetor.buscaBinaria(chave);
                } else {
                    vetor.buscaSequencial(chave);
                }
            } else if (estrutura instanceof ArvoreBuscaBinaria) {
                ((ArvoreBuscaBinaria) estrutura).buscar(chave);
            } else { // ArvoreAVL
                ((ArvoreAVL) estrutura).buscar(chave);
            }
            long fim = System.nanoTime();
            tempoTotalNano += (fim - inicio);
        }

        return (tempoTotalNano / (double) REPETICOES) / 1_000_000.0;
    }

    /**
     * Executa e exibe os resultados dos 6 cenários de busca para as 3 estruturas.
     */
    private void executarTestesBusca(int n, int[] dadosIniciais, Array vetor,
            ArvoreBuscaBinaria abb, ArvoreAVL avl) {

        int primeiro = dadosIniciais[0];
        int ultimo = dadosIniciais[n - 1];
        int meio = dadosIniciais[n / 2];
        int inexistente = n + 1;

        int aleatorio1 = dadosIniciais[1];
        int aleatorio2 = dadosIniciais[2];
        int aleatorio3 = dadosIniciais[3];

        System.out.println("\n--- MEDIÇÃO DO TEMPO DE BUSCA (ms) ---");

        System.out.printf("Vetor Sequencial | 1º: %.3f | Último: %.3f | Meio: %.3f | Al 1: %.3f | Inex: %.3f\n",
                medirTempoBusca(vetor, primeiro, false),
                medirTempoBusca(vetor, ultimo, false),
                medirTempoBusca(vetor, meio, false),
                medirTempoBusca(vetor, aleatorio1, false),
                medirTempoBusca(vetor, inexistente, false));

        // ------------------------------------
        // Vetor - Binária (O(log N))
        // (vetorPopulada)
        // ------------------------------------
        Array vetorOrdenado = new Array(n);
        for (int val : dadosIniciais)
            vetorOrdenado.inserir(val);
        vetorOrdenado.mergeSort();

        System.out.printf("Vetor Binária (Pós-Ord) | 1º: %.3f | Último: %.3f | Meio: %.3f | Al 1: %.3f | Inex: %.3f\n",
                medirTempoBusca(vetorOrdenado, primeiro, true),
                medirTempoBusca(vetorOrdenado, ultimo, true),
                medirTempoBusca(vetorOrdenado, meio, true),
                medirTempoBusca(vetorOrdenado, aleatorio1, true),
                medirTempoBusca(vetorOrdenado, inexistente, true));

        // ------------------------------------
        // ABB e AVL (O(log N) ou O(N))
        // ------------------------------------
        System.out.printf("ABB | 1º: %.3f | Último: %.3f | Meio: %.3f | Al 1: %.3f | Inex: %.3f\n",
                medirTempoBusca(abb, primeiro, false),
                medirTempoBusca(abb, ultimo, false),
                medirTempoBusca(abb, meio, false),
                medirTempoBusca(abb, aleatorio1, false),
                medirTempoBusca(abb, inexistente, false));

        System.out.printf("AVL | 1º: %.3f | Último: %.3f | Meio: %.3f | Al 1: %.3f | Inex: %.3f\n",
                medirTempoBusca(avl, primeiro, false),
                medirTempoBusca(avl, ultimo, false),
                medirTempoBusca(avl, meio, false),
                medirTempoBusca(avl, aleatorio1, false),
                medirTempoBusca(avl, inexistente, false));
    }
}