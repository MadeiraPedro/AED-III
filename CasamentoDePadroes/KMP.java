package CasamentoDePadroes;

import java.util.*;
import entidades.usuario.Movies;
import operacoes.Utils;

public class KMP {
    
	static boolean patternExists = false;
	static int totalComparacoes = 0;

	private static boolean KMPSearch(String pat, Movies movie)
	{
		String txt = movie.fullString();
		int M = pat.length();
		int N = txt.length();

		// cria lps[] que vai segurar o mais longo
		// valores de sufixo de prefixo para padrão
		int lps[] = new int[M];
		int j = 0; // index for pat[]

		// Pré-processa o padrão (calcula lps[] arrays)
		computeLPSArray(pat, M, lps);

		int i = 0; // index for txt[]
		while ((N - i) >= (M - j)) {
			if (pat.charAt(j) == txt.charAt(i)) {
				j++;
				i++;
			}
			if (j == M) {
				System.out.println("\nO padrao foi encontrado na posicao ("+ (i - j) + ") no objeto de ID: " + movie.getID());
				System.out.println("Padrao procurado: " + pat);
				System.out.println("Sequencia:\n" + txt);

				j = lps[j - 1];
				patternExists = true;
			}

			// incompatibilidade após j correspondências
			else if (i < N
					&& pat.charAt(j) != txt.charAt(i)) {
				// Não corresponde aos caracteres lps[0..lps[j-1]],
				// eles irão corresponder de qualquer maneira
				if (j != 0)
					j = lps[j - 1];
				else
					i = i + 1;
			}
			totalComparacoes++;
		}
		return patternExists; //retornando flag informando se o padrao esta ou nao contido
	}

	private static void computeLPSArray(String pat, int M, int lps[])
	{
		// comprimento do sufixo de prefixo mais longo anterior
		int len = 0;
		int i = 1;
		lps[0] = 0; // lps[0] é sempre 0

		// o loop calcula lps[i] para i = 1 a M-1
		while (i < M) {
			if (pat.charAt(i) == pat.charAt(len)) {
				len++;
				lps[i] = len;
				i++;
			}
			else // (pat[i] != pat[len])
			{
				// Isso é complicado. Considere o exemplo.
				// AAACAAAA e i = 7. A ideia é semelhante
				// para a etapa de pesquisa.
				if (len != 0) {
					len = lps[len - 1];

					// Além disso, observe que não incrementamos
					// eu aqui
				}
				else // if (len == 0)
				{
					lps[i] = len;
					i++;
				}
			}
		}
	}

	public static void init(ArrayList<Movies> movieList) {
		/*
		 * Inicializador da funcao de KMP
		 * Recebe uma lista de objetos (movies)
		 * pega toda sua sequencia de caracter 
		 * utilizando da funcao .fullString()
		 * e faz a busca do padrao digitado pelo usuario
		 */
		long start = System.currentTimeMillis(); //iniciando a contagem do tempo de execucao

		String str = Utils.leString("sequencia que deseja procurar","A");

		for (int i = 0; i < movieList.size(); i++) {
			KMPSearch(str, movieList.get(i));
		}

		System.out.println("\nNumero total de comparacoes: (" + totalComparacoes + ")\n");

		if (patternExists == false) {
			System.out.println("\nO padrao procurado nao foi encontrado em nenhuma sequencia.\n");
		}

		long end = System.currentTimeMillis() - start;
		System.out.println("\nEsta funcao foi executada em (" + end +" milissegundos)\n");
	}
}
