package latmod.ftbu.net;
import java.util.UUID;

import com.mojang.authlib.GameProfile;

import cpw.mods.fml.common.network.simpleimpl.*;
import cpw.mods.fml.relauncher.*;
import io.netty.buffer.ByteBuf;
import latmod.ftbu.api.EventLMPlayerClient;
import latmod.ftbu.mod.FTBU;
import latmod.ftbu.world.*;
import net.minecraft.nbt.NBTTagCompound;

public class MessageLMPlayerLoggedIn extends MessageLM<MessageLMPlayerLoggedIn> implements IClientMessageLM<MessageLMPlayerLoggedIn>
{
	public int playerID;
	public UUID uuid;
	public String username;
	public NBTTagCompound data;
	public boolean firstTime;
	
	public MessageLMPlayerLoggedIn() { }
	
	public MessageLMPlayerLoggedIn(LMPlayerServer p, boolean first, boolean self)
	{
		playerID = p.playerID;
		uuid = p.getUUID();
		username = p.getName();
		
		data = new NBTTagCompound();
		p.writeToNet(data, self);
		
		firstTime = first;
	}
	
	public void fromBytes(ByteBuf bb)
	{
		playerID = bb.readInt();
		uuid = LMNetHelper.readUUID(bb);
		username = LMNetHelper.readString(bb);
		data = LMNetHelper.readTagCompound(bb);
		firstTime = bb.readBoolean();
	}
	
	public void toBytes(ByteBuf bb)
	{
		bb.writeInt(playerID);
		LMNetHelper.writeUUID(bb, uuid);
		LMNetHelper.writeString(bb, username);
		LMNetHelper.writeTagCompound(bb, data);
		bb.writeBoolean(firstTime);
	}
	
	public IMessage onMessage(MessageLMPlayerLoggedIn m, MessageContext ctx)
	{ FTBU.proxy.handleClientMessage(m, ctx); return null; }
	
	@SideOnly(Side.CLIENT)
	public void onMessageClient(MessageLMPlayerLoggedIn m, MessageContext ctx)
	{
		LMPlayerClient p = LMWorldClient.inst.getPlayer(m.playerID);
		boolean add = p == null;
		if(add) p = new LMPlayerClient(LMWorldClient.inst, m.playerID, new GameProfile(m.uuid, m.username));
		p.readFromNet(m.data, p.playerID == LMWorldClient.inst.clientPlayerID);
		if(add) LMWorldClient.inst.players.add(p);
		new EventLMPlayerClient.DataLoaded(p).post();
		new EventLMPlayerClient.LoggedIn(p, m.firstTime).post();
	}
}