package de.Ste3et_C0st.FurnitureLib.Command;

import java.util.ArrayList;
import java.util.List;

import mkremins.fanciful.FancyMessage;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import de.Ste3et_C0st.FurnitureLib.Crafting.Project;
import de.Ste3et_C0st.FurnitureLib.Events.FurnitureClickEvent;
import de.Ste3et_C0st.FurnitureLib.main.ArmorStandPacket;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureManager;
import de.Ste3et_C0st.FurnitureLib.main.ObjectID;

public class command implements CommandExecutor, Listener{

	FurnitureManager manager;
	Plugin plugin;
	List<Player> playerList = new ArrayList<Player>();
	
	public command(FurnitureManager manager, Plugin plugin){
		this.manager = manager;
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}
	
	@EventHandler
	public void onArmorRightClick(FurnitureClickEvent e){
		if(playerList.contains(e.getPlayer())){
			e.setCancelled(true);
			Player p = e.getPlayer();
			p.sendMessage("�6Furniture Info about�e " + e.getID().getSerial());
			p.sendMessage("�6Plugin:�e " + e.getID().getPlugin());
			p.sendMessage("�6Type:�e " + e.getID().getProject());
			playerList.remove(p);
		}
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String arg2, String[] args) {
		if(sender instanceof Player){
			Player p = (Player) sender;
			if(cmd.getName().equalsIgnoreCase("furniture")){
				if(args.length==0){
					sendHelp(p);
					return true;
				}else if(args.length==1){
					if(args[0].equalsIgnoreCase("list")){
						if(!p.hasPermission("furniture.list") && !p.hasPermission("furniture.admin")) return true;
						getList("type", p);
						return true;
					}else if(args[0].equalsIgnoreCase("debug")){
						if(!p.hasPermission("furniture.debug") && !p.hasPermission("furniture.admin")) return true;
						playerList.add(p);
					}else{
						sendHelp(p);
						return true;
					}
				}else if(args.length==2){
					if(args[0].equalsIgnoreCase("list")){
						if(!p.hasPermission("furniture.list") && !p.hasPermission("furniture.admin")) return true;
						if(args[1].equalsIgnoreCase("plugin")){
							if(!p.hasPermission("furniture.list.plugin") && !p.hasPermission("furniture.admin")) return true;
							getList(null, p);
						}else if(args[1].equalsIgnoreCase("type")){
							if(!p.hasPermission("furniture.list.type") && !p.hasPermission("furniture.admin")) return true;
							getList("type", p);
						}else if(args[1].equalsIgnoreCase("world")){
							if(!p.hasPermission("furniture.list.world") && !p.hasPermission("furniture.admin")) return true;
							getList("world", p);
						}
						return true;
					}else if(args[0].equalsIgnoreCase("remove")){
						if(!p.hasPermission("furniture.remove") && !p.hasPermission("furniture.admin")) return true;
						if(FurnitureLib.getInstance().isInt(args[1])){
							if(!p.hasPermission("furniture.remove.Distance") && !p.hasPermission("furniture.admin")) return true;
							Integer Distance = Integer.parseInt(args[1]);
							List<ObjectID> objList = getFromDistance(Distance, p.getLocation());
							if(objList!=null&&!objList.isEmpty()){
								removeListObj(objList);
							}
							return true;
						}else if(getID(args[1])!=null){
							if(!p.hasPermission("furniture.remove.obj") && !p.hasPermission("furniture.admin")) return true;
							ObjectID id = getID(args[1]);
							if(id!=null){
								manager.remove(id);
							}
							return true;
						}else if(!getPlugin(args[1]).isEmpty()){
							if(!p.hasPermission("furniture.remove.plugin") && !p.hasPermission("furniture.admin")) return true;
							List<ObjectID> objList = getPlugin(args[1]);
							if(objList!=null){
								removeListObj(objList);
							}
							return true;
						}else if(!getType(args[1]).isEmpty()){
							if(!p.hasPermission("furniture.remove.type") && !p.hasPermission("furniture.admin")) return true;
							List<ObjectID> objList = getType(args[1]);
							if(objList!=null){
								removeListObj(objList);
							}
							return true;
						}else if(args[1].equalsIgnoreCase("all")){
							if(!p.hasPermission("furniture.remove.all") && !p.hasPermission("furniture.admin")) return true;
							removeListObj(manager.getObjectList());
							return true;
						}else if(args[1].equalsIgnoreCase("lookat")){
							if(!p.hasPermission("furniture.remove.lookat") && !p.hasPermission("furniture.admin")) return true;
							ObjectID obj = getFromSight(p.getLocation());
							if(obj!=null){
								manager.remove(obj);
							}
							return true;
						}
					}else if(args[0].equalsIgnoreCase("give")){
						if(!p.hasPermission("furniture.give") && !p.hasPermission("furniture.admin")) return true;
						give(sender, p, args[1]);
					}else if(args[0].equalsIgnoreCase("recipe")){
						if(!p.hasPermission("furniture.recipe") && !p.hasPermission("furniture.admin")) return true;
						FurnitureLib.getInstance().getCraftingInv().openCrafting(p, args[1]);
					}else{
						sendHelp(p);
						return true;
					}
					return true;
				}else if(args.length==3){
					if(args[1].equalsIgnoreCase("give")){
						if(!p.hasPermission("furniture.give.player") && !p.hasPermission("furniture.admin")) return true;
						if(Bukkit.getPlayer(args[1]) == null || !Bukkit.getPlayer(args[1]).isOnline()){
							sender.sendMessage("The player : " + args[1] + " is not Online");
							return true;
						}
						give(sender, Bukkit.getPlayer(args[1]), args[2]);
					}else{
						sendHelp(p);
						return true;
					}
					return true;
				}else{
					sendHelp(p);
					return true;
				}
				
				return true;
			}
		}else if(sender instanceof BlockCommandSender){
			if(FurnitureLib.getInstance().isDonate()){
				if(cmd.getName().equalsIgnoreCase("furniture")){
					BlockCommandSender bs = (BlockCommandSender) sender;
					//Furniture spawn x y z yaw type
					//Furniture crafting player type
					if(args.length==6){
						if(args[0].equalsIgnoreCase("spawn")){
							Boolean Yaw = FurnitureLib.getInstance().isInt(args[4]);
							if(FurnitureLib.getInstance().getFurnitureManager().getProject(args[5])!=null){
								Integer x = Integer.parseInt(relativ((BlockCommandSender) sender, args[1], 0));
								Integer y = Integer.parseInt(relativ((BlockCommandSender) sender, args[2], 1));
								Integer z = Integer.parseInt(relativ((BlockCommandSender) sender, args[3], 2));
								Integer yaw = 0;
								
								if(Yaw){
									yaw = Integer.parseInt(args[4]);
								}
								
									World w = bs.getBlock().getWorld();
									Location l = new Location(w, x, y, z).getBlock().getLocation();
									l.setYaw(yaw);
									Project pro = FurnitureLib.getInstance().getFurnitureManager().getProject(args[5]);
									FurnitureLib.getInstance().spawn(pro, l);
							}
						}
					}else if(args.length==3){
						if(args[0].equalsIgnoreCase("crafting")){
							if(Bukkit.getPlayer(args[1])==null){return true;}
							if(FurnitureLib.getInstance().getFurnitureManager().getProject(args[2])==null){return true;}
							FurnitureLib.getInstance().getCraftingInv().openCrafting(Bukkit.getPlayer(args[1]), args[2]);
						}
					}
				}
			}
		}
		return false;
	}
	
