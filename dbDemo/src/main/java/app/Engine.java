package app;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class Engine {
    private Connection connection;

    public Engine(Connection connection) {
        this.connection = connection;
    }

    public void run() throws SQLException, IOException {
        // this.getVillainsName();
        //  this.getMinionName();
        //  this.addMinion();
        //  this.changeTownNamesCasing();
        //  this.printAllMinionNames();
        //  this.increaseMinionAge();
       // this.increaseAgeStoredProcedure();
        this.removeVillains();
    }

    /*
     ** Problem 9. Increase Age Stored Procedure
     */
    private void increaseAgeStoredProcedure() throws SQLException {
        Scanner scanner = new Scanner(System.in);
        int idToTest = Integer.parseInt(scanner.nextLine());

        CallableStatement callableStatement = this.connection.prepareCall("{CALL usp_get_older(?)}");
        callableStatement.setInt(1, idToTest);
        callableStatement.execute();

        PreparedStatement preparedStatement = this.connection.prepareStatement("SELECT name, age FROM minions WHERE id = ?");
        preparedStatement.setInt(1, idToTest);
        ResultSet rs = preparedStatement.executeQuery();
        rs.next();
        System.out.printf("%s %s%n", rs.getString("name"), rs.getInt("age"));
    }


    /*
     ** Problem 8. Increase Minions Age
     */
    private void increaseMinionAge() throws SQLException, IOException {
        Scanner scanner = new Scanner(System.in);

        String[] arr = scanner.nextLine().split(" ");
        int[] listOfNumbers = new int[arr.length];
        for (int i = 0; i < arr.length; i++)
            listOfNumbers[i] = Integer.parseInt(arr[i]);

        String q = "SELECT name, age FROM minions WHERE id = ? ";
        String query = "UPDATE minions m SET m.name = CONCAT(UCASE(SUBSTRING(m.name, 1, 1)), lower(SUBSTRING(m.name, 2))), m.age = m.age + 1 WHERE id = ?";
        PreparedStatement preparedStatement = this.connection.prepareStatement(query);
        PreparedStatement ps = this.connection.prepareStatement(q);

        for (int i = 0; i < listOfNumbers.length; i++) {
            preparedStatement.setInt(1, listOfNumbers[i]);
            ps.setInt(1, listOfNumbers[i]);
            preparedStatement.executeUpdate();
            ResultSet rs = ps.executeQuery();
        }

        String queryResult = "SELECT name, age FROM minions";
        PreparedStatement psResult = this.connection.prepareStatement(queryResult);
        ResultSet resultSet = psResult.executeQuery();
        while (resultSet.next()) {
            System.out.println(String.format("%s %d", resultSet.getString(1), resultSet.getInt(2)));
        }
    }


    /*
     ** Problem 7. Print All Minion Names
     */
    private void printAllMinionNames() throws SQLException {
        List<String> listOfNames = new ArrayList<>();
        List<String> temp = new ArrayList<>();

        String query = "SELECT name FROM minions";
        PreparedStatement preparedStatement = this.connection.prepareStatement(query);
        ResultSet rs = preparedStatement.executeQuery();

        while (rs.next()) {
            listOfNames.add(rs.getString(1));
        }
        int count = 0;
        for (int i = 0; i < listOfNames.size() / 2; i++) {
            String temporary1 = listOfNames.get(i);
            String temporary2 = listOfNames.get(listOfNames.size() - 1 - i);
            temp.add(temporary1);
            temp.add(temporary2);
        }
        // System.out.println(String.join(", ", listOfNames));
        System.out.println(String.join(", ", temp));
    }

        /*
        ** Problem 6. Remove Villain
     */

    private void removeVillains() throws SQLException {
        Scanner scanner = new Scanner(System.in);
        String villainName;
        int villainsId = Integer.parseInt(scanner.nextLine());

        ResultSet resultSet = selectVillainStatements(villainsId);

        if (!resultSet.isBeforeFirst()) {
            System.out.println("No such villain was found");
        } else {
            resultSet.next();
            villainName = resultSet.getString("name");

            PreparedStatement releaseMinionsStatement =
                    connection.prepareStatement("DELETE mv FROM minions_villains AS mv WHERE mv.villain_id = ?");
            releaseMinionsStatement.setInt(1, villainsId);
            int affectedRows = releaseMinionsStatement.executeUpdate();

            PreparedStatement removeVillainStatement = removeVillainStatements(villainName, villainsId, affectedRows);

            releaseMinionsStatement.closeOnCompletion();
            removeVillainStatement.closeOnCompletion();
        }

        scanner.close();
        connection.close();
    }

    private ResultSet selectVillainStatements(int villainsId) throws SQLException {
        PreparedStatement preparedStatement =
                connection.prepareStatement("SELECT name FROM villains WHERE id = ?");
        preparedStatement.setInt(1, villainsId);
        return preparedStatement.executeQuery();
    }

    private PreparedStatement removeVillainStatements(String villainName, int villainsId, int affectedRows) throws SQLException {
        PreparedStatement preparedStatement =
                connection.prepareStatement("DELETE FROM villains WHERE id = ?");
        preparedStatement.setInt(1, villainsId);
        preparedStatement.executeUpdate();

        System.out.println(villainName + " was deleted");
        System.out.println(affectedRows + " minions released");
        return preparedStatement;
    }


    /*
     ** Problem 5. Change Town Names Casing
     */
    private void changeTownNamesCasing() throws SQLException {
        Scanner scanner = new Scanner(System.in);
        String countryName = scanner.nextLine();

        String query = "UPDATE towns SET name = UPPER(towns.name) WHERE country LIKE ?";
        PreparedStatement preparedStatement = this.connection.prepareStatement(query);
        preparedStatement.setString(1, countryName);
        int affectedRows = preparedStatement.executeUpdate();
        List<String> updateNames = new ArrayList<String>();

        updateTownNames(countryName, affectedRows, updateNames);
    }

    private void updateTownNames(String countryName, int affectedRows, List<String> updateNames) throws
            SQLException {
        if (affectedRows != 0) {
            String query = "SELECT name FROM towns WHERE country = ?";
            PreparedStatement preparedStatement =
                    connection.prepareStatement(query);
            preparedStatement.setString(1, countryName);

            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                String townName = rs.getString("name");
                updateNames.add(townName);
            }

            System.out.println(affectedRows + " town names were affected.");
            System.out.println(String.join(", ", updateNames));
        } else {
            System.out.println("No towns were effected.");
        }
    }


    /*
     ** Problem 4. Add Minion
     */
    private void addMinion() throws SQLException {
        Scanner scanner = new Scanner(System.in);

        String[] minionData = scanner.nextLine().split("\\s+");
        String[] villainData = scanner.nextLine().split("\\s+");

        String minionName = minionData[1];
        int minionAge = Integer.parseInt(minionData[2]);
        String townName = minionData[3];
        String villainName = villainData[1];

        if (!this.checkIfEntityExists(townName, "towns")) {
            this.insertTown(townName);
        }
        if (!this.checkIfEntityExists(villainName, "villains")) {
            this.insertVillain(villainName);
        }
        int townId = this.getEntityId(townName, "towns");

        this.insertMinion(minionName, minionAge, townId);
        int minionId = this.getEntityId(minionName, "minions");
        int villainId = this.getEntityId(villainName, "villains");
        this.hireMinion(minionId, villainId);

        System.out.println(String.format("Successfully added %s to be minion of %s", minionName, villainName));
    }

    private void insertMinion(String minionName, int age, int townId) throws SQLException {
        String query = String
                .format("INSERT INTO minions (name, age, town_id) VALUES ('%s', '%d', '%d')", minionName, age, townId);
        PreparedStatement preparedStatement = this.connection.prepareStatement(query);
        preparedStatement.execute();
    }

    private int getEntityId(String name, String tableName) throws SQLException {
        String query = String.format("SELECT id FROM %s WHERE name = '%s'", tableName, name);
        PreparedStatement preparedStatement = this.connection.prepareStatement(query);
        ResultSet resultSet = preparedStatement.executeQuery();

        resultSet.next();
        return resultSet.getInt(1);
    }

    private void insertVillain(String villainName) throws SQLException {
        String query = String
                .format("INSERT INTO villains(name, evilness_factor) VALUES ('%s', 'evil')", villainName);
        PreparedStatement preparedStatement = this.connection.prepareStatement(query);
        preparedStatement.execute();

        System.out.println(String.format("Villain %s was added to the database.", villainName));
    }

    private boolean checkIfEntityExists(String name, String tableName) throws SQLException {
        String query = "SELECT * FROM " + tableName + " WHERE name = ?";
        PreparedStatement preparedStatement = this.connection.prepareStatement(query);
        preparedStatement.setString(1, name);

        ResultSet rs = preparedStatement.executeQuery();
        if (rs.next()) {
            return true;
        }
        return false;
    }

    private void insertTown(String townName) throws SQLException {
        String query = "INSERT INTO towns(name, country) VALUES ('" + townName + "', NULL)";
        PreparedStatement statement = this.connection.prepareStatement(query);
        statement.execute();

        System.out.println(String.format("Town %s was added to the database.", townName));
    }

    private void hireMinion(int minionId, int villainId) throws SQLException {
        String query = String
                .format("INSERT INTO minions_villains (minion_id, villain_id) VALUES (%d, %d)", minionId, villainId);
        PreparedStatement preparedStatement = this.connection.prepareStatement(query);
        preparedStatement.execute();
    }


    /*
     ** Problem 3. Get Minion Names
     */
    private void getMinionName() throws SQLException {
        Scanner scanner = new Scanner(System.in);

        int villainId = Integer.parseInt(scanner.nextLine());
        String query = "SELECT m.name, m.age, v.name FROM villains AS v LEFT JOIN minions_villains AS mv ON mv.villain_id = v.id LEFT JOIN minions AS m ON mv.minion_id = m.id WHERE v.id = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setInt(1, villainId);

        ResultSet rs = preparedStatement.executeQuery();


        if (!rs.next()) {
            System.out.println(String.format("No villain with ID %d exists in the database.", villainId));
        } else if (rs.getString(1) == null) {
            System.out.println(String.format("Villain: %s", rs.getString(3)));
        } else {
            System.out.println(String.format("Villains: %s", rs.getString(3)));
            while (rs.next()) {
                System.out.println(String.format("%s %d", rs.getString(1), rs.getInt(2)));
            }
        }
    }


    /*
     ** Problem 2. Get Villains' Names
     */
    private void getVillainsName() throws SQLException {
        String query = "SELECT v.name, COUNT(minion_id) FROM villains v JOIN minions_villains v2 on v.id = v2.villain_id GROUP BY v.name HAVING COUNT(minion_id) > ? ORDER BY COUNT(minion_id) DESC";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setInt(1, 3);

        ResultSet rs = preparedStatement.executeQuery();

        while (rs.next()) {
            System.out.println(String.format("%s %d", rs.getString(1), rs.getInt(2)));
        }
    }
}
