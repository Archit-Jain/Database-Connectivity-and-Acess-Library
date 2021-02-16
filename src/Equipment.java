/**
 * Author : Archit Jain
 * ISTE 722
 * PE 2
 */

import java.util.*;


public class Equipment {

    //Equipment (EquipID, EquipmentName, EquipmentDescription, EquipmentCapacity)

    private int equipId;
    private String equipmentName;
    private String equipmentDescription;
    private int equipmentCapacity;

    private String dbfile = "dbConfig.properties";

    public Equipment(int equipId) {
        this.equipId = equipId;
    }

    public Equipment(int equipId, String equipmentName, String equipmentDescription, int equipmentCapacity) {
        this.equipId = equipId;
        this.equipmentName = equipmentName;
        this.equipmentDescription = equipmentDescription;
        this.equipmentCapacity = equipmentCapacity;
    }

    public int getEquipId() {
        return equipId;
    }

    public void setEquipId(int equipId) {
        this.equipId = equipId;
    }

    public String getEquipmentName() {
        return equipmentName;
    }

    public void setEquipmentName(String equipmentName) {
        this.equipmentName = equipmentName;
    }

    public String getEquipmentDescription() {
        return equipmentDescription;
    }

    public void setEquipmentDescription(String equipmentDescription) {
        this.equipmentDescription = equipmentDescription;
    }

    public int getEquipmentCapacity() {
        return equipmentCapacity;
    }

    public void setEquipmentCapacity(int equipmentCapacity) {
        this.equipmentCapacity = equipmentCapacity;
    }


    public boolean insert() throws DLException {
        boolean status = false;
        int num = 0;
        Database db = new Database(dbfile);

        this.equipId = 0;
        this.equipId = Integer.parseInt(db.getNewId("Equipment", "equipId"));
        // System.out.println("value:" + Integer.valueOf(this.equipId));
        String sql = "UPDATE equipment SET equipmentname ='" + this.equipmentName + "', equipmentdescription = '" + this.equipmentDescription + "', equipmentcapacity =" + this.equipmentCapacity + " where equipid = " + this.equipId;
        num = db.execute(sql, null);
        status = (num == 1);
        db.close();
        return status;
    }


    public boolean read() throws DLException {
        boolean rc = false;
        List<String> row = null;
        Database db = new Database(dbfile);
        String sql = "select equipmentname, equipmentdescription, equipmentcapacity from equipment where equipid=" + this.equipId;
        row = db.getRow(sql, null);
        if (row.size() == 0) {
            rc = false;
        } else {
            this.setEquipmentName(row.get(0));
            this.setEquipmentDescription(row.get(1));
            this.setEquipmentCapacity(Integer.valueOf(row.get(2)));
            rc = true;
        }
        db.close();
        return rc;
    }

    public boolean delete() throws DLException {
        boolean status = false;
        int num = 0;
        Database db = new Database(dbfile);
        String sql = "Delete from equipment where equipid=" + this.equipId;
        num = db.execute(sql, null);

        status = (num == 1);
        db.close();
        return status;
    }

    public boolean update() throws DLException {
        boolean status = false;
        int num = 0;
        Database db = new Database(dbfile);
        String sql = "UPDATE equipment SET equipmentname ='" + this.equipmentName + "', equipmentdescription = '" + this.equipmentDescription + "', equipmentcapacity =" + this.equipmentCapacity + " where equipid = " + this.equipId;
        num = db.execute(sql, null);
        status = (num == 1);
        db.close();

        return status;

    }

    public static void main(String args[]) {
        try {
            System.out.println("************");
            //read
            Equipment test = new Equipment(894);
            test.read();
            System.out.printf("Read: %s, %s , %s , %s", test.equipId, test.equipmentName, test.equipmentDescription, test.equipmentCapacity);
            System.out.println("");

            //update
            System.out.println("************");
            test.setEquipmentName("updated");
            test.setEquipmentDescription("updated");
            test.setEquipmentCapacity(2);
            test.update();
            test.read();
            System.out.printf("Updated: %s , %s , %s", test.equipmentName, test.equipmentDescription, test.equipmentCapacity);
            System.out.println("");

            //insert
            System.out.println("************");
            test.setEquipmentName("New");
            test.setEquipmentDescription("New");
            test.setEquipmentCapacity(100000);
            test.insert();
            test.read();
            System.out.printf("Inserted: %s %s %s", test.equipmentName, test.equipmentDescription, test.equipmentCapacity);
            System.out.println("");

            //delete
            System.out.println("************");
            test.delete();
            test.setEquipmentName("");
            test.setEquipmentDescription("");
            test.read();
            System.out.printf("Deleted: with id: %s %s %s", test.equipId,test.equipmentName, test.equipmentDescription);
            System.out.println("");


        } catch (DLException dl) {
            System.out.println(dl.getMessage());
        }

    }
}