import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class WordSquare {
    static int n;
    public static void main(String argv[]) throws IOException{
        long startTime = System.nanoTime();

        String[] args = "5 aaaeeeefhhmoonssrrrrttttw".split(" ");

        n = Integer.parseInt(args[0]);
        char[] charset = args[1].toCharArray();

        Node wordTrie = new Node();
        int[] charFreq = new int[26];
        for (char c : charset)
            charFreq[c - 'a']++;

        BufferedReader br = new BufferedReader(new FileReader("words.txt"));
        String dictWord;
        while ((dictWord = br.readLine()) != null) {
            if (dictWord.length() == n && fitsInLetterBank(dictWord, charFreq)) {
                Node curNode = wordTrie;
                for (int i = 0; i < dictWord.length(); i++) {
                    int c = dictWord.charAt(i) - 'a';
                    if (curNode.children[c] == null)
                        curNode.children[c] = new Node(c);
                    curNode = curNode.children[c];
                }
            }
        }
        char[][] result = getWordSquare(wordTrie, charFreq);
        long endTime = System.nanoTime();

        if (result != null)
            for (int i = 0; i < result.length; i++)
                System.out.println(new String(result[i]));
        else
            System.out.println("No valid word square could be made.");

        System.out.println("\nExecution Time: " + Double.toString((endTime - startTime) / 1000000000.0)+"s");
    }

    static char[][] getWordSquare(Node trieRoot, int[] charFreq) {
        Node[][] mat = new Node[n][n+1];
        for (int i = 0; i < mat.length; i++)
            mat[i][0] = trieRoot;
        int[] bank = charFreq.clone();
        if (rec(0, 1, mat, bank)) {
            char[][] result = new char[n][n];
            for (int r = 0; r < n; r++)
                for (int c = 0; c < n; c++)
                    result[r][c] = (char) (mat[r][c + 1].val + 'a');
            return result;
        } else {
            return null;
        }
    }

    static boolean rec(int r, int c, Node[][] mat, int[] bank)  {
        int incrAmt = r==c-1 ? 1 : 2;

        for (int l = 0; l < 26; l++) {
            Node node = mat[r][c-1].children[l];
            Node nodeMirrorSide = mat[c-1][r].children[l];
            if (node != null && nodeMirrorSide != null && bank[l] >= incrAmt) {

                mat[r][c] = node;
                mat[c - 1][r + 1] = nodeMirrorSide;
                bank[l] -= incrAmt; 

                if (c == n) { 
                    if (r == n - 1 
                            || rec(r + 1, r + 2, mat, bank)) { 
                        return true;
                    }
                } else if (rec(r, c + 1, mat, bank)) { 
                    return true;
                }

                bank[l] += incrAmt; 
            }
        }
        return false; 
    }

    private static boolean fitsInLetterBank(String word, int[] charFreq) {
        int[] charsUsed = new int[26];
        boolean diagonalUsed = false;
        for (int i = 0; i < word.length(); i++) {
            int c = word.charAt(i) - 'a';

            int spaceLeft = charFreq[c] - charsUsed[c];
            if (spaceLeft > 1) { 
                charsUsed[c] += 2;
            } else if (spaceLeft == 1 && !diagonalUsed) { 
                charsUsed[c] += 1;
                diagonalUsed = true;
            } else { 
                return false;
            }
        }
        return true;
    }

    static class Node {
        int val;
        Node[] children;

        Node() {
            children = new Node[26];
        }
        Node(int val){
            this();
            this.val = val;
        }
    }
}