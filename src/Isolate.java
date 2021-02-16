import java.util.*;

public class Isolate {

    // Attributes
    private int isolateId;
    private String bacteriaTypeId;
    private String isolateLocation;
    private int sampleId;
    private String notation;

    private String dbPropsFileName = "AOMSimple.properties";

    // Constructors
    public Isolate() {
    }

    public Isolate(int isolateId) {
        this.isolateId = isolateId;
    }

    public Isolate(int isolateId, String bacteriaTypeId, String isolateLocation, int sampleId, String notation) {
        this.isolateId = isolateId;
        this.bacteriaTypeId = bacteriaTypeId;
        this.isolateLocation = isolateLocation;
        this.sampleId = sampleId;
        this.notation = notation;
    }

    // Accessors and Mutators
    public int getIsolateId() {
        return this.isolateId;
    }

    public String getBacteriaTypeId() {
        return this.bacteriaTypeId;
    }

    public String getIsolateLocation() {
        return this.isolateLocation;
    }

    public int getSampleId() {
        return this.sampleId;
    }

    public String getNotation() {
        return this.notation;
    }

    public void setIsolateId(int isolateId) {
        this.isolateId = isolateId;
    }

    public void setBacteriaTypeId(String bacteriaTypeId) {
        this.bacteriaTypeId = bacteriaTypeId;
    }

    public void setIsolateLocation(String isolateLocation) {
        this.isolateLocation = isolateLocation;
    }

    public void setSampleId(int sampleId) {
        this.sampleId = sampleId;
    }

    public void setNotation(String notation) {
        this.notation = notation;
    }

    // CRUD functions
    public boolean insert() throws DLException {
        boolean rc = false;
        List<String> row = null;
        Database db = new Database(dbPropsFileName);
        List<String> params = new ArrayList<String>();
        db.startTransaction();
        this.isolateId = 0;
        this.isolateId = Integer.parseInt(db.getNewId("isolates", "isolate_Id"));
        if(this.isolateId==0){
            db.rollbackTransaction();
            db.close();
            return false;
        }
        String sql = "UPDATE isolates SET bacteriaType_id = ?, isolate_location =?, sample_id=? , notation=? WHERE isolate_Id = ?";
        // System.out.println("value:" + Integer.valueOf(this.equipId));
        params.add(this.bacteriaTypeId);
        params.add(this.isolateLocation);
        params.add("" + this.sampleId);
        params.add(this.notation);
        params.add("" + this.isolateId);

        int num = db.execute(sql, params);
        if(num==0){
            db.rollbackTransaction();
            db.close();
            return false;
        }
        rc = (num == 1);
        db.commitTransaction();
        db.close();
        return rc;
    }

    public boolean read() throws DLException {
        boolean rc = false;
        List<String> row = null;
        List<String> params = new ArrayList<String>();

        Database db = new Database(dbPropsFileName);
        String sql = "SELECT bacteriaType_id, isolate_location, sample_id, notation FROM isolates WHERE isolate_Id = ?";
        params.add("" + this.isolateId);
        row = db.getRow(sql, params);
        if (row.size() == 0) {
            rc = false;
        } else {
            this.setBacteriaTypeId(row.get(0));
            this.setIsolateLocation(row.get(1));
            this.setSampleId(Integer.valueOf(row.get(2)));
            this.setNotation(row.get(3));
            rc = true;
        }
        db.close();
        return rc;
    }

    public boolean update() throws DLException {
        boolean rc = false;
        int num = 0;
        List<String> params = new ArrayList<String>();

        Database db = new Database(dbPropsFileName);
        String sql = "UPDATE isolates SET bacteriaType_id = ?, isolate_location =?, sample_id=? , notation=? WHERE isolate_id = ? ";
        params.add(this.bacteriaTypeId);
        params.add(this.isolateLocation);
        params.add("" + this.sampleId);
        params.add(this.notation);
        params.add("" + this.isolateId);

        num = db.execute(sql, params);
        rc = (num == 1);
        db.close();
        return rc;
    }

    public boolean delete() throws DLException {
        boolean rc = false;
        int num = 0;
        List<String> params = new ArrayList<String>();

        Database db = new Database(dbPropsFileName);
        String sql = "DELETE FROM isolates WHERE isolate_id=?";
        params.add("" + this.isolateId);
        num = db.execute(sql, params);

        rc = (num == 1);
        db.close();

        return rc;
    }

    public String toString() {

        return "Isolate Id: " + this.isolateId
                + "\nBacteria Id: " + this.bacteriaTypeId
                + "\nLocation: " + this.isolateLocation
                + "\nSample Id: " + this.sampleId
                + "\nNotation: " + this.notation;
    }

    //Testing
    public static void main(String args[]) {
        try {
            Isolate test = new Isolate(13);
            test.read();
            System.out.printf("Read:%n%s%s", test.toString(), "\n\n");
            //********
            test.isolateLocation = "updated location NEW";
            test.update();
            test.read();
            System.out.printf("Update:%n%s%s", test.toString(), "\n\n");
            //********
            test.bacteriaTypeId = "spn";
            test.isolateLocation = "Fridge";
            test.sampleId = 403;
            test.notation = "new isolate";
            test.insert();
            test.read();
            System.out.printf("Insert:%n%s%s", test.toString(), "\n\n");
            // ********
            test.delete();
            test.bacteriaTypeId = "";
            test.isolateLocation = "";
            test.sampleId = 0;
            test.notation = "";
            test.read();
            System.out.printf("Delete:%n%s%s", test.toString(), "\n\n");
        } catch (DLException dl) {
            System.out.println(dl.getMessage());
        }

    }// end main

}// end class
