import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        
        // 1. CARREGAMENTO DO LOGOTIPO (Tratado uma única vez)
        ImageIcon icon = null;
        try {
            ImageIcon originalIcon = new ImageIcon(Main.class.getResource("/logoLogrisk.png"));
            Image image = originalIcon.getImage();
            Image newimg = image.getScaledInstance(120, 92, java.awt.Image.SCALE_SMOOTH);
            icon = new ImageIcon(newimg);
        } catch (Exception e) {
            System.out.println("Aviso: Logotipo não encontrado.");
        }

        // Instanciando os objetos de dados
        Motorista motorista = new Motorista();
        Caminhao caminhao = new Caminhao();
        Rota rota = new Rota();

        // 2. TELA DE APRESENTAÇÃO
        exibirMensagemCustomizada(
            "<html><div style='text-align: center;'><b>Bem-vindo ao Sistema de Logística Logrisk</b></div></html>", 
            "SISTEMA", 
            icon
        );

        // --- ENTRADA DE DADOS: MOTORISTA ---
        String nome = JOptionPane.showInputDialog(null, "Nome do Motorista:", "Cadastro", JOptionPane.QUESTION_MESSAGE);
        if (nome == null) System.exit(0);
        motorista.setNomeMotorista(nome);

        String cpfStr = null;
        while (true) {
            cpfStr = JOptionPane.showInputDialog(null, "CPF do Motorista (11 dígitos):", "Cadastro", JOptionPane.QUESTION_MESSAGE);
            if (cpfStr == null) System.exit(0);
            String cpfNumerico = cpfStr.replaceAll("[^\\d]", "");
            if (validaCPF(cpfNumerico)) break;
            JOptionPane.showMessageDialog(null, "CPF inválido!", "Erro", JOptionPane.ERROR_MESSAGE);
        }
        motorista.setCpf(formatarCpf(cpfStr.replaceAll("[^\\d]", "")));

        LocalDate dataNascimento = null;
        while (true) {
            String dataTxt = JOptionPane.showInputDialog(null, "Data de Nascimento (dd/MM/yyyy):", "Cadastro", JOptionPane.QUESTION_MESSAGE);
            if (dataTxt == null) System.exit(0);
            try {
                dataNascimento = LocalDate.parse(dataTxt, fmt);
                break;
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Formato incorreto!", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
        motorista.setDataNascimento(dataNascimento);

        // --- SALVAMENTO VIA DAO ---
        try {
            MotoristaDAO dao = new MotoristaDAO();
            dao.salvar(motorista);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Erro ao salvar no banco: " + e.getMessage());
        }

        // --- ENTRADA DE DADOS: CAMINHÃO ---
        String modelo = JOptionPane.showInputDialog(null, "Modelo do Caminhão:");
        if (modelo == null) System.exit(0);
        caminhao.setNomeCaminhao(modelo);

        // --- ENTRADA DE DADOS: ROTA ---
        rota.setCidadePartida(JOptionPane.showInputDialog(null, "Cidade de Partida:"));
        rota.setCidadeDestino(JOptionPane.showInputDialog(null, "Cidade de Destino:"));
        
        String distStr = JOptionPane.showInputDialog(null, "Distância (km):");
        rota.setDistancia(Double.parseDouble(distStr));

        // --- RESUMO FINAL ---
        String resumo = String.format(
            "<html><div style='text-align: left;'>" +
            "<b>Olá %s!</b><br><br>" +
            "CPF N: %s<br>" +
            "Data de Nascimento: %s<br><br>" +
            "<u>DADOS DA ROTA:</u><br>" +
            "Partida: %s<br>" +
            "Destino: %s<br>" +
            "Distância: %.1f km<br><br>" +
            "<i>Boa viagem!</i></div></html>",
            motorista.getNomeMotorista(), motorista.getCpf(),
            motorista.getDataNascimento().format(fmt),
            rota.getCidadePartida(), rota.getCidadeDestino(), rota.getDistancia()
        );

        exibirMensagemCustomizada(resumo, "Resumo da Rota", icon);
    }

    // MÉTODOS AUXILIARES OBRIGATÓRIOS
    public static String formatarCpf(String cpf) {
        return cpf.replaceFirst("(\\d{3})(\\d{3})(\\d{3})(\\d{2})", "$1.$2.$3-$4");
    }

    public static boolean validaCPF(String cpf) {
        cpf = cpf.replaceAll("[^\\d]", "");
        if (cpf.length() != 11 || cpf.matches("(\\d)\\1{10}")) return false;
        try {
            int soma1 = 0, soma2 = 0;
            for (int i = 0; i < 9; i++) {
                int num = Character.getNumericValue(cpf.charAt(i));
                soma1 += num * (10 - i);
            }
            int resto1 = (soma1 * 10) % 11;
            if (resto1 == 10) resto1 = 0;
            for (int i = 0; i < 10; i++) {
                int num = Character.getNumericValue(cpf.charAt(i));
                soma2 += num * (11 - i);
            }
            int resto2 = (soma2 * 10) % 11;
            if (resto2 == 10) resto2 = 0;
            return (resto1 == Character.getNumericValue(cpf.charAt(9)) &&
                    resto2 == Character.getNumericValue(cpf.charAt(10)));
        } catch (Exception e) { return false; }
    }

    public static void exibirMensagemCustomizada(String texto, String titulo, ImageIcon icon) {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setPreferredSize(new Dimension(500, 300));
        JLabel labelTexto = new JLabel(texto);
        labelTexto.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(labelTexto, BorderLayout.CENTER);
        if (icon != null) {
            JLabel labelIcone = new JLabel(icon);
            labelIcone.setHorizontalAlignment(SwingConstants.CENTER);
            panel.add(labelIcone, BorderLayout.SOUTH);
        }
        JOptionPane.showMessageDialog(null, panel, titulo, JOptionPane.PLAIN_MESSAGE);
    }
}





