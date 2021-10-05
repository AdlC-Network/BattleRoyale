package es.outlook.adriansrj.battleroyale.packet.factory;

import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.game.PacketPlayOutEntityTeleport;

/**
 * @author AdrianSR / 28/09/2021 / 08:05 p. m.
 */
class PacketFactoryServiceHandle_v1_17_R1 implements PacketFactoryServiceHandle {
	
	public PacketFactoryServiceHandle_v1_17_R1 ( ) {
		// java 16 and its constructor system!
	}
	
	@Override
	public Object createEntityTeleportPacket ( int entity_id , boolean on_ground ,
			double x , double y , double z ,
			float yaw , float pitch ) {
		PacketDataSerializer data = new PacketDataSerializer ( Unpooled.buffer ( ) );
		
		// entity id
		data.d ( entity_id );
		// x, y, z
		data.writeDouble ( x );
		data.writeDouble ( y );
		data.writeDouble ( z );
		// yaw, pitch
		data.writeByte ( ( byte ) ( yaw * 256.0F / 360.0F ) );
		data.writeByte ( ( byte ) ( pitch * 256.0F / 360.0F ) );
		// on ground flag
		data.writeBoolean ( on_ground );
		
		return new PacketPlayOutEntityTeleport ( data );
	}
}