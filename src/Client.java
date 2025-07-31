import java.io.*;
import java.net.Socket;


public class Client {
    public static void main(String[] args) {

        CPUPlayer bot = null;

        Socket MyClient;
        BufferedInputStream input;
        BufferedOutputStream output;
        int[][] board = new int[8][8];
        Gameboard gameboard = null;
        try {
            MyClient = new Socket("localhost", 8888);

            input    = new BufferedInputStream(MyClient.getInputStream());
            output   = new BufferedOutputStream(MyClient.getOutputStream());
            BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
            while(1 == 1){
                char cmd = 0;

                cmd = (char)input.read();
                System.out.println(cmd);
                // Debut de la partie en joueur blanc
                if(cmd == '1'){
                    bot = new CPUPlayer(Color.RED);
                    byte[] aBuffer = new byte[1024];

                    int size = input.available();
                    //System.out.println("size " + size);
                    // On importe le plateau de jeu
                    input.read(aBuffer,0,size);
                    String s = new String(aBuffer).trim();
                    System.out.println(s);
                    gameboard = Gameboard.syncGameboard(s);
                    // On sync aussi le bot
                    bot.initializeTree(gameboard);

                    System.out.println("Nouvelle partie! Vous jouer blanc, entrez votre premier coup : ");

                    // On génère le meilleur coup
                    Move bestMove = bot.getBestMove(gameboard);
                    gameboard.play(bestMove);
                    bot.advanceTree(bestMove, gameboard);
                    System.out.println("Bot : " + bestMove);
                    output.write(bestMove.toString().getBytes(),0,bestMove.toString().length());
                    output.flush();
                }
                // Debut de la partie en joueur Noir
                if(cmd == '2'){
                    bot = new CPUPlayer(Color.BLACK);
                    System.out.println("Nouvelle partie! Vous jouer noir, attendez le coup des rouges");
                    byte[] aBuffer = new byte[1024];

                    int size = input.available();
                    //System.out.println("size " + size);
                    // On importe le plateau de jeu
                    input.read(aBuffer,0,size);
                    String s = new String(aBuffer).trim();
                    System.out.println(s);
                    gameboard = Gameboard.syncGameboard(s);
                    // On sync aussi le bot
                    bot.initializeTree(gameboard);
                }
                // Le serveur demande le prochain coup
                // Le message contient aussi le dernier coup joue.
                if(cmd == '3'){
                    byte[] aBuffer = new byte[16];

                    int size = input.available();
                    // System.out.println("size :" + size);
                    // On importe le dernier coup joué par le serveur
                    input.read(aBuffer,0,size);
                    String s = new String(aBuffer);
                    System.out.println("Dernier coup :"+ s);
                    Move lastMove = Move.getMoveFromString(s);
                    gameboard.play(lastMove);
                    // On sync avec le bot
                    bot.advanceTree(lastMove, gameboard);

                    // On génère le meilleur coup
                    System.out.println("Entrez votre coup : ");
                    Move bestMove = bot.getBestMove(gameboard);
                    gameboard.play(bestMove);
                    bot.advanceTree(bestMove, gameboard);
                    System.out.println("Bot : " + bestMove);
                    output.write(bestMove.toString().getBytes(),0,bestMove.toString().length());
                    output.flush();
                }
                // Le dernier coup est invalide
                if(cmd == '4'){
                    System.out.println("Coup invalide, entrez un nouveau coup : ");
                    // C'est la sauce
                    Move bestMove = bot.getBestMove(gameboard);
                    System.out.println("Bot : " + bestMove);
                    output.write(bestMove.toString().getBytes(),0,bestMove.toString().length());
                    output.flush();
                }
                // La partie est terminée
                if(cmd == '5'){
                    byte[] aBuffer = new byte[16];
                    int size = input.available();
                    input.read(aBuffer,0,size);
                    String s = new String(aBuffer);
                    System.out.println("Partie Terminé. Le dernier coup joué est: "+s);
                    String move = null;
                    move = console.readLine();
                    output.write(move.getBytes(),0,move.length());
                    output.flush();
                }
            }
        }
        catch (IOException e) {
            System.out.println(e);
        }

    }
}
