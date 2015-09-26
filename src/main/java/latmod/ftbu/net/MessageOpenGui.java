package latmod.ftbu.net;
import cpw.mods.fml.common.network.simpleimpl.*;
import cpw.mods.fml.relauncher.*;
import io.netty.buffer.ByteBuf;
import latmod.ftbu.mod.FTBU;
import latmod.ftbu.util.LMGuiHandler;
import latmod.ftbu.util.client.LatCoreMCClient;
import net.minecraft.nbt.NBTTagCompound;

public class MessageOpenGui extends MessageLM<MessageOpenGui> implements IClientMessageLM<MessageOpenGui>
{
	public String modID;
	public int guiID;
	public NBTTagCompound data;
	public int windowID;
	
	public MessageOpenGui() { }
	
	public MessageOpenGui(String mod, int id, NBTTagCompound tag, int wid)
	{
		modID = mod;
		guiID = id;
		data = tag;
		windowID = wid;
	}
	
	public void fromBytes(ByteBuf bb)
	{
		modID = LMNetHelper.readString(bb);
		guiID = bb.readInt();
		data = LMNetHelper.readTagCompound(bb);
		windowID = bb.readUnsignedByte();
	}
	
	public void toBytes(ByteBuf bb)
	{
		LMNetHelper.writeString(bb, modID);
		bb.writeInt(guiID);
		LMNetHelper.writeTagCompound(bb, data);
		bb.writeByte(windowID);
	}
	
	public IMessage onMessage(MessageOpenGui m, MessageContext ctx)
	{ FTBU.proxy.handleClientMessage(m, ctx); return null; }
	
	@SideOnly(Side.CLIENT)
	public void onMessageClient(MessageOpenGui m, MessageContext ctx)
	{
		LMGuiHandler h = LMGuiHandler.Registry.getLMGuiHandler(m.modID);
		
		if(h != null)
		{
			if(FTBU.proxy.openClientGui(LatCoreMCClient.mc.thePlayer, m.modID, m.guiID, data))
				LatCoreMCClient.mc.thePlayer.openContainer.windowId = m.windowID;
		}
	}
}