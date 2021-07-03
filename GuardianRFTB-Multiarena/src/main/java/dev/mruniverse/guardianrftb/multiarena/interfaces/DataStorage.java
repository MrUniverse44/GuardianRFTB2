package dev.mruniverse.guardianrftb.multiarena.interfaces;

import dev.mruniverse.guardianrftb.multiarena.GuardianRFTB;

import java.util.List;

@SuppressWarnings("unused")
public interface DataStorage {
    void setPlugin(GuardianRFTB plugin);

    void setMySQL(MySQL mysql);

    void setDataInfo(DataInfo dataInfo);

    void setSQL(SQL sql);

    void createMultiTable(String tableName, List<String> intLists, List<String> sLists);

    void setInt(String tableName, String column, String where, String what, int integer);

    void setString(String tableName, String column, String where, String is, String result);

    String getString(String tableName, String column, String where, String what);

    Integer getInt(String tableName, String column, String where, String what);

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    boolean isRegistered(String tableName, String where, String what);

    void register(String tableName, List<String> values);

    void loadDatabase();

    void disableDatabase();

    DataInfo getData();

    MySQL getMySQL();
    SQL getSQL();
}
