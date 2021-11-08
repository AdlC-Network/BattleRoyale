package es.outlook.adriansrj.battleroyale.world.arena;

import com.sk89q.worldedit.extent.clipboard.Clipboard;
import es.outlook.adriansrj.battleroyale.enums.EnumDataVersion;
import es.outlook.adriansrj.battleroyale.util.math.ChunkLocation;
import es.outlook.adriansrj.battleroyale.util.math.Location2I;
import es.outlook.adriansrj.battleroyale.world.arena.v12.ArenaWorldGenerator12;
import es.outlook.adriansrj.battleroyale.world.arena.v13.ArenaWorldGenerator13;
import es.outlook.adriansrj.battleroyale.world.chunk.Chunk;
import es.outlook.adriansrj.battleroyale.world.data.WorldData;
import es.outlook.adriansrj.battleroyale.world.region.Region;
import org.bukkit.util.Vector;

import java.io.File;

/**
 * @author AdrianSR / 25/08/2021 / Time: 11:06 a. m.
 */
public interface ArenaWorldGenerator {
	
	public static ArenaWorldGenerator createGenerator ( File world_folder ) {
		EnumDataVersion data_version = EnumDataVersion.getServerDataVersion ( );
		
		if ( data_version.getId ( ) < EnumDataVersion.v1_13.getId ( ) ) {
			return new ArenaWorldGenerator12 (
					world_folder , data_version );
		} else {
			return new ArenaWorldGenerator13 (
					world_folder , data_version );
		}
	}
	
	public File getWorldFolder ( );
	
	public WorldData getWorldData ( );
	
	public Region getRegion ( Location2I location );
	
	public Chunk getChunk ( ChunkLocation location );
	
	public Chunk getChunkAt ( Vector vector );
	
	public void setBlockAtFromLegacyId ( int x , int y , int z , int id );
	
	public void insert ( Clipboard schematic , Vector location , boolean ignore_air_blocks );
	
	public void save ( );
}