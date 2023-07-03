package CasamentoDePadroes;

import java.util.*;
import entidades.usuario.Movies;
import operacoes.Utils;


public class BoyerMoore {
	
	static int NO_OF_CHARS = 256;
	static boolean patternExists = false;
	static int totalComparacoes = 0;
	
	//Uma função utilitária para obter no máximo dois inteiros
	static int max (int a, int b) { return (a > b)? a: b; }

	//A função de pré-processamento para Boyer Moore's
	// heurística de mau caráter
	static void badCharHeuristic( char []str, int size,int badchar[]) {

		// Inicializa todas as ocorrências como -1
		for (int i = 0; i < NO_OF_CHARS; i++)
			badchar[i] = -1;

		// Preenche o valor real da última ocorrência
		// de um caractere (índices de tabela são ascii e valores são índice de ocorrência)
		for (int i = 0; i < size; i++)
			badchar[(int) str[i]] = i;
	}

	/* Uma função de busca de padrão que usa Bad
	 Heurística de Caracteres do Algoritmo de Boyer Moore
	*/
	static boolean search(Movies movie, String padrao) {

		char txt[] = movie.fullString().toCharArray();
		char pat[] = padrao.toCharArray();
		int m = pat.length;
		int n = txt.length;

		int badchar[] = new int[NO_OF_CHARS];

		/* Preencha a matriz de caracteres inválidos chamando
		 a função de pré-processamento badCharHeuristic()
		 para determinado padrão
		*/
		badCharHeuristic(pat, m, badchar);

		int s = 0; 
		// s é o deslocamento do padrão com
		// respeita o texto
		//existem n-m+1 alinhamentos potenciais

		while(s <= (n - m)) {
			int j = m-1;
			totalComparacoes++;

			/* Continua reduzindo o índice j do padrão enquanto
			caracteres de padrão e texto são
			combinando neste turno s
			*/
			while(j >= 0 && pat[j] == txt[s+j]) {
				j--;
				totalComparacoes++;
			}
			/* Se o padrão estiver presente no atual
			deslocamento, então o índice j se tornará -1 após
			o loop acima
			*/
			if (j < 0)
			{
				System.out.println("\nO padrao foi encontrado no shift ("+ (s) + ") no objeto de ID: " + movie.getID());
				System.out.println("Padrao procurado: " + padrao);
				System.out.println("Sequencia:\n" + movie.fullString());

				/* Muda o padrão para que o próximo
				caractere no texto se alinha com o último
				ocorrência dele no padrão.
				A condição s+m < n é necessária para
				o caso quando o padrão ocorre no final
				de texto
				*/
				//txt[s+m] é o caractere após o padrão no texto
				s += (s+m < n)? m-badchar[txt[s+m]] : 1;
				patternExists = true;
			}

			else
				/* Muda o padrão para que o personagem ruim
				no texto se alinha com a última ocorrência de
				isso em padrão. A função máxima é usada para
				certifique-se de obter uma mudança positiva.
				Podemos obter uma mudança negativa se o último
				ocorrência de mau caráter no padrão
				está do lado direito da corrente
				personagem.
				*/
				s += max(1, j - badchar[txt[s+j]]);
		}
		return patternExists; //retornando flag informando se o padrao esta ou nao contido
	}

	public static void init(ArrayList<Movies> movieList) {
		/*
		 * Inicializador da funcao de BoyerMoore
		 * Recebe uma lista de objetos (movies)
		 * pega toda sua sequencia de caracter 
		 * utilizando da funcao .fullString()
		 * e faz a busca do padrao digitado pelo usuario
		 */
		long start = System.currentTimeMillis(); //iniciando a contagem do tempo de execucao
		 
		String str = Utils.leString("sequencia que deseja procurar","A");

		for (int i = 0; i < movieList.size(); i++) {
			search(movieList.get(i), str);
		}

		System.out.println("\nNumero total de comparacoes: (" + totalComparacoes + ")\n");

		if (patternExists == false) {
			System.out.println("\nO padrao procurado nao foi encontrado em nenhuma sequencia.\n");
		}

		long end = System.currentTimeMillis() - start;
		System.out.println("\nEsta funcao foi executada em (" + end +" milissegundos)\n");
	}
}
