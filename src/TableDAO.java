import java.util.*;

public class TableDAO {
    private int passengerId;
    private String Fname;
    private String Lname;
    private String street;
    private String city;
    private String state;
    private String zipcode;
    private String homephone;
    private String cellphone;
    private String dbConfig = "dbConfig.properties";
    private List<TableDAO> list = null;


    public TableDAO() {
        this.list = new ArrayList<TableDAO>();
    }

    public TableDAO(int passengerId) {
        this.passengerId = passengerId;
    }

    public TableDAO(int passengerId, String Fname, String Lname, String street, String city, String state,
                    String zipcode, String homephone, String cellphone) {
        this.passengerId = passengerId;
        this.Fname = Fname;
        this.Lname = Lname;
        this.street = street;
        this.state = state;
        this.city = city;
        this.zipcode = zipcode;
        this.homephone = homephone;
        this.cellphone = cellphone;
    }

    //Accessors and Mutators
    public int getPassengerId() {
        return passengerId;
    }

    public void setPassengerId(int passengerId) {
        this.passengerId = passengerId;
    }

    public String getFname() {
        return Fname;
    }

    public void setFname(String fname) {
        Fname = fname;
    }

    public String getLname() {
        return Lname;
    }

    public void setLname(String lname) {
        Lname = lname;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }


    public void setState(String state) {
        this.state = state;
    }

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    public String getHomephone() {
        return homephone;
    }

    public void setHomephone(String homephone) {
        this.homephone = homephone;
    }

    public String getCellphone() {
        return cellphone;
    }

    public void setCellphone(String cellphone) {
        this.cellphone = cellphone;
    }


    public boolean read() throws DLException {
        boolean rc = false;
        List<List<String>> table = null;

        Database d = new Database(dbConfig);

        String sql = "SELECT passenger.passengerid, fname, lname, street, passenger.zip,city,state, phonenum, phonetype from passenger inner join phones on passenger.passengerid = phones.passengerid inner join zips on passenger.zip = zips.zip order by passenger.passengerid;";
        table = d.getTable(sql, null);

        if (table == null || table.size() == 0) {
            rc = false;
        } else {
            load(table);
            rc = true;
        }

//        table.forEach(passenger -> System.out.println(passenger));


        return rc;
    }

    public boolean read(int passengerId) throws DLException {
        boolean rc = false;
        List<List<String>> table = null;

        Database d = new Database(dbConfig);

        String sql = "SELECT passenger.passengerid, fname, lname, street, passenger.zip,city,state, phonenum, phonetype FROM passenger INNER JOIN phones ON passenger.passengerid = phones.passengerid inner join zips on passenger.zip = zips.zip WHERE passenger.passengerid = " + passengerId;
        table = d.getTable(sql, null);

        if (table == null || table.size() == 0) {
            rc = false;
        } else {
            load(table);
            rc = true;
        }

        //table.forEach(passenger -> System.out.println(passenger));


        return rc;
    }


    public void load(List<List<String>> list) {
        this.list = new ArrayList<TableDAO>();
        for (int i = 0; i < list.size(); i++) {
            List<String> row = list.get(i);
            this.list.add(new TableDAO(Integer.parseInt(row.get(0)), row.get(1), row.get(2), row.get(3), row.get(4)
                    , row.get(5), row.get(6), row.get(7), row.get(8)));


        }
    }


    public static void main(String[] args) {
        try {

            TableDAO t = new TableDAO();
            t.read();
            t.list.forEach(passenger -> System.out.println(passenger.toString()));


            TableDAO t2 = new TableDAO();
            t2.read(2);
            t2.list.forEach(passenger -> System.out.println(passenger.toString()));

        } catch (DLException dl) {
            System.out.println(dl.getMessage());
        }

    }//main end

    @Override
    public String toString() {
        System.out.println("");
        return "TableDAO \n " +
                "passengerId=" + passengerId +
                ",\n Fname='" + Fname + '\'' +
                ",\n Lname='" + Lname + '\'' +
                ",\n street='" + street + '\'' +
                ",\n city='" + city + '\'' +
                ",\n state='" + state + '\'' +
                ",\n zipcode='" + zipcode + '\'' +
                ",\n homephone='" + homephone + '\'' +
                ",\n cellphone='" + cellphone + '\'';


    }
}// class end
