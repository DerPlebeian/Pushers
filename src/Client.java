import java.io.*;
import java.net.*;


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
                    input.read(aBuffer,0,size);
                    String s = new String(aBuffer).trim();
                    System.out.println(s);
                    String[] boardValues;
                    boardValues = s.split(" ");
                    int x=0,y=0;
                    for(int i=0; i<boardValues.length;i++){
                        board[y][x] = Integer.parseInt(boardValues[i]);
                        x++;
                        if(x == 8){
                            x = 0;
                            y++;
                        }
                    }
                    for(int i=0; i<board.length;i++) {
                        for(int j=0; j<board[i].length;j++) {
                            System.out.print(board[i][j] + " ");
                        }
                    }
                    System.out.println("Nouvelle partie! Vous jouer blanc, entrez votre premier coup : ");
                    String move = null;
                    //move = console.readLine();
                    gameboard = new Gameboard(board);
                    Move bestMove = bot.getNextMoveAB(gameboard).get(0);
                    move = bestMove.toString();
                    System.out.println("Bot : " + move);
                    output.write(move.getBytes(),0,move.length());
                    output.flush();
                }
                // Debut de la partie en joueur Noir
                if(cmd == '2'){
                    bot = new CPUPlayer(Color.BLACK);
                    System.out.println("Nouvelle partie! Vous jouer noir, attendez le coup des blancs");
                    byte[] aBuffer = new byte[1024];

                    int size = input.available();
                    //System.out.println("size " + size);
                    input.read(aBuffer,0,size);
                    String s = new String(aBuffer).trim();
                    System.out.println(s);
                    String[] boardValues;
                    boardValues = s.split(" ");
                    int x=0,y=0;
                    for(int i=0; i<boardValues.length;i++){
                        board[y][x] = Integer.parseInt(boardValues[i]);
                        x++;
                        if(x == 8){
                            x = 0;
                            y++;
                        }
                    }
                    for(int i=0; i<board.length;i++) {
                        for(int j=0; j<board[i].length;j++) {
                            System.out.print(board[i][j] + " ");
                        }
                    }
                    gameboard = new Gameboard(board);

                }
                // Le serveur demande le prochain coup
                // Le message contient aussi le dernier coup joue.
                if(cmd == '3'){
                    byte[] aBuffer = new byte[16];

                    int size = input.available();
                    System.out.println("size :" + size);
                    input.read(aBuffer,0,size);

                    String s = new String(aBuffer);
                    System.out.println("Dernier coup :"+ s);
                    System.out.println("Entrez votre coup : ");
                    String move = null;
                    //move = console.readLine();
                    gameboard.play(Move.getMoveFromString(s));
                    Move bestMove = bot.getNextMoveAB(gameboard).get(0);
                    move = bestMove.toString();
                    System.out.println("Bot : " + move);
                    output.write(move.getBytes(),0,move.length());
                    output.flush();
                }
                // Le dernier coup est invalide
                if(cmd == '4'){
                    System.out.println("Coup invalide, entrez un nouveau coup : ");
                    String move = null;
                    Move bestMove = bot.getNextMoveAB(gameboard).get(0);
                    move = bestMove.toString();
                    System.out.println("Bot : " + move);
                    output.write(move.getBytes(),0,move.length());
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
