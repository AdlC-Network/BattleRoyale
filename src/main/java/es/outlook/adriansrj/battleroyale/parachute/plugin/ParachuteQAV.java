package es.outlook.adriansrj.battleroyale.parachute.plugin;

import es.outlook.adriansrj.battleroyale.parachute.Parachute;
import es.outlook.adriansrj.battleroyale.player.Player;
import es.outlook.adriansrj.core.util.StringUtil;
import es.outlook.adriansrj.core.util.configurable.ConfigurableEntry;
import me.zombie_striker.qav.api.QualityArmoryVehicles;
import me.zombie_striker.qav.vehicles.AbstractVehicle;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.permissions.Permission;

/**
 * Parachute with a model provided by the <b>Quality Armory Vehicles</b> plugin.
 *
 * @author AdrianSR / 12/09/2021 / 09:48 a. m.
 */
public class ParachuteQAV extends Parachute {
	
	public static ParachuteQAV of ( ConfigurationSection section ) {
		return new ParachuteQAV ( ).load ( section );
	}
	
	@ConfigurableEntry ( key = "model" )
	protected String model_name;
	
	public ParachuteQAV ( int price , Permission permission , String model_name ) {
		super ( price , permission );
		
		this.model_name = model_name;
	}
	
	public ParachuteQAV ( String model_name ) {
		this.model_name = model_name;
	}
	
	public ParachuteQAV ( ) {
		// to be loaded
	}
	
	public String getModelName ( ) {
		return model_name;
	}
	
	public AbstractVehicle getModel ( ) {
		return QualityArmoryVehicles.getVehicle ( model_name );
	}
	
	@Override
	public ParachuteQAVInstance createInstance ( Player player ) {
		return new ParachuteQAVInstance ( player , this );
	}
	
	@Override
	public ParachuteQAV load ( ConfigurationSection section ) {
		super.load ( section );
		loadEntries ( section );
		
		return this;
	}
	
	@Override
	public int save ( ConfigurationSection section ) {
		return super.save ( section ) + saveEntries ( section );
	}
	
	@Override
	public boolean isValid ( ) {
		return StringUtil.isNotBlank ( model_name ) && QualityArmoryVehicles.getVehicle ( model_name ) != null;
	}
}