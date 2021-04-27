package dev.mruniverse.guardianrftb.multiarena.storage;

import dev.mruniverse.guardianrftb.multiarena.GuardianRFTB;
import dev.mruniverse.guardianrftb.multiarena.enums.GuardianFiles;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

@SuppressWarnings("unused")
public class DataStorage {
    private final GuardianRFTB plugin;
    private final DataInfo dataInfo;
    private final MySQL mySQL;
    private final SQL sql;
    public DataStorage(GuardianRFTB main) {
        plugin = main;
        mySQL = new MySQL(main);
        dataInfo = new DataInfo(main);
        sql = new SQL(main);
    }

    public void createMultiTable(String tableName, List<String> intLists, List<String> sLists) {
        StringBuilder intList = new StringBuilder();
        for (String text : intLists)
            intList.append(", ").append(text).append(" INT(255)");
        StringBuilder vList = new StringBuilder();
        for (String string : sLists)
            vList.append(", ").append(string).append(" VARCHAR(255)");
        mySQL.Update("CREATE TABLE IF NOT EXISTS " + tableName + " (id INT AUTO_INCREMENT PRIMARY KEY" + vList + intList + ")");
    }

    public void setInt(String tableName, String column, String where, String what, int integer) {
        what = what.replace("-","");
        mySQL.Update("UPDATE " + tableName + " SET " + column + "= '" + integer + "' WHERE " + where + "= '" + what + "';");
    }

    public void setString(String tableName, String column, String where, String is, String result) {
        is = is.replace("-","");
        mySQL.pUpdate("UPDATE `" + tableName + "` SET " + column + "=? WHERE " + where + "=?;",result,is);
    }

    public String getString(String tableName, String column, String where, String what) {
        what = what.replace("-","");
        String string = "";
        try {
            String query = "SELECT " + column + " FROM " + tableName + " WHERE " + where + "= '" + what + "';";
            Connection connection = mySQL.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet results = statement.executeQuery();
            if (results.next()) return results.getString(column);
        } catch (Throwable throwable) {
            plugin.getLogs().error("Can't get a string from the database.");
            plugin.getLogs().error(throwable);
        }
        return string;
    }

    public Integer getInt(String tableName, String column, String where, String what) {
        what = what.replace("-","");
        int integer = 0;
        try {
            ResultSet rs = mySQL.Query("SELECT " + column + " FROM " + tableName + " WHERE " + where + "= '" + what + "';");
            while (rs.next())
                integer = rs.getInt(1);
            rs.close();
        } catch (Throwable throwable) {
            plugin.getLogs().error("Can't get an integer from the database.");
            plugin.getLogs().error(throwable);
        }
        return integer;
    }

    public boolean isRegistered(String tableName, String where, String what) {
        what = what.replace("-","");
        try (ResultSet rs = mySQL.Query("SELECT id FROM " + tableName + " WHERE " + where + "= '" + what + "';")) {
            boolean bol = rs.next();
            rs.close();
            return bol;
        } catch (Throwable throwable) {
            plugin.getLogs().error("Can't check internal data in database.");
            plugin.getLogs().error(throwable);
        }
        return false;
    }

    public void register(String tableName, List<String> values) {
        StringBuilder names = new StringBuilder();
        StringBuilder names2 = new StringBuilder();
        for (String value : values) {
            String[] valSplit = value.split("-");
            names.append(valSplit[0]).append(", ");
            names2.append("'").append(valSplit[1]).append("', ");
        }
        names = new StringBuilder(names.substring(0, names.length() - 2));
        names2 = new StringBuilder(names2.substring(0, names2.length() - 2));
        mySQL.Update("INSERT INTO " + tableName + " (" + names + ") VALUES (" + names2 + ")");
    }

    public void loadDatabase() {
        if (plugin.getStorage().getControl(GuardianFiles.MYSQL).getBoolean("mysql.enabled")) {
            mySQL.connect(plugin.getStorage().getControl(GuardianFiles.MYSQL).getString("mysql.host"),plugin.getStorage().getControl(GuardianFiles.MYSQL).getString("mysql.database"),plugin.getStorage().getControl(GuardianFiles.MYSQL).getString("mysql.username"),plugin.getStorage().getControl(GuardianFiles.MYSQL).getString("mysql.password"));
        } else {
            sql.loadData();
            plugin.getLogs().info("MySQL is disabled, using data.yml");
        }
    }

    public void disableDatabase() {
        if (plugin.getStorage().getControl(GuardianFiles.MYSQL).getBoolean("mysql.enabled")) {
            dataInfo.save();
            mySQL.close();
        } else {
            sql.putData();
        }
    }
    public DataInfo getData() {
        return dataInfo;
    }

    public MySQL getMySQL() {
        return mySQL;
    }
    public SQL getSQL() {
        return sql;
    }
}