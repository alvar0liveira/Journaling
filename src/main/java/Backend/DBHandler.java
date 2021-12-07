/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Backend;

import java.sql.Connection; 
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.sqlite.mc.SQLiteMCSqlCipherConfig;

/**
 *
 * @author Asus
 */
public class DBHandler {
    
    //private String filePath;
    private Connection con;
    
    public DBHandler(String filePath, String password) throws SQLException {
      con = SQLiteMCSqlCipherConfig.getV4Defaults().withKey(password)
              .createConnection("jdbc:sqlite:" + filePath);
    
    }
    
    public void createDatabase(String key) throws SQLException{
        Statement smt = con.createStatement();
        smt.executeUpdate("DROP TABLE IF EXISTS Notes");
        smt.executeUpdate("CREATE TABLE \"Notes\" (\n" +
            "	\"id\"	INTEGER NOT NULL UNIQUE,\n" +
            "	\"day\"	TEXT NOT NULL,\n" +
            "	\"note\"	TEXT,\n" +
            "	PRIMARY KEY(\"id\" AUTOINCREMENT))");
        smt.executeUpdate("PRAGMA rekey=\"" + key + "\"");
        con.close();
    }
    
    public List<String> getDays() throws SQLException{
        ArrayList<String> listOfDays = new ArrayList<>();
        Statement smt = con.createStatement();
        ResultSet rs = smt.executeQuery("Select day From Notes;");
        while(rs.next()){
            listOfDays.add(rs.getString("day"));
        }
        return listOfDays;
    }
    
    public String getNote(String day) throws SQLException{
        PreparedStatement  smt = con.prepareStatement("SELECT * FROM Notes WHERE day = ?;");
        smt.setString(1, day);
        ResultSet rs = smt.executeQuery();
        return rs.getString("note");
    }
    
    public void updateNote(String day, String note) throws SQLException{
        PreparedStatement  smt = con.prepareStatement("UPDATE Notes SET note = ? WHERE day = ?;");
        smt.setString(1, note);
        smt.setString(2, day);
        smt.executeUpdate();
    }
    
    public void createToday() throws SQLException{
       
        LocalDateTime today = LocalDateTime.now();
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String todayFormated = today.format(dateFormat);
        
        
        PreparedStatement statement = con.prepareStatement("SELECT count(*) as rowcount FROM Notes WHERE day = ?");
        statement.setString(1, todayFormated);
        ResultSet rs = statement.executeQuery();
        
        if(rs.getInt("rowcount") >= 1){
            
        } else {
            PreparedStatement  smt = con.prepareStatement("INSERT INTO Notes (day) VALUES (?)");
            smt.setString(1, todayFormated);
            smt.executeUpdate();
        }
    }
}
