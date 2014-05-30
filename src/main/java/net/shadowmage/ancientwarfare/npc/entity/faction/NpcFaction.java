package net.shadowmage.ancientwarfare.npc.entity.faction;

import io.netty.buffer.ByteBuf;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.npc.AncientWarfareNPC;
import net.shadowmage.ancientwarfare.npc.config.AWNPCStatics;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;
import net.shadowmage.ancientwarfare.npc.entity.NpcPlayerOwned;
import net.shadowmage.ancientwarfare.npc.faction.FactionTracker;
import cpw.mods.fml.common.network.ByteBufUtils;

public abstract class NpcFaction extends NpcBase
{

String subType = "";

public NpcFaction(World par1World)
  {
  super(par1World);
  }

@Override
public boolean canBeCommandedBy(String playerName)
  {
  return false;
  }

@Override
public boolean isHostileTowards(Entity e)
  {
  if(e instanceof EntityPlayer)
    {
    int standing = FactionTracker.INSTANCE.getStandingFor(worldObj, e.getCommandSenderName(), getFaction());
    return standing<0;
    }
  else if(e instanceof NpcPlayerOwned)
    {
    NpcBase npc = (NpcBase)e;
    int standing = FactionTracker.INSTANCE.getStandingFor(worldObj, npc.getOwnerName(), getFaction());
    return standing<0;
    }
  else
    {
    List <String> targets = AncientWarfareNPC.statics.getValidTargetsFor(getFaction(), "");
    String t = EntityList.getEntityString(e);
    return targets.contains(t);
    }
  }

@Override
public void onDeath(DamageSource damageSource)
  {  
  super.onDeath(damageSource);
  if(damageSource.getEntity() instanceof EntityPlayer)
    {
    String faction = getFaction();
    EntityPlayer player = (EntityPlayer)damageSource.getEntity();
    FactionTracker.INSTANCE.adjustStandingFor(worldObj, player.getCommandSenderName(), faction, -AWNPCStatics.factionLossOnDeath);
    }  
  else if(damageSource.getEntity() instanceof NpcPlayerOwned)
    {
    NpcBase npc = (NpcBase)damageSource.getEntity();
    String faction = getFaction();
    String playerName = npc.getOwnerName();
    if(!playerName.isEmpty())
      {
      FactionTracker.INSTANCE.adjustStandingFor(worldObj, playerName, faction, -AWNPCStatics.factionLossOnDeath);
      }
    }
  }

public void setSubtype(String subtype)
  {
  if(subtype==null){subtype="";}
  this.subType = subtype;
  }

@Override
public String getNpcSubType()
  {
  return subType;
  }

public String getFaction()
  {
  String type = getNpcType();
  String faction = type.substring(0, type.indexOf("."));
  return faction;
  }

@Override
public void writeEntityToNBT(NBTTagCompound tag)
  {
  super.writeEntityToNBT(tag);
  tag.setString("subType", subType);
  }

@Override
public void readEntityFromNBT(NBTTagCompound tag)
  {  
  super.readEntityFromNBT(tag);
  subType = tag.getString("subType");
  }

@Override
public void writeSpawnData(ByteBuf buffer)
  {  
  super.writeSpawnData(buffer);
  ByteBufUtils.writeUTF8String(buffer, subType);
  }

@Override
public void readSpawnData(ByteBuf additionalData)
  {
  super.readSpawnData(additionalData);
  subType = ByteBufUtils.readUTF8String(additionalData);
  }
  
@Override
public void readAdditionalItemData(NBTTagCompound tag)
  {
  //TODO
  }

@Override
public void writeAdditionalItemData(NBTTagCompound tag)
  {
   //TODO
  }

@Override
public boolean isValidOrdersStack(ItemStack stack)
  {
  //noop, no orders
  return false;
  }

@Override
public void onOrdersInventoryChanged()
  {
  //noop, no orders
  }

@Override
public void onWeaponInventoryChanged()
  {
  //noop, no inventory changing
  }

}