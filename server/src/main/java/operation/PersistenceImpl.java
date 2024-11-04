package operation;
import config.DatabaseConfig;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PersistenceImpl implements operation.interfaces.Persistence {

    public String getPollingStation(int citizenId) {
        String pollingStation = null;
        String sql = "SELECT vp.address FROM person p "
            + "JOIN asign a ON p.id = a.person "
            + "JOIN votingpost vp ON a.post = vp.id "
            + "WHERE p.id = ?";

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, citizenId);

            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    pollingStation = rs.getString("address");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return pollingStation;
    }
}