/*import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        ImageIcon icon = null;
        try {
            ImageIcon originalIcon = new ImageIcon(Main.class.getResource("/logoLogrisk.png"));
            Image image = originalIcon.getImage();
            Image newimg = image.getScaledInstance(120, 92, java.awt.Image.SCALE_SMOOTH);
            icon = new ImageIcon(newimg);
        } catch (Exception e) {
            System.out.println("Aviso: Logotipo não encontrado.");
        }

        Caminhao caminhao = new Caminhao();
        Motorista motorista = new Motorista();
        Rota rota = new Rota();

        exibirMensagemCustomizada("<html><b>Bem-vindo ao Sistema de Logística Logrisk</b></html>", "SISTEMA", icon);

        String nome = JOptionPane.showInputDialog(null, "Nome do Motorista:", "Cadastro", JOptionPane.QUESTION_MESSAGE);
        if (nome == null) System.exit(0);
        motorista.setNomeMotorista(nome);

        String cpfStr = null;
        while (true) {
            cpfStr = JOptionPane.showInputDialog(null, "CPF do Motorista (somente números, 11 dígitos):", "Cadastro", JOptionPane.QUESTION_MESSAGE);
            if (cpfStr == null) System.exit(0);

            String cpfNumerico = cpfStr.replaceAll("[^\\d]", "");
            if (validaCPF(cpfNumerico)) {
                break;
            } else {
                JOptionPane.showMessageDialog(null, "CPF inválido! Digite novamente.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
        String cpfFormatado = formatarCpf(cpfStr.replaceAll("[^\\d]", ""));
        motorista.setCpf(cpfFormatado);

        LocalDate dataNascimento = null;
        while (true) {
            String dataTxt = JOptionPane.showInputDialog(null, "Data de Nascimento (dd/MM/yyyy):", "Cadastro", JOptionPane.QUESTION_MESSAGE);
            if (dataTxt == null) System.exit(0);
            try {
                dataNascimento = LocalDate.parse(dataTxt, fmt);
                break;
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Formato incorreto! Favor preencher novamente!", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
        motorista.setDataNascimento(dataNascimento);

        salvarNoBanco(motorista.getNomeMotorista(), motorista.getCpf(), motorista.getDataNascimento());

        String modelo = JOptionPane.showInputDialog(null, "Modelo do Caminhão:", "Cadastro", JOptionPane.QUESTION_MESSAGE);
        if (modelo == null) System.exit(0);
        caminhao.setNomeCaminhao(modelo);

        int anoCaminhao = 0;
        int anoAtual = Year.now().getValue();
        while (true) {
            String anoStr = JOptionPane.showInputDialog(null, "Ano de Fabricação (4 dígitos, entre 1900 e " + (anoAtual - 1) + "):", "Cadastro", JOptionPane.QUESTION_MESSAGE);
            if (anoStr == null) System.exit(0);
            if (anoStr.matches("\\d{4}")) {
                anoCaminhao = Integer.parseInt(anoStr);
                if (anoCaminhao >= 1900 && anoCaminhao < anoAtual) {
                    break;
                } else {
                    JOptionPane.showMessageDialog(null, "Ano inválido! Deve estar entre 1900 e " + (anoAtual - 1) + ".", "Erro", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(null, "Formato incorreto! O ano deve conter exatamente 4 dígitos numéricos.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
        caminhao.setAnoCaminhao(anoCaminhao);

        double distancia = 0;
        while (true) {
            String distStr = JOptionPane.showInputDialog(null, "Distância da Rota (km):", "Cadastro", JOptionPane.QUESTION_MESSAGE);
            if (distStr == null) System.exit(0);
            try {
                distancia = Double.parseDouble(distStr);
                if (distancia > 0) {
                    break;
                } else {
                    JOptionPane.showMessageDialog(null, "A distância deve ser maior que zero.", "Erro", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Formato incorreto! A distância deve ser numérica.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
        rota.setDistancia(distancia);

        String partida = null;
        while (true) {
            partida = JOptionPane.showInputDialog(null, "Cidade de Partida:", "Cadastro", JOptionPane.QUESTION_MESSAGE);
            if (partida == null) System.exit(0);
            if (partida.matches("[A-Za-zÀ-ÿ\\s]+")) {
                break;
            } else {
                JOptionPane.showMessageDialog(null, "Formato incorreto! O nome da cidade não pode conter números.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
        rota.setCidadePartida(partida);

        String destino = null;
        while (true) {
            destino = JOptionPane.showInputDialog(null, "Cidade de Destino:", "Cadastro", JOptionPane.QUESTION_MESSAGE);
            if (destino == null) System.exit(0);
            if (destino.matches("[A-Za-zÀ-ÿ\\s]+")) {
                break;
            } else {
                JOptionPane.showMessageDialog(null, "Formato incorreto! O nome da cidade não pode conter números.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
        rota.setCidadeDestino(destino);

        String resumo = String.format(
            "<html><div style='text-align: left;'>" +
            "<b>Olá %s!</b><br><br>" +
            "CPF N: %s<br>" +
            "Data de Nascimento: %s<br><br>" +
            "<u>DADOS DA ROTA:</u><br>" +
            "Partida: %s<br>" +
            "Destino: %s<br>" +
            "Distância: %.1f km<br><br>" +
            "<i>Boa viagem!</i></div></html>",
            motorista.getNomeMotorista(), motorista.getCpf(),
            motorista.getDataNascimento().format(fmt),
            rota.getCidadePartida(), rota.getCidadeDestino(), rota.getDistancia()
        );

        exibirMensagemCustomizada(resumo, "Resumo da Rota", icon);
    }

    public static void salvarNoBanco(String nome, String cpf, LocalDate data) {
        String url = "jdbc:mysql://localhost:3306/logrisk_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
        String usuario = "root";
        String senha = "admin@840"; 

        String sql = "INSERT INTO motoristas (nome, cpf, data_nascimento) VALUES (?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(url, usuario, senha);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, nome);
            pstmt.setString(2, cpf);
            pstmt.setDate(3, java.sql.Date.valueOf(data));

            pstmt.executeUpdate();
            System.out.println("Motorista salvo no banco com sucesso!");

            } catch (Exception e) {
            e.printStackTrace(); 
            JOptionPane.showMessageDialog(null, "Erro detalhado: " + e.getMessage());
        }

    }

    public static String formatarCpf(String cpf) {
        return cpf.replaceFirst("(\\d{3})(\\d{3})(\\d{3})(\\d{2})", "$1.$2.$3-$4");
    }

    public static boolean validaCPF(String cpf) {
        cpf = cpf.replaceAll("[^\\d]", "");
        if (cpf.length() != 11) return false;
        if (cpf.matches("(\\d)\\1{10}")) return false;

        int soma1 = 0, soma2 = 0;
        int resto1, resto2;

        for (int i = 0; i < 9; i++) {
            int num = Character.getNumericValue(cpf.charAt(i));
            soma1 += num * (10 - i);
        }
        resto1 = (soma1 * 10) % 11;
        if (resto1 == 10) resto1 = 0;

        for (int i = 0; i < 10; i++) {
            int num = Character.getNumericValue(cpf.charAt(i));
            soma2 += num * (11 - i);
        }
        resto2 = (soma2 * 10) % 11;
        if (resto2 == 10) resto2 = 0;

        return (resto1 == Character.getNumericValue(cpf.charAt(9)) &&
                resto2 == Character.getNumericValue(cpf.charAt(10)));
    }

    public static void exibirMensagemCustomizada(String texto, String titulo, ImageIcon icon) {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setPreferredSize(new Dimension(500, 500));

        JLabel labelTexto = new JLabel(texto);
        labelTexto.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(labelTexto, BorderLayout.CENTER);

        if (icon != null) {
            JLabel labelIcone = new JLabel(icon);
            labelIcone.setHorizontalAlignment(SwingConstants.CENTER);
            panel.add(labelIcone, BorderLayout.SOUTH);
        }

        JOptionPane.showMessageDialog(null, panel, titulo, JOptionPane.PLAIN_MESSAGE);
    }
}
*/