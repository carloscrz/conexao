import java.io.DataInputStream;
import java.io.PrintStream;
import java.io.IOException;
import java.net.Socket;
import java.net.ServerSocket;

public class Servidor {
  private static ServerSocket servidor = null;  
  private static Socket cliente = null;
  private static final int qtdClientes = 10;
  private static final ThreadCliente[] threads = new ThreadCliente[qtdClientes];

  public static void main(String args[]){
    int porta = 4000;
    if(args.length < 1){
        System.out.println("Usando porta padrao " + porta);
    }else{
        porta = Integer.valueOf(args[0]).intValue();
    }

    try{
        servidor = new ServerSocket(porta);
    }catch(IOException e){
        System.out.println(e);
    }

    for(;;){
        try{
            cliente = servidor.accept();            
            int i = 0;
            for(i = 0; i < qtdClientes; i++){
                if(threads[i] == null){
                    (threads[i] = new ThreadCliente(cliente, threads)).start();
                    break;
                }
            }
        
            if(i == qtdClientes) {
                PrintStream os = new PrintStream(cliente.getOutputStream());
                os.println("Servidor ocupado. Tente mais tarde.");
                os.close();
                cliente.close();
            }
        }catch (IOException e) {
            System.out.println(e);
        }
    }
  }
}

class ThreadCliente extends Thread {
  private DataInputStream is = null;
  private PrintStream os = null;
  private Socket cliente = null;
  private final ThreadCliente[] threads;
  private int qtdClientes;

  public ThreadCliente(Socket cs, ThreadCliente[] t){
    cliente = cs;
    threads = t;
    qtdClientes = threads.length;
  }

  public void run(){
    int qtdClientes = this.qtdClientes;
    ThreadCliente[] threads = this.threads;

    try{
      is = new DataInputStream(cliente.getInputStream());
      os = new PrintStream(cliente.getOutputStream());
      os.print("Nome: ");
      String nome = is.readLine().trim();
      os.println("Ola " + nome + "\nDigite /sair para encerrar");
      
      for(int i = 0; i < qtdClientes; i++){
        if(threads[i] != null && threads[i] != this){
          threads[i].os.println("---" + nome + " entra na sala ---");
        }
      }

      for(;;){
        String linha = is.readLine();
        if(linha.startsWith("/sair")){
          break;
        }

        for(int i = 0; i < qtdClientes; i++){
          if(threads[i] != null){
            threads[i].os.println(nome + "> " + linha);
          }
        }
      }

      for(int i = 0; i < qtdClientes; i++){
        if(threads[i] != null && threads[i] != this){
          threads[i].os.println(nome + " saiu da sala");
        }
      }

      for(int i = 0; i < qtdClientes; i++){
        if (threads[i] == this) {
          threads[i] = null;
        }
      }

      is.close();
      os.close();
      cliente.close();
    } catch (IOException e) {
    }
  }
}