	private String relativ(BlockCommandSender sender, String s, int i){
		Location l = sender.getBlock().getLocation();
		Integer j = 0;
		if(s.startsWith("~")){
			s = s.replace("~", "");
			if(i==0) j = (int) l.getX();
			if(i==1) j = (int) l.getY();
			if(i==2) j = (int) l.getZ();
			if(s.isEmpty()) return j+"";
			if(s.startsWith("-")){
				s = s.replace("-", "");
				if(!FurnitureLib.getInstance().isInt(s)) return j+"";
				j-=Integer.parseInt(s);
			}else if(s.startsWith("+")){
				s = s.replace("+", "");
				if(!FurnitureLib.getInstance().isInt(s)) return j+"";
				j+=Integer.parseInt(s);
			}
		}else{
			return s;
		}
		return j+"";
	}
	
	private void give(CommandSender sender, Player p2, String string) {
		if(!(sender.hasPermission("furniture.admin") || sender.isOp())){ sender.sendMessage("�cYou don't have permissions to do this"); return;}
		Project project = null;
		for(Project pro : manager.getProjects()){
			if(pro.getName().equalsIgnoreCase(string)) project = pro;
		}
		if(project==null){
			sender.sendMessage("�cThe project" + string + " deos not exist"); 
			return;
		}
		
		p2.getInventory().addItem(project.getCraftingFile().getRecipe().getResult());
		if(sender instanceof Player && sender.equals(p2)){return;}
		sender.sendMessage("The Player " + p2.getName() + " becomes: " + project.getName());
	}

