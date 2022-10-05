/*
 * Desenvolvedores: Isabela Ferreira Scarabelli e Pedro Hnerique de Almeida Santos
 * Engenharia de Computação 
 * 2º período de 2022 -- AEDS 3
 */

import java.text.DecimalFormat;
import java.util.Scanner;
import java.io.DataOutputStream;
import java.io.RandomAccessFile;
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

    // Construtor da classe Conta com atributos
    public Conta(String nome, String c, String city, String[] e, String nickname, String s) {
        this.nomePessoa = nome;
        this.cpf = c;
        this.cidade = city;
        this.nomeUser = nickname;
        this.senha = s;

    }

    // Construtor vazio da classe Conta
    public Conta() {
        this.nomePessoa = "";
        this.cpf = "";
        this.cidade = "";
        this.nomeUser = "";
        this.senha = "";

    }

    // Menu --------------------------------------
    public void menu() {
        Scanner sc = new Scanner(System.in);
        int op = 1;

        System.out.println("\t= = = = = = = = = = = = = = = = = = =");
        System.out.println("\t\t      Banco SD      ");
        System.out.println("\t= = = = = = = = = = = = = = = = = = =\n");
        System.out.println("Bem vindo ao Banco SD! Como podemos ajudá-lo hoje?\n\n");
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
                deleteMenu();
                break;
            case 3:

                break;
            case 4:
                readMenu();
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

        sc.close();

    }

    // ---------------------------------------
    // Menu para criar um novo usuário: Salva os dados do usuário cadastrado
    public void createMenu() {
        try {
            Scanner sc2 = new Scanner(System.in);
            RandomAccessFile ras = new RandomAccessFile("banco.db", "rw");
            short qtd;

            // Se arquivo estiver vazio então é o primeiro registro, sendo assim o id
            // daquele registro é um, se não, lê o ultimo id do arquivo (salvo nos primeiros
            // 4 bytes do arquivo - int) e soma mais 1
            if (ras.length() == 0) {
                this.idConta = 1;
            } else {
                ras.seek(0);
                this.idConta = ras.readInt() + 1;
            }

            // Lendo as informações do usuário
            System.out.println("Nome Usuário: ");
            this.nomePessoa = sc2.nextLine();
            System.out.println("Username: ");
            this.nomeUser = sc2.nextLine();

            while (searchUser(nomeUser) != 0) {
                System.out.println();
                System.out.println("Nome de usuário já existe, favor digitar um novo username: ");
                this.nomeUser = sc2.nextLine();
            }

            System.out.println("CPF: ");
            this.cpf = sc2.nextLine();

            while (cpf.length() != 11) {
                System.out.println();
                System.out.println("Quantidade de números do CPF. Favor digitar novamente: ");
                this.cpf = sc2.nextLine();
            }

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
            this.senha = sc2.nextLine();
            System.out.println("Saldo Conta: ");
            this.saldoConta = sc2.nextFloat();
            // ---------------------

            // Salvando no início do arquivo o novo último id
            ras.seek(0);
            ras.writeInt(this.idConta);

            // Escrita no arquivo
            create();

            ras.close();
            sc2.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    // ------------------------------------

    // Escrita no arquivo -----------------
    public void create() throws Exception {
        byte[] ba;
        byte lapide = 0;
        RandomAccessFile ras = new RandomAccessFile("banco.db", "rw");

        ba = this.toByteArray(0);
        ras.seek(ras.length());
        ras.writeByte(lapide);
        ras.writeInt(ba.length);
        ras.write(ba);

        ras.close();
    }
    // ----------------------------------

    // Menu leitura --------------------
    public void readMenu() {
        Scanner sc = new Scanner(System.in);
        String username;
        int pos;

        System.out.println("De qual usuário você deseja saber informações?");
        username = sc.nextLine();

        pos = searchUser(username);

        if (pos != 0) {
            read(pos);
        } else {
            System.out.println("Usuário não encontrado!");
        }
        sc.close();
    }
    // ------------------------------------------

    // Leitura ----------------------------------
    // Pos --> pos inicial do registro que será lido e já foi procurado com o
    // searchUser
    public void read(int pos) {
        try {
            RandomAccessFile ras = new RandomAccessFile("banco.db", "rw");
            int tamanho;
            Conta c1 = new Conta();

            ras.seek(pos);
            if (ras.readByte() == 0) {
                tamanho = ras.readInt();
                byte[] ba = new byte[tamanho];

                for (int i = 0; i < tamanho; i++) {
                    ba[i] = ras.readByte();
                }

                c1 = fromByte(pos);

                if (c1 != null) {
                    System.out.println(c1);
                    for (int i = 0; i < c1.qtdEmails; i++) {
                        System.out.println("Email " + (i + 1) + ": " + c1.emails[i]);
                    }

                    System.out.println();

                } else {
                    System.err.println("Erro!");
                }
            }

            ras.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    // ------------------------------------------------

    // Menu do delete ---------------------------------
    public void deleteMenu() {

        Scanner sc4 = new Scanner(System.in);
        String usuario;
        System.out.println("Qual usuário deseja deletar?");
        usuario = sc4.nextLine();
        delete(usuario);

        sc4.close();
    }
    // ------------------------------------------------

    // Delete ----------------------------------------
    public void delete(String username) {
        try {
            RandomAccessFile ras = new RandomAccessFile("banco.db", "rw");
            byte lapide = 1;
            int pos;

            pos = searchUser(username);

            if (pos != 0) {
                ras.seek(pos);
                System.out.println("Usuário " + username + " deletado com sucesso!");
            } else {
                System.out.println("Usuário não cadastrado!");
            }
            ras.writeByte(lapide);
            ras.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    // -----------------------------------------------

    // Menu do update --------------------------------
    public void updateMenu() {
        update();
    }
    // ----------------------------------------------

    // Update --------------------------------------
    public void update() {
        System.err.println(this.nomePessoa);
    }

    // searchUser retorna posicao de um username no arquivo
    public int searchUser(String username) {
        try {
            RandomAccessFile ras = new RandomAccessFile("banco.db", "rw");
            short auxQtdEmails;
            String searchUsername = null;
            int tamRegistro = 0;

            // i indica a posição no arquivo, começando no 4 para pular os ids gerais no
            // cabeçalho e indo até o final do arquivo. O incremento é para não ter a
            // necessidade de percorrer todo o arquivo. Pula o tamanho do registro mais 5
            // bytes = 4 id + 1 lápide

            for (int i = 4; i < ras.length(); i += (tamRegistro + 5)) {
                if (i < ras.length()) {
                    ras.seek(i);

                    if (ras.readByte() == 0) {
                        tamRegistro = ras.readInt();
                        ras.readInt();
                        ras.readUTF();
                        auxQtdEmails = ras.readShort();
                        for (int j = 0; j < auxQtdEmails; j++) {
                            ras.readUTF();
                        }
                        searchUsername = ras.readUTF();
                    } else {
                        tamRegistro = ras.readInt();
                    }

                    if (searchUsername != null) {
                        if (searchUsername.equals(username)) {
                            ras.close();
                            return i;
                        }
                    }
                }
            }
            ras.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }
    // ------------------------------------------------------

    // Transferências ---------------------------------------
    public void tranferencia() {
        Scanner sc3 = new Scanner(System.in);
        String userTranferencia;

        System.out.println("Bem vindo ao menu de transferencias!");
        System.out.println("Insira o nome do usuário de destino: ");
        userTranferencia = sc3.nextLine();

        if (searchUser(userTranferencia) != 0) {
            System.out.println("Pode fazer a transferencia");
        }
        sc3.close();
    }
    // -------------------------------------------------------

    // toByteArray: criar vetor de byte --------------------
    public byte[] toByteArray(int tranferenciasCompletas) throws Exception {
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
        dos.writeInt(tranferenciasCompletas); // transferencias completas
        dos.writeFloat(this.saldoConta);
        return baos.toByteArray();
    }
    // --------------------------------------------------------

    // fromByte: ler um registro e passá-lo para os atributos da classe conta
    public Conta fromByte(int pos) {

        try {
            RandomAccessFile ras = new RandomAccessFile("banco.db", "rw");
            Conta c1 = new Conta();

            byte lapide;

            ras.seek(pos);
            lapide = ras.readByte();
            if (lapide == 0) { // dupla checagem lápide
                ras.readInt(); // tamanho do registro
                c1.idConta = ras.readInt();
                c1.nomePessoa = ras.readUTF();
                c1.qtdEmails = ras.readShort();
                c1.emails = new String[c1.qtdEmails];
                for (int i = 0; i < c1.qtdEmails; i++) {
                    c1.emails[i] = ras.readUTF();
                }
                c1.nomeUser = ras.readUTF();
                c1.senha = ras.readUTF();
                c1.cpf = ras.readUTF();
                c1.cidade = ras.readUTF();
                c1.tranferenciasCompletas = ras.readInt();
                c1.saldoConta = ras.readFloat();

                ras.close();
                return c1;
            }

            ras.close();

        } catch (Exception e) {
            System.err.println(e.getLocalizedMessage());
        }

        return null;

    }
    // ---------------------------------------------------------

    // toString ------------------------------------------------
    public String toString() {
        DecimalFormat df = new DecimalFormat("#,##0.00");

        return "\nNome usuário: " + nomePessoa +
                "\nUsername: " + nomeUser +
                "\nCPF: " + cpf +
                "\nCidade: " + cidade +
                "\nSaldo Conta: " + df.format(saldoConta);
    }
    // ---------------------------------------------------------

    // Clear buffer --------------------------------------------
    private static void clearBuffer(Scanner sc) {
        if (sc.hasNextLine()) {
            sc.nextLine();
        }
    }
    // ---------------------------------------------------------
}
