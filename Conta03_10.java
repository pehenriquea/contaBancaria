import java.text.DecimalFormat;
import java.util.Scanner;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.RandomAccessFile;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class Conta {

    public int idConta;
    public int tranferenciasCompletas;
    protected String nomePessoa;
    protected String[] emails;
    protected String nomeUser;
    protected String senha;
    protected String cpf;
    protected String cidade;
    protected float saldoConta;
    protected short qtdEmails;

    public Conta(String nome, String c, String city, String[] e, String nickname, String s) {
        this.nomePessoa = nome;
        this.cpf = c;
        this.cidade = city;
        this.nomeUser = nickname;
        this.senha = s;

    }

    public Conta() {
        this.nomePessoa = "";
        this.cpf = "";
        this.cidade = "";
        this.nomeUser = "";
        this.senha = "";

    }

    public String toString() {
        DecimalFormat df = new DecimalFormat("#,##0.00");
        return "\nNome usuário " + nomePessoa +
                "\nCPF: " + cpf +
                "\nUsername: " + nomeUser +
                "\nSaldo Conta: " + df.format(saldoConta);
    }

    public void menu() {
        Scanner sc = new Scanner(System.in);
        int op = 1;

        System.out.println("\t= = = = = = = = = = = = = = = = = = =");
        System.out.println("\t\t      Banco SD      ");
        System.out.println("\t= = = = = = = = = = = = = = = = = = =\n");
        System.out.println("Bem vindo ao Banco SD! Como podemos ajudá-lo hoje?\n\n");
        // while (op != 0) {
        System.out.println("1 - Criar uma nova conta");
        System.out.println("2 - Deletar uma conta");
        System.out.println("3 - Atualizar uma conta");
        System.out.println("4 - Ler dados de uma conta");
        System.out.println("5 - Realizar uma transferência");
        System.out.println("\n0 - SAIR");
        op = sc.nextInt();

        switch (op) {
            case 1:
                createMenu();
                break;
            case 2:

                break;
            case 3:

                break;
            case 4:
                read(0);
                //leDados();
                break;
            case 5:
                tranferencia();
                break;
            case 0:
                System.out.println("Saindo ...");
                System.out.println("\nObrigada por utilizar nossos serviços :)");
                break;
            default:
                System.err.println("Opção Correta! Favor tentar novamente.");
                break;
        }
        // }

        sc.close();

    }

    public byte[] toByteArray() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        dos.writeInt(this.idConta);
        dos.writeUTF(this.nomePessoa);
        dos.writeShort(this.qtdEmails);
        for (int i = 0; i < this.qtdEmails; i++) {
            dos.writeUTF(this.emails[i]);
        }
        dos.writeUTF(this.nomeUser);
        dos.writeUTF(this.senha);
        dos.writeUTF(this.cpf);
        dos.writeUTF(this.cidade);
        dos.writeInt(0); // transferencias completas
        dos.writeFloat(this.saldoConta);
        return baos.toByteArray();
    }

    public int fromByteArray(byte[] ba) throws Exception {
        ByteArrayInputStream bais = new ByteArrayInputStream(ba);
        DataInputStream dis = new DataInputStream(bais);
        byte lapide;
        int tamFile = 0;
        lapide = dis.readByte();

        if (lapide != 0) {
            tamFile = dis.readInt();
            this.idConta = dis.readInt();
            this.nomePessoa = dis.readUTF();
            this.qtdEmails = dis.readShort();
            for (int i = 0; i < qtdEmails; i++) {
                this.emails[i] = dis.readUTF();
            }
            this.nomeUser = dis.readUTF();
            this.senha = dis.readUTF();
            this.cpf = dis.readUTF();
            this.cidade = dis.readUTF();
            this.tranferenciasCompletas = dis.readInt();
            this.saldoConta = dis.readFloat();

            return 0;
        }
        return -1;

    }

    public void createMenu() {
        try {
            Scanner sc2 = new Scanner(System.in);
            RandomAccessFile ras = new RandomAccessFile("banco.db", "rw");
            short qtd = -1;
            byte lapide = 10;

            if (ras.length() == 0) {
                this.idConta = 1;
            } else {
                ras.seek(0);
                this.idConta = ras.readInt() + 1;
            }
            
            System.out.println("Nome Usuário: ");
            this.nomePessoa = sc2.nextLine();
            System.out.println("Username: ");
            this.nomeUser = sc2.nextLine();
            System.out.println("CPF: ");
            this.cpf = sc2.nextLine();
            System.out.println("Cidade: ");
            this.cidade = sc2.nextLine();
            System.out.println("Quantos emails você gostaria de cadastrar?");
            qtd = sc2.nextShort();

            clearBuffer(sc2);

            String vetoresEmail[] = new String[qtd];
            this.emails = new String[qtd];

            this.qtdEmails = qtd;
            for (int i = 0; i < qtd; i++) {
                System.out.println("Email " + (i + 1) + ": ");
                vetoresEmail[i] = sc2.nextLine();
                this.emails[i] = vetoresEmail[i];
            }
            System.out.println("Senha: ");
            this.nomeUser = sc2.nextLine();
            System.out.println("Saldo Conta: ");
            this.saldoConta = sc2.nextFloat();

            ras.seek(0);
            ras.writeInt(this.idConta);
            create();

            ras.close();
            sc2.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void create() throws Exception {
        byte[] ba;
        byte lapide = 0;
        RandomAccessFile ras = new RandomAccessFile("banco.db", "rw");

        ba = this.toByteArray();
        ras.seek(ras.length());
        System.out.println(ba.length);
        ras.writeByte(lapide);
        ras.writeInt(ba.length);
        ras.write(ba);

        ras.close();
    }

    public void tranferencia() {
        Scanner sc3 = new Scanner(System.in);
        String userTranferencia;

        System.out.println("Bem vindo ao menu de transferencias!");
        System.out.println("Insira o nome do usuário de destino: ");
        userTranferencia = sc3.nextLine();

        sc3.close();
    }

    // searchUser retorna posicao no vetor de bytes
    public int searchUser(String username) {

        try {
            RandomAccessFile ras = new RandomAccessFile("banco.db", "rw");
            
            
            

            ras.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    public void leDados(){
        Scanner sc = new Scanner(System.in);
        String username;
        int pos;

        System.out.println("De qual usuário você deseja saber informações?");
        username = sc.nextLine();

        searchUser(username);

    }

    public void deleteMenu() {

        Scanner sc4 = new Scanner(System.in);
        String usuario;
        System.out.println("Qual usuário deseja deletar?");
        usuario = sc4.nextLine();
        delete(usuario);

        sc4.close();
    }

    public void delete(String username) {
        try {
            RandomAccessFile ras = new RandomAccessFile("banco.db", "rw");

            ras.seek(searchUser(username) + 4);
            ras.writeByte(1);
            ras.seek(0);
            ras.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateMenu() {
        update();
    }   

    public void update() {
        System.err.println(this.nomePessoa);
    }

    public void readMenu() {
        read(0);
    }

    public void read(int pos) {
        int tamanhoRegistro, tamanhoRegistro_i;
        try {
            RandomAccessFile ras = new RandomAccessFile("banco.db", "rw");
            int tamanho;
            
            if (ras.readByte() != 1){
                tamanho = ras.readInt();
                byte[] ba = new byte[tamanho];
                ras.seek(tamanho);

                for (int i = 0; i < tamanho; i++) {
                    ba[i] = ras.readByte();
                }

                ba = this.toByteArray();
                if(this.fromByteArray(ba) == 0){
                    System.out.println(this.nomeUser);
                    System.out.println(this.cpf);
                    System.out.println(this.saldoConta);
                }
            }
            
            ras.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public int proximoRegistro(int tamanho){
        int posicao;
        try{
            RandomAccessFile ras = new RandomAccessFile("banco.db", "rw");

            ras.seek(tamanho);
            if (ras.readByte() != 1){
                ras.seek(tamanho + 1);
                posicao = ras.readInt();
                return posicao;
            } else {
                tamanho += ras.readInt();
                return proximoRegistro(tamanho);
            }
            
        }catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
        
    }

    private static void clearBuffer(Scanner sc) {
        if (sc.hasNextLine()) {
            sc.nextLine();
        }
    }
}
