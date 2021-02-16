
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

public class Database {
    private Connection conn = null;
    private String protocol = "";
    private String host = "";
    private String db = "";
    private String user = "";
    private String password = "";
    private String driver = "";
    private int transactionCount = 0;

    public Connection getConnection() {
        return this.conn;
    }

    public String getCatalog() {
        return this.db;
    }

    private boolean getConnection(String fileName) throws Exception {
        try {
            if (fileName == null) {
                fileName = "/dbconfig.properties";
            }

            InputStream input = this.getClass().getResourceAsStream(fileName);
            Properties prop = new Properties();
            prop.load(input);
            this.protocol = prop.getProperty("protocol");
            this.host = prop.getProperty("host");
            this.db = prop.getProperty("db");
            this.user = prop.getProperty("user");
            this.password = prop.getProperty("password");
            this.driver = prop.getProperty("driver");
            return true;
        } catch (IOException e) {
            throw new Exception(e);
        }
    }

    private PreparedStatement prepareAndBind(String sql, List<String> params) throws Exception {
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            if (params != null) {
                for (int i = 0; i < params.size(); i++) {
                    pstmt.setString(i + 1, params.get(i));
                }
            }
            return pstmt;
        } catch (SQLException e) {
            throw new Exception(e);
        }
    }

    public Database() throws Exception {
        getConnection(null);
        try {
            Class.forName(this.driver);
            String uri = this.protocol + "://" + this.host + "/" + this.db;
            this.conn = DriverManager.getConnection(uri, this.user, this.password);
            //setting default autocommit to true
            this.conn.setAutoCommit(true);
        } catch (ClassNotFoundException e) {
            throw new Exception(e);
        } catch (SQLException e) {
            throw new Exception(e);
        }
    }


    public Database(String fileName) throws Exception {
        getConnection(fileName);
        try {
            Class.forName(this.driver);
            String uri = this.protocol + "://" + this.host + "/" + this.db;
            this.conn = DriverManager.getConnection(uri, this.user, this.password);
            this.conn.setAutoCommit(true);
        } catch (ClassNotFoundException e) {
            throw new Exception(e);
        } catch (SQLException e) {
            throw new Exception(e);
        }

    }

    public Database(String protocol, String host, String db, String user, String password, String driver) throws Exception {
        this.protocol = protocol;
        this.host = host;
        this.db = db;
        this.user = user;
        this.password = password;
        this.driver = driver;
        try {
            Class.forName(this.driver);
            String uri = this.protocol + "://" + this.host + "/" + this.db;
            this.conn = DriverManager.getConnection(uri, this.user, this.password);
            this.conn.setAutoCommit(true);
        } catch (ClassNotFoundException e) {
            throw new Exception(e);
        } catch (SQLException e) {
            throw new Exception(e);
        }
    }

    public boolean close() throws Exception {
        try {
            this.conn.close();
            return true;
        } catch (Exception e) {
            System.out.println("Database.java:close() " + e);
            return false;
        }
    }

    //Performs Insert/delete update
    public int execute(String sql, List<String> params) throws Exception {
        try {
            PreparedStatement stmt = prepareAndBind(sql, params);
            return stmt.executeUpdate();
        } catch (SQLException e) {
            throw new Exception(e);
        }
    }


    public List<List<String>> getTable(String sql, List<String> params) throws Exception {
        return getTable(sql, params, false);
    }

    //Gets all the rows from Table
    public List<List<String>> getTable(String sql, List<String> params, boolean includeHeader) throws Exception {
        List<List<String>> data = new ArrayList<List<String>>();

        try {
            PreparedStatement stmt = prepareAndBind(sql, params);
            //Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery();
            ResultSetMetaData rsmd = rs.getMetaData();
            int numCols = rsmd.getColumnCount();
            ArrayList<String> row = null;

            if (includeHeader) {
                row = new ArrayList<String>();
                for (int i = 1; i <= numCols; i++) {
                    row.add(rsmd.getColumnName(i));
                }
                data.add(row);
            }

            while (rs.next()) {
                row = new ArrayList<String>();
                for (int i = 1; i <= numCols; i++) {
                    row.add(rs.getString(i));
                }
                data.add(row);
            }
            return data;
        } catch (SQLException e) {
            throw new Exception(e);
        }
    }

    //returns row
    public List<String> getRow(String sql, List<String> params) throws Exception {
        List<String> data = new ArrayList<String>();
        try {
            PreparedStatement stmt = prepareAndBind(sql, params);
            ResultSet rs = stmt.executeQuery();
            ResultSetMetaData rsmd = rs.getMetaData();
            int numCols = rsmd.getColumnCount();
            if (rs.next()) {
                for (int i = 1; i <= numCols; i++) {
                    data.add(rs.getString(i));
                }
            }
            return data;
        } catch (SQLException e) {
            throw new Exception(e);
        }
    }

    //Return Single value
    public String getValue(String sql, List<String> params) throws Exception {
        String data = new String();
        try {
            PreparedStatement stmt = prepareAndBind(sql, params);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                data = rs.getString(1);
            }
            return data;
        } catch (SQLException e) {
            throw new Exception(e);
        }
    }

    //set autocommits to false
    public void startTransaction() throws Exception {
        transactionCount++;
        //checks for nested transaction
        if (transactionCount == 1) {
            try {
                conn.setAutoCommit(false);
            } catch (SQLException e) {
                throw new Exception(e);
            }
        }
    }

    //commits the sql
    public void commitTransaction() throws Exception {
        transactionCount--;
        if (transactionCount == 0) {
            try {
                conn.commit();
                conn.setAutoCommit(true);

            } catch (SQLException e) {
                throw new Exception(e);
            }
        }
    }

    //rollbacks the sql
    public void rollbackTransaction() throws Exception {
        transactionCount--;
        if (transactionCount == 0) {
            try {
                conn.rollback();
                conn.setAutoCommit(true);
                transactionCount = 0;
            } catch (SQLException e) {
                throw new Exception(e);
            }
        }
    }


    public static void main(String[] args) {
        try {
            Database db = new Database();
            System.out.println(db.getConnection().getCatalog());

            //Database db3 = new Database("jdbc:mysql", "localhost","Travel?serverTimezone=UTC","root",password,"com.mysql.cj.jdbc.Driver" );

            //tested from debugger
            db.getRow("select * from passenger where PassengerID = 1", null);

            //test for params
            List<String> params = new ArrayList<String>();
            params.add("1");
            System.out.println(db.getRow("select fname, zip from passenger where passengerID = ?", params));

            //test for nested transactions
            db.startTransaction();
            db.startTransaction();
            db.startTransaction();
            System.out.println(db.transactionCount);
            db.commitTransaction();
            db.commitTransaction();
            System.out.println(db.transactionCount);
            db.rollbackTransaction();
            System.out.println(db.transactionCount);

            System.out.println("db.close: " + db.close());
        } catch (Exception e) {
            System.out.println("Oops");
        }
    }//end main
}//end databa