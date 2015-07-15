package de.Ste3et_C0st.FurnitureLib.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import de.Ste3et_C0st.FurnitureLib.main.ArmorStandPacket;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureManager;
import de.Ste3et_C0st.FurnitureLib.main.ObjectID;

public abstract class Database {
    FurnitureLib plugin;
    Connection connection;
    Statement statement;
    public Database(FurnitureLib instance){
        plugin = instance;
    }

    public abstract Connection getSQLConnection();

    public abstract void load();
    
    public void initialize(){
        connection = getSQLConnection();
        try{
        	statement = connection.createStatement();
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM FurnitureLib_ArmorStand WHERE ID = ?");
            ResultSet rs = ps.executeQuery();
            close(ps,rs);
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, "Unable to retreive connection", ex);
        }
    }
    
    public void save(ObjectID id){
    	FurnitureManager manager = FurnitureLib.getInstance().getFurnitureManager();
    	for(ArmorStandPacket as : manager.getArmorStandPacketByObjectID(id)){
    		set(as);
    	}
    }
    
    private void set(ArmorStandPacket as){
    	String[] a = FurnitureLib.getInstance().getSerialize().toArmorStandString(as);
    	String query = "REPLACE INTO FurnitureLib_ArmorStand (`ID`,`ObjID`,`Metadata`,`Boolean`,`Location`,`ObjIDLocation`,`InHand`,`boots`,`chestplate`,`leggings`,`helm`, `EA_Head`,`EA_Body`,`EA_Left_Arm`,`EA_Right_Arm`,`EA_Left_Leg`,`EA_Right_Leg`) VALUES(" +
    			"'" + as.getArmorID() + "', "
    			+ "'" + a[0] + "', "
    			+ "'" + a[1] + "', "
    			+ "'" + a[2] + "', "
    			+ "'" + a[3] + "', "
    			+ "'" + a[4] + "', "
    			+ "'" + a[5] + "', "
    			+ "'" + a[6] + "', "
    			+ "'" + a[7] + "', "
    			+ "'" + a[8] + "', "
    			+ "'" + a[9] + "', "
    			+ "'" + a[10] + "', "
    			+ "'" + a[11] + "', "
    			+ "'" + a[12] + "', "
    			+ "'" + a[13] + "', "
    			+ "'" + a[14] + "', "
    			+ "'" + a[15] + "');";
    	try{
    		statement.executeUpdate(query);
    	}catch(Exception e){
    		
    	}
    }
    
    public void loadAll(){
    	try{
    		ResultSet rs = statement.executeQuery("SELECT * FROM FurnitureLib_ArmorStand");
    		List<String[]> asList = new ArrayList<String[]>();
    		while (rs.next()) {
				String a[] = new String[17];
				for(int i = 0; i<=16;i++){
						a[i] = rs.getString(i+1);
				}
				
				if(!asList.contains(a)){
					asList.add(a);
				}
				
			}
    		rs.close();
    		for(String[] l : asList){
    			FurnitureLib.getInstance().getSerialize().fromArmorStandString(l);
    		}
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    }

    public void delete(ObjectID objID){
    	try {
			statement.execute("DELETE FROM FurnitureLib_ArmorStand WHERE ObjID = '" + objID.getID() + "'");
		} catch (SQLException e) {
			e.printStackTrace();
		}
    }
 

    public void close(PreparedStatement ps,ResultSet rs){
        try {
            if (ps != null)
                ps.close();
            if (rs != null)
                rs.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}