/*
 * Desenvolvedores: Isabela Ferreira Scarabelli e Pedro Henrique de Almeida Santos
 * Engenharia de Computação 
 * 2º período de 2022 -- AEDS 3
 */

import java.text.DecimalFormat;
import java.util.Scanner;
import java.io.DataOutputStream;
import java.io.RandomAccessFile;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    protected long adress; // endereço no arquivo banco.db
    protected int versaoCompressao = 0;

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
        System.out.println("Bem vindo ao Banco SD! Como podemos ajudá-lo hoje?\n");

        do {
            System.out.println("\n1 - Criar uma nova conta");
            System.out.println("2 - Deletar uma conta");
            System.out.println("3 - Atualizar uma conta");
            System.out.println("4 - Ler dados de uma conta");
            System.out.println("5 - Realizar uma transferência");
            System.out.println("6 - Comprimir o arquivo de dados");
            System.out.println("7 - Descomprimir o arquivo de dados");
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
                    updateMenu();
                    break;
                case 4:
                    readMenu();
                    break;
                case 5:
                    tranferencia();
                    break;
                case 6:
                    toByteFromString();
                    break;
                case 7:
                    toFilefromClass();
                    break;
                case 0:
                    System.out.println("Saindo ...");
                    System.out.println("\nObrigada por utilizar nossos serviços :)");
                    break;
                default:
                    System.err.println("Opção Correta! Favor tentar novamente.");
                    break;
            }

        } while (op != 0);
    }

    // ---------------------------------------

    // Menu para criar um novo usuário: Salva os dados do usuário cadastrado
    public void createMenu() {
        try {
            Conta c1 = new Conta();
            Scanner sc2 = new Scanner(System.in);
            RandomAccessFile ras = new RandomAccessFile("banco.db", "rw");
            short qtd;

            // Se arquivo estiver vazio então é o primeiro registro, sendo assim o id
            // daquele registro é um, se não, lê o ultimo id do arquivo (salvo nos primeiros
            // 4 bytes do arquivo - int) e soma mais 1
            if (ras.length() == 0) {
                c1.idConta = 1;
            } else {
                ras.seek(0);
                c1.idConta = ras.readInt() + 1;
            }

            // Lendo as informações do usuário
            System.out.println("Nome Usuário: ");
            c1.nomePessoa = sc2.nextLine();
            System.out.println("Username: ");
            c1.nomeUser = sc2.nextLine();

            while (searchUser(c1.nomeUser) != 0) {
                System.out.println();
                System.out.println("Nome de usuário já existe, favor digitar um novo username: ");
                c1.nomeUser = sc2.nextLine();
            }

            System.out.println("CPF: ");
            c1.cpf = sc2.nextLine();

            while (c1.cpf.length() != 11) {
                System.out.println();
                System.out.println("Quantidade de números do CPF. Favor digitar novamente: ");
                c1.cpf = sc2.nextLine();
            }

            System.out.println("Cidade: ");
            c1.cidade = sc2.nextLine();
            System.out.println("Quantos emails você gostaria de cadastrar?");
            qtd = sc2.nextShort();

            clearBuffer(sc2);

            String vetoresEmail[] = new String[qtd];
            c1.emails = new String[qtd];

            c1.qtdEmails = qtd;
            for (int i = 0; i < qtd; i++) {
                System.out.println("Email " + (i + 1) + ": ");
                vetoresEmail[i] = sc2.nextLine();
                c1.emails[i] = vetoresEmail[i];
            }
            System.out.println("Senha: ");
            c1.senha = sc2.nextLine();
            System.out.println("Saldo Conta: ");
            c1.saldoConta = sc2.nextFloat();
            c1.tranferenciasCompletas = 0;
            // ---------------------

            // Salvando no início do arquivo o novo último id
            ras.seek(0);
            ras.writeInt(c1.idConta);

            // Escrita no arquivo
            create(c1);

            ras.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    // ------------------------------------

    // Escrita no arquivo -----------------
    public void create(Conta c1) throws Exception {
        byte[] ba;
        byte lapide = 0;
        RandomAccessFile ras = new RandomAccessFile("banco.db", "rw");

        ba = this.toByteArray(c1);
        ras.seek(ras.length());
        c1.adress = ras.getFilePointer();
        ras.writeByte(lapide);
        ras.writeInt(ba.length);
        ras.write(ba);

        createIndex(c1);

        System.out.println("Conta criada com sucesso!!");
        ras.close();
    }

    // ----------------------------------

    // Criando registro no arquivo de index
    public void createIndex(Conta c1) {
        try {
            RandomAccessFile ras = new RandomAccessFile("index.db", "rw");
            byte[] ba2;
            byte lapide = 0;

            ba2 = this.toByteArrayIndex(c1);
            ras.seek(ras.length());
            ras.writeByte(lapide);
            ras.writeShort(ba2.length);
            ras.write(ba2);

            ras.close();
        } catch (Exception e) {

        }
    }

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
    }
    // ------------------------------------------------

    // Delete ----------------------------------------
    public void delete(String username) {
        try {
            RandomAccessFile ras = new RandomAccessFile("banco.db", "rw");
            Scanner sc = new Scanner(System.in);
            byte lapide = 1;
            int pos;
            short senha = 1234, senhaLida, i = 3;
            int ultimoId;

            ras.seek(0);
            ultimoId = ras.readInt();
            ultimoId--; // dimiuir um id, já que um registro foi deletado
            System.out.println(ultimoId);
            ras.seek(0);
            ras.writeInt(ultimoId);

            pos = searchUser(username);

            if (pos != 0) {
                // Forma de apenas autorizados apagarem algum registro

                do {
                    System.out.println("Digite a senha para deletar algum registro: (1234)");
                    senhaLida = sc.nextShort();
                    if (senhaLida == senha) {
                        ras.seek(pos);
                        ras.writeByte(lapide);
                        System.out.println("Usuário " + username + " deletado com sucesso!");
                        i = 0;
                    } else {
                        i--;
                        System.out.println("Senha inválida! Você tem mais " + i + " tentativas");
                    }
                } while (i > 0);

                deleteIndex(username);
            } else {
                System.out.println("Usuário não cadastrado!");
            }

            ras.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    // -----------------------------------------------

    // Delete do index --------------------------------
    public void deleteIndex(String username) {
        try {
            RandomAccessFile ras = new RandomAccessFile("index.db", "rw");
            String searchUsername = "";
            byte lapide;
            byte flag = 0;
            int tamRegistro = 0;
            long i = 0;

            while (flag == 0) {
                ras.seek(i);
                lapide = ras.readByte();
                tamRegistro = ras.readShort();
                if (lapide == 0) {
                    searchUsername = ras.readUTF();
                    if (searchUsername.equals(username)) {
                        flag = 1;
                        ras.seek(i);
                        ras.writeByte(1);
                    }
                    i += tamRegistro + 3; // mais 3 porque existem 3 bytes antes dele (1 para a lápide e 2 para o
                                          // tamanho do registro)
                } else {
                    i += tamRegistro + 3;
                }
            }

            ras.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Menu do update --------------------------------
    public void updateMenu() {
        Scanner sc5 = new Scanner(System.in);
        String user;
        short operacao, op_e;
        int pos;
        Conta c2 = new Conta();

        System.out.println("Qual o username a ser atualizado?");
        user = sc5.nextLine();
        pos = searchUser(user);

        if (pos != 0) {
            System.out.println(
                    "\nQual dado será atualizado?\n1-Nome\n2-Nome do usuário\n3-Emails\n4-Senha\n5-CPF\n6-Cidade:\n");
            operacao = sc5.nextShort();

            switch (operacao) {
                case 1:
                    System.out.print("Digite o novo nome: ");
                    clearBuffer(sc5);
                    c2 = fromByte(pos);
                    c2.nomePessoa = sc5.nextLine();
                    update(pos, c2);
                    break;
                case 2:
                    System.out.print("Digite o novo username: ");
                    clearBuffer(sc5);
                    c2 = fromByte(pos);
                    // procura no arquivo de index a posição que estava o antigo username, para
                    // alterá-lo
                    c2.nomeUser = updateIndexUsername(searchIndex(c2.nomeUser), c2);
                    update(pos, c2);
                    break;
                case 3:
                    c2 = fromByte(pos);
                    System.out.println(c2.qtdEmails);
                    if (c2.qtdEmails != 0) {
                        System.out.println("Qual email você deseja alterar?");

                        if (c2 != null) {
                            for (int i = 0; i < c2.qtdEmails; i++) {
                                System.out.println("Email " + (i + 1) + ": " + c2.emails[i]);
                            }
                        }
                        op_e = sc5.nextShort();
                        System.out.println("Digite o novo email: ");
                        clearBuffer(sc5);
                        c2.emails[op_e - 1] = sc5.nextLine();
                        update(pos, c2);

                    } else {
                        System.out.println("Este usuário não possui nenhum email cadastrado!");
                    }

                    break;
                case 4:
                    System.out.print("Digite a nova senha: ");
                    clearBuffer(sc5);
                    c2 = fromByte(pos);
                    c2.senha = sc5.nextLine();
                    update(pos, c2);
                    break;
                case 5:
                    System.out.print("Digite o novo CPF: ");
                    clearBuffer(sc5);
                    c2 = fromByte(pos);
                    c2.cpf = sc5.nextLine();
                    update(pos, c2);
                    break;
                case 6:
                    System.out.print("Digite a nova cidade: ");
                    clearBuffer(sc5);
                    c2 = fromByte(pos);
                    c2.cidade = sc5.nextLine();
                    update(pos, c2);
                    break;
                default:
                    System.out.println("Operação inválida!");
                    break;
            }
        } else {
            System.out.println("Usuário não cadastrado!");
        }
    }

    // Update ---------------------------------------------
    public void update(int posicao, Conta c2) {
        byte[] ba;
        int tamReg, tamBA;

        try {
            RandomAccessFile ras = new RandomAccessFile("banco.db", "rw");

            ras.seek(posicao + 1);
            tamReg = ras.readInt();

            ba = new byte[tamReg];
            ba = toByteArray(c2);

            tamBA = ba.length;

            if (tamBA <= tamReg) { // se for menor, salva na mesma posição
                ras.seek(posicao);
                ras.writeByte(0); // lapide
                ras.writeInt(tamReg);
                ras.write(ba);

            } else {// se não, marca aquele registro como deletado e salva o novo no fim do arquivo
                ras.seek(posicao);
                ras.writeByte(1); // lápide
                c2.adress = ras.length();
                ras.seek(ras.length());
                ras.writeByte(0);
                ras.writeInt(tamBA);
                ras.write(ba);

                updateIndex(c2);
            }

            ras.close();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
    // ---------------------------------------------------------

    // update do arquivo de Index
    public void updateIndex(Conta c2) {
        int indice = 0;
        indice = searchIndex(c2.nomeUser);

        try {
            RandomAccessFile ras = new RandomAccessFile("index.db", "rw");

            if (indice != -1) {
                ras.seek(indice);
                if (ras.readByte() == 0) { // lápide
                    ras.readShort();
                    ras.readUTF();
                    ras.writeLong(c2.adress);
                }
            } else {
                System.out.println("Erro no arquivo de índice!");
            }

        } catch (Exception e) {
            System.err.println(e.getLocalizedMessage());
        }
    }

    // ----------------------------------------------------

    // Atualiza o arquivo de indice caso o username tenha sido alterado
    public String updateIndexUsername(int posIndex, Conta c2) {
        try {

            RandomAccessFile rasIndex = new RandomAccessFile("index.db", "rw");
            Scanner sc = new Scanner(System.in);
            String username = c2.nomeUser;
            byte lapide = 1;

            c2.nomeUser = sc.nextLine();

            if (c2.nomeUser.length() <= username.length()) {
                rasIndex.seek(posIndex + 3); // 1 byte -> lapide 2 byte --> tamanho registro
                rasIndex.writeUTF(c2.nomeUser);
            } else if (c2.nomeUser.length() > username.length()){
                rasIndex.seek(posIndex);
                rasIndex.writeByte(lapide);
                createIndex(c2);
            }
            rasIndex.close();
        } catch (Exception e) {
            System.err.println(e.getLocalizedMessage());
        }

        return c2.nomeUser;
    }

    // Procura a posição no arquivo de index de um username
    public int searchIndex(String username) {
        try {
            RandomAccessFile ras = new RandomAccessFile("index.db", "rw");
            String searchUsername = "";
            int tamRegistro = 0;
            byte lapide;

            // O incremento é para não ter a
            // necessidade de percorrer todo o arquivo. Pula o tamanho do registro mais 5
            // bytes = 4 tamanho do registro + 1 lápide

            for (int i = 0; i < ras.length(); i += (tamRegistro + 3)) {
                ras.seek(i);
                lapide = ras.readByte();
                tamRegistro = ras.readShort();

                if (lapide == 0) {
                    searchUsername = ras.readUTF();

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

        return -1;
    }
    // ------------------------------------------------------

    // searchUser retorna posicao de um username no arquivo
    public int searchUser(String username) {
        try {
            RandomAccessFile res = new RandomAccessFile("index.db", "rw");
            int index = 0, pos;
            long posLong = 0;
            byte lapide;

            index = searchIndex(username);

            if (index != -1) {
                res.seek(index);

                lapide = res.readByte();

                if (lapide == 0) { // dupla vericação
                    res.readShort();
                    res.readUTF();
                    posLong = res.readLong();
                    pos = (int) posLong;
                    res.close();
                    return pos;
                }
            }

            res.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }
    // ------------------------------------------------------

    // Transferências ---------------------------------------
    public void tranferencia() {
        Conta envia = new Conta();
        Conta recebe = new Conta();
        Scanner sc3 = new Scanner(System.in);
        String userRecebo, userEnvio;
        short valido = 0;
        int posEnvio, posRecebe;
        float valorTransferido;

        System.out.println();
        System.out.println("Bem vindo ao menu de transferencias!");

        do {
            System.out.println("Insira o username de origem da transferência: ");
            userEnvio = sc3.nextLine();
            System.out.println("Insira o username de destino: ");
            userRecebo = sc3.nextLine();

            posEnvio = searchUser(userEnvio);
            posRecebe = searchUser(userRecebo);

            if (posEnvio != 0 && posRecebe != 0) {
                valido = 1;
            } else {
                System.out.println("Username inválido!");
            }
        } while (valido == 0);

        System.out.print("Valor a ser transferido: R$ ");
        valorTransferido = sc3.nextFloat();
        System.out.println();

        envia.emails = new String[envia.qtdEmails];
        recebe.emails = new String[recebe.qtdEmails];

        envia = fromByte(posEnvio);
        recebe = fromByte(posRecebe);

        if (envia != null && recebe != null) {
            if (envia.saldoConta >= valorTransferido) {
                envia.saldoConta -= valorTransferido;
                envia.tranferenciasCompletas++;
                recebe.saldoConta += valorTransferido;
                update(posRecebe, recebe);
                update(posEnvio, envia);
            } else {
                System.out.println("Saldo inválido para realizar a transferência.");
            }
        }

    }
    // -------------------------------------------------------

    // toByteArray: criar vetor de bytes --------------------
    public byte[] toByteArray(Conta c1) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        dos.writeInt(c1.idConta);
        dos.writeUTF(c1.nomePessoa);
        dos.writeShort(c1.qtdEmails);
        for (int i = 0; i < c1.qtdEmails; i++) {
            dos.writeUTF(c1.emails[i]);
        }
        dos.writeUTF(c1.nomeUser);
        dos.writeUTF(c1.senha);
        dos.writeUTF(c1.cpf);
        dos.writeUTF(c1.cidade);
        dos.writeInt(c1.tranferenciasCompletas);
        dos.writeFloat(c1.saldoConta);
        return baos.toByteArray();
    }
    // --------------------------------------------------------

    // Indexação dos arquivos criados
    public byte[] toByteArrayIndex(Conta c1) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeUTF(c1.nomeUser);
        dos.writeLong(c1.adress);

        return baos.toByteArray();
    }

    // fromByte: ler um registro e passá-lo para os atributos de um objeto da classe
    // conta
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
                "\nSaldo Conta: R$" + df.format(saldoConta) +
                "\nSenha: " + senha;
    }
    // ---------------------------------------------------------

    // Clear buffer --------------------------------------------
    private static void clearBuffer(Scanner sc) {
        if (sc.hasNextLine()) {
            sc.nextLine();
        }
    }
    // ---------------------------------------------------------

    //Compressão de Dados --------------------------------------
    public void toByteFromString(){
        long tempoInicial = System.currentTimeMillis();
        Conta c1 = new Conta();
        String arq = "";
        int tamCom = 0;
        try {
            RandomAccessFile ras = new RandomAccessFile("banco.db", "rw");
            long pos = 0;
            long l = ras.length();
            tamCom = (int) l;
            arq = Integer.toString(ras.readInt());

            while (pos < ras.length()){
                ras.readByte();
                pos += ras.readInt();
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

                arq = arq + "\n" + Integer.toString(c1.idConta) + "\n" + c1.nomePessoa + "\n" + c1.qtdEmails + "\n";

                for (int i = 0; i < c1.qtdEmails; i++) {
                    arq = arq + c1.emails[i] + "\n";
                }

                arq = arq + c1.nomeUser + "\n" + c1.senha + "\n" + c1.cpf + "\n" + c1.cidade + "\n" + Integer.toString(c1.tranferenciasCompletas) + "\n" + c1.saldoConta;
            }
            ras.close();
            
        } catch (Exception e) {
            
        }
        encode(arq, this.versaoCompressao, tamCom);
        this.versaoCompressao++;
        System.out.println("Compressão realizada com sucesso!");
        System.out.println("Tempo de Execução: " + (System.currentTimeMillis() - tempoInicial) + "ms");
    }

    public void encode(String text, int x, int tamCom) {
        int tam;
        int dictSize = 256;
        Map<String, Integer> dictionary = new HashMap<>();

        for (int i = 0; i < dictSize; i++) {
            dictionary.put(String.valueOf((char) i), i);
        }

        String foundChars = "";
        List<Integer> result = new ArrayList<>();

        for (char character : text.toCharArray()) {
            String charsToAdd = foundChars + character;
            if (dictionary.containsKey(charsToAdd)) {
                foundChars = charsToAdd;
            } else {
                result.add(dictionary.get(foundChars));
                dictionary.put(charsToAdd, dictSize++);
                foundChars = String.valueOf(character);
            }
        }
        if (!foundChars.isEmpty()) {
            result.add(dictionary.get(foundChars));
        }
        tam = result.size();

        float num = 0;
        float den = 0;

        try {
            String arq = "bancoCompressao"+ x +".db";
            RandomAccessFile ras = new RandomAccessFile(arq, "rw");

            for (int i = 0; i <= tam; i++){
                ras.writeShort(result.get(i));
            }

            ras.close();
            
        } catch (Exception e) {

        }
        
        num = ((float)tam*2)-1;
        den = (float)tamCom;

        System.out.printf("\nTaxa de Compressão: %.2f", (1-(num/den))*100);
        System.out.println("%");

    }

    public void toFilefromClass(){
        Scanner sc1 = new Scanner(System.in);
        Conta c1 = new Conta();
        int i = 0, versao;

        System.out.println("Qual versão comprimida deseja descomprimir?");
        versao = sc1.nextInt();
        if (versao > this.versaoCompressao){
            System.out.println("Versão inválida, pois é maior que a ultima versão de compressão realizada.");
            versao = -1;
        }

        String arq = decode(versao);
        Scanner sc = new Scanner(arq);

        i = Integer.parseInt(sc.nextLine());

        try {
            String arq2 = "banco" + versao + ".db";
            RandomAccessFile ras = new RandomAccessFile(arq2, "rw");
            ras.writeInt(i);

            ras.close();
        } catch (Exception e) {}

        for (int j = 0; j < i; j++){
            c1.idConta = Integer.parseInt(sc.nextLine());
            c1.nomePessoa = sc.nextLine();
            c1.qtdEmails = Short.parseShort(sc.nextLine());
            c1.emails = new String [c1.qtdEmails];

            for (int u = 0; u < c1.qtdEmails; u++){
                c1.emails[u] = sc.nextLine();
            }

            c1.nomeUser = sc.nextLine();
            c1.senha = sc.nextLine();
            c1.cpf = sc.nextLine();
            c1.cidade = sc.nextLine();
            c1.tranferenciasCompletas = Integer.parseInt(sc.nextLine());
            c1.saldoConta = Float.parseFloat(sc.nextLine());

            try {
                createDescompressao(c1, versao);
            } catch (Exception e) {

            }
        }
        
        System.out.println("Descompressão realizada com sucesso!");
    }

    public void createDescompressao(Conta c1, int versao) throws Exception {
        byte[] ba;
        byte lapide = 0;
        String arq = "banco" + versao + ".db";
        RandomAccessFile ras = new RandomAccessFile(arq, "rw");

        ba = this.toByteArray(c1);
        ras.seek(ras.length());
        ras.writeByte(lapide);
        ras.writeInt(ba.length);
        ras.write(ba);
        ras.close();
    }

    public String decode(int x) {
        List<Integer> encodedText = new ArrayList<>();
        short aux;
        int aux2;
        try {
            String arq = "bancoCompressao"+ x +".db";
            RandomAccessFile ras = new RandomAccessFile(arq, "rw");

            for (long i = 0; i <= ras.length(); i = ras.getFilePointer()){
                aux = ras.readShort();
                aux2 = (int) aux;
                encodedText.add(aux2);
            }
            ras.close();
        } catch (Exception e) {

        }

        int dictSize = 256;
        Map<Integer, String> dictionary = new HashMap<>();
        for (int i = 0; i < dictSize; i++) {
            dictionary.put(i, String.valueOf((char) i));
        }

        String characters = String.valueOf((char) encodedText.remove(0).intValue());
        StringBuilder result = new StringBuilder(characters);
        for (int code : encodedText) {
            String entry = dictionary.containsKey(code)
                    ? dictionary.get(code)
                    : characters + characters.charAt(0);
            result.append(entry);
            dictionary.put(dictSize++, characters + entry.charAt(0));
            characters = entry;
        }

        return result.toString();
    }

}
