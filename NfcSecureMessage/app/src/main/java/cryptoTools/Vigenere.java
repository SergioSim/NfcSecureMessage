package cryptoTools;

import java.io.*;

public class Vigenere {
    private String[] lignes;

    /* Divise le texte en autant de lignes que longueur
       et sauvegarde le resultat */
    public Vigenere(String texte, int longueur) {
        lignes = new String[longueur];
        for (int i = 0; i < longueur; i++)
            lignes[i] = "";

        for (int i = 0; i < texte.length(); i++) {
            char c = texte.charAt(i);
            // Pour eviter de prendre en compte les espaces
            if ('a' <= c && c <= 'z')
                lignes[i % longueur] += c;
        }
    }

    /* Donne un tableau comptant le nombre d'occurrences de
       chaque lettre dans ligne */
    private static int[] compte(String ligne) {
        int[] occ = new int[26];

        for (int i = 0; i < ligne.length(); i++)
            occ[ligne.charAt(i) - (int) 'a']++;

        return occ;
    }

    /* Donne les indices de coincidence reels du texte */
    public float[] coincidence() {
        float[] ic = new float[lignes.length];

        for (int i = 0; i < ic.length; i++) {
            int[] occ = compte(lignes[i]);
            float sum = 0;
            for (int j = 0; j < 26; j++)
                sum += occ[j] * (occ[j] - 1);
            ic[i] = sum / (lignes[i].length() * (lignes[i].length() - 1));
        }
        return ic;
    }

    /* Dechiffre le texte en utilisant la clef et sauvegarde
       le resultat */
    public void dechiffre(String clef) {
        for (int i = 0; i < lignes.length; i++) {
            String newligne = "";
            for (int j = 0; j < lignes[i].length(); j++)
                newligne += (char) ((lignes[i].charAt(j) - clef.charAt(i) + 26) % 26 + 'a');
            lignes[i] = newligne;
        }
    }

    /* Prend une chaine de caracteres arbitraire et donne une chaine
    contenant chaque caractere une seule fois, dans l'ordre de
    frequence decroissant */
    public static String ordonne(String ligne) {
        int[] occ = compte(ligne);
        String ord = "";

        /* Un algorithme de tri TRES naif */
        for (int i = 0; i < 26; i++) {
            /* Recherche du maximum */
            int max = 0, pos = -1;
            for (int j = 0; j < 26; j++) {
                if (occ[j] > max) {
                    max = occ[j];
                    pos = j;
                }
            }
	    /* Si le maximum est superieur a 0,
	       on le met dans la sortie et on
	       l'efface du tableau */
            if (max > 0) {
                occ[pos] = 0;
                ord += (char) ('a' + pos);
            }
            /* Sinon on a termine */
            else
                break;
        }
        return ord;
    }

    /* Fonction pour afficher le texte */
    public void affiche() throws IOException {
        Boolean cont = true;
        BufferedWriter out = new BufferedWriter(new FileWriter("vigenere-decrypt.txt"));
        for (int i = 0; cont; i++) {
            if (lignes[i % lignes.length].length() > i / lignes.length) {
                System.out.print(lignes[i % lignes.length].charAt(i / lignes.length));
                out.write(lignes[i % lignes.length].charAt(i / lignes.length));
            } else
                cont = false;
        }
        out.close();
    }
}