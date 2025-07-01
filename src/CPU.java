import java.io.*;
import java.net.*;

public class CPU {

    int[][] board = new int[8][8];

    public CPU() {
        this.board = new int[8][8];
    }

    public static void main(String[] args) {
        Socket MyClient;
        BufferedInputStream input;
        BufferedOutputStream output;

        try{
            MyClient = new Socket("localhost", 8888);

            input = new BufferedInputStream(MyClient.getInputStream());
            output = new BufferedOutputStream(MyClient.getOutputStream());
            BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
        }catch (IOException e) {
            System.out.println(e);
        }

        
    }
}