	private void getList(String option, Player p){
		String s = "�7�m+--------------------------------------------------+";
		p.sendMessage(s);
		if(option==null){option="Plugin";}
		if(option.equalsIgnoreCase("Plugin")){
			p.sendMessage("�ePlugins: ");
			List<String> plugins = new ArrayList<String>();
			for(ObjectID id : manager.getObjectList()){
				if(!plugins.contains(id.getPlugin())){
					String plugin = id.getPlugin();
					String toolTip = "�aObjects: " + getPlugin(plugin).size() + "\n" + getTypes(plugin);
					
					new FancyMessage("�6- " +plugin).tooltip(toolTip).send(p);
					plugins.add(plugin);
				}
			}
		}else if(option.equalsIgnoreCase("type")){
			p.sendMessage("�eTypes: ");
			List<String> types = new ArrayList<String>();
			for(ObjectID id : manager.getObjectList()){
				if(!types.contains(id.getProject())){
					String plugin = id.getPlugin();
					String toolTip = "�aObjects: " + getPlugin(plugin).size() + "\n" + "�aPlugin: " + plugin;
					new FancyMessage("�6- " +id.getProject()).tooltip(toolTip).send(p);
					types.add(id.getProject());
				}
			}
			
			for(Project pro : manager.getProjects()){
				if(!types.contains(pro.getName())){
					String plugin = pro.getPlugin().getName();
					String toolTip = "�aObjects: " + getPlugin(plugin).size() + "\n" + "�aPlugin: " + plugin;
					new FancyMessage("�6- " + pro.getName()).tooltip(toolTip).send(p);
					types.add(pro.getName());
				}
			}
		}else if(option.equalsIgnoreCase("world")){
			p.sendMessage("�eWorlds: ");
			for(World w : Bukkit.getWorlds()){
				new FancyMessage("�6- " + w.getName()).tooltip("�aObjects: " + getWInt(w.getName())).send(p);
			}
		}
		s = "�7�m+--------------------------------------------------+";
		p.sendMessage(s);
	}
	
	private String getTypes(String plugin){
		String types="";
		for(ObjectID obj : manager.getObjectList()){
			if(!types.contains(obj.getProject())){
				types += "�a- " + obj.getProject() + " \n";
			}
		}
		return types;
	}
	
