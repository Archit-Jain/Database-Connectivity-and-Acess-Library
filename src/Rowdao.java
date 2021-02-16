import java.sql.DatabaseMetaData;
import java.util.*;


public class Rowdao {

    private int equipId;
    private String equipmentName;
    private String equipmentDescription;
    private int equipmentCapacity;

    private String dbfile = "dbConfig.properties";

    public Rowdao(int equipId) {
        this.equipId = equipId;
    }

    //public Rowdao(){}
    public Rowdao(int equipId, String equipmentName, String equipmentDescription, int equipmentCapacity) {
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

    public boolean read() throws DLException {
        boolean rc = false;
        Database db = new Database(dbfile);
        String sql = "Select Equipid, eqipmentname, equipementdescription, equipemntcapacity from Equipment where equipid= ?";
        List<String> row = null;
        List<String> param = new ArrayList<String>();
        param.add("" + this.equipId);
        row = db.getRow(sql, param);
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
        boolean rc = false;
        Database db = new Database(dbfile);
        String sql = "Delete from Equipment where equipid = ?";
        List<String> params = new ArrayList<String>();
        params.add("" + this.equipId);
        int r = db.execute(sql, params);
        rc = (r == 1);
        db.close();
        return rc;
    }

    public boolean update() throws DLException {
        boolean rc = false;
        Database db = new Database(dbfile);
        String sql = "UPDATE equipment SET equipmentname ='" + this.equipmentName + "', equipmentdescription = '" + this.equipmentDescription + "', equipmentcapacity =" + this.equipmentCapacity + " where equipid = " + this.equipId;
        ArrayList<String> params = new ArrayList<String>();
        params.add("" + this.equipId);
        return rc;
    }

    public boolean insert() throws DLException {
        boolean rc = false;
        List<String> row = null;
        Database db = new Database(dbfile);
        this.equipId = 0;
        this.equipId = Integer.parseInt(db.getNewId("Equipment", "equipId"));
        // System.out.println("value:" + Integer.valueOf(this.equipId));
        String sql = "UPDATE equipment SET equipmentname ='" + this.equipmentName + "', equipmentdescription = '" + this.equipmentDescription + "', equipmentcapacity =" + this.equipmentCapacity + " where equipid = " + this.equipId;
        int num = db.execute(sql, null);
        rc = (num == 1);
        db.close();
        return rc;
    }

    public static void main(String[] args) {
        Rowdao r = new Rowdao(1);

        try {
            r.read();
        } catch (DLException e) {
            e.printStackTrace();
        }

    }
}
