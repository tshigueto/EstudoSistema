import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class MotoristaDAO {
    public void salvar(Motorista motorista) throws SQLException {
        String sql = "INSERT INTO motoristas (nome, cpf, data_nascimento) VALUES (?, ?, ?)";

        try (Connection conn = Conexao.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, motorista.getNomeMotorista());
            pstmt.setString(2, motorista.getCpf());
            pstmt.setDate(3, Date.valueOf(motorista.getDataNascimento()));

            pstmt.executeUpdate();
            System.out.println("Motorista salvo com sucesso via DAO!");
        }
    }
}
