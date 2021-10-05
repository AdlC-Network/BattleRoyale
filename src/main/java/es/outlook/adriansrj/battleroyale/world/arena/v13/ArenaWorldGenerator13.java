package es.outlook.adriansrj.battleroyale.world.arena.v13;

import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.block.BaseBlock;
import com.sk89q.worldedit.world.block.BlockType;
import es.outlook.adriansrj.battleroyale.enums.EnumDataVersion;
import es.outlook.adriansrj.battleroyale.util.math.ChunkLocation;
import es.outlook.adriansrj.battleroyale.util.math.Location2I;
import es.outlook.adriansrj.battleroyale.world.Material;
import es.outlook.adriansrj.battleroyale.world.arena.ArenaWorldGenerator;
import es.outlook.adriansrj.battleroyale.world.chunk.Chunk;
import es.outlook.adriansrj.battleroyale.world.chunk.v13.Chunk13;
import es.outlook.adriansrj.battleroyale.world.data.v13.WorldData13;
import es.outlook.adriansrj.battleroyale.world.region.v13.Region13;
import es.outlook.adriansrj.core.util.world.WorldUtil;
import net.kyori.adventure.nbt.BinaryTagIO;
import org.apache.commons.lang.Validate;
import org.bukkit.util.Vector;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * World generator for versions {@link EnumDataVersion#v1_13}+.
 *
 * @author AdrianSR / 25/08/2021 / Time: 09:19 a. m.
 */
public class ArenaWorldGenerator13 implements ArenaWorldGenerator {
	
	protected final Map < Location2I, Region13 > region_map = new ConcurrentHashMap <> ( );
	protected final WorldData13                  world_data;
	protected final File                         world_folder;
	
	public ArenaWorldGenerator13 ( File world_folder , EnumDataVersion data_version ) {
		Validate.notNull ( world_folder , "world folder cannot be null" );
		Validate.notNull ( data_version , "data version cannot be null" );
		Validate.isTrue ( data_version.getId ( ) >= EnumDataVersion.v1_13.getId ( ) ,
						  "unsupported data version" );
		
		this.world_data   = new WorldData13 ( data_version );
		this.world_folder = world_folder;
	}
	
	public ArenaWorldGenerator13 ( File world_folder ) {
		this ( world_folder , EnumDataVersion.v1_13 );
	}
	
	@Override
	public File getWorldFolder ( ) {
		return world_folder;
	}
	
	@Override
	public WorldData13 getWorldData ( ) {
		return world_data;
	}
	
	@Override
	public Region13 getRegion ( Location2I location ) {
		Region13 region = region_map.get ( location );
		
		if ( region == null ) {
			region_map.put ( location , region = new Region13 ( location ) );
		}
		
		return region;
	}
	
	@Override
	public Chunk13 getChunk ( ChunkLocation location ) {
		return getRegion ( new Location2I ( location.getRegionX ( ) , location.getRegionZ ( ) ) ).getChunk ( location );
	}
	
	@Override
	public Chunk getChunkAt ( Vector vector ) {
		return getChunk ( new ChunkLocation ( vector.getBlockX ( ) >> 4 , vector.getBlockZ ( ) >> 4 ) );
	}
	
	@Override
	public void setBlockAtFromLegacyId ( int x , int y , int z , int id ) {
		setBlockAt ( x , y , z , id , ( byte ) 0 );
	}
	
	public int getBlockIDAt ( int x , int y , int z ) {
		return getChunk ( new ChunkLocation ( x >> 4 , z >> 4 ) ).getBlockIDAt ( x & 15 , y , z & 15 );
	}
	
	public int getBlockDataAt ( int x , int y , int z ) {
		return getChunk ( new ChunkLocation ( x >> 4 , z >> 4 ) ).getBlockDataAt ( x & 15 , y , z & 15 );
	}
	
	public void setBlockAt ( int x , int y , int z , int id , byte data ) {
		getChunk ( new ChunkLocation ( x >> 4 , z >> 4 ) )
				.setBlockAt ( x & 15 , y , z & 15 , id , data );
	}
	
	public Material getMaterialAt ( int x , int y , int z ) {
		return getChunk ( new ChunkLocation ( x >> 4 , z >> 4 ) ).getMaterial ( x & 15 , y , z & 15 );
	}
	
	public void setMaterialAt ( int x , int y , int z , Material material ) {
		getChunk ( new ChunkLocation ( x >> 4 , z >> 4 ) ).setMaterial ( x & 15 , y , z & 15 , material );
	}
	
	@Override
	public void insert ( Clipboard schematic , Vector location , boolean ignore_air_blocks ) {
		BlockVector3    dimensions = schematic.getDimensions ( );
		BaseBlock[][][] blocks     = extractBlocks ( schematic );
		
		if ( blocks != null ) {
			for ( int x = 0 ; x < dimensions.getX ( ) ; x++ ) {
				for ( int y = 0 ; y < dimensions.getY ( ) ; y++ ) {
					for ( int z = 0 ; z < dimensions.getZ ( ) ; z++ ) {
						BaseBlock block = blocks[ x ][ y ][ z ];
						BlockType type  = block != null ? block.getBlockType ( ) : null;
						
						if ( ignore_air_blocks && ( type == null || type.getMaterial ( ).isAir ( ) ) ) {
							continue;
						}
						
						setMaterialAt (
								( int ) Math.floor ( location.getX ( ) + x ) ,
								( int ) Math.floor ( location.getY ( ) + y ) ,
								( int ) Math.floor ( location.getZ ( ) + z ) ,
								Material.from ( block ) );
					}
				}
			}
		} else {
			throw new IllegalArgumentException ( "invalid schematic" );
		}
	}
	
	protected BaseBlock[][][] extractBlocks ( Clipboard schematic ) {
		BaseBlock[][][] blocks = null;
		
		// BlockArrayClipboard clipboard.
		// any other clipboard implementation from world edit
		// should be included here.
		if ( schematic instanceof BlockArrayClipboard ) {
			BlockArrayClipboard array = ( BlockArrayClipboard ) schematic;
			
			try {
				Field field = BlockArrayClipboard.class.getDeclaredField ( "blocks" );
				field.setAccessible ( true );
				
				blocks = ( BaseBlock[][][] ) field.get ( array );
			} catch ( NoSuchFieldException | IllegalAccessException e ) {
				e.printStackTrace ( );
			}
		}
		
		return blocks;
	}
	
	@Override
	public void save ( ) {
		// saving world data
		File level_data_file = new File ( world_folder , WorldUtil.LEVEL_DATA_FILE_NAME );
		
		try {
			if ( ! level_data_file.exists ( ) ) {
				level_data_file.getParentFile ( ).mkdirs ( );
				level_data_file.createNewFile ( );
			}
			
			BinaryTagIO.writer ( ).write ( world_data.toNBT ( ) , level_data_file.toPath ( ) ,
										   BinaryTagIO.Compression.GZIP );
		} catch ( IOException ex ) {
			ex.printStackTrace ( );
		}
		
		// saving chunks
		File region_folder = new File ( world_folder , WorldUtil.REGION_FOLDER_NAME );
		
		if ( ! region_folder.exists ( ) ) {
			region_folder.mkdirs ( );
		}
		
		for ( Region13 region : region_map.values ( ) ) {
			region.save ( region_folder );
		}
	}
}