import java.io.DataInputStream;
import java.io.PrintStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class Cliente implements Runnable{  
    private static Socket cliente = null;
    private static PrintStream os = null;  
    private static DataInputStream is = null;
    private static BufferedReader linha = null;
    private static boolean fechado = false;
  
    public static void main(String[] args){
        int porta = 4000;    
        String host = "localhost";
    
        if(args.length < 2){
            System.out.println("Usando porta padrao " + porta);
        }else{
            host = args[0];
            porta = Integer.valueOf(args[1]).intValue();
        }

        try{
            cliente = new Socket(host, porta);
            linha = new BufferedReader(new InputStreamReader(System.in));
            os = new PrintStream(cliente.getOutputStream());
            is = new DataInputStream(cliente.getInputStream());
        }catch(UnknownHostException e){
            System.err.println("Falha ao tentar se conectar com " + host);
        }catch(IOException e){
            System.err.println("Nao foi possivel atender I/O para a conexao com " + host);
        }

        if(cliente != null && os != null && is != null){
            try{
                new Thread(new Cliente()).start();
                while(!fechado) {
                    os.println(linha.readLine().trim());
                }
        
                os.close();
                is.close();
                cliente.close();
            }catch(IOException e){
                System.err.println("IOException:  " + e);
            }
        }
    }

    public void run(){
        String resposta;
        try{
            while((resposta = is.readLine()) != null){
                System.out.println(resposta);
            if(resposta.indexOf("*** Saindo") != -1)
                break;
            }
            fechado = true;
        }catch(IOException e){
            System.err.println("IOException:  " + e);
        }
    }
}
