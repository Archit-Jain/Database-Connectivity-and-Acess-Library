import java.util.*;

public class Passengers {
    //Attributes
    private int passengerId;
    private String fName;
    private String lName;
    private String street;
    private String city;
    private String state;
    private String zip;
    private String homePhone;
    private String cellPhone;
    private List<Passengers> list = null;
    private String dbFile = "dbConfig.properties";

    //getter and setters

    public int getPassengerId() {
        return passengerId;
    }

    public void setPassengerId(int passengerId) {
        this.passengerId = passengerId;
    }

    public String getfName() {
        return fName;
    }

    public void setfName(String fName) {
        this.fName = fName;
    }

    public String getlName() {
        return lName;
    }

    public void setlName(String lName) {
        this.lName = lName;
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

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getHomePhone() {
        return homePhone;
    }

    public void setHomePhone(String homePhone) {
        this.homePhone = homePhone;
    }

    public String getCellPhone() {
        return cellPhone;
    }

    public void setCellPhone(String cellPhone) {
        this.cellPhone = cellPhone;
    }

    //Accessors
    List<Passengers> getPassengers() {
        return this.list;
    }

    Passengers getPassenger(int i) {
        return this.list.get(i);
    }

    //Constructor
    public Passengers() {
        this.list = new ArrayList<Passengers>();
    }

    public Passengers(int passengerId) {
        this.passengerId = passengerId;
    }

    public Passengers(int passengerId, String fName, String lName, String street, String city, String state, String zip, String homePhone, String cellPhone) {
        this.passengerId = passengerId;
        this.fName = fName;
        this.lName = lName;
        this.street = street;
        this.city = city;
        this.state = state;
        this.zip = zip;
        this.homePhone = homePhone;
        this.cellPhone = cellPhone;
    }

    //Virtual DAO read operation
    public boolean read(int passengerId) throws DLException {
        boolean rc = false;
        List<List<String>> tbl = null;
        Database db = new Database(dbFile);
        String sql = "SELECT passenger.passengerid, fname, lname,street,passenger.zip,city,state,home.phonenum AS homephone,cell.phonenum AS cellphone FROM passenger " +
                "JOIN zips ON passenger.zip = zips.zip " +
                "LEFT JOIN phones home ON passenger.passengerid=home.passengerId AND home.phonetype=\"Home\" " +
                "LEFT JOIN phones cell ON passenger.passengerid=cell.passengerId AND cell.phonetype=\"Cell\" " +
                "WHERE passenger.passengerid=" + passengerId;
        tbl = db.getTable(sql, null);

        if (tbl == null || tbl.size() == 0) {
            rc = false;
        } else {
            load(tbl);
            rc = true;
        }
        db.close();
        return rc;
    }

    private void load(List<List<String>> rs) {
        this.list = new ArrayList<Passengers>();
        //load data
        for (int i = 0; i < rs.size(); i++) {
            List<String> row = rs.get(i);
            //condition to check if alteast Home or Cell phonenumber is present
            if (row.get(7) != null || row.get(8) != null) {
                this.list.add(new Passengers(Integer.parseInt(row.get(0)), row.get(1), row.get(2), row.get(3), row.get(4), row.get(5), row.get(6), row.get(7), row.get(8)));
            }
        }
    }

    public static void main(String[] args) {
        try {
            Passengers p = new Passengers();
            //checks if read returns true
            if (p.read(1)) {
                System.out.println("********");
                for (int i = 0; i < p.list.size(); i++) {
                    System.out.println("" +
                            "PassengerID: " + p.list.get(i).passengerId
                            + "\nFname: " + p.list.get(i).fName
                            + "\nLname: " + p.list.get(i).lName
                            + "\nStreet: " + p.list.get(i).street
                            + "\nZip: " + p.list.get(i).zip
                            + "\nCity: " + p.list.get(i).city
                            + "\nState: " + p.list.get(i).state
                            + "\nCellPhone: " + p.list.get(i).cellPhone
                            + "\nHomePhone: " + p.list.get(i).homePhone + "\n\n"
                    );
                }
            } else {
                System.out.println("Not Found");
            }
        } catch (DLException dl) {
            System.out.println("Error");
            System.out.println(dl.getMessage());
        }
    }
}
