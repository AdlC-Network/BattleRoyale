package es.outlook.adriansrj.battleroyale.gui.setup.battlefield.session;

import es.outlook.adriansrj.battleroyale.battlefield.setup.BattlefieldSetupHandler;
import es.outlook.adriansrj.battleroyale.battlefield.setup.BattlefieldSetupSession;
import es.outlook.adriansrj.battleroyale.main.BattleRoyale;
import es.outlook.adriansrj.battleroyale.player.Player;
import es.outlook.adriansrj.core.handler.PluginHandler;
import es.outlook.adriansrj.core.menu.ItemMenu;
import es.outlook.adriansrj.core.menu.size.ItemMenuSize;
import org.bukkit.ChatColor;

/**
 * @author AdrianSR / 28/08/2021 / 03:50 p. m.
 */
public final class BattlefieldSetupSessionGUI extends PluginHandler {
	
	public static BattlefieldSetupSessionGUI getInstance ( ) {
		return getPluginHandler ( BattlefieldSetupSessionGUI.class );
	}
	
	// name
	// bounds
	// bus spawns
	// player spawns
	// vehicle spawns
	// border shrink
	// spawns
	// loot configuration
	// loot chests
	
	protected final ItemMenu handle;
	
	/**
	 * Constructs the plugin handler.
	 *
	 * @param plugin the plugin to handle.
	 */
	public BattlefieldSetupSessionGUI ( BattleRoyale plugin ) {
		super ( plugin );
		
		this.handle = new ItemMenu ( ChatColor.BLACK + "Battlefield Setup" , ItemMenuSize.THREE_LINE );
		this.handle.registerListener ( plugin );
	}
	
	public void open ( org.bukkit.entity.Player player ) {
		this.build ( Player.getPlayer ( player ) );
		this.handle.open ( player );
	}
	
	public void refresh ( org.bukkit.entity.Player player ) {
		this.build ( Player.getPlayer ( player ) );
		this.handle.update ( player );
	}
	
	protected void build ( Player player ) {
		this.handle.clear ( );
		
		BattlefieldSetupHandler handler = BattlefieldSetupHandler.getInstance ( );
		BattlefieldSetupSession session = handler.getSession ( player )
				.orElse ( handler.getSessionFromInvited ( player ).orElse ( null ) );
		
		if ( session != null ) {
			this.handle.setItem ( 10 , new SetNameButton ( ) );
			this.handle.setItem ( 12 , new SetBoundsButton ( ) );
			
			if ( session.getResult ( ) != null && session.getResult ( ).getBounds ( ) != null ) {
				this.handle.setItem ( 14 , new SetBusSpawnsButton ( ) );
				this.handle.setItem ( 16 , new SetPlayerSpawnsButton ( ) );
				this.handle.setItem ( 19 , new SetLootChestsButton ( ) );
				this.handle.setItem ( 21 , new SetVehicleSpawnsButton ( ) );
				
				// vehicles configuration
				SetVehiclesConfigurationButton vehicles_configuration = new SetVehiclesConfigurationButton ( );
				
				if ( vehicles_configuration.available ( ) ) {
					this.handle.setItem ( 23 , vehicles_configuration );
				}
				
				// loot configuration
				SetLootConfigurationButton loot_configuration = new SetLootConfigurationButton ( );
				
				if ( loot_configuration.available ( ) ) {
					this.handle.setItem ( 25 , loot_configuration );
				}
			}
		}
	}
	
	@Override
	protected boolean isAllowMultipleInstances ( ) {
		return false;
	}
}