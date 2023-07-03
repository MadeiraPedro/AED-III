package Compressores;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

// Classe Nodo da árvore
class Node 
{
    public char character;
    public int frequency;
    public Node leftNode = null;
    public Node rightNode = null;

    // Construtor da classe Node
    public Node(char character, int frequency)
    {
        this.character = character;
        this.frequency = frequency;
    }

    // Construtor da classe Node
    public Node(char character, int frequency, Node leftNode, Node rightNode)
    {
        this.character = character;
        this.frequency = frequency;
        this.leftNode = leftNode;
        this.rightNode = rightNode;
    }

}  
    
// Classe principal
public class HuffmanCode 
{   
    static RandomAccessFile arq, arqInd;

    public static String readFile(String path, Charset encoding) throws IOException
    {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }

    public static void init(String filePath, int returnType) throws IOException {

        String content = null;

        if (returnType == 1) {
            try {
                content = readFile(filePath, StandardCharsets.UTF_8); // recebendo o conteudo do arquivo em formato de string
            } catch (IOException e) {
                System.out.println("\nErro! Nao foi possivel ler o arquivo ("+ filePath +")\n");
                e.printStackTrace();
            }

            String encoded = createHuffmanTree(content, returnType);
            byte[] ba = encoded.getBytes();
            arq = new RandomAccessFile(filePath.replace(".db", ".hff"), "rw");
            arq.write(ba);
            arq.close();

            System.out.println("\nArquivo comprimido (" + filePath.replace(".db", ".hff") +") gerado com sucesso!");

        } else if (returnType == 2) {

            System.out.println("\nDescompressao ainda nao implementada!\n");

            // BufferedReader br = new BufferedReader(new FileReader(filePath.replace(".db", ".hff"))); //lendo arquivo de dados
		    // content = br.readLine();

            // nao foi finalizado.

            // byte[] ba = decoded.getBytes();
            // arq = new RandomAccessFile(filePath, "rw");
            // arq.write(ba);
            // arq.close();
        }
    }

    //-------------------------
    // CRIA A ARVORE DE HUFFMAN
    private static String createHuffmanTree(String text, int returnType) { //chave de retorno, 1 =  retornar String codificada | 2 = retornar decodificada

        String encoded = "";
        String decoded = "";

        // Em caso de texto vazio 
        if (text == null || text.length() == 0) {  
            return "";  
        }
    
        Map<Character, Integer> frequency = new HashMap<>();

        for (char c: text.toCharArray()) {  
  
            frequency.put(c, frequency.getOrDefault(c, 0) + 1);  
        }  

        PriorityQueue<Node> pq = new PriorityQueue<>(Comparator.comparingInt(l -> l.frequency));  
        // O loop itera sobre o mapa e retorna uma visualização Set dos mapeamentos contidos neste mapa  
        for (Map.Entry<Character, Integer> entry: frequency.entrySet()) {  
            // cria uma folha e a adiciona em uma fila 
            pq.add(new Node(entry.getKey(), entry.getValue()));  
        }  
        // Loop while é executado até que haja mais de um nó na fila  
        while (pq.size() != 1) {  
            // Removendo os nós com a prioridade mais alta (a frequência mais baixa) da fila  
            Node left = pq.poll();  
            Node right = pq.poll();  
            // Cria um novo nó interno com esses dois nós como filhos e com frequência igual à soma das frequências de ambos os nós. Adiciona o novo nó à fila de prioridade.
               
            int sum = left.frequency + right.frequency; // Soma a frequência dos nós (esquerda e direita) que excluímos  
              
            pq.add(new Node('\0', sum, left, right)); // Adiciona um novo nó interno (nós excluídos, ou seja, direito e esquerdo) à fila com uma frequência igual à soma de ambos os nós
        }  
          
        Node root = pq.peek(); // A variável root armazena o ponteiro para a raiz da Árvore Huffman
          
        Map<Character, String> huffmanCode = new HashMap<>(); // Traça a árvore Huffman e armazena os códigos Huffman em um mapa
        encodeData(root, "", huffmanCode);  
           
        StringBuilder sb = new StringBuilder(); // Criando uma instância da classe StringBuilder
        StringBuilder sbDecode = new StringBuilder();

        // Loop for itera sobre a matriz de caracteres  
        for (char c: text.toCharArray()) {  
            // Imprime string codificada através dos caracteres   
            sb.append(huffmanCode.get(c));  
        }   

        encoded += sb; //passando valor codificado para string

        if (isLeaf(root)) {  
            // Caso especial: Para entradas como a, aa, aaa, etc  
            while (root.frequency-- > 0) {  
                System.out.print(root.character);  
            }  
        }  
        else {  
            // Atravessa a árvore Huffman novamente e, desta vez, decodifica a string codificada  
            int index = -1;  
            while (index < sb.length() - 1) {  
                index = decodeData(root, index, sb, sbDecode);  
            }  
        }

        decoded += sbDecode;

        if (returnType == 1) { return encoded; } 
        else { return decoded; }
    }     
    //-------------------------
    // Atravessa a Árvore Huffman e armazena os Códigos Huffman em um Mapa  
    // CODIFICA DADOS  
    public static void encodeData(Node root, String str, Map<Character, String> huffmanCode) {  
        if (root == null) {  
            return;  
        }  

        // Verifica se o nó é um nó folha ou não  
        if (isLeaf(root)) {  
            huffmanCode.put(root.character, str.length() > 0 ? str : "1");  
        }  
        encodeData(root.leftNode, str + '0', huffmanCode);  
        encodeData(root.rightNode, str + '1', huffmanCode);  
    }  
    //-------------------------
    // Percorre a Árvore Huffman e decodifica os dados codificados  
    // DECODIFICA DADOS 
    public static int decodeData(Node root, int index, StringBuilder sb, StringBuilder decoded) {  
        // Verifica se o nó raiz é nulo ou não  
        if (root == null) {  
            return index;  
        }   
        // Verifica se o nó é um nó folha ou não  
        if (isLeaf(root)) {  
            decoded.append(root.character);  
            return index;  
        }  
        index++;   
        root = (sb.charAt(index) == '0') ? root.leftNode : root.rightNode;  
        index = decodeData(root, index, sb, decoded);  
        return index;  
    }  

    // Função para verificar se a Árvore Huffman contém um único nó 
    public static boolean isLeaf(Node root) {  
        // Retorna true se ambas as condições retornarem true  
        return root.leftNode == null && root.rightNode == null;  
    }
}

