import java.util.*;
import java.util.HashMap;

public class Isolates {

    //Attributes
    private List<Isolate> list = null;
    private String dbPropsFileName = "AOMSimple.properties";
    private HashMap<String, String> map = new HashMap<String, String>();

    // Accessors
    List<Isolate> getIsolates() {
        return this.list;
    }

    Isolate getIsolate(int i) {
        return this.list.get(i);
    }

    // Constructors
    public Isolates() {
        this.list = new ArrayList<Isolate>();
        //to map the table names with the user passed info
        map.put("isolate_Id", "isolate_Id");
        map.put("bacteriaTypeid", "bacteriaType_id");
        map.put("isolatelocation", "isolate_location");
        map.put("sampleid", "sample_id");
        map.put("notation", "notation");
    }

    // CRUD Operations (a single read method that accepts information regarding what filters to apply and populates the list attribute)

    public boolean read(String[][] filters) throws DLException {
        boolean rc = false;
        List<List<String>> tbl = null;
        List<String> params = new ArrayList<String>();
        Database db = new Database(dbPropsFileName);

        String sql = "SELECT isolate_Id, bacteriaType_id, isolate_location, sample_id, notation FROM isolates ";
        //flag for marking first where clause
        boolean flag = false;
        //function to process filters and append sql query accordingly
        // for(ArrayList<String> filter:filters){
        for (int i = 0; i < filters.length; i++) {
            if (filters[i][0] != null && filters[i][0].length() > 0 && filters[i][1] != null && filters[i][1].length() > 0) {
                if (flag) {
                    //to add second or more where clause
                    sql += " AND " + map.get(filters[i][0]) + " = ? ";
                    params.add(filters[i][1]);
                } else {
                    //to add first where clause
                    sql += " WHERE " + map.get(filters[i][0]) + " = ? ";
                    params.add(filters[i][1]);
                }
                //flag is updated when first where clause is appended.
                flag = true;
            }
        }
        tbl = db.getTable(sql, params);

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
        this.list = new ArrayList<Isolate>();
        //load data
        for (int i = 0; i < rs.size(); i++) {
            List<String> row = rs.get(i);
            this.list.add(new Isolate(Integer.parseInt(row.get(0)), row.get(1), row.get(2), Integer.parseInt(row.get(3)), row.get(4)));
        }
    }


    // testing (Optional)
    public static void main(String args[]) {
        Isolates i = new Isolates();

        try {
            String[][] filters = new String[5][5];
            //filters
            filters[0][0] = "isolate_Id";
            filters[0][1] = "";

            filters[1][0] = "bacteriaTypeid";
            filters[1][1] = "Mcat";

            filters[2][0] = "isolatelocation";
            filters[2][1] = "Inventory";

            filters[3][0] = "sampleid";
            filters[3][1] = "";//368

            filters[4][0] = "notation";
            filters[4][1] = "";

            i.read(filters);
            i.list.forEach(iso ->
                    {
                        System.out.printf("%n%s%n", iso.toString());
                    }
            );
            //********
            //System.out.println("Completed");
        } catch (DLException e) {
            e.printStackTrace();
        }


    } // end main

} // end Isolates