	private void sendHelp(Player player){
		if(!player.hasPermission("furniture.help")) return;
		new FancyMessage("�7�m+--------------------�7[")
		.then("�2Furniture").tooltip("")
		.then("�7]�m---------------------+\n")
		.then("�6/furniture list (Option)\n").tooltip("�6list all available furniture\n\n�cOption:\n�6Plugin\n�6Type\n�6World").suggest("/furniture list (Option)")
		.then("�6/furniture give �e<furniture> �c(player)\n").tooltip("�6if the �cPlayer not set:\n"
				+ "�cyou become a furniture\n" + 
				"�6if the Player set:\n" + 
				"�cgive the player one furniture").suggest("/furniture give <FURNITURE> (player)")
		.then("�6/furniture debug \n").tooltip("�6You can become some information about\n�6abaout the furniture you are rightclicked").suggest("/furniture debug")
		.then("�6/furniture recipe �e<type>\n").tooltip("�6View recipe from a furniture").suggest("/furniture recipe �e<type>")
		.then("�6/furniture remove �e<type>\n").tooltip("�6It's remove only one type of the \n�6Furniture").suggest("/furniture remove <type>")
		.then("�6/furniture remove �e<plugin>\n").tooltip("�6It's remove only all Furniture from one Plugin").suggest("/furniture remove <plugin>")
		.then("�6/furniture remove �e<ID>\n").tooltip("�6Remove a furniture by id").suggest("/furniture remove <ID>")
		.then("�6/furniture remove �e<Distance>\n").tooltip("�6Remove all furniture in Distance").suggest("/furniture remove <Distance>")
		.then("�6/furniture remove �elookat\n").tooltip("�6Remove a furniture at you looked").suggest("/furniture remove lookat")
		.then("�6/furniture remove �eall\n").tooltip("�6Remove all furniture and reset database").suggest("/furniture remove all")
		.then("\n").then("�e�lTIP: �r�7Try to �e�nclick�7 or �e�nhover�7 the commands").
		then("�7�m+--------------------------------------------------+").send(player);
	}
	
	private ObjectID getID(String serial){
		for(ObjectID id : manager.getObjectList()){
			if(serial.equalsIgnoreCase(id.getSerial())){
				return id;
			}
		}
		return null;
	}
	
	private Integer getWInt(String s){
		Integer j = 0;
		for(ArmorStandPacket aPacket : manager.getAsList()){
			if(aPacket.getLocation().getWorld().getName().equalsIgnoreCase(s)){
				j++;
			}
		}
		return j;
	}
	
	private List<ObjectID> getPlugin(String plugin){
		List<ObjectID> objList = new ArrayList<ObjectID>();
		for(ObjectID id : manager.getObjectList()){
			if(id.getPlugin().equalsIgnoreCase(plugin)){
				objList.add(id);
			}
		}
		return objList;
	}
	
	private List<ObjectID> getType(String type){
		List<ObjectID> objList = new ArrayList<ObjectID>();
		for(ObjectID obj : manager.getObjectList()){
			if(obj.getProject().equalsIgnoreCase(type)){
				objList.add(obj);
			}
		}
		return objList;
	}
	
	private List<ObjectID> getFromDistance(Integer i, Location l){
		List<ObjectID> objList = new ArrayList<ObjectID>();
		for(ArmorStandPacket packet : manager.getAsList()){
			if(packet.getLocation().getWorld().getName().equalsIgnoreCase(l.getWorld().getName())){
				if(packet.getLocation().toVector().distance(l.toVector())<=i){
					objList.add(packet.getObjectId());
				}
			}
		}
		return objList;
	}
	
	private ObjectID getFromSight(Location l){
		Integer i = 10;
		BlockFace face = FurnitureLib.getInstance().getLocationUtil().yawToFace(l.getYaw());
		for(int j = 0; j<=i;j++){
			Location loc = FurnitureLib.getInstance().getLocationUtil().getRelativ(l, face,(double) j, 0D);
			if(loc.getBlock()!=null&&loc.getBlock().getType()!=Material.AIR){return null;}
			for(ArmorStandPacket packet : manager.getAsList()){
				if(packet.getLocation().getWorld().getName().equalsIgnoreCase(loc.getWorld().getName())){
					Double d = packet.getLocation().toVector().distanceSquared(loc.toVector());
					if(d<=2.0){
						return packet.getObjectId();
					}
				}
			}
		}
		return null;
	}
	
	private void removeListObj(List<ObjectID> objList){
		if(objList==null){return;}
		try{
			for(ObjectID obj : objList){
				manager.remove(obj);
			}
		}catch(Exception e){}

	}
}