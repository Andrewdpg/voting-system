package operation;
import config.DatabaseConfig;
import VotingSystem.PollingStation;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PersistenceImpl implements operation.interfaces.Persistence {

    public PollingStation getPollingStation(int citizenId) {
        PollingStation pollingStationInfo = null;
        String sql = "SELECT pv.nombre AS puesto_votacion, pv.direccion, m.nombre AS ciudad, d.nombre AS departamento "
            + "FROM ciudadano c "
            + "JOIN mesa_votacion mv ON c.mesa_id = mv.id "
            + "JOIN puesto_votacion pv ON mv.puesto_id = pv.id "
            + "JOIN municipio m ON pv.municipio_id = m.id "
            + "JOIN departamento d ON m.departamento_id = d.id "
            + "WHERE c.documento = ?";

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, citizenId + "");

            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    String puestoVotacion = rs.getString("puesto_votacion");
                    String direccion = rs.getString("direccion");
                    String ciudad = rs.getString("ciudad");
                    String departamento = rs.getString("departamento");
                    pollingStationInfo = new PollingStation(puestoVotacion, direccion, ciudad, departamento);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return pollingStationInfo;
    }
}
