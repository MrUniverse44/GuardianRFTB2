package dev.mruniverse.guardianrftb.multiarena.interfaces;

import dev.mruniverse.guardianrftb.multiarena.GuardianRFTB;

import java.sql.*;

public interface MySQL {

    void setPlugin(GuardianRFTB plugin);

    void connect(String host, String db, String user, String password);

    void close();

    void pUpdate(String qry,String result,String ID);

    Connection getConnection();

    void Update(String qry);

    @SuppressWarnings("unused")
    ResultSet pQuery(String query);

    ResultSet Query(String qry);
}
