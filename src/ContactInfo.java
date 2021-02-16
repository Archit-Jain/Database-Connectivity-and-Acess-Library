/**
 * Author : Archit Jain
 * ISTE 722
 * PE 4
 */

import java.util.*;

public class ContactInfo {
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
    //private List<Passengers> list = null;
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
/*    List<Passengers> getPassengers() {
        return this.list;
    }

    Passengers getPassenger(int i) {
        return this.list.get(i);
    }*/

    //constructors
    public ContactInfo() {
    }

    public ContactInfo(int passengerId) {
        this.passengerId = passengerId;
    }

    public ContactInfo(int passengerId, String fName, String lName, String street, String city, String state, String zip, String homePhone, String cellPhone) {
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

    //Virtual DAO READ() operation
    public boolean read() throws DLException {
        boolean rc = false;
        List<String> row = null;
        Database db = new Database(dbFile);
        List<String> params = new ArrayList<String>();
        String sql = "SELECT passenger.passengerid, fname, lname,street,passenger.zip,city,state,home.phonenum AS homephone,cell.phonenum AS cellphone FROM passenger " +
                "JOIN zips ON passenger.zip = zips.zip " +
                "LEFT JOIN phones home ON passenger.passengerid=home.passengerId AND home.phonetype=\"Home\" " +
                "LEFT JOIN phones cell ON passenger.passengerid=cell.passengerId AND cell.phonetype=\"Cell\" " +
                "WHERE passenger.passengerid= ? ";
        params.add("" + this.passengerId);

        row = db.getRow(sql, params);
        //return false if PassengerId is non- existent
        if (row.size() == 0) {
            rc = false;
        } else {
            if (row.get(7) != null || row.get(8) != null) {
                this.setPassengerId(Integer.parseInt(row.get(0)));
                this.setfName(row.get(1));
                this.setlName(row.get(2));
                this.setStreet(row.get(3));
                this.setZip(row.get(4));
                this.setCity(row.get(5));
                this.setState(row.get(6));
                this.setHomePhone(row.get(7));
                this.setCellPhone(row.get(8));
                rc = true;
            }
        }
        db.close();
        return rc;
    }

    /**
     * Virtual DAO Insert() operation
     * Inserts into Zips, Passenger , Phones tables of 'Travel' Database
     *
     * @return boolean status
     **/
    public boolean insert() throws DLException {

        boolean rc = false;
        if (this.zip != null && this.zip.isEmpty()) {
            return rc;
        }

        //Intialise Database and Start Transaction
        Database db = new Database(dbFile);
        db.startTransaction();

        //Check if zip-code exists in Database
        List<String> params = new ArrayList<String>();
        String sql = "SELECT  zip,city,state FROM zips WHERE zip = ?";
        params.add(this.zip);
        List<String> row = db.getRow(sql, params);

        if (row.size() == 0) {
            //Insert the new zipcode
            params = new ArrayList<String>();
            String sqlZips = "INSERT INTO zips (city, state, zip) VALUES (?, ?, ?)";
            params.add(this.city);
            params.add(this.state);
            params.add(this.zip);
            int numZ = db.execute(sqlZips, params);
            //System.out.println(numZ != 1 ? "Insert Zip Failed" : "Insert Zip completed: " + numZ);
            if (numZ != 1) {
                db.rollbackTransaction();
                db.close();
                return false;
            }
        } else {
            //check if Existing zipcode matches with Database's City and State
            if (!this.city.toLowerCase().equals(row.get(1).toLowerCase()) || !this.state.toLowerCase().equals(row.get(2).toLowerCase())) {
                rc = false;
                db.rollbackTransaction();
                db.close();
                return false;
            } /*else {
                System.out.println("Zip exists and matched with State/City Fields");
            }*/
        }

        //Passengers table
        List<List<String>> flds = new ArrayList<>();
        ArrayList<String> fld = new ArrayList<>();
        params = new ArrayList<String>();
        fld.add("zip");
        fld.add(this.zip);
        flds.add(fld);
        this.passengerId = 0;
        this.passengerId = Integer.parseInt(db.getNewId("Passenger", "passengerId", flds));
        int numP = 0;
        //Checks if getNewId method worked or not
        if (this.passengerId != 0) {
            String sqlPassenger = "UPDATE passenger SET fname = ?,lname = ?, street = ? WHERE passengerId = ?";
            params.add(this.fName);
            params.add(this.lName);
            params.add(this.street);
            params.add("" + this.passengerId);
            numP = db.execute(sqlPassenger, params);
        }
        //System.out.println(numP != 1 ? "Insert Passenger Failed" : "Insert Passenger completed: " + numP);
        if (numP != 1) {
            db.rollbackTransaction();
            db.close();
            return false;
        }

        //Insert Home-number
        if (this.homePhone != null && !this.homePhone.isEmpty()) {
            params = new ArrayList<>();
            String sqlHomePhones = "INSERT INTO Phones (passengerId , phonenum, phonetype) VALUES (?, ?, ?)";
            params.add("" + this.passengerId);
            params.add(this.homePhone);
            params.add("Home");
            int numH = db.execute(sqlHomePhones, params);
            if (numH != 1) {
                db.rollbackTransaction();
                db.close();
                return false;
            }
        }
        //Insert Cell-number
        if (this.cellPhone != null && !this.cellPhone.isEmpty()) {
            params = new ArrayList<>();
            String sqlCellPhones = "INSERT INTO Phones (passengerId , phonenum, phonetype)VALUES (?, ?, ?)";
            params.add("" + this.passengerId);
            params.add(this.cellPhone);
            params.add("Cell");
            int numC = db.execute(sqlCellPhones, params);
            if (numC != 1) {
                db.rollbackTransaction();
                db.close();
                return false;
            }
        }

        //db.commit
        db.commitTransaction();
        db.close();
        return true;
    }

    public static void main(String[] args) {
        try {
            ContactInfo p = new ContactInfo();

           /* if (p.read()) {
                System.out.println("********");
                System.out.printf("Read:\n Passenger ID: %s, \n Fname: %s ,\n Lname: %s ,\n Street: %s,\n City: %s,\n State: %s ,\n Zip: %s ,\n HomePhone: %s,\n CellPhone:  %s",
                        p.getPassengerId(), p.getfName(), p.getlName(), p.getStreet(), p.getCity(), p.getState(), p.getZip(), p.getHomePhone(), p.getCellPhone());
                System.out.println("");
            } else {
                System.out.println("Not Found");
            }*/

            //insert
            System.out.println("************");
            p.setfName("Archit");
            p.setlName("Jain");
            p.setStreet("RIT Street Rd");
            p.setCity("New Town");
            p.setState("NY");
            p.setZip("12378");
            p.setHomePhone("1234567890");
            p.setCellPhone("1234567809");
            boolean status = p.insert();
            if (status) {
                System.out.println("Insertion Success!");
                p.read();
                System.out.printf("\nRead:\n Passenger ID: %s, \n Fname: %s ,\n Lname: %s ,\n Street: %s,\n City: %s,\n State: %s ,\n Zip: %s ,\n HomePhone: %s,\n CellPhone:  %s",
                        p.getPassengerId(), p.getfName(), p.getlName(), p.getStreet(), p.getCity(), p.getState(), p.getZip(), p.getHomePhone(), p.getCellPhone());
                System.out.println("");
            } else {
                System.out.println("Insertion Failed!");
            }


        } catch (DLException dl) {
            System.out.println("Exception Occured!");
            System.out.println(dl.getMessage());
        }
    }
}